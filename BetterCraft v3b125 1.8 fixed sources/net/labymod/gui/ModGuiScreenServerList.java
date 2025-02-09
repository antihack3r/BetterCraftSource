/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.ServerInfoRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class ModGuiScreenServerList
extends GuiScreen {
    private final GuiScreen guiScreen;
    private final ServerData serverData;
    private GuiTextField textField;
    private long lastUpdate = 0L;
    private long updateCooldown = 2000L;
    private ServerInfoRenderer serverInfoRenderer;

    public ModGuiScreenServerList(GuiScreen guiScreen, ServerData serverData) {
        this.guiScreen = guiScreen;
        this.serverData = serverData;
    }

    @Override
    public void updateScreen() {
        this.textField.updateCursorCounter();
        if (LabyMod.getSettings().directConnectInfo && !this.textField.getText().replace(" ", "").isEmpty()) {
            if (this.lastUpdate + this.updateCooldown < System.currentTimeMillis()) {
                this.lastUpdate = System.currentTimeMillis();
                LabyModCore.getServerPinger().pingServer(null, this.lastUpdate, this.textField.getText(), new Consumer<ServerPingerData>(){

                    @Override
                    public void accept(ServerPingerData accepted) {
                        if (accepted != null && accepted.getTimePinged() != ModGuiScreenServerList.this.lastUpdate) {
                            return;
                        }
                        ModGuiScreenServerList.this.serverInfoRenderer = new ServerInfoRenderer(ModGuiScreenServerList.this.textField.getText(), accepted);
                    }
                });
            }
        } else {
            this.serverInfoRenderer = new ServerInfoRenderer(this.textField.getText(), null);
            this.lastUpdate = -1L;
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, I18n.format("selectServer.select", new Object[0])));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
        this.textField = new GuiTextField(2, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 100, 116, 200, 20);
        this.textField.setMaxStringLength(128);
        this.textField.setFocused(true);
        this.textField.setText(this.mc.gameSettings.lastServer);
        ((GuiButton)this.buttonList.get((int)0)).enabled = this.textField.getText().length() > 0 && this.textField.getText().split(":").length > 0;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.textField.getText();
        this.mc.gameSettings.saveOptions();
        LabyModCore.getServerPinger().closePendingConnections();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.guiScreen.confirmClicked(false, 0);
            } else if (button.id == 0) {
                this.serverData.serverIP = this.textField.getText();
                this.guiScreen.confirmClicked(true, 5);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
            ((GuiButton)this.buttonList.get((int)0)).enabled = this.textField.getText().length() > 0 && this.textField.getText().split(":").length > 0;
        } else if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        ModGuiScreenServerList.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), I18n.format("selectServer.direct", new Object[0]), width / 2, 20, 0xFFFFFF);
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), I18n.format("addServer.enterIp", new Object[0]), width / 2 - 100, 100, 0xA0A0A0);
        this.textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.serverInfoRenderer == null || this.lastUpdate == -1L) {
            return;
        }
        DrawUtils drawUtils = LabyMod.getInstance().getDrawUtils();
        int leftBound = width / 2 - 150;
        int rightBound = width / 2 + 150;
        int posY = 44;
        int height = 30;
        drawUtils.drawRectangle(leftBound, 40, rightBound, 80, Integer.MIN_VALUE);
        int stateColorR = this.serverInfoRenderer.canReachServer() ? 105 : 240;
        int stateColorG = this.serverInfoRenderer.canReachServer() ? 240 : 105;
        int stateColorB = 105;
        double total = rightBound - leftBound;
        double barPercent = total / (double)this.updateCooldown * (double)(System.currentTimeMillis() - this.lastUpdate);
        if (barPercent > total) {
            barPercent = total;
        }
        int colorPercent = (int)Math.round(155.0 / (double)this.updateCooldown * (double)(System.currentTimeMillis() - this.lastUpdate - 100L));
        drawUtils.drawRectangle(leftBound, 38, rightBound, 39, Integer.MIN_VALUE);
        drawUtils.drawRectangle(leftBound, 38, rightBound, 39, ModColor.toRGB(stateColorR, stateColorG, 105, 155 - colorPercent));
        DrawUtils.drawRect((double)leftBound, 38.0, (double)leftBound + barPercent, 39.0, ModColor.toRGB(stateColorR, stateColorG, 105, 150));
        drawUtils.drawGradientShadowTop(40.0, leftBound, rightBound);
        drawUtils.drawGradientShadowBottom(80.0, leftBound, rightBound);
    }
}

