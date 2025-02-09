/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import net.labymod.gui.elements.ModTextField;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriend;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiFriendsAddFriend
extends GuiScreen {
    public static String response = null;
    private GuiScreen lastScreen;
    private ModTextField username;
    private GuiButton done;
    private GuiButton cancel;
    private String error = "";
    private long time = 0L;
    private boolean flash = false;
    private boolean check = false;
    private boolean canAdd = true;
    private String defaultQuery = "";

    public GuiFriendsAddFriend(GuiScreen lastScreen, String friendAddQuery) {
        this.lastScreen = lastScreen;
        this.defaultQuery = friendAddQuery;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.username = new ModTextField(-1, LabyMod.getInstance().getDrawUtils().fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20);
        this.username.setBlacklistWord(" ");
        this.username.setMaxStringLength(16);
        this.username.setText(this.defaultQuery);
        if (this.defaultQuery != null && !this.defaultQuery.isEmpty()) {
            this.username.setCursorPositionEnd();
            this.username.setSelectionPos(0);
        }
        this.done = new GuiButton(0, width / 2 + 3, height / 2 + 5, 98, 20, LanguageManager.translate("button_request_user"));
        this.buttonList.add(this.done);
        this.cancel = new GuiButton(1, width / 2 - 101, height / 2 + 5, 98, 20, LanguageManager.translate("button_cancel"));
        this.buttonList.add(this.cancel);
        super.initGui();
        this.check();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.check && this.username.textboxKeyTyped(typedChar, keyCode)) {
            this.check();
        }
        if (this.done.enabled && (keyCode == 28 || keyCode == 156)) {
            this.actionPerformed(this.done);
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void check() {
        ArrayList<ChatUser> list = new ArrayList<ChatUser>(LabyMod.getInstance().getLabyConnect().getFriends());
        this.canAdd = true;
        for (ChatUser p2 : list) {
            if (!p2.getGameProfile().getName().equalsIgnoreCase(this.username.getText())) continue;
            this.canAdd = false;
            break;
        }
        if (this.username.getText().equalsIgnoreCase(LabyMod.getInstance().getPlayerName())) {
            this.canAdd = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            this.check = true;
            LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketPlayRequestAddFriend(this.username.getText()));
            this.username.setText("");
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (response != null) {
            this.error = response;
            this.check = false;
            response = null;
            this.flash = true;
            this.time = System.currentTimeMillis();
        }
        this.done.enabled = !this.check && !this.username.getText().isEmpty() && this.canAdd;
        LabyMod.getInstance().getDrawUtils().drawString(String.valueOf(LanguageManager.translate("minecraft_name")) + ":", width / 2 - 100, height / 2 - 33);
        if (!this.error.isEmpty()) {
            if (this.error.contains("true")) {
                GuiFriendsAddFriend.drawRect(0, 10, width, 30, ModColor.toRGB(30, 220, 100, 200));
                LabyMod.getInstance().getDrawUtils().drawCenteredString("The request has been sent!", width / 2, 16.0);
            } else {
                String c2 = ModColor.cl("f");
                GuiFriendsAddFriend.drawRect(0, 10, width, 30, Color.RED.getRGB());
                if (this.time + 1000L > System.currentTimeMillis() && this.flash) {
                    LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(c2) + "Error: " + this.error + "test2", width / 2 - 1, 16.0);
                } else {
                    LabyMod.getInstance().getDrawUtils().drawCenteredString(String.valueOf(c2) + "Error: " + this.error + "test3", width / 2, 16.0);
                }
            }
            boolean bl2 = this.flash = !this.flash;
        }
        if (this.check) {
            GuiFriendsAddFriend.drawRect(0, 10, width, 30, Color.BLUE.getRGB());
            LabyMod.getInstance().getDrawUtils().drawCenteredString("Loading..", width / 2 - 1, 16.0);
        }
        this.username.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

