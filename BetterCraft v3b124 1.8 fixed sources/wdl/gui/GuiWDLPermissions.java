/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.gui.GuiWDLChunkOverrides;
import wdl.gui.GuiWDLPermissionRequest;
import wdl.gui.TextList;

public class GuiWDLPermissions
extends GuiScreen {
    private static final int TOP_MARGIN = 61;
    private static final int BOTTOM_MARGIN = 32;
    private GuiButton reloadButton;
    private int refreshTicks = -1;
    private final GuiScreen parent;
    private TextList list;

    public GuiWDLPermissions(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(200, width / 2 - 155, 39, 100, 20, I18n.format("wdl.gui.permissions.current", new Object[0])));
        if (WDLPluginChannels.canRequestPermissions()) {
            this.buttonList.add(new GuiButton(201, width / 2 - 50, 39, 100, 20, I18n.format("wdl.gui.permissions.request", new Object[0])));
            this.buttonList.add(new GuiButton(202, width / 2 + 55, 39, 100, 20, I18n.format("wdl.gui.permissions.overrides", new Object[0])));
        }
        this.reloadButton = new GuiButton(1, width / 2 + 5, 18, 150, 20, "Reload permissions");
        this.buttonList.add(this.reloadButton);
        this.list = new TextList(this.mc, width, height, 61, 32);
        this.list.addLine("\u00a7c\u00a7lThis is a work in progress.");
        if (!WDLPluginChannels.hasPermissions()) {
            return;
        }
        this.list.addBlankLine();
        if (!WDLPluginChannels.canRequestPermissions()) {
            this.list.addLine("\u00a7cThe serverside permission plugin is out of date and does support permission requests.  Please go ask a server administrator to update the plugin.");
            this.list.addBlankLine();
        }
        if (WDLPluginChannels.getRequestMessage() != null) {
            this.list.addLine("Note from the server moderators: ");
            this.list.addLine(WDLPluginChannels.getRequestMessage());
            this.list.addBlankLine();
        }
        this.list.addLine("These are your current permissions:");
        this.list.addLine("Can download: " + WDLPluginChannels.canDownloadInGeneral());
        this.list.addLine("Can save chunks as you move: " + WDLPluginChannels.canCacheChunks());
        if (!WDLPluginChannels.canCacheChunks() && WDLPluginChannels.canDownloadInGeneral()) {
            this.list.addLine("Nearby chunk save radius: " + WDLPluginChannels.getSaveRadius());
        }
        this.list.addLine("Can save entities: " + WDLPluginChannels.canSaveEntities());
        this.list.addLine("Can save tile entities: " + WDLPluginChannels.canSaveTileEntities());
        this.list.addLine("Can save containers: " + WDLPluginChannels.canSaveContainers());
        this.list.addLine("Received entity ranges: " + WDLPluginChannels.hasServerEntityRange() + " (" + WDLPluginChannels.getEntityRanges().size() + " total)");
    }

    @Override
    public void updateScreen() {
        if (this.refreshTicks > 0) {
            --this.refreshTicks;
        } else if (this.refreshTicks == 0) {
            this.initGui();
            this.refreshTicks = -1;
        }
    }

    @Override
    public void onGuiClosed() {
        WDL.saveProps();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            C17PacketCustomPayload initPacket;
            try {
                initPacket = new C17PacketCustomPayload("WDL|INIT", new PacketBuffer(Unpooled.copiedBuffer("1.8.9a-beta2".getBytes("UTF-8"))));
            }
            catch (UnsupportedEncodingException e2) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.noUTF8", new Object[0]);
                initPacket = new C17PacketCustomPayload("WDL|INIT", new PacketBuffer(Unpooled.buffer()));
            }
            WDL.minecraft.getNetHandler().addToSendQueue(initPacket);
            button.enabled = false;
            button.displayString = "Refershing...";
            this.refreshTicks = 50;
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
        int cfr_ignored_0 = button.id;
        if (button.id == 201) {
            this.mc.displayGuiScreen(new GuiWDLPermissionRequest(this.parent));
        }
        if (button.id == 202) {
            this.mc.displayGuiScreen(new GuiWDLChunkOverrides(this.parent));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.list == null) {
            return;
        }
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLPermissions.drawCenteredString(this.fontRendererObj, "Permission info", width / 2, 8, 0xFFFFFF);
        if (!WDLPluginChannels.hasPermissions()) {
            GuiWDLPermissions.drawCenteredString(this.fontRendererObj, "No permissions received; defaulting to everything enabled.", width / 2, (height - 32 - 23) / 2 + 23 - this.fontRendererObj.FONT_HEIGHT / 2, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

