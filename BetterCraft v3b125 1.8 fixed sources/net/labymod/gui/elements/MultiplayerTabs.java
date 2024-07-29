/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import net.labymod.gui.GuiServerList;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.labyplay.party.model.PartyMember;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class MultiplayerTabs {
    private static int hoverMultiplayerTab = -1;
    private static int lastOpenTab = -1;

    public static void initMultiplayerTabs(int tabIndex) {
        if (lastOpenTab != -1 && tabIndex != lastOpenTab) {
            MultiplayerTabs.openMultiPlayerTab(lastOpenTab);
        }
    }

    public static void drawParty(int mouseX, int mouseY, int width) {
        int headSize = 10;
        int posX = width - headSize - 2;
        int posY = 3;
        PartyMember[] partyMemberArray = LabyMod.getInstance().getLabyPlay().getPartySystem().getMembers();
        int n2 = partyMemberArray.length;
        int n3 = 0;
        while (n3 < n2) {
            PartyMember member = partyMemberArray[n3];
            boolean hover = mouseX > posX && mouseX < posX + headSize && mouseY > posY && mouseY < posY + headSize;
            LabyMod.getInstance().getDrawUtils().drawPlayerHead(member.getUuid(), posX - (hover ? 1 : 0), posY - (hover ? 1 : 0), headSize + (hover ? 2 : 0));
            if (hover) {
                LabyMod.getInstance().getDrawUtils().drawRightString(member.getName(), width - 3, 16.0);
            }
            posX -= headSize + 2;
            ++n3;
        }
    }

    public static void drawMultiplayerTabs(int tabIndex, int mouseX, int mouseY, boolean isScrolled, boolean isIndex0Selected) {
        TabType tab;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GlStateManager.disableLighting();
        hoverMultiplayerTab = -1;
        int space = 3;
        int paddingWidth = 6;
        int posX = (draw.getWidth() + space) / 2;
        int tabY = 28;
        int tabHeight = 12;
        int index = 0;
        TabType[] tabTypeArray = TabType.values();
        int n2 = tabTypeArray.length;
        int n3 = 0;
        while (n3 < n2) {
            tab = tabTypeArray[n3];
            if (tab.isVisible()) {
                posX -= (draw.getStringWidth(tab.getDisplayName()) + space + paddingWidth) / 2;
            }
            ++index;
            ++n3;
        }
        index = 0;
        tabTypeArray = TabType.values();
        n2 = tabTypeArray.length;
        n3 = 0;
        while (n3 < n2) {
            tab = tabTypeArray[n3];
            if (tab.isVisible()) {
                String displayString = tab.getDisplayName();
                int tabWidth = draw.getStringWidth(displayString) + paddingWidth;
                boolean hover = mouseX > posX && mouseX < posX + tabWidth && mouseY > tabY && mouseY < tabY + tabHeight;
                draw.drawRectangle(posX, tabY, posX + tabWidth, tabY + tabHeight, Integer.MIN_VALUE);
                if (tabIndex == index) {
                    draw.drawOverlayBackground(posX, tabY, tabWidth, tabY + tabHeight + 2 + (isIndex0Selected || !isScrolled ? 0 : 3), 32);
                    draw.drawGradientShadowTop(tabY, posX, posX + tabWidth);
                    lastOpenTab = index;
                } else {
                    draw.drawRectangle(posX, tabY + tabHeight, posX + tabWidth, tabY + tabHeight + 1, ModColor.toRGB(100, 100, 100, 60));
                }
                draw.drawCenteredString(String.valueOf(ModColor.cl(tabIndex == index ? "f" : (hover ? "7" : "8"))) + displayString, posX + tabWidth / 2, tabY + 2);
                if (hover) {
                    hoverMultiplayerTab = index;
                }
                posX += tabWidth + space;
            }
            ++index;
            ++n3;
        }
    }

    public static void mouseClickedMultiplayerTabs(int index, int mouseX, int mouseY) {
        if (index == hoverMultiplayerTab || hoverMultiplayerTab == -1) {
            return;
        }
        MultiplayerTabs.openMultiPlayerTab(hoverMultiplayerTab);
    }

    private static void openMultiPlayerTab(int index) {
        GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
        if (lastScreen instanceof ModGuiMultiplayer) {
            lastScreen = ((ModGuiMultiplayer)lastScreen).getParentScreen();
        }
        if (lastScreen instanceof GuiServerList) {
            lastScreen = ((GuiServerList)lastScreen).getParentScreen();
        }
        if (lastScreen instanceof GuiPlayLayout) {
            lastScreen = ((GuiPlayLayout)lastScreen).getParentScreen();
        }
        lastOpenTab = index;
        switch (index) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayer(lastScreen));
                break;
            }
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiServerList(lastScreen));
                break;
            }
            case 2: {
                Minecraft.getMinecraft().displayGuiScreen(new GuiPlayLayout(lastScreen));
            }
        }
    }

    public boolean isVisible() {
        switch (this.ordinal()) {
            case 0: 
            case 2: {
                return true;
            }
            case 1: {
                return LabyMod.getSettings().publicServerList;
            }
        }
        return false;
    }

    private int ordinal() {
        return 0;
    }

    private static enum TabType {
        MY_SERVER_LIST("tab_my_server_list"),
        PUBLIC_SERVER_LIST("tab_public_server_list");

        private String langKey;

        public String getDisplayName() {
            return LanguageManager.translate(this.langKey);
        }

        public boolean isVisible() {
            switch (this.ordinal()) {
                case 0: {
                    return true;
                }
                case 1: {
                    return LabyMod.getSettings().publicServerList;
                }
                case 2: {
                    return true;
                }
            }
            return false;
        }

        public String getLangKey() {
            return this.langKey;
        }

        private TabType(String langKey) {
            this.langKey = langKey;
        }
    }
}

