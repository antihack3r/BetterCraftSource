/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CustomGuiButton;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsAddFriend;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.gui.GuiFriendsRequests;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class WinMyProfile
extends WindowElement<GuiFriendsLayout> {
    private ChatUser clientUser;
    private CustomGuiButton buttonFriendRequests;
    private CustomGuiButton buttonAddFriend;
    private int totalButtonWidth;
    private boolean hoverProfileIcon = false;

    public WinMyProfile(GuiFriendsLayout chatLayout, ChatUser clientUser) {
        super(chatLayout);
        this.clientUser = clientUser;
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        this.buttonFriendRequests = new CustomGuiButton(2, 0, top + (bottom - top - 20) / 2, 0, 20, "");
        buttonlist.add(this.buttonFriendRequests);
        this.buttonAddFriend = new CustomGuiButton(3, 0, top + (bottom - top - 20) / 2, 22, 20, "+");
        buttonlist.add(this.buttonAddFriend);
        this.updateButtons();
    }

    public void updateButtons() {
        int headSize = this.bottom - this.top - 3 - 2;
        int requestCount = LabyMod.getInstance().getLabyConnect().getRequests().size();
        String buttonString = String.valueOf(LanguageManager.translate("button_requests")) + (requestCount == 0 ? "" : " (" + requestCount + ")");
        int buttonWidth = LabyMod.getInstance().getDrawUtils().getStringWidth(buttonString) + 8;
        this.buttonFriendRequests.displayString = buttonString;
        this.buttonFriendRequests.enabled = requestCount != 0;
        this.buttonFriendRequests.setXPosition(this.right - headSize - buttonWidth - 5);
        this.buttonFriendRequests.setWidth(buttonWidth);
        this.buttonAddFriend.setXPosition(this.right - headSize - buttonWidth - 5 - 25);
        this.totalButtonWidth = LabyModCore.getMinecraft().getXPosition(this.buttonAddFriend);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        try {
            if (this.clientUser == null) {
                return;
            }
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            GameProfile gameProfile = this.clientUser.getGameProfile();
            int headSize = this.bottom - this.top - 3 - 2;
            this.hoverProfileIcon = mouseX > this.right - headSize - 2 && mouseX < this.right - headSize - 2 + headSize && mouseY > this.top + 2 && mouseY < this.top + 2 + headSize;
            GlStateManager.color(this.hoverProfileIcon ? 0.6f : 1.0f, this.hoverProfileIcon ? 0.6f : 1.0f, this.hoverProfileIcon ? 0.6f : 1.0f);
            GlStateManager.enableAlpha();
            draw.drawPlayerHead(gameProfile, this.right - headSize - 2, this.top + 2, headSize);
            if (this.hoverProfileIcon) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, new String[]{String.valueOf(ModColor.cl("e")) + gameProfile.getName(), String.valueOf(ModColor.cl("7")) + LanguageManager.translate("button_open_profile_settings")});
                GlStateManager.color(1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        GuiFriendsLayout layout = (GuiFriendsLayout)this.layout;
        if (this.hoverProfileIcon && this.clientUser != null) {
            layout.setProfileOpen(!layout.isProfileOpen());
            ((GuiFriendsLayout)this.layout).initGui();
            this.buttonFriendRequests.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        return false;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        GuiFriendsLayout layout = (GuiFriendsLayout)this.layout;
        if (button.id == this.buttonAddFriend.id) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsAddFriend(this.layout, layout.getChatElementSearchField().getFieldSearch().getText()));
        }
        if (button.id == this.buttonFriendRequests.id) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiFriendsRequests(this.layout));
        }
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

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void updateScreen() {
    }

    public int getTotalButtonWidth() {
        return this.totalButtonWidth;
    }
}

