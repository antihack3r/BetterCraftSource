/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.gui.elements;

import java.util.List;
import java.util.UUID;
import net.labymod.gui.elements.GuiTextboxPrompt;
import net.labymod.gui.elements.SmallDropDownMenu;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.labyplay.party.model.PartyMember;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class WinCurrentParty
extends WindowElement<GuiPlayLayout> {
    private PartyMember hoverMember = null;
    private PartyMember dropDownMember = null;
    private SmallDropDownMenu dropDown = null;

    public WinCurrentParty(GuiPlayLayout layout) {
        super(layout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        int padding = 6;
        int space = 2;
        String invitePlayer = "Invite player";
        int invitePlayerWidth = this.draw.getStringWidth("Invite player") + 6;
        buttonlist.add(new GuiButton(1, left, bottom - 20, invitePlayerWidth, 20, "Invite player"));
        String leave = "Leave";
        int leaveWidth = this.draw.getStringWidth("Leave") + 6;
        buttonlist.add(new GuiButton(2, left + invitePlayerWidth + 2, bottom - 20, leaveWidth, 20, "Leave"));
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        int paddingX = 2;
        int paddingY = 4;
        int posX = this.left + 2;
        int posY = this.top + 4 + 14;
        int headSize = 18;
        int space = 3;
        this.draw.drawString("Party", this.left + 2, this.top + 4);
        this.hoverMember = null;
        PartyMember[] partyMemberArray = ((GuiPlayLayout)this.layout).getPartySystem().getMembers();
        int n2 = partyMemberArray.length;
        int n3 = 0;
        while (n3 < n2) {
            PartyMember member = partyMemberArray[n3];
            if (this.drawMember(member, posX, posY, mouseX, mouseY, 18)) {
                posX += 21;
            }
            ++n3;
        }
        if (this.dropDown != null) {
            this.dropDown.renderButton(mouseX, mouseY);
        }
    }

    private boolean drawMember(PartyMember member, int posX, int posY, int mouseX, int mouseY, int headSize) {
        boolean hover = mouseX > posX && mouseX < posX + headSize && mouseY > posY && mouseY < posY + headSize;
        LabyMod.getInstance().getDrawUtils().drawPlayerHead(member.getUuid(), posX, posY, headSize);
        if (!member.isMember()) {
            long inviteAliveDuration = System.currentTimeMillis() - member.getTimestamp();
            if (inviteAliveDuration > 60000L) {
                return false;
            }
            double progress = (double)headSize / 60000.0 * (double)(60000L - inviteAliveDuration);
            DrawUtils.drawRect(posX, posY, posX + headSize, posY + headSize, ModColor.toRGB(0, 0, 0, 200));
            DrawUtils.drawRect(posX, posY + headSize - 1, posX + headSize, posY + headSize, ModColor.toRGB(0, 0, 0, 200));
            DrawUtils.drawRect((double)posX, (double)(posY + headSize - 1), (double)posX + progress, (double)(posY + headSize), ModColor.toRGB(100, 200, 100, 200));
        }
        if (member.isOwner()) {
            int crownSize = 10;
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_CROWN);
            this.draw.drawTexture(posX, posY - 5 - 1, 255.0, 255.0, 10.0, 10.0);
        }
        if (hover && this.dropDown == null) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, member.getName());
            this.hoverMember = member;
        }
        return true;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiTextboxPrompt(Minecraft.getMinecraft().currentScreen, "Player to invite:", "Invite", "Cancel", "", new Consumer<String>(){

                @Override
                public void accept(String username) {
                    if (!username.isEmpty()) {
                        ((GuiPlayLayout)WinCurrentParty.this.layout).getPartySystem().invitePlayer(username);
                    }
                }
            }));
        }
        if (button.id == 2) {
            ((GuiPlayLayout)this.layout).getPartySystem().leaveParty();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.dropDown != null && this.dropDownMember != null) {
            int result = this.dropDown.onClick(mouseX, mouseY);
            if (((GuiPlayLayout)this.layout).getPartySystem().hasParty()) {
                UUID targetUUID = this.dropDownMember.getUuid();
                switch (result) {
                    case 0: {
                        ((GuiPlayLayout)this.layout).getPartySystem().kickPlayer(targetUUID);
                        break;
                    }
                    case 1: {
                        ((GuiPlayLayout)this.layout).getPartySystem().changeOwner(targetUUID);
                    }
                }
            }
            this.dropDown = null;
            this.dropDownMember = null;
        }
        if (this.hoverMember != null && this.dropDown == null && mouseButton == 1) {
            PartyMember clientMember = ((GuiPlayLayout)this.layout).getPartySystem().getClientMember();
            boolean isPartyOwner = clientMember.isOwner();
            boolean isClientMember = this.hoverMember.getUuid().equals(clientMember.getUuid());
            if (isPartyOwner && !isClientMember) {
                this.dropDown = new SmallDropDownMenu(mouseX, mouseY, 0, 0);
                this.dropDown.addDropDownEntry((Object)((Object)ModColor.RED) + "Kick " + this.hoverMember.getName());
                this.dropDown.addDropDownEntry((Object)((Object)ModColor.AQUA) + "Make party leader");
                this.dropDown.setMinecraftStyle(false);
                this.dropDown.setOpen(true);
                this.dropDown.setDropDownX(mouseX);
                this.dropDown.setDropDownY(mouseY - this.dropDown.getMaxY());
                this.dropDownMember = this.hoverMember;
            }
        }
        return false;
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
}

