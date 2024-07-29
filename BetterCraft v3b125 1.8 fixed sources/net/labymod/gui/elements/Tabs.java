/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.labymod.gui.GuiServerList;
import net.labymod.gui.ModGuiMultiplayer;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyplay.gui.GuiPlayLayout;
import net.labymod.labyplay.party.model.PartyMember;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class Tabs {
    private static Map<String, Class<? extends GuiScreen>[]> guiMap = new LinkedHashMap<String, Class<? extends GuiScreen>[]>();
    private static String lastOpenScreen = null;
    private static int hoverMultiplayerTab = -1;
    private static int lastOpenTab = -1;
    private static List<Consumer<Map<String, Class<? extends GuiScreen>[]>>> tabUpdateListener = new ArrayList<Consumer<Map<String, Class<? extends GuiScreen>[]>>>();

    public static void initGuiScreen(List<GuiButton> buttonList, GuiScreen screen) {
        guiMap.clear();
        for (Consumer<Map<String, Class<? extends GuiScreen>[]>> consumer : tabUpdateListener) {
            consumer.accept(guiMap);
        }
        int positionX = 0;
        int index = 0;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        for (Map.Entry<String, Class<? extends GuiScreen>[]> guiEntry : guiMap.entrySet()) {
            String displayString = LanguageManager.translate(guiEntry.getKey());
            boolean isSelected = false;
            Class<? extends GuiScreen>[] classArray = guiEntry.getValue();
            int n2 = classArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Class<? extends GuiScreen> screenClass;
                Class<? extends GuiScreen> guiClass = classArray[n3];
                if (screen != null && (screenClass = screen.getClass()).isAssignableFrom(guiClass)) {
                    isSelected = true;
                    break;
                }
                ++n3;
            }
            if (!isSelected && guiEntry.getKey().equals("tab_chat")) {
                int count = 0;
                for (ChatUser chatUser : LabyMod.getInstance().getLabyConnect().getFriends()) {
                    count += chatUser.getUnreadMessages();
                }
                if (count != 0) {
                    displayString = String.valueOf(displayString) + ModColor.cl('c') + " (" + count + ")";
                }
            }
            int nameWidth = draw.getStringWidth(displayString);
            GuiButton button = new GuiButton(100 + index, 5 + positionX, 5, 10 + nameWidth, 20, "test");
            button.enabled = !isSelected;
            buttonList.add(button);
            if (lastOpenScreen != null && lastOpenScreen.equals(guiEntry.getKey()) && !isSelected) {
                Tabs.actionPerformedButton(button);
                break;
            }
            positionX += nameWidth + 12;
            ++index;
        }
        if (!LabyMod.getInstance().getLabyPlay().getPartySystem().hasParty() && screen != null) {
            String displayString2 = LanguageManager.translate("tab_account");
            int nameWidth2 = draw.getStringWidth(displayString2);
            GuiButton button2 = new GuiButton(200, 15, 5, 10 + nameWidth2, 20, "Back");
            buttonList.add(button2);
        }
    }

    public static void actionPerformedButton(GuiButton button) {
        int index = 0;
        for (Map.Entry<String, Class<? extends GuiScreen>[]> guiEntry : guiMap.entrySet()) {
            if (button.id == 100 + index) {
                GuiScreen screen = Tabs.getGuiScreenByClass(guiEntry.getValue());
                if (screen == null) break;
                lastOpenScreen = LabyMod.getInstance().isInGame() ? guiEntry.getKey() : null;
                Minecraft.getMinecraft().displayGuiScreen(screen);
                break;
            }
            ++index;
        }
        if (button.id == 200) {
            Minecraft.getMinecraft().displayGuiScreen(GuiFriendsLayout.prevScreen);
        }
    }

    private static GuiScreen getGuiScreenByClass(Class<? extends GuiScreen>[] guiScreenClasses) {
        Class<? extends GuiScreen>[] classArray = guiScreenClasses;
        int n2 = guiScreenClasses.length;
        int n3 = 0;
        while (n3 < n2) {
            Class<? extends GuiScreen> guiClass = classArray[n3];
            if (GuiMultiplayer.class.isAssignableFrom(guiClass)) {
                return new ModGuiMultiplayer(LabyMod.getInstance().isInGame() ? new GuiIngameMenu() : new GuiMultiplayer(new GuiIngameMenu()));
            }
            ++n3;
        }
        try {
            return guiScreenClasses[0].newInstance();
        }
        catch (IllegalAccessException | InstantiationException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static void initMultiplayerTabs(int tabIndex) {
        if (lastOpenTab != -1 && tabIndex != lastOpenTab) {
            Tabs.openMultiPlayerTab(lastOpenTab);
        }
    }

    public static void drawParty(int mouseX, int mouseY, int width) {
        int headSize = 10;
        int posX = width - 10 - 2;
        int posY = 3;
        PartyMember[] partyMemberArray = LabyMod.getInstance().getLabyPlay().getPartySystem().getMembers();
        int n2 = partyMemberArray.length;
        int n3 = 0;
        while (n3 < n2) {
            PartyMember member = partyMemberArray[n3];
            boolean hover = mouseX > posX && mouseX < posX + 10 && mouseY > 3 && mouseY < 13;
            LabyMod.getInstance().getDrawUtils().drawPlayerHead(member.getUuid(), posX - (hover ? 1 : 0), 3 - (hover ? 1 : 0), 10 + (hover ? 2 : 0));
            if (hover) {
                LabyMod.getInstance().getDrawUtils().drawRightString(member.getName(), width - 3, 16.0);
            }
            posX -= 12;
            ++n3;
        }
    }

    public static void drawMultiplayerTabs(int tabIndex, int mouseX, int mouseY, boolean isScrolled, boolean isIndex0Selected) {
        MultiplayerTabs tab;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GlStateManager.disableLighting();
        hoverMultiplayerTab = -1;
        int space = 3;
        int paddingWidth = 6;
        int posX = (draw.getWidth() + 3) / 2;
        int tabY = 28;
        int tabHeight = 12;
        int index = 0;
        MultiplayerTabs[] multiplayerTabsArray = MultiplayerTabs.values();
        int n2 = multiplayerTabsArray.length;
        int n3 = 0;
        while (n3 < n2) {
            tab = multiplayerTabsArray[n3];
            if (tab.isVisible()) {
                posX -= (draw.getStringWidth(tab.getDisplayName()) + 3 + 6) / 2;
            }
            ++index;
            ++n3;
        }
        index = 0;
        multiplayerTabsArray = MultiplayerTabs.values();
        n2 = multiplayerTabsArray.length;
        n3 = 0;
        while (n3 < n2) {
            tab = multiplayerTabsArray[n3];
            if (tab.isVisible()) {
                String displayString = tab.getDisplayName();
                int tabWidth = draw.getStringWidth(displayString) + 6;
                boolean hover = mouseX > posX && mouseX < posX + tabWidth && mouseY > 28 && mouseY < 40;
                draw.drawRectangle(posX, 28, posX + tabWidth, 40, Integer.MIN_VALUE);
                if (tabIndex == index) {
                    draw.drawOverlayBackground(posX, 28, tabWidth, 42 + (isIndex0Selected || !isScrolled ? 0 : 3), 32);
                    draw.drawGradientShadowTop(28.0, posX, posX + tabWidth);
                    lastOpenTab = index;
                } else {
                    draw.drawRectangle(posX, 40, posX + tabWidth, 41, ModColor.toRGB(100, 100, 100, 60));
                }
                draw.drawCenteredString(String.valueOf(ModColor.cl(tabIndex == index ? "f" : (hover ? "7" : "8"))) + displayString, posX + tabWidth / 2, 30.0);
                if (hover) {
                    hoverMultiplayerTab = index;
                }
                posX += tabWidth + 3;
            }
            ++index;
            ++n3;
        }
    }

    public static void mouseClickedMultiplayerTabs(int index, int mouseX, int mouseY) {
        if (index == hoverMultiplayerTab || hoverMultiplayerTab == -1) {
            return;
        }
        Tabs.openMultiPlayerTab(hoverMultiplayerTab);
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
        switch (lastOpenTab) {
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

    public static Map<String, Class<? extends GuiScreen>[]> getGuiMap() {
        return guiMap;
    }

    public static List<Consumer<Map<String, Class<? extends GuiScreen>[]>>> getTabUpdateListener() {
        return tabUpdateListener;
    }

    private static enum MultiplayerTabs {
        MY_SERVER_LIST("tab_my_server_list"),
        PUBLIC_SERVER_LIST("tab_public_server_list");

        private final String langKey;

        private MultiplayerTabs(String langKey) {
            this.langKey = langKey;
        }

        public String getDisplayName() {
            return LanguageManager.translate(this.langKey);
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

        public String getLangKey() {
            return this.langKey;
        }
    }
}

