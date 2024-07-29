/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui;

import java.io.IOException;
import java.util.List;
import net.labymod.gui.elements.Tabs;
import net.labymod.gui.layout.WindowElement;
import net.labymod.gui.layout.WindowLayout;
import net.labymod.labyconnect.gui.GuiFriendsNotConnected;
import net.labymod.labyconnect.gui.elements.WinChatlog;
import net.labymod.labyconnect.gui.elements.WinFriendlist;
import net.labymod.labyconnect.gui.elements.WinLogoutButton;
import net.labymod.labyconnect.gui.elements.WinMessageField;
import net.labymod.labyconnect.gui.elements.WinMyProfile;
import net.labymod.labyconnect.gui.elements.WinPartnerProfile;
import net.labymod.labyconnect.gui.elements.WinProfileSettings;
import net.labymod.labyconnect.gui.elements.WinSearchField;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiFriendsLayout
extends WindowLayout {
    private static final int WINDOW_SPLIT_DEPTH = 2;
    private static final int WINDOW_SPLIT_MAX = 200;
    private static final int WINDOW_SPLIT_MIN = 80;
    public static ChatUser selectedUser;
    private boolean hoverTableSplit = false;
    private boolean draggingTable = false;
    private boolean profileOpen = false;
    private WinChatlog chatElementChatlog;
    private WinFriendlist chatElementFriendlist;
    private WinLogoutButton chatElementLogoutButton;
    private WinMessageField chatElementMessageField;
    private WinMyProfile chatElementMyProfile;
    private WinPartnerProfile chatElementPartnerProfile;
    private WinProfileSettings chatElementProfileSettings;
    private WinSearchField chatElementSearchField;
    public static GuiScreen prevScreen;

    public GuiFriendsLayout(GuiScreen previousGui) {
        prevScreen = previousGui;
    }

    @Override
    public void initGui() {
        if (!LabyMod.getInstance().getLabyConnect().isOnline()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsNotConnected(this));
            selectedUser = null;
        }
        super.initGui();
        this.chatElementPartnerProfile.setPartner(selectedUser);
        if (this.chatElementMessageField.getFieldMessage() != null) {
            this.chatElementMessageField.getFieldMessage().setFocused(true);
        }
    }

    @Override
    public void onGuiClosed() {
        LabyMod.getMainConfig().save();
        super.onGuiClosed();
    }

    @Override
    protected void initLayout(List<WindowElement<?>> windowElements) {
        this.buttonList.clear();
        Tabs.initGuiScreen(this.buttonList, this);
        ChatUser clientUser = LabyMod.getInstance().getLabyConnect().getClientProfile().buildClientUser();
        int marginWindowX = 10;
        int marginWindowY = 10;
        int windowTop = 30;
        int windowLeft = 10;
        int windowRight = width - 10;
        int windowBottom = height - 10;
        int tableSplitSearchListHeight = 30;
        int tableSplitProfileLogHeight = 30;
        int tableSplitLogChatFieldHeight = 25;
        int tableSplitListLogWidth = LabyMod.getSettings().labymodChatSplitX;
        int tableSplitLogProfileWidth = this.profileOpen ? 170 : 0;
        this.chatElementPartnerProfile = new WinPartnerProfile(this);
        this.chatElementPartnerProfile.construct(10 + tableSplitListLogWidth, 30.0, windowRight, 60.0);
        this.chatElementMyProfile = new WinMyProfile(this, clientUser);
        this.chatElementMyProfile.construct(10 + tableSplitListLogWidth, 30.0, windowRight, 60.0);
        this.chatElementChatlog = new WinChatlog(this, clientUser);
        this.chatElementChatlog.construct(10 + tableSplitListLogWidth, 60.0, windowRight - tableSplitLogProfileWidth, windowBottom - 25);
        this.chatElementFriendlist = new WinFriendlist(this);
        this.chatElementFriendlist.construct(10.0, 60.0, 10 + tableSplitListLogWidth, windowBottom - 25);
        this.chatElementMessageField = new WinMessageField(this);
        this.chatElementMessageField.construct(10 + tableSplitListLogWidth, windowBottom - 25, windowRight - tableSplitLogProfileWidth, windowBottom);
        this.chatElementSearchField = new WinSearchField(this);
        this.chatElementSearchField.construct(10.0, 30.0, 10 + tableSplitListLogWidth, 60.0);
        this.chatElementLogoutButton = new WinLogoutButton(this);
        this.chatElementLogoutButton.construct(10.0, windowBottom - 25, 10 + tableSplitListLogWidth, windowBottom);
        this.chatElementProfileSettings = new WinProfileSettings(this);
        this.chatElementProfileSettings.construct(windowRight - tableSplitLogProfileWidth + 3, 60.0, windowRight, windowBottom - 25);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (!LabyMod.getInstance().getLabyConnect().isOnline()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsNotConnected(this));
            selectedUser = null;
        }
        this.drawDefaultBackground();
        DrawUtils.startScissor(this.chatElementChatlog.getLeft(), this.chatElementChatlog.getTop(), this.chatElementChatlog.getRight(), this.chatElementChatlog.getBottom());
        this.chatElementChatlog.draw(mouseX, mouseY);
        DrawUtils.stopScissor();
        DrawUtils.startScissor(this.chatElementFriendlist.getLeft(), this.chatElementFriendlist.getTop(), this.chatElementFriendlist.getRight(), this.chatElementFriendlist.getBottom());
        this.chatElementFriendlist.draw(mouseX, mouseY);
        DrawUtils.stopScissor();
        if (this.profileOpen) {
            this.chatElementProfileSettings.draw(mouseX, mouseY);
        }
        this.drawHeaderAndFooter(this.chatElementChatlog.getTop(), this.chatElementChatlog.getBottom());
        this.chatElementPartnerProfile.draw(mouseX, mouseY);
        this.chatElementMyProfile.draw(mouseX, mouseY);
        this.chatElementMessageField.draw(mouseX, mouseY);
        this.chatElementSearchField.draw(mouseX, mouseY);
        this.chatElementLogoutButton.draw(mouseX, mouseY);
        boolean bl2 = this.hoverTableSplit = mouseX > this.chatElementFriendlist.getRight() - 2 && mouseX < this.chatElementChatlog.getLeft() + 2 && mouseY > this.chatElementFriendlist.getTop() && mouseY < this.chatElementFriendlist.getBottom();
        if (this.hoverTableSplit || this.draggingTable) {
            draw.drawCenteredString("|||", mouseX + 1, mouseY - 3);
            draw.drawRectangle(this.chatElementFriendlist.getRight() - 1, this.chatElementFriendlist.getTop(), this.chatElementChatlog.getLeft(), this.chatElementFriendlist.getBottom(), Integer.MAX_VALUE);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        Tabs.drawParty(mouseX, mouseY, width);
    }

    private void drawHeaderAndFooter(int maxHeader, int minFooter) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawGradientShadowTop(maxHeader, 0.0, width);
        draw.drawGradientShadowBottom(minFooter, 0.0, width);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.chatElementMessageField.updateScreen();
        this.chatElementSearchField.updateScreen();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.chatElementChatlog.handleMouseInput();
        this.chatElementFriendlist.handleMouseInput();
        if (this.profileOpen) {
            this.chatElementProfileSettings.handleMouseInput();
        }
        if (selectedUser != null) {
            selectedUser.setUnreadMessages(0);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Tabs.actionPerformedButton(button);
        this.chatElementChatlog.actionPerformed(button);
        this.chatElementFriendlist.actionPerformed(button);
        this.chatElementPartnerProfile.actionPerformed(button);
        this.chatElementMyProfile.actionPerformed(button);
        this.chatElementMessageField.actionPerformed(button);
        this.chatElementSearchField.actionPerformed(button);
        this.chatElementLogoutButton.actionPerformed(button);
        if (this.profileOpen) {
            this.chatElementProfileSettings.actionPerformed(button);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.chatElementSearchField.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        if (this.chatElementMyProfile.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        this.chatElementChatlog.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.chatElementFriendlist.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }
        this.chatElementPartnerProfile.mouseClicked(mouseX, mouseY, mouseButton);
        this.chatElementMessageField.mouseClicked(mouseX, mouseY, mouseButton);
        this.chatElementLogoutButton.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.profileOpen) {
            this.chatElementProfileSettings.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (this.hoverTableSplit) {
            this.draggingTable = true;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.chatElementChatlog.mouseClickMove(mouseX, mouseY);
        this.chatElementFriendlist.mouseClickMove(mouseX, mouseY);
        if (this.profileOpen) {
            this.chatElementProfileSettings.mouseClickMove(mouseX, mouseY);
        }
        if (this.draggingTable) {
            LabyMod.getSettings().labymodChatSplitX = mouseX - this.chatElementFriendlist.getLeft();
            if (LabyMod.getSettings().labymodChatSplitX < 80) {
                LabyMod.getSettings().labymodChatSplitX = 80;
            }
            if (LabyMod.getSettings().labymodChatSplitX > 200) {
                LabyMod.getSettings().labymodChatSplitX = 200;
            }
            this.initLayout();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.draggingTable) {
            this.draggingTable = false;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.chatElementChatlog.keyTyped(typedChar, keyCode);
        this.chatElementFriendlist.keyTyped(typedChar, keyCode);
        this.chatElementPartnerProfile.keyTyped(typedChar, keyCode);
        this.chatElementMyProfile.keyTyped(typedChar, keyCode);
        this.chatElementMessageField.keyTyped(typedChar, keyCode);
        this.chatElementSearchField.keyTyped(typedChar, keyCode);
        this.chatElementLogoutButton.keyTyped(typedChar, keyCode);
        if (this.profileOpen) {
            this.chatElementProfileSettings.keyTyped(typedChar, keyCode);
        }
    }

    public boolean isDraggingTable() {
        return this.draggingTable;
    }

    public boolean isHoverTableSplit() {
        return this.hoverTableSplit;
    }

    public boolean isProfileOpen() {
        return this.profileOpen;
    }

    public void setProfileOpen(boolean profileOpen) {
        this.profileOpen = profileOpen;
    }

    public WinChatlog getChatElementChatlog() {
        return this.chatElementChatlog;
    }

    public WinFriendlist getChatElementFriendlist() {
        return this.chatElementFriendlist;
    }

    public WinLogoutButton getChatElementLogoutButton() {
        return this.chatElementLogoutButton;
    }

    public WinMessageField getChatElementMessageField() {
        return this.chatElementMessageField;
    }

    public WinMyProfile getChatElementMyProfile() {
        return this.chatElementMyProfile;
    }

    public WinPartnerProfile getChatElementPartnerProfile() {
        return this.chatElementPartnerProfile;
    }

    public WinProfileSettings getChatElementProfileSettings() {
        return this.chatElementProfileSettings;
    }

    public WinSearchField getChatElementSearchField() {
        return this.chatElementSearchField;
    }
}

