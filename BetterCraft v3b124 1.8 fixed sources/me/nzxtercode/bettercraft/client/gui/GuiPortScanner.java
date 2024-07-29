/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiPortScanner
extends GuiScreen {
    private GuiScreen parent;
    private GuiTextField ipField;
    private GuiTextField threadsField;
    private GuiTextField startPortField;
    private GuiTextField endPortField;
    private List<Integer> openPorts = new ArrayList<Integer>();

    public GuiPortScanner(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int centerX = width / 2;
        int centerY = height / 2;
        this.ipField = new GuiTextField(0, this.fontRendererObj, centerX - 100, centerY - 60, 200, 20);
        this.ipField.setText("localhost");
        this.threadsField = new GuiTextField(1, this.fontRendererObj, centerX - 100, centerY - 30, 200, 20);
        this.threadsField.setText("10");
        this.startPortField = new GuiTextField(2, this.fontRendererObj, centerX - 100, centerY, 90, 20);
        this.startPortField.setText("1");
        this.endPortField = new GuiTextField(3, this.fontRendererObj, centerX + 10, centerY, 90, 20);
        this.endPortField.setText("65535");
        this.buttonList.add(new GuiButton(4, centerX - 50, centerY + 30, 100, 20, "Start Scan"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 4) {
            this.openPorts.clear();
            final String host = this.ipField.getText();
            int threads = Integer.parseInt(this.threadsField.getText());
            final int startPort = Integer.parseInt(this.startPortField.getText());
            final int endPort = Integer.parseInt(this.endPortField.getText());
            int i2 = 0;
            while (i2 < threads) {
                Thread scannerThread = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        GuiPortScanner.this.scanPorts(host, startPort, endPort);
                    }
                });
                scannerThread.start();
                ++i2;
            }
        }
    }

    private void scanPorts(String host, int minPort, int maxPort) {
        int port = minPort;
        while (port <= maxPort) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 1000);
                socket.close();
                if (socket.isConnected()) {
                    this.openPorts.add(port);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            ++port;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.ipField.textboxKeyTyped(typedChar, keyCode);
        this.threadsField.textboxKeyTyped(typedChar, keyCode);
        this.startPortField.textboxKeyTyped(typedChar, keyCode);
        this.endPortField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.ipField.mouseClicked(mouseX, mouseY, mouseButton);
        this.threadsField.mouseClicked(mouseX, mouseY, mouseButton);
        this.startPortField.mouseClicked(mouseX, mouseY, mouseButton);
        this.endPortField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiPortScanner.drawCenteredString(this.fontRendererObj, "IP:", width / 2 - 110, height / 2 - 64, 0xFFFFFF);
        GuiPortScanner.drawCenteredString(this.fontRendererObj, "Threads:", width / 2 - 110, height / 2 - 34, 0xFFFFFF);
        GuiPortScanner.drawCenteredString(this.fontRendererObj, "Start Port:", width / 2 - 110, height / 2 - 4, 0xFFFFFF);
        GuiPortScanner.drawCenteredString(this.fontRendererObj, "End Port:", width / 2 + 30, height / 2 - 4, 0xFFFFFF);
        this.ipField.drawTextBox();
        this.threadsField.drawTextBox();
        this.startPortField.drawTextBox();
        this.endPortField.drawTextBox();
        int yPos = height / 2 + 60;
        for (Integer port : this.openPorts) {
            GuiPortScanner.drawCenteredString(this.fontRendererObj, "Port " + port + " is open", width / 2, yPos, 65280);
            yPos += 10;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

