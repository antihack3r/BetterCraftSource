/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.partner;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.gui.GuiProtocolSelector;
import java.io.IOException;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.PartnerServerList;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.PartnerServerListEntryNormal;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.PartnerServerSelectionList;
import me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.GuiServerFinderMultiplayer;
import me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.ServerFinderServerList;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiPartnerMultiplayer
extends GuiScreen
implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private final OldServerPinger oldServerPinger = new OldServerPinger();
    private GuiScreen parentScreen;
    private PartnerServerSelectionList serverListSelector;
    public PartnerServerList savedServerList;
    private GuiButton btnSelectServer;
    private boolean deletingServer;
    private boolean addingServer;
    private boolean editingServer;
    private boolean directConnect;
    private String hoveringText;
    private ServerData selectedServer;
    private LanServerDetector.LanServerList lanServerList;
    private LanServerDetector.ThreadLanServerFind lanServerDetector;
    private boolean initialized;
    public ServerList normalServerList;
    public ServerFinderServerList serverFinderServerList;

    public GuiPartnerMultiplayer(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (!this.initialized) {
            this.initialized = true;
            this.savedServerList = new PartnerServerList(this.mc);
            this.savedServerList.loadServerList();
            this.normalServerList = new ServerList(this.mc);
            this.normalServerList.loadServerList();
            this.serverFinderServerList = new ServerFinderServerList(this.mc);
            this.serverFinderServerList.loadServerList();
            this.lanServerList = new LanServerDetector.LanServerList();
            try {
                this.lanServerDetector = new LanServerDetector.ThreadLanServerFind(this.lanServerList);
                this.lanServerDetector.start();
            }
            catch (Exception exception) {
                logger.warn("Unable to start LAN server detection: " + exception.getMessage());
            }
            this.serverListSelector = new PartnerServerSelectionList(this, this.mc, width, height, 32, height - 64, 36);
            this.serverListSelector.func_148195_a(this.savedServerList);
        } else {
            this.serverListSelector.setDimensions(width, height, 32, height - 64);
        }
        this.createButtons();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.serverListSelector.handleMouseInput();
    }

    public void createButtons() {
        this.btnSelectServer = new GuiButton(1, width / 2 - 154, height - 52, 100, 20, I18n.format("selectServer.select", new Object[0]));
        this.buttonList.add(this.btnSelectServer);
        this.buttonList.add(new GuiButton(8, width / 2 + 4 + 50, height - 52, 100, 20, I18n.format("selectServer.refresh", new Object[0])));
        this.buttonList.add(new GuiButton(0, width / 2 - 50, height - 52, 100, 20, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(10, 5, 5, 75, 20, ViaLoadingBase.getInstance().getTargetVersion().getName()));
        this.buttonList.add(new GuiButton(11, width - 80, 5, 75, 20, "LabyMod"));
        this.buttonList.add(new GuiButton(100, width / 2 - 100, 15, 50, 10, "My " + EnumChatFormatting.RED.toString() + String.valueOf(this.normalServerList.countServers())));
        this.buttonList.add(new GuiButton(101, width / 2 - 25, 15, 50, 12, "Partner " + EnumChatFormatting.RED.toString() + String.valueOf(this.savedServerList.countServers())));
        this.buttonList.add(new GuiButton(102, width / 2 + 50, 15, 50, 10, "Finder " + EnumChatFormatting.RED.toString() + String.valueOf(this.serverFinderServerList.countServers())));
        this.selectServer(this.serverListSelector.func_148193_k());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.oldServerPinger.pingPendingNetworks();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }
        this.oldServerPinger.clearPendingNetworks();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
            GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.func_148193_k() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
            if (button.id == 2 && guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
                String s4 = ((PartnerServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;
                if (s4 != null) {
                    this.deletingServer = true;
                    String s2 = I18n.format("selectServer.deleteQuestion", new Object[0]);
                    String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning", new Object[0]);
                    String s22 = I18n.format("selectServer.deleteButton", new Object[0]);
                    String s3 = I18n.format("gui.cancel", new Object[0]);
                    GuiYesNo guiyesno = new GuiYesNo(this, s2, s1, s22, s3, this.serverListSelector.func_148193_k());
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 1) {
                this.connectToSelected();
            } else if (button.id == 4) {
                this.directConnect = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer));
            } else if (button.id == 3) {
                this.addingServer = true;
                this.selectedServer = new ServerData(I18n.format("selectServer.defaultName", new Object[0]), "", false);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 7 && guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
                this.editingServer = true;
                ServerData serverdata = ((PartnerServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
                this.selectedServer.copyFrom(serverdata);
                this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (button.id == 8) {
                this.refreshServerList();
            } else if (button.id == 10) {
                this.mc.displayGuiScreen(new GuiProtocolSelector(this));
            } else if (button.id == 11) {
                this.mc.displayGuiScreen(new GuiFriendsLayout(this));
            } else if (button.id == 100) {
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            } else if (button.id == 102) {
                this.mc.displayGuiScreen(new GuiServerFinderMultiplayer(new GuiMainMenu()));
            }
        }
    }

    private void refreshServerList() {
        this.mc.displayGuiScreen(new GuiPartnerMultiplayer(this.parentScreen));
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.func_148193_k() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
        if (this.deletingServer) {
            this.deletingServer = false;
            if (result && guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.func_148195_a(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        } else if (this.directConnect) {
            this.directConnect = false;
            if (result) {
                this.connectToServer(this.selectedServer);
            } else {
                this.mc.displayGuiScreen(this);
            }
        } else if (this.addingServer) {
            this.addingServer = false;
            if (result) {
                this.serverListSelector.setSelectedSlotIndex(-1);
                this.serverListSelector.func_148195_a(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        } else if (this.editingServer) {
            this.editingServer = false;
            if (result && guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
                ServerData serverdata = ((PartnerServerListEntryNormal)guilistextended$iguilistentry).getServerData();
                serverdata.serverName = this.selectedServer.serverName;
                serverdata.serverIP = this.selectedServer.serverIP;
                serverdata.copyFrom(this.selectedServer);
                this.serverListSelector.func_148195_a(this.savedServerList);
            }
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        int i2 = this.serverListSelector.func_148193_k();
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = i2 < 0 ? null : this.serverListSelector.getListEntry(i2);
        if (keyCode == 63) {
            this.refreshServerList();
        } else if (i2 >= 0) {
            if (keyCode == 200) {
                if (GuiPartnerMultiplayer.isShiftKeyDown()) {
                    if (i2 > 0 && guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
                        this.selectServer(this.serverListSelector.func_148193_k() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        this.serverListSelector.func_148195_a(this.savedServerList);
                    }
                } else if (i2 > 0) {
                    this.selectServer(this.serverListSelector.func_148193_k() - 1);
                    this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.func_148193_k() > 0) {
                            this.selectServer(this.serverListSelector.getSize() - 1);
                            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                        } else {
                            this.selectServer(-1);
                        }
                    }
                } else {
                    this.selectServer(-1);
                }
            } else if (keyCode == 208) {
                if (GuiPartnerMultiplayer.isShiftKeyDown()) {
                    if (i2 < this.savedServerList.countServers() - 1) {
                        this.selectServer(i2 + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        this.serverListSelector.func_148195_a(this.savedServerList);
                    }
                } else if (i2 < this.serverListSelector.getSize()) {
                    this.selectServer(this.serverListSelector.func_148193_k() + 1);
                    this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                    if (this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k()) instanceof ServerListEntryLanScan) {
                        if (this.serverListSelector.func_148193_k() < this.serverListSelector.getSize() - 1) {
                            this.selectServer(this.serverListSelector.getSize() + 1);
                            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                        } else {
                            this.selectServer(-1);
                        }
                    }
                } else {
                    this.selectServer(-1);
                }
            } else if (keyCode != 28 && keyCode != 156) {
                super.keyTyped(typedChar, keyCode);
            } else {
                this.actionPerformed((GuiButton)this.buttonList.get(2));
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;
        this.drawDefaultBackground();
        this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveringText != null) {
            this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }

    public void connectToSelected() {
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry;
        GuiListExtended.IGuiListEntry iGuiListEntry = guilistextended$iguilistentry = this.serverListSelector.func_148193_k() < 0 ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
        if (guilistextended$iguilistentry instanceof PartnerServerListEntryNormal) {
            this.connectToServer(((PartnerServerListEntryNormal)guilistextended$iguilistentry).getServerData());
        } else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected) {
            LanServerDetector.LanServer lanserverdetector$lanserver = ((ServerListEntryLanDetected)guilistextended$iguilistentry).getLanServer();
            this.connectToServer(new ServerData(lanserverdetector$lanserver.getServerMotd(), lanserverdetector$lanserver.getServerIpPort(), true));
        }
    }

    private void connectToServer(ServerData server) {
        this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
    }

    public void selectServer(int index) {
        this.serverListSelector.setSelectedSlotIndex(index);
        GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
        this.btnSelectServer.enabled = false;
        if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan)) {
            this.btnSelectServer.enabled = true;
            boolean cfr_ignored_0 = guilistextended$iguilistentry instanceof PartnerServerListEntryNormal;
        }
    }

    public OldServerPinger getOldServerPinger() {
        return this.oldServerPinger;
    }

    public void setHoveringText(String p_146793_1_) {
        this.hoveringText = p_146793_1_;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverListSelector.mouseReleased(mouseX, mouseY, state);
    }

    public PartnerServerList getServerList() {
        return this.savedServerList;
    }

    public boolean func_175392_a(PartnerServerListEntryNormal p_175392_1_, int p_175392_2_) {
        return p_175392_2_ > 0;
    }

    public boolean func_175394_b(PartnerServerListEntryNormal p_175394_1_, int p_175394_2_) {
        return p_175394_2_ < this.savedServerList.countServers() - 1;
    }

    public void func_175391_a(PartnerServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_) {
        int i2;
        int n2 = i2 = p_175391_3_ ? 0 : p_175391_2_ - 1;
        if (this.serverListSelector.func_148193_k() == p_175391_2_) {
            this.selectServer(i2);
        }
        this.serverListSelector.func_148195_a(this.savedServerList);
    }

    public void func_175393_b(PartnerServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_) {
        int i2;
        int n2 = i2 = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
        if (this.serverListSelector.func_148193_k() == p_175393_2_) {
            this.selectServer(i2);
        }
        this.serverListSelector.func_148195_a(this.savedServerList);
    }
}

