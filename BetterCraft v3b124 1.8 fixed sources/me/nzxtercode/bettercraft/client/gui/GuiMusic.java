/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import javazoom.jl.player.Player;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiMusic
extends GuiScreen {
    private GuiScreen parent;
    private static Thread thread;
    private GuiList radioList;
    private GuiTextField stream;
    private GuiTextField name;
    private int selectedRadio;
    private Player player;
    private float volume = -40.0f;

    public GuiMusic(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void updateScreen() {
        this.stream.updateCursorCounter();
        this.name.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - 70, height - 26, 60, 20, "Back"));
        this.buttonList.add(new GuiButton(1, width / 2 - 50, height - 26, 100, 20, String.format("%s", GuiMusic.isEnabled() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "On" : String.valueOf(EnumChatFormatting.RED.toString()) + "Off")));
        this.buttonList.add(new GuiButton(2, width / 2 - 100 + 200 - 20, height - 26, 20, 20, "+"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100 - 2, height - 26, 20, 20, "-"));
        this.buttonList.add(new GuiButton(4, 5, 5, 60, 20, "Add"));
        this.buttonList.add(new GuiButton(5, width - 65, 5, 60, 20, "Remove"));
        this.stream = new GuiTextField(height, this.fontRendererObj, width / 2 - 15, 5, 150, 20);
        this.stream.setMaxStringLength(Integer.MAX_VALUE);
        this.stream.setText(this.getStreamUrl());
        this.name = new GuiTextField(height, this.fontRendererObj, width / 2 - 133, 5, 100, 20);
        this.name.setMaxStringLength(Integer.MAX_VALUE);
        this.radioList = new GuiList(this.mc, width, height, 32, height - 32, 10);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.getInstance().editMusic("Music", json -> json.add("id", new JsonPrimitive(this.selectedRadio)));
    }

    @Override
    public void handleMouseInput() throws IOException {
        this.radioList.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.startStream();
                button.displayString = String.format("%s", GuiMusic.isEnabled() ? String.valueOf(EnumChatFormatting.GREEN.toString()) + "On" : String.valueOf(EnumChatFormatting.RED.toString()) + "Off");
                break;
            }
            case 2: {
                this.volume += 5.0f;
                if (this.volume >= -10.0f) {
                    this.volume = -10.0f;
                }
                if (this.player == null) break;
                this.player.setGain(this.volume);
                break;
            }
            case 3: {
                this.volume -= 5.0f;
                if (this.volume < -60.0f) {
                    this.volume = -60.0f;
                }
                if (this.player == null) break;
                this.player.setGain(this.volume);
                break;
            }
            case 4: {
                Config.getInstance().editMusic("RadioList", json -> {
                    if (!(this.name.getText().isEmpty() && this.stream.getText().isEmpty() || json.has(this.name.getText()))) {
                        json.add(this.name.getText(), new JsonPrimitive(this.stream.getText()));
                    }
                    this.initGui();
                });
                break;
            }
            case 5: {
                Config.getInstance().editMusic("RadioList", json -> {
                    Map.Entry entry = (Map.Entry)Config.getInstance().getMusic("RadioList").getAsJsonObject().entrySet().toArray()[this.selectedRadio];
                    json.remove((String)entry.getKey());
                    this.initGui();
                });
                this.selectedRadio = 0;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.stream.textboxKeyTyped(typedChar, keyCode)) {
            ((GuiButton)this.buttonList.get((int)1)).enabled = this.stream.getText().length() > 0 && this.stream.getText().split(":").length > 0;
        }
        this.name.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.stream.mouseClicked(mouseX, mouseY, mouseButton);
        this.name.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.radioList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawString(this.fontRendererObj, "Volume: " + EnumChatFormatting.RED.toString() + String.valueOf(this.volume + 65.0f), 2, height - 10, -1);
        this.stream.drawTextBox();
        this.name.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void startStream() {
        if (!GuiMusic.isEnabled()) {
            thread = new Thread(() -> {
                try {
                    this.player = new Player(new BufferedInputStream(new URL(this.getStreamUrl()).openStream()));
                    this.player.setGain(this.volume);
                    this.player.play();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            });
            thread.start();
        } else {
            if (Objects.nonNull(this.player)) {
                this.player.close();
            }
            if (Objects.nonNull(thread)) {
                thread.stop();
                thread = null;
            }
        }
    }

    public static final boolean isEnabled() {
        return Objects.nonNull(thread);
    }

    public final String getStreamUrl() {
        return ((JsonElement)((Map.Entry)Config.getInstance().getMusic("RadioList").getAsJsonObject().entrySet().toArray()[this.selectedRadio]).getValue()).getAsString().replace("https", "http");
    }

    class GuiList
    extends GuiSlot {
        public GuiList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, width, height, topIn, bottomIn, 12);
        }

        @Override
        protected int getSize() {
            return Config.getInstance().getMusic("RadioList").getAsJsonObject().entrySet().size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            Map.Entry entry = (Map.Entry)Config.getInstance().getMusic("RadioList").getAsJsonObject().entrySet().toArray()[slotIndex];
            String radioName = (String)entry.getKey();
            String radioUrl = ((JsonElement)entry.getValue()).getAsString();
            GuiMusic.this.name.setText(radioName);
            GuiMusic.this.stream.setText(radioUrl);
            GuiMusic.this.selectedRadio = slotIndex;
            GuiMusic.this.startStream();
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiMusic.this.selectedRadio;
        }

        @Override
        protected int getContentHeight() {
            return this.getSize() * 12;
        }

        @Override
        protected void drawBackground() {
        }

        @Override
        public void scrollBy(int amount) {
            super.scrollBy(amount);
        }

        @Override
        protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6) {
            Map.Entry entry = (Map.Entry)Config.getInstance().getMusic("RadioList").getAsJsonObject().entrySet().toArray()[p_192637_1_];
            String name = (String)entry.getKey();
            GuiMusic.drawCenteredString(this.mc.fontRendererObj, name, this.width / 2, p_192637_3_, -1);
        }
    }
}

