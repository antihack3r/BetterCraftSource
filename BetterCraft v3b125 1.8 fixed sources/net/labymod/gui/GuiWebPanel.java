/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.GuiRefreshSession;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.support.util.Debug;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiWebPanel
extends GuiScreen {
    private GuiScreen multiplayerScreen;
    private String pin;
    private GuiTextField field;

    public static void open(String message, GuiScreen multiplayerScreen) {
        message = ModColor.removeColor(message);
        Debug.log(Debug.EnumDebugMode.MINECRAFT, "Disconnected: " + message);
        if (message.equals(I18n.format("disconnect.loginFailedInfo", I18n.format("disconnect.loginFailedInfo.invalidSession", new Object[0])))) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiRefreshSession(multiplayerScreen));
            return;
        }
        if (!message.contains("Created PIN for ") && !message.contains("You need LabyMod to register")) {
            return;
        }
        if (!message.contains(":")) {
            return;
        }
        Minecraft.getMinecraft().displayGuiScreen(new GuiWebPanel(message.split("\n")[1], multiplayerScreen));
    }

    public GuiWebPanel(String pin, GuiScreen multiplayerScreen) {
        this.pin = pin;
        this.multiplayerScreen = multiplayerScreen;
        LabyMod.getInstance().openWebpage(String.format("http://www.labymod.net/key/?id=%s&pin=%s", LabyMod.getInstance().getPlayerUUID().toString(), this.pin), false);
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(6, width / 2 - 120, height / 2 + 10, 100, 20, LanguageManager.translate("button_not_working")));
        this.buttonList.add(new GuiButton(5, width / 2 - 10, height / 2 + 10, 130, 20, LanguageManager.translate("button_okay")));
        this.field = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 100, height / 2 + 35, 200, 20);
        this.field.setVisible(false);
        this.field.setMaxStringLength(640);
        this.field.setText(String.format("http://www.labymod.net/key/?id=%s&pin=%s", LabyMod.getInstance().getPlayerUUID().toString(), this.pin));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 5) {
            Minecraft.getMinecraft().displayGuiScreen(this.multiplayerScreen);
        }
        if (button.id == 6) {
            this.field.setVisible(true);
            this.field.setFocused(true);
            this.field.setCursorPositionZero();
            this.field.setSelectionPos(this.field.getMaxStringLength() - 1);
            button.enabled = false;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.field.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.field.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        List<String> list = LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth(LanguageManager.translate("tab_opened"), width / 3, 4);
        int y2 = 0;
        for (String s2 : list) {
            LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(ModColor.cl("a")) + s2, width / 2, height / 2 - 40 + y2);
            y2 += 10;
        }
        this.field.drawTextBox();
        if (this.field.visible) {
            LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(ModColor.cl("c")) + LanguageManager.translate("open_link_in_browser"), width / 2, height / 2 + 60);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

