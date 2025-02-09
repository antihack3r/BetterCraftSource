/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.GuiServerFinderMultiplayer;
import me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.impl.NewServerPinger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

public class GuiServerFinderAddServer
extends GuiScreen {
    private static final String[] stateStrings = new String[]{"", "\u00a7dSearching...", "\u00a7dResolving...", "\u00a74Unknown Host!", "\u00a74Cancelled!", "\u00a7aDone!", "\u00a74An error occurred!"};
    private GuiTextField ipBox;
    private GuiTextField maxThreadsBox;
    private int checked;
    private int working;
    private ServerFinderState state;
    private GuiServerFinderMultiplayer parent;

    public GuiServerFinderAddServer(GuiServerFinderMultiplayer parent) {
        this.parent = parent;
    }

    @Override
    public void updateScreen() {
        this.ipBox.updateCursorCounter();
        ((GuiButton)this.buttonList.get((int)0)).displayString = this.state.isRunning() ? "\u00a7cCancel" : "\u00a7aSearch";
        this.ipBox.setEnabled(!this.state.isRunning());
        this.maxThreadsBox.setEnabled(!this.state.isRunning());
        ((GuiButton)this.buttonList.get((int)0)).enabled = !this.ipBox.getText().isEmpty() && !this.maxThreadsBox.getText().isEmpty();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 100, "Search"));
        this.buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 125, "Back"));
        this.ipBox = new GuiTextField(0, this.fontRendererObj, width / 2 - 100, height / 4 + 25, 200, 20);
        this.ipBox.setMaxStringLength(200);
        this.ipBox.setFocused(true);
        this.maxThreadsBox = new GuiTextField(1, this.fontRendererObj, width / 2 - 32, height / 4 + 50, 26, 12);
        this.maxThreadsBox.setMaxStringLength(3);
        this.maxThreadsBox.setFocused(false);
        this.maxThreadsBox.setText(Integer.toString(1));
        this.state = ServerFinderState.NOT_RUNNING;
    }

    @Override
    public void onGuiClosed() {
        this.state = ServerFinderState.CANCELLED;
        Boolean.parseBoolean(String.valueOf(Integer.parseInt(this.maxThreadsBox.getText())));
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                if (this.state.isRunning()) {
                    this.state = ServerFinderState.CANCELLED;
                } else {
                    this.state = ServerFinderState.RESOLVING;
                    this.checked = 0;
                    this.working = 0;
                    new Thread("Server Finder"){

                        @Override
                        public void run() {
                            try {
                                int[] changes;
                                InetAddress addr = InetAddress.getByName(GuiServerFinderAddServer.this.ipBox.getText().split(":")[0].trim());
                                int[] ipParts = new int[4];
                                int i2 = 0;
                                while (i2 < 4) {
                                    ipParts[i2] = addr.getAddress()[i2] & 0xFF;
                                    ++i2;
                                }
                                GuiServerFinderAddServer.this.state = ServerFinderState.SEARCHING;
                                ArrayList<NewServerPinger> pingers = new ArrayList<NewServerPinger>();
                                int[] nArray = new int[7];
                                nArray[1] = 1;
                                nArray[2] = -1;
                                nArray[3] = 2;
                                nArray[4] = -2;
                                nArray[5] = 3;
                                nArray[6] = -3;
                                int[] nArray2 = changes = nArray;
                                int n2 = changes.length;
                                int n3 = 0;
                                while (n3 < n2) {
                                    int change = nArray2[n3];
                                    int i22 = 0;
                                    while (i22 <= 255) {
                                        if (GuiServerFinderAddServer.this.state == ServerFinderState.CANCELLED) {
                                            return;
                                        }
                                        int[] ipParts2 = (int[])ipParts.clone();
                                        ipParts2[2] = ipParts[2] + change & 0xFF;
                                        ipParts2[3] = i22;
                                        String ip2 = String.valueOf(ipParts2[0]) + "." + ipParts2[1] + "." + ipParts2[2] + "." + ipParts2[3];
                                        NewServerPinger pinger = new NewServerPinger();
                                        pinger.ping(ip2);
                                        pingers.add(pinger);
                                        while (pingers.size() >= Integer.parseInt(GuiServerFinderAddServer.this.maxThreadsBox.getText())) {
                                            if (GuiServerFinderAddServer.this.state == ServerFinderState.CANCELLED) {
                                                return;
                                            }
                                            GuiServerFinderAddServer.this.updatePingers(pingers);
                                        }
                                        ++i22;
                                    }
                                    ++n3;
                                }
                                while (pingers.size() > 0) {
                                    if (GuiServerFinderAddServer.this.state == ServerFinderState.CANCELLED) {
                                        return;
                                    }
                                    GuiServerFinderAddServer.this.updatePingers(pingers);
                                }
                                GuiServerFinderAddServer.this.state = ServerFinderState.DONE;
                            }
                            catch (UnknownHostException e2) {
                                GuiServerFinderAddServer.this.state = ServerFinderState.UNKNOWN_HOST;
                            }
                            catch (Exception e3) {
                                e3.printStackTrace();
                                GuiServerFinderAddServer.this.state = ServerFinderState.ERROR;
                            }
                        }
                    }.start();
                }
            } else if (button.id == 2) {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    private boolean serverInList(String ip2) {
        int i2 = 0;
        while (i2 < GuiServerFinderMultiplayer.savedServerList.countServers()) {
            if (GuiServerFinderMultiplayer.savedServerList.getServerData((int)i2).serverIP.equals(ip2)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    private void updatePingers(ArrayList<NewServerPinger> pingers) {
        int i2 = 0;
        while (i2 < pingers.size()) {
            if (!pingers.get(i2).isStillPinging()) {
                ++this.checked;
                if (pingers.get(i2).isWorking()) {
                    ++this.working;
                    if (!this.serverInList(pingers.get((int)i2).serverData.serverIP)) {
                        GuiServerFinderMultiplayer.savedServerList.addServerData(new ServerData("Grief me #" + this.working, pingers.get((int)i2).serverData.serverIP, false));
                        GuiServerFinderMultiplayer.savedServerList.saveServerList();
                        this.parent.serverListSelector.setSelectedSlotIndex(-1);
                        this.parent.serverListSelector.func_148195_a(GuiServerFinderMultiplayer.savedServerList);
                    }
                }
                pingers.remove(i2);
            }
            ++i2;
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        this.ipBox.textboxKeyTyped(par1, par2);
        this.maxThreadsBox.textboxKeyTyped(par1, par2);
        if (par2 == 28 || par2 == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        super.mouseClicked(par1, par2, par3);
        this.ipBox.mouseClicked(par1, par2, par3);
        this.maxThreadsBox.mouseClicked(par1, par2, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        GuiServerFinderAddServer.drawCenteredString(this.fontRendererObj, "This will search for servers with similar IPs", width / 2, 20, 0xA0A0A0);
        GuiServerFinderAddServer.drawCenteredString(this.fontRendererObj, "to the IP you type into the field below.", width / 2, 30, 0xA0A0A0);
        GuiServerFinderAddServer.drawCenteredString(this.fontRendererObj, "The servers it finds will be added to your server list.", width / 2, 40, 0xA0A0A0);
        GuiServerFinderAddServer.drawCenteredString(this.fontRendererObj, "LivingBots \u00a7c45.88.108.0 - 45.88.111.255 \u00a7rMCHost24 \u00a7c45.81.233.0 - 45.81.235.255", width / 2, 60, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Server address:", width / 2 - 100, height / 4 + 15, 0xA0A0A0);
        this.ipBox.drawTextBox();
        this.drawString(this.fontRendererObj, "Max. threads:", width / 2 - 100, height / 4 + 50, 0xA0A0A0);
        this.maxThreadsBox.drawTextBox();
        GuiServerFinderAddServer.drawCenteredString(this.fontRendererObj, this.state.toString(), width / 2, height / 4 + 65, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Checked: \u00a7f" + this.checked + " \u00a78/ \u00a7c1792", width / 2 - 100, height / 4 + 75, 0xA0A0A0);
        this.drawString(this.fontRendererObj, "Working: \u00a7a" + this.working, width / 2 - 100, height / 4 + 85, 0xA0A0A0);
        super.drawScreen(par1, par2, par3);
    }

    static void access(GuiServerFinderAddServer guiServerFinder, ServerFinderState state) {
        guiServerFinder.state = state;
    }

    static enum ServerFinderState {
        NOT_RUNNING("NOT_RUNNING", 0),
        SEARCHING("SEARCHING", 1),
        RESOLVING("RESOLVING", 2),
        UNKNOWN_HOST("UNKNOWN_HOST", 3),
        CANCELLED("CANCELLED", 4),
        DONE("DONE", 5),
        ERROR("ERROR", 6);


        private ServerFinderState(String s2, int n3) {
        }

        public boolean isRunning() {
            return this == SEARCHING || this == RESOLVING;
        }

        public String toString() {
            return stateStrings[this.ordinal()];
        }
    }
}

