/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import java.util.UUID;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class WinFriendlist
extends WindowElement<GuiFriendsLayout> {
    private Scrollbar scrollbar;
    private int entryHeight;
    private ChatUser hoveredUser;

    public WinFriendlist(GuiFriendsLayout chatLayout) {
        super(chatLayout);
        if (GuiFriendsLayout.selectedUser != null) {
            GuiFriendsLayout.selectedUser.setUnreadMessages(0);
        }
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        this.scrollbar = new Scrollbar(0);
        this.scrollbar.init();
        this.scrollbar.setSpeed(15);
        this.scrollbar.setPosition(this.left, this.top, this.left + 4, this.bottom);
        this.updateGuiElements();
    }

    private void updateGuiElements() {
        int elementWidth = this.right - this.left;
        this.entryHeight = (int)((double)(elementWidth * elementWidth) * 0.001);
        if (this.entryHeight < 12) {
            this.entryHeight = 12;
        }
        if (this.entryHeight > 40) {
            this.entryHeight = 40;
        }
        this.scrollbar.setEntryHeight(this.entryHeight);
        this.scrollbar.setPosition(this.left + 2, this.top + 4, this.left + 6, this.bottom - 4);
    }

    public void updateBoundRight(int right) {
        this.right = right;
        this.updateGuiElements();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        List<ChatUser> friendList = LabyMod.getInstance().getLabyConnect().getSortFriends();
        String searchQueryString = ((GuiFriendsLayout)this.layout).getChatElementSearchField().getFieldSearch().getText();
        int totalListedEntrys = 0;
        for (ChatUser chatUser : friendList) {
            if (!searchQueryString.isEmpty() && !chatUser.getGameProfile().getName().toLowerCase().contains(searchQueryString.toLowerCase())) continue;
            ++totalListedEntrys;
        }
        this.scrollbar.update(totalListedEntrys);
        double scrollbarWidth = this.scrollbar.isHidden() ? 0.0 : this.scrollbar.getRight() - this.scrollbar.getLeft();
        double x2 = (double)this.left + scrollbarWidth + 5.0;
        double y2 = (double)this.top + this.scrollbar.getScrollY() + 4.0;
        int width = this.right - this.left - 12;
        this.hoveredUser = null;
        totalListedEntrys = 0;
        for (ChatUser chatUser2 : friendList) {
            if (!searchQueryString.isEmpty() && !chatUser2.getGameProfile().getName().toLowerCase().contains(searchQueryString.toLowerCase())) continue;
            if (this.drawEntry(chatUser2, (int)x2, (int)y2, width, this.entryHeight, mouseX, mouseY)) {
                this.hoveredUser = chatUser2;
            }
            y2 += (double)this.entryHeight;
            ++totalListedEntrys;
        }
        this.scrollbar.draw();
        super.draw(mouseX, mouseY);
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    private boolean drawEntry(ChatUser chatUser, int x2, int y2, int width, int height, int mouseX, int mouseY) {
        DrawUtils drawUtils = LabyMod.getInstance().getDrawUtils();
        int size = height - 2;
        int textPosX = x2 + size + 2;
        if (GuiFriendsLayout.selectedUser == chatUser) {
            WinFriendlist.drawRect(x2, y2, x2 + width + 2, y2 + height, ModColor.toRGB(255, 183, 76, 29));
        }
        String unreadMessage = String.valueOf(ModColor.cl("c")) + "(" + chatUser.getUnreadMessages() + ")";
        int unreadWidth = chatUser.getUnreadMessages() == 0 ? 0 : drawUtils.getStringWidth(unreadMessage);
        UserManager userManager = LabyMod.getInstance().getUserManager();
        UUID uuid = chatUser.getGameProfile().getId();
        String username = chatUser.getGameProfile().getName();
        if (chatUser.isOnline() && userManager.isWhitelisted(uuid)) {
            User user = userManager.getUser(uuid);
            char hexColor = user.getGroup().getColorMinecraft();
            username = String.valueOf(ModColor.cl(hexColor)) + username;
        }
        String partyUsername = chatUser.isParty() ? "Party" : username;
        String displayName = String.valueOf(chatUser.isOnline() ? "" : ModColor.cl("8")) + partyUsername;
        String playerName = String.valueOf(GuiFriendsLayout.selectedUser == chatUser ? ModColor.cl("e") : "") + drawUtils.trimStringToWidth(displayName, x2 + width - textPosX + 2 - unreadWidth);
        drawUtils.drawString(playerName, textPosX, y2 + 2);
        if (!chatUser.isOnline()) {
            GlStateManager.color(0.2f, 0.2f, 0.2f, 1.0f);
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (chatUser.isParty()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_PARTY);
            drawUtils.drawTexture(x2, y2, 255.0, 255.0, 12.0, 12.0);
        } else {
            drawUtils.drawPlayerHead(chatUser.getGameProfile(), x2, y2 + 1, size);
        }
        if (chatUser.getUnreadMessages() != 0) {
            drawUtils.drawRightString(unreadMessage, x2 + width - 2, y2 + 3, 0.7);
        }
        return mouseX > x2 && mouseX < x2 + width && mouseY > y2 && mouseY < y2 + height;
    }

    public void handleMouseInput() {
        if (this.isMouseOver()) {
            this.scrollbar.mouseInput();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        GuiFriendsLayout layout = (GuiFriendsLayout)this.layout;
        if (this.hoveredUser != null && this.isMouseOver()) {
            GuiFriendsLayout.selectedUser = this.hoveredUser;
            layout.getChatElementPartnerProfile().setPartner(this.hoveredUser);
            if (layout.getChatElementMessageField().getFieldMessage() != null) {
                layout.getChatElementMessageField().getFieldMessage().setFocused(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    public boolean isScrolledToTop() {
        return this.scrollbar.getScrollY() == 0.0;
    }

    @Override
    public void updateScreen() {
    }
}

