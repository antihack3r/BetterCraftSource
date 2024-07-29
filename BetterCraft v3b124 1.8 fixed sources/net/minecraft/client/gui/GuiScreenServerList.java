/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Objects;
import me.nzxtercode.bettercraft.client.utils.ProxyUtils;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.utils.Consumer;
import net.labymod.utils.manager.ServerInfoRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiScreenServerList
extends GuiScreen {
    private final GuiScreen field_146303_a;
    private final ServerData field_146301_f;
    private GuiTextField field_146302_g;
    private long lastUpdate = 0L;
    private long updateCooldown = 5000L;
    private ServerInfoRenderer serverInfoRenderer;
    public static GuiTextField proxy;
    public static boolean isEnabled;

    static {
        isEnabled = false;
    }

    public GuiScreenServerList(GuiScreen p_i1031_1_, ServerData p_i1031_2_) {
        this.field_146303_a = p_i1031_1_;
        this.field_146301_f = p_i1031_2_;
    }

    @Override
    public void updateScreen() {
        proxy.updateCursorCounter();
        this.field_146302_g.updateCursorCounter();
        if (!this.field_146302_g.getText().replace(" ", "").isEmpty()) {
            if (this.lastUpdate + this.updateCooldown < System.currentTimeMillis()) {
                this.lastUpdate = System.currentTimeMillis();
                LabyModCore.getServerPinger().pingServer(null, this.lastUpdate, this.field_146302_g.getText(), new Consumer<ServerPingerData>(){

                    @Override
                    public void accept(ServerPingerData accepted) {
                        if (accepted != null && accepted.getTimePinged() != GuiScreenServerList.this.lastUpdate) {
                            return;
                        }
                        GuiScreenServerList.this.serverInfoRenderer = new ServerInfoRenderer(GuiScreenServerList.this.field_146302_g.getText(), accepted);
                    }
                });
            }
        } else {
            this.serverInfoRenderer = new ServerInfoRenderer(this.field_146302_g.getText(), null);
            this.lastUpdate = -1L;
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 72 + 12, String.format("%s", isEnabled ? String.valueOf(EnumChatFormatting.RED.toString()) + "Disconnect" : String.valueOf(EnumChatFormatting.GREEN.toString()) + "Connect")));
        this.field_146302_g = new GuiTextField(2, this.fontRendererObj, width / 2 - 100, 116, 200, 20);
        this.field_146302_g.setMaxStringLength(128);
        this.field_146302_g.setFocused(true);
        this.field_146302_g.setText(this.mc.gameSettings.lastServer);
        proxy = new GuiTextField(3, this.fontRendererObj, width / 2 - 100, 66, 200, 20);
        proxy.setMaxStringLength(128);
        proxy.setText(Objects.nonNull(ProxyUtils.getProxy()) ? ProxyUtils.getProxy().address().toString().replace("/", "") : "127.0.0.1:9050");
        ((GuiButton)this.buttonList.get((int)0)).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
        ((GuiButton)this.buttonList.get((int)2)).enabled = proxy.getText().length() > 0 && proxy.getText().split(":").length > 0;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.field_146302_g.getText();
        this.mc.gameSettings.saveOptions();
        LabyModCore.getServerPinger().closePendingConnections();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.field_146303_a.confirmClicked(false, 0);
            } else if (button.id == 0) {
                this.field_146301_f.serverIP = this.field_146302_g.getText();
                this.field_146303_a.confirmClicked(true, 0);
            } else if (button.id == 2) {
                ProxyUtils.setProxy(isEnabled ? null : ProxyUtils.getProxyFromString(proxy.getText()));
                isEnabled = !isEnabled;
                button.displayString = String.format("%s", isEnabled ? String.valueOf(EnumChatFormatting.RED.toString()) + "Disconnect" : String.valueOf(EnumChatFormatting.GREEN.toString()) + "Connect");
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.field_146302_g.textboxKeyTyped(typedChar, keyCode)) {
            ((GuiButton)this.buttonList.get((int)0)).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
        } else if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        } else if (proxy.textboxKeyTyped(typedChar, keyCode)) {
            ((GuiButton)this.buttonList.get((int)2)).enabled = proxy.getText().length() > 0 && proxy.getText().split(":").length > 0;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field_146302_g.mouseClicked(mouseX, mouseY, mouseButton);
        proxy.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, I18n.format("addServer.enterIp", new Object[0]), width / 2 - 100, 100, 0xA0A0A0);
        this.field_146302_g.drawTextBox();
        this.drawString(this.fontRendererObj, "Proxyaddress", width / 2 - 100, 50, 0xA0A0A0);
        proxy.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.serverInfoRenderer == null || this.lastUpdate == -1L) {
            return;
        }
        this.serverInfoRenderer.drawEntry(0, width / 2 - 138, 10, 275, 35, 0, 0, false);
    }
}

