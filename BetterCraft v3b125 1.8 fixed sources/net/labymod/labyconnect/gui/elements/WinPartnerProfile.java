/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import java.util.UUID;
import net.labymod.gui.elements.SmallDropDownMenu;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsAbout;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.packets.PacketPlayFriendRemove;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.ServerInfo;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;

public class WinPartnerProfile
extends WindowElement<GuiFriendsLayout> {
    private SmallDropDownMenu smallDropDownMenu;

    public WinPartnerProfile(GuiFriendsLayout chatLayout) {
        super(chatLayout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        this.initDropDown();
    }

    private void initDropDown() {
        this.smallDropDownMenu = new SmallDropDownMenu(0, this.bottom - 14, 30, 10);
        this.smallDropDownMenu.setRenderCustomSelected(LanguageManager.translate("chat_user_options"));
        if (GuiFriendsLayout.selectedUser != null && GuiFriendsLayout.selectedUser.getCurrentServerInfo() != null && GuiFriendsLayout.selectedUser.getCurrentServerInfo().isServerAvailable()) {
            this.smallDropDownMenu.addDropDownEntry(LanguageManager.translate("chat_user_join_server"));
        }
        this.smallDropDownMenu.addDropDownEntry(LanguageManager.translate("chat_user_about"));
        this.smallDropDownMenu.addDropDownEntry(LanguageManager.translate("chat_user_remove_friend"));
        this.smallDropDownMenu.setChangeable(false);
        this.smallDropDownMenu.setMinecraftStyle(false);
        this.smallDropDownMenu.setColor(Integer.MIN_VALUE);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        boolean isTyping;
        String displayServerStatus;
        super.draw(mouseX, mouseY);
        if (GuiFriendsLayout.selectedUser == null) {
            return;
        }
        DrawUtils drawUtils = LabyMod.getInstance().getDrawUtils();
        drawUtils.drawOverlayBackground(this.left, this.top, this.right, this.top);
        if (!GuiFriendsLayout.selectedUser.isOnline()) {
            GlStateManager.color(0.2f, 0.2f, 0.2f, 1.0f);
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        int headPadding = 2;
        int headSize = this.bottom - this.top - 4;
        if (GuiFriendsLayout.selectedUser.isParty()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_PARTY);
            drawUtils.drawTexture(this.left + 1 + 2, this.top + 2, 255.0, 255.0, headSize, headSize);
        } else {
            drawUtils.drawPlayerHead(GuiFriendsLayout.selectedUser.getGameProfile(), this.left + 1 + 2, this.top + 2, headSize);
        }
        ServerInfo serverInfo = GuiFriendsLayout.selectedUser.getCurrentServerInfo();
        String displayOnlineStatus = String.valueOf(ModColor.cl(GuiFriendsLayout.selectedUser.getStatus().getChatColor())) + GuiFriendsLayout.selectedUser.getStatus().getName();
        boolean statusAvailable = serverInfo != null && serverInfo.isServerAvailable() && GuiFriendsLayout.selectedUser.getStatus() != UserStatus.OFFLINE;
        String string = displayServerStatus = statusAvailable ? String.valueOf(ModColor.cl("f")) + " " + LanguageManager.translate("chat_user_online_on") + " " + ModColor.cl("a") + serverInfo.getDisplayAddress() : "";
        if (statusAvailable && serverInfo.getSpecifiedServerName() != null && !serverInfo.getSpecifiedServerName().isEmpty()) {
            displayServerStatus = String.valueOf(displayServerStatus) + ModColor.cl("f") + ", " + ModColor.cl("e") + serverInfo.getSpecifiedServerName();
        }
        UserManager userManager = LabyMod.getInstance().getUserManager();
        UUID uuid = GuiFriendsLayout.selectedUser.getGameProfile().getId();
        String username = GuiFriendsLayout.selectedUser.getGameProfile().getName();
        if (GuiFriendsLayout.selectedUser.isOnline() && userManager.isWhitelisted(uuid)) {
            User user = userManager.getUser(uuid);
            char hexColor = user.getGroup().getColorMinecraft();
            username = String.valueOf(ModColor.cl(hexColor)) + username;
            if (mouseX > this.left + 2 + headSize + 5 && mouseX < this.left + 2 + headSize + 5 + this.draw.getStringWidth(username) && mouseY > this.top + 4 && mouseY < this.top + 4 + 10) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, String.valueOf(ModColor.cl(hexColor)) + user.getGroup().getDisplayName());
            }
        }
        String partyUsername = GuiFriendsLayout.selectedUser.isParty() ? "Party" : username;
        String finalNameLine = String.valueOf(partyUsername) + ModColor.cl("7") + " (" + displayOnlineStatus + displayServerStatus + ModColor.cl("7") + ")";
        GuiFriendsLayout layout = (GuiFriendsLayout)this.layout;
        finalNameLine = LabyMod.getInstance().getDrawUtils().trimStringToWidth(finalNameLine, layout.getChatElementMyProfile().getTotalButtonWidth() - this.left - 2 - headSize - 10);
        drawUtils.drawString(finalNameLine, this.left + 2 + headSize + 5, this.top + 4);
        boolean bl2 = isTyping = GuiFriendsLayout.selectedUser.getLastTyping() + 1500L > System.currentTimeMillis();
        if (isTyping) {
            drawUtils.drawString(String.valueOf(ModColor.cl('7')) + LanguageManager.translate("chat_is_typing"), this.left + 2 + headSize + 5 + this.smallDropDownMenu.getWidthIn() + 5, this.top + 4 + 15, 0.7);
        }
        if (!GuiFriendsLayout.selectedUser.isOnline()) {
            drawUtils.drawString(ModUtils.getTimeDiff(GuiFriendsLayout.selectedUser.getLastOnline()), this.left + 2 + headSize + 5 + drawUtils.getStringWidth(finalNameLine) + 5, this.top + 4, 0.7);
        }
        if (!GuiFriendsLayout.selectedUser.isParty()) {
            this.smallDropDownMenu.setX(this.left + 2 + headSize + 5);
            this.smallDropDownMenu.renderButton(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int entryId = this.smallDropDownMenu.onClick(mouseX, mouseY);
        if (entryId >= 0) {
            if (this.smallDropDownMenu.getDropDownEntrys().size() != 3) {
                ++entryId;
            }
            switch (entryId) {
                case 0: {
                    ServerInfo serverInfo = GuiFriendsLayout.selectedUser.getCurrentServerInfo();
                    if (serverInfo == null || !serverInfo.isServerAvailable()) break;
                    LabyMod.getInstance().switchServer(String.valueOf(serverInfo.getServerIp()) + ":" + serverInfo.getServerPort(), true);
                    break;
                }
                case 1: {
                    if (GuiFriendsLayout.selectedUser == null) break;
                    Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsAbout(Minecraft.getMinecraft().currentScreen, GuiFriendsLayout.selectedUser));
                    break;
                }
                case 2: {
                    final GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
                    if (GuiFriendsLayout.selectedUser == null) break;
                    Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                        @Override
                        public void confirmClicked(boolean result, int id2) {
                            if (GuiFriendsLayout.selectedUser != null && result) {
                                LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketPlayFriendRemove(GuiFriendsLayout.selectedUser));
                                Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                                GuiFriendsLayout.selectedUser = null;
                            } else {
                                Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                            }
                        }
                    }, LanguageManager.translate("chat_user_remove_friend_popup"), String.valueOf(ModColor.cl("c")) + GuiFriendsLayout.selectedUser.getGameProfile().getName(), 0));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    public void setPartner(ChatUser partner) {
        GuiFriendsLayout.selectedUser = partner;
        if (partner != null) {
            partner.setUnreadMessages(0);
        }
        this.initDropDown();
    }

    @Override
    public void updateScreen() {
    }
}

