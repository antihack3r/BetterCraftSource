/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.gui.elements;

import java.util.List;
import java.util.UUID;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.labyplay.party.model.PartyInvite;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class WinPartyInvites
extends WindowElement<GuiPlayLayout> {
    private UUID hoverParty;
    private boolean hoverAccept;
    private boolean hoverDeny;

    public WinPartyInvites(GuiPlayLayout layout) {
        super(layout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        int padding = 5;
        int posX = this.left + 5;
        int posY = this.top + 5 + 15;
        this.draw.drawString("Party invites", this.left + 5, this.top + 5);
        this.hoverParty = null;
        this.hoverAccept = false;
        this.hoverDeny = false;
        for (PartyInvite party : ((GuiPlayLayout)this.layout).getPartySystem().getPartyInvites()) {
            LabyMod.getInstance().getDrawUtils().drawPlayerHead(party.getUsername(), posX, posY, 18);
            this.draw.drawString(String.valueOf(ModColor.cl('a')) + party.getUsername(), posX + 18 + 2, posY + 1);
            this.draw.drawString(String.valueOf(ModColor.cl('e')) + "invites you", posX + 18 + 2, posY + 10, 0.7);
            int iconSize = 13;
            boolean hoverDeny = mouseX > this.right - 20 && mouseX < this.right - 20 + 13 && mouseY > posY + 1 && mouseY < posY + 1 + 13;
            boolean hoverAccept = mouseX > this.right - 40 && mouseX < this.right - 40 + 13 && mouseY > posY + 1 && mouseY < posY + 1 + 13;
            this.hoverDeny = hoverDeny;
            this.hoverAccept = hoverAccept;
            this.hoverParty = party.getPartyUUID();
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_DENY);
            this.draw.drawTexture(this.right - 20 - (hoverDeny ? 1 : 0), posY + 1 - (hoverDeny ? 1 : 0), 255.0, 255.0, 13 + (hoverDeny ? 2 : 0), 13 + (hoverDeny ? 2 : 0), 1.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_ACCEPT);
            this.draw.drawTexture(this.right - 40 - (hoverAccept ? 1 : 0), posY + 1 - (hoverAccept ? 1 : 0), 255.0, 255.0, 13 + (hoverAccept ? 2 : 0), 13 + (hoverAccept ? 2 : 0), 1.0f);
            posY += 20;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.hoverAccept && this.hoverParty != null) {
            ((GuiPlayLayout)this.layout).getPartySystem().sendInvitePlayerResponse(this.hoverParty, true);
        }
        if (this.hoverDeny && this.hoverParty != null) {
            ((GuiPlayLayout)this.layout).getPartySystem().sendInvitePlayerResponse(this.hoverParty, false);
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
    public void actionPerformed(GuiButton button) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void updateScreen() {
    }
}

