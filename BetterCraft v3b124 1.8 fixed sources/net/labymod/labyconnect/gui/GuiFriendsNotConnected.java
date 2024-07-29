/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui;

import java.io.IOException;
import net.labymod.gui.elements.Tabs;
import net.labymod.gui.layout.WindowLayout;
import net.labymod.labyconnect.packets.EnumConnectionState;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiFriendsNotConnected
extends GuiScreen {
    private WindowLayout prevTabSelected;
    private GuiButton buttonLogin;

    public GuiFriendsNotConnected(WindowLayout prevTabSelected) {
        this.prevTabSelected = prevTabSelected;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonLogin = new GuiButton(6, width / 2 - 100, height / 2 + 10, 200, 20, "");
        this.buttonList.add(this.buttonLogin);
        Tabs.initGuiScreen(this.buttonList, this);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.drawDefaultBackground();
        boolean isOnline = LabyMod.getInstance().getLabyConnect().isOnline();
        EnumConnectionState currentConnectionState = LabyMod.getInstance().getLabyConnect().getClientConnection().getCurrentConnectionState();
        boolean isOffline = currentConnectionState == EnumConnectionState.OFFLINE;
        this.buttonLogin.visible = !isOnline;
        this.buttonLogin.enabled = isOffline;
        String string = this.buttonLogin.displayString = isOffline ? LanguageManager.translate("button_connect_to_chat") : currentConnectionState.getButtonState();
        if (isOnline) {
            Minecraft.getMinecraft().displayGuiScreen(this.prevTabSelected);
            return;
        }
        LabyMod.getInstance().getDrawUtils().drawGradientShadowTop(41.0, 0.0, width);
        LabyMod.getInstance().getDrawUtils().drawGradientShadowBottom(height - 50, 0.0, width);
        String accountName = LabyMod.getInstance().getPlayerName();
        EnumConnectionState connectionState = LabyMod.getInstance().getLabyConnect().getClientConnection().getCurrentConnectionState();
        LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(ModColor.cl("e")) + "LabyMod Chat " + ModColor.cl("7") + "| " + ModColor.cl(connectionState.getDisplayColor()) + connectionState.name(), width / 2, height / 2 - 5);
        LabyMod.getInstance().getDrawUtils().drawRightString(accountName, width - 2, 29.0);
        LabyMod.getInstance().getDrawUtils().drawCenteredString(LabyMod.getInstance().getLabyConnect().getClientConnection().getLastKickMessage(), width / 2, height / 2 + 40);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        Tabs.actionPerformedButton(button);
        if (button.id == 6) {
            LabyMod.getInstance().getLabyConnect().getClientConnection().connect();
        }
    }

    public WindowLayout getPrevTabSelected() {
        return this.prevTabSelected;
    }

    public GuiButton getButtonLogin() {
        return this.buttonLogin;
    }
}

