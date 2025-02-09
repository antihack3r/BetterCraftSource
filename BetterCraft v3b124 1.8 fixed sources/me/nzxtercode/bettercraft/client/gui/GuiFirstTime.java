/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui;

import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.io.IOException;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiFirstTime
extends GuiScreen {
    public static boolean isEnabled = Config.getInstance().getConfig("FirstTime").getAsJsonObject().get("enabled").getAsBoolean();

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width - (width / (width / 2) + width / 6) - 6, height - 26, width / (width / 2) + width / 6, 20, "Quit"));
        this.buttonList.add(new GuiButton(1, 6, height - 26, width / (width / 2) + width / 6, 20, "Ok"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Config.getInstance().editConfig("FirstTime", json -> json.add("enabled", new JsonPrimitive(!isEnabled)));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.shutdownMinecraftApplet();
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new GuiMainMenu());
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Welcome!", width / 2, 20 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Thank you for downloading and installing our client!", width / 2, 40 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        StringBuilder stringBuilder = new StringBuilder("Here is some information you might need if you are using \u00a7c\u00a7l\u00a7n");
        BetterCraft.getInstance();
        GuiFirstTime.drawCenteredString(this.fontRendererObj, stringBuilder.append(BetterCraft.clientName).append("\u00a7r for the first time").toString(), width / 2, 50 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "UI editing", width / 2, 70 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Press M to open up the UI options", width / 2, 80 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "NBTEdit", width / 2, 100 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Press N with NBTData Item to open up the NBTEdit", width / 2, 110 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Creative Own Items", width / 2, 130 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Press I to save a Item to own items creative tab", width / 2, 140 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Important Commands", width / 2, 160 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, ".help", width / 2, 170 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "Need help? Feel free to contact us!", width / 2, 190 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        GuiFirstTime.drawCenteredString(this.fontRendererObj, "https://nzxter.de.cool/", width / 2, 200 + this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

