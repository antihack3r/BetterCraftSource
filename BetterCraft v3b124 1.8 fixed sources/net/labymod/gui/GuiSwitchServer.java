/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.gui.elements.CheckBox;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.manager.ServerInfoRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiSwitchServer
extends GuiScreen {
    private String title;
    private String address;
    private Result result;
    private boolean connectingScreen;
    private ServerInfoRenderer serverInfoRenderer;
    private long lastUpdate = 0L;
    private long updateCooldown = 2000L;
    private String currentServer;
    private CheckBox checkBox;

    public GuiSwitchServer(String address) {
        this.address = address;
        this.connectingScreen = true;
    }

    public GuiSwitchServer(String title, String address, boolean preview, Result result) {
        this.title = ModColor.createColors(title);
        this.address = address;
        this.result = result;
        this.connectingScreen = false;
        String string = this.currentServer = LabyMod.getInstance().getCurrentServerData() == null ? null : ModUtils.getProfileNameByIp(LabyMod.getInstance().getCurrentServerData().getIp());
        if (preview) {
            this.serverInfoRenderer = new ServerInfoRenderer(address, null);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (!this.connectingScreen) {
            this.buttonList.add(new GuiButton(0, width / 2 + 20, height / 2 + 20, 100, 20, LanguageManager.translate("server_switch_no")));
            this.buttonList.add(new GuiButton(1, width / 2 - 120, height / 2 + 20, 130, 20, LanguageManager.translate("server_switch_yes")));
            this.checkBox = new CheckBox("", CheckBox.EnumCheckBoxValue.DISABLED, null, width / 2 - 120, height / 2 + 50, 20, 20);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (this.connectingScreen) {
            draw.drawCenteredString(I18n.format("connect.connecting", new Object[0]), width / 2, height / 2 - 5);
            draw.drawCenteredString(this.address, width / 2, height / 2 + 5);
        } else {
            draw.drawCenteredString(this.title, width / 2, height / 2 - 55 + (this.serverInfoRenderer == null ? 0 : -15));
            List<String> list = draw.listFormattedStringToWidth(LanguageManager.translate("server_switch_confirm", this.address), 250);
            int i2 = 0;
            for (String line : list) {
                draw.drawCenteredString(line, width / 2, height / 2 - 40 + i2 * 10 + (this.serverInfoRenderer == null ? 10 : -10));
                ++i2;
            }
            if (this.currentServer != null) {
                this.checkBox.drawCheckbox(mouseX, mouseY);
                draw.drawString(String.valueOf(ModColor.cl('a')) + LanguageManager.translate("server_switch_trust", this.currentServer), width / 2 - 95, height / 2 + 56);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.connectingScreen) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!this.connectingScreen && this.serverInfoRenderer != null && this.lastUpdate + this.updateCooldown < System.currentTimeMillis()) {
            this.lastUpdate = System.currentTimeMillis();
            LabyModCore.getServerPinger().pingServer(null, this.lastUpdate, this.address, new Consumer<ServerPingerData>(){

                @Override
                public void accept(ServerPingerData accepted) {
                    if (accepted != null && accepted.getTimePinged() != GuiSwitchServer.this.lastUpdate) {
                        return;
                    }
                    if (GuiSwitchServer.this.serverInfoRenderer != null) {
                        GuiSwitchServer.this.serverInfoRenderer.init(GuiSwitchServer.this.address, GuiSwitchServer.this.address, accepted);
                    }
                }
            });
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0) {
            this.result.notify(this.address, false, false);
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        if (button.id == 1) {
            boolean trusted;
            boolean bl2 = trusted = this.currentServer != null && this.checkBox.getValue() == CheckBox.EnumCheckBoxValue.ENABLED;
            if (trusted) {
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(LabyMod.getSettings().trustedServers));
                String profileAddress = ModUtils.getProfileNameByIp(this.currentServer);
                if (trusted && !list.contains(profileAddress)) {
                    list.add(profileAddress);
                    String[] array = new String[list.size()];
                    list.toArray(array);
                    LabyMod.getSettings().trustedServers = array;
                    LabyMod.getMainConfig().save();
                }
            }
            this.result.notify(this.address, true, trusted);
            LabyMod.getInstance().switchServer(this.address, true);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.connectingScreen) {
            this.checkBox.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public static interface Result {
        public void notify(String var1, boolean var2, boolean var3);
    }
}

