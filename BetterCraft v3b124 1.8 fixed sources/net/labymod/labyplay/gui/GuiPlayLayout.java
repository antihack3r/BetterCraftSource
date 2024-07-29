/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyplay.gui;

import java.io.IOException;
import java.util.List;
import net.labymod.gui.elements.Tabs;
import net.labymod.gui.layout.WindowElement;
import net.labymod.gui.layout.WindowLayout;
import net.labymod.labyplay.gui.elements.WinCurrentParty;
import net.labymod.labyplay.gui.elements.WinPartyCreator;
import net.labymod.labyplay.gui.elements.WinPartyInvites;
import net.labymod.labyplay.party.PartySystem;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiPlayLayout
extends WindowLayout {
    private GuiScreen parentScreen;
    private PartySystem partySystem;

    public GuiPlayLayout(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.partySystem = LabyMod.getInstance().getLabyPlay().getPartySystem();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void initLayout(List<WindowElement<?>> windowElements) {
        int partyInvitesSplit;
        this.buttonList.clear();
        Tabs.initMultiplayerTabs(2);
        Tabs.initGuiScreen(this.buttonList, this);
        int marginWindowX = width / 4;
        int marginWindowY = 10;
        int windowLeft = marginWindowX;
        int windowRight = width - marginWindowX;
        int windowBottom = height - 10;
        boolean hasParty = this.partySystem.hasParty();
        boolean hasInvites = !hasParty && this.partySystem.getPartyInvites().size() != 0;
        int partyPlaySplit = height - 80 + 10;
        int n2 = partyInvitesSplit = hasInvites ? width / 2 : windowRight;
        if (hasParty) {
            windowElements.add(new WinCurrentParty(this).construct(windowLeft, partyPlaySplit, partyInvitesSplit, windowBottom));
        } else {
            windowElements.add(new WinPartyCreator(this).construct(windowLeft, partyPlaySplit, partyInvitesSplit, windowBottom));
        }
        if (hasInvites) {
            windowElements.add(new WinPartyInvites(this).construct(partyInvitesSplit, partyPlaySplit, windowRight, windowBottom));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawAutoDimmedBackground(0, 41, width, height - 80);
        draw.drawOverlayBackground(0, 41);
        draw.drawOverlayBackground(0, height - 80, width, height);
        draw.drawGradientShadowTop(41.0, 0.0, width);
        draw.drawGradientShadowBottom(height - 80, 0.0, width);
        super.drawScreen(mouseX, mouseY, partialTicks);
        Tabs.drawMultiplayerTabs(2, mouseX, mouseY, true, false);
        Tabs.drawParty(mouseX, mouseY, width);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Tabs.actionPerformedButton(button);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Tabs.mouseClickedMultiplayerTabs(2, mouseX, mouseY);
    }

    public GuiScreen getParentScreen() {
        return this.parentScreen;
    }

    public PartySystem getPartySystem() {
        return this.partySystem;
    }
}

