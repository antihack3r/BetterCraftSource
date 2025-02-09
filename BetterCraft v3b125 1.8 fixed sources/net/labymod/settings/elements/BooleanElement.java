/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.servermanager.Server;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class BooleanElement
extends ControlElement {
    private boolean currentValue;
    private Consumer<Boolean> toggleListener;
    private GuiButton buttonToggle;
    private String stringEnabled = "ON";
    private String stringDisabled = "OFF";
    private Consumer<Boolean> callback;

    public BooleanElement(String displayName, final String configEntryName, ControlElement.IconData iconData) {
        super(displayName, configEntryName, iconData);
        if (configEntryName != null) {
            if (!configEntryName.isEmpty()) {
                try {
                    this.currentValue = ModSettings.class.getDeclaredField(configEntryName).getBoolean(LabyMod.getSettings());
                }
                catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                }
                catch (NoSuchFieldException e2) {
                    e2.printStackTrace();
                }
            }
            this.toggleListener = new Consumer<Boolean>(){

                @Override
                public void accept(Boolean accepted) {
                    try {
                        ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), accepted);
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    BooleanElement.this.setSettingEnabled(accepted);
                }
            };
        }
        this.createButton();
    }

    public BooleanElement(String displayName, final Server server, ControlElement.IconData iconData, final String attributeName) {
        super(displayName, null, iconData);
        this.currentValue = server.getConfig().get(attributeName).getAsBoolean();
        this.toggleListener = new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                server.getConfig().addProperty(attributeName, accepted);
                server.saveConfig();
                server.loadConfig();
                BooleanElement.this.setSettingEnabled(accepted);
            }
        };
        this.createButton();
    }

    public BooleanElement(String configEntryName, ControlElement.IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public void createButton() {
        this.buttonToggle = new GuiButton(-2, 0, 0, 0, 20, "");
        this.setSettingEnabled(this.currentValue);
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        if (this.buttonToggle == null) {
            return;
        }
        this.buttonToggle.enabled = false;
        LabyModCore.getMinecraft().setButtonYPosition(this.buttonToggle, y2 + 1);
        LabyModCore.getMinecraft().drawButton(this.buttonToggle, mouseX, mouseY);
        this.buttonToggle.enabled = true;
        int buttonWidth = this.buttonToggle.getButtonWidth();
        int valueXPos = this.currentValue ? (buttonWidth - 4) / 2 : (buttonWidth - 4) / 2 + 6;
        String displayString = (Object)((Object)(this.buttonToggle.hovered ? ModColor.YELLOW : (this.currentValue ? ModColor.WHITE : ModColor.GRAY))) + (this.currentValue ? this.stringEnabled : this.stringDisabled);
        LabyMod.getInstance().getDrawUtils().drawCenteredString(displayString, LabyModCore.getMinecraft().getXPosition(this.buttonToggle) + valueXPos, LabyModCore.getMinecraft().getYPosition(this.buttonToggle) + 6);
        LabyMod.getInstance().getDrawUtils().drawString(this.currentValue ? ModColor.GREEN.toString() : ModColor.RED.toString(), 0.0, 0.0);
        this.mc.getTextureManager().bindTexture(buttonTextures);
        LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, this.currentValue ? ModColor.toRGB(20, 120, 20, 120) : ModColor.toRGB(120, 20, 20, 120));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.buttonToggle.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            boolean bl2 = this.currentValue = !this.currentValue;
            if (this.toggleListener != null) {
                this.toggleListener.accept(this.currentValue);
            }
            if (this.callback != null) {
                this.callback.accept(this.currentValue);
            }
            this.buttonToggle.playPressSound(this.mc.getSoundHandler());
        }
    }

    public BooleanElement custom(String ... args) {
        if (args.length >= 1) {
            this.stringEnabled = args[0];
        }
        if (args.length >= 2) {
            this.stringDisabled = args[1];
        }
        return this;
    }

    public BooleanElement addCallback(Consumer<Boolean> callback) {
        this.callback = callback;
        return this;
    }

    public boolean getCurrentValue() {
        return this.currentValue;
    }
}

