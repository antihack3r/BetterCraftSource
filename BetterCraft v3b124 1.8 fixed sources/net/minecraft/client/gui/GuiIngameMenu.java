/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import me.nzxtercode.bettercraft.client.misc.irc.GuiIRC;
import net.labymod.addons.teamspeak3.GuiTeamSpeak;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.utils.Consumer;
import net.labymod.utils.manager.ServerInfoRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import wdl.WDLHooks;

public class GuiIngameMenu
extends GuiScreen {
    private int field_146445_a;
    private int field_146444_f;
    private long lastUpdate = 0L;
    private long updateCooldown = 5000L;
    private ServerInfoRenderer serverInfoRenderer;
    private ServerData serverData;

    public GuiIngameMenu() {
        String serverIp = Minecraft.getMinecraft().isIntegratedServerRunning() ? "localhost" : Minecraft.getMinecraft().getCurrentServerData().serverIP;
        String serverName = Minecraft.getMinecraft().isIntegratedServerRunning() ? "localhost" : Minecraft.getMinecraft().getNetHandler().getNetworkManager().getRemoteAddress().toString();
        this.serverData = new ServerData(serverName, serverIp, false);
    }

    @Override
    public void initGui() {
        this.field_146445_a = 0;
        this.buttonList.clear();
        int i2 = -16;
        int j2 = 98;
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + i2, I18n.format("menu.returnToMenu", new Object[0])));
        if (!this.mc.isIntegratedServerRunning()) {
            ((GuiButton)this.buttonList.get((int)0)).displayString = I18n.format("menu.disconnect", new Object[0]);
        }
        this.buttonList.add(new GuiButton(4, width - 80, 5, 75, 20, "LabyMod"));
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + i2, 98, 20, I18n.format("menu.options", new Object[0])));
        GuiButton guibutton = new GuiButton(7, width / 2 + 2, height / 4 + 96 + i2, 98, 20, I18n.format("menu.shareToLan", new Object[0]));
        this.buttonList.add(guibutton);
        this.buttonList.add(new GuiButton(5, width / 2 - 100, height / 4 + 24 + i2, "Chat"));
        this.buttonList.add(new GuiButton(6, width / 2 - 100, height / 4 + 32, I18n.format("gui.stats", new Object[0])));
        this.buttonList.add(new GuiButton(10, width / 2 - 100, height / 4 + 72 + i2, I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add(new GuiButton(11, 5, 5, 75, 20, "TeamSpeak"));
        guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
        WDLHooks.injectWDLButtons(this, this.buttonList);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        WDLHooks.handleWDLButtonClick(this, button);
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 1: {
                boolean flag = this.mc.isIntegratedServerRunning();
                boolean flag1 = this.mc.isConnectedToRealms();
                button.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                if (flag) {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                    break;
                }
                if (flag1) {
                    RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiMainMenu());
                    break;
                }
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }
            default: {
                break;
            }
            case 4: {
                this.mc.displayGuiScreen(new GuiFriendsLayout(this));
                break;
            }
            case 5: {
                this.mc.displayGuiScreen(new GuiIRC(this));
                break;
            }
            case 6: {
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            }
            case 10: {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            }
            case 11: {
                this.mc.displayGuiScreen(new GuiTeamSpeak(this));
                break;
            }
            case 7: {
                this.mc.displayGuiScreen(new GuiShareToLan(this));
            }
        }
    }

    @Override
    public void updateScreen() {
        if (this.lastUpdate + this.updateCooldown < System.currentTimeMillis()) {
            this.lastUpdate = System.currentTimeMillis();
            LabyModCore.getServerPinger().pingServer(null, this.lastUpdate, this.serverData.serverIP, new Consumer<ServerPingerData>(){

                @Override
                public void accept(ServerPingerData accepted) {
                    if (accepted != null && accepted.getTimePinged() != GuiIngameMenu.this.lastUpdate) {
                        return;
                    }
                    GuiIngameMenu.this.serverInfoRenderer = new ServerInfoRenderer(((GuiIngameMenu)GuiIngameMenu.this).serverData.serverIP, accepted);
                }
            });
        }
        super.updateScreen();
        ++this.field_146444_f;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.serverInfoRenderer == null || this.lastUpdate == -1L) {
            return;
        }
        this.serverInfoRenderer.drawEntry(0, width / 2 - 138, height / 4 + 24 - 65, 275, 35, 0, 0, false);
    }
}

