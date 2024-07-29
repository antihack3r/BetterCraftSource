/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.Tabs;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;

public class ModGuiMultiplayer
extends GuiMultiplayer {
    private static final String[] serverListSelectorMappings = LabyModCore.getMappingAdapter().getServerListSelectorMappings();
    private static final String[] savedServerListMappings = LabyModCore.getMappingAdapter().getSavedServerListMappings();
    private final GuiScreen parentScreen;
    private long lastRefreshed;
    private ServerSelectionList serverSelector;
    private boolean inited;
    private Object serverListSelectorField;

    public ModGuiMultiplayer(GuiScreen parentScreen) {
        super(parentScreen);
        this.parentScreen = parentScreen;
        this.lastRefreshed = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        super.initGui();
        Tabs.initMultiplayerTabs(0);
        this.lastRefreshed = System.currentTimeMillis();
        this.inited = true;
        this.serverSelector = this.getServerSelectionList();
        Tabs.initGuiScreen(this.buttonList, this);
        if (LabyModCore.getMinecraft().getPlayer() != null) {
            for (GuiButton button : this.buttonList) {
                if (button.id != 0) continue;
                button.enabled = false;
            }
        }
        if (LabyMod.getSettings().publicServerList) {
            int addSpace = 9;
            this.serverSelector.setDimensions(width, height, 41, height - 64);
        }
        try {
            this.serverListSelectorField = ReflectionHelper.findField(GuiMultiplayer.class, serverListSelectorMappings).get(this);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
            this.serverListSelectorField = null;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.serverListSelectorField == null) {
            return;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean isScrolled = !LabyMod.getInstance().isInGame() && this.serverSelector != null && this.serverSelector.getAmountScrolled() == 0;
        boolean isIndex0Selected = !LabyMod.getInstance().isInGame() && this.serverSelector != null && LabyModCore.getMinecraft().isSelected(this.serverSelector, 0);
        Tabs.drawMultiplayerTabs(0, mouseX, mouseY, isScrolled, isIndex0Selected);
        Tabs.drawParty(mouseX, mouseY, width);
    }

    @Override
    public void updateScreen() {
        if (this.inited) {
            try {
                super.updateScreen();
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
        if (LabyMod.getSettings().serverlistLiveView && System.currentTimeMillis() - this.lastRefreshed >= (long)(LabyMod.getSettings().serverlistLiveViewInterval * 1000)) {
            this.refreshServerListSilently();
            this.lastRefreshed = System.currentTimeMillis();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Tabs.mouseClickedMultiplayerTabs(0, mouseX, mouseY);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 4) {
            LabyMod.getInstance().getLabyConnect().setViaServerList(false);
        }
        if (button.id != 8) {
            super.actionPerformed(button);
        } else {
            this.refreshServerList();
        }
        Tabs.actionPerformedButton(button);
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (this.inited) {
            super.handleMouseInput();
        }
    }

    @Override
    public void connectToSelected() {
        if (LabyMod.getInstance().isInGame() && !Minecraft.getMinecraft().isSingleplayer() && LabyMod.getSettings().confirmDisconnect) {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
            String ip2 = "";
            GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = LabyModCore.getMinecraft().getSelectedServerInSelectionList(this.getServerSelector()) < 0 ? null : this.getServerSelector().getListEntry(LabyModCore.getMinecraft().getSelectedServerInSelectionList(this.getServerSelector()));
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                ip2 = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverIP;
            }
            final GuiScreen lastScreen = Minecraft.getMinecraft().currentScreen;
            Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback(){

                @Override
                public void confirmClicked(boolean result, int id2) {
                    if (result) {
                        ModGuiMultiplayer.this.joinToServer();
                    } else {
                        Minecraft.getMinecraft().displayGuiScreen(lastScreen);
                    }
                }
            }, "Are you sure you want to leave the current server and join this one?", String.valueOf(ModColor.cl("c")) + ip2, 0));
        } else {
            this.joinToServer();
        }
    }

    private void joinToServer() {
        LabyMod.getInstance().getLabyConnect().setViaServerList(false);
        if (LabyModCore.getMinecraft().getWorld() != null) {
            LabyModCore.getMinecraft().getWorld().sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
        }
        if (LabyMod.getInstance().isInGame()) {
            LabyMod.getInstance().onQuit();
        }
        super.connectToSelected();
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (id2 == 5 && result && LabyModCore.getMinecraft().getWorld() != null) {
            LabyModCore.getMinecraft().getWorld().sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
        }
        super.confirmClicked(result, id2);
    }

    public GuiScreen getParentScreen() {
        return this.parentScreen;
    }

    private void refreshServerList() {
        try {
            ServerList serverList = null;
            serverList = new ServerList(this.mc);
            ReflectionHelper.findField(GuiMultiplayer.class, savedServerListMappings).set(this, serverList);
            LabyModCore.getMinecraft().updateOnlineServers(this.serverSelector, serverList);
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    private void refreshServerListSilently() {
        try {
            Object var1_1 = null;
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }

    private ServerSelectionList getServerSelectionList() {
        try {
            return (ServerSelectionList)ReflectionHelper.findField(GuiMultiplayer.class, serverListSelectorMappings).get(this);
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public ServerSelectionList getServerSelector() {
        return this.serverSelector;
    }
}

