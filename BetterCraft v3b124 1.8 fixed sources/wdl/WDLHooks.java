/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemMap;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import wdl.CapeHandler;
import wdl.WDL;
import wdl.WDLEvents;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.api.IBlockEventListener;
import wdl.api.IChatMessageListener;
import wdl.api.IGuiHooksListener;
import wdl.api.IPluginChannelListener;
import wdl.api.WDLApi;
import wdl.gui.GuiWDL;
import wdl.gui.GuiWDLAbout;
import wdl.gui.GuiWDLChunkOverrides;
import wdl.gui.GuiWDLPermissions;

public class WDLHooks {
    private static final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
    private static final int WDLs = 1464093811;
    private static final int WDLo = 1464093807;

    public static void onWorldClientTick(WorldClient sender) {
        try {
            profiler.startSection("wdl");
            ImmutableList<EntityPlayer> players = ImmutableList.copyOf(sender.playerEntities);
            if (sender != WDL.worldClient) {
                profiler.startSection("onWorldLoad");
                if (WDL.worldLoadingDeferred) {
                    return;
                }
                WDLEvents.onWorldLoad(sender);
                profiler.endSection();
            } else {
                profiler.startSection("inventoryCheck");
                if (WDL.downloading && WDL.thePlayer != null && WDL.thePlayer.openContainer != WDL.windowContainer) {
                    if (WDL.thePlayer.openContainer == WDL.thePlayer.inventoryContainer) {
                        profiler.startSection("onItemGuiClosed");
                        profiler.startSection("Core");
                        boolean handled = WDLEvents.onItemGuiClosed();
                        profiler.endSection();
                        Container container = WDL.thePlayer.openContainer;
                        if (WDL.lastEntity != null) {
                            Entity entity = WDL.lastEntity;
                            for (WDLApi.ModInfo<IGuiHooksListener> info : WDLApi.getImplementingExtensions(IGuiHooksListener.class)) {
                                if (handled) break;
                                profiler.startSection(info.id);
                                handled = ((IGuiHooksListener)info.mod).onEntityGuiClosed(sender, entity, container);
                                profiler.endSection();
                            }
                            if (!handled) {
                                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.unhandledEntity", entity);
                            }
                        } else {
                            BlockPos pos = WDL.lastClickedBlock;
                            for (WDLApi.ModInfo<IGuiHooksListener> info : WDLApi.getImplementingExtensions(IGuiHooksListener.class)) {
                                if (handled) break;
                                profiler.startSection(info.id);
                                handled = ((IGuiHooksListener)info.mod).onBlockGuiClosed(sender, pos, container);
                                profiler.endSection();
                            }
                            if (!handled) {
                                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.unhandledTileEntity", pos, sender.getTileEntity(pos));
                            }
                        }
                        profiler.endSection();
                    } else {
                        profiler.startSection("onItemGuiOpened");
                        profiler.startSection("Core");
                        WDLEvents.onItemGuiOpened();
                        profiler.endSection();
                        profiler.endSection();
                    }
                    WDL.windowContainer = WDL.thePlayer.openContainer;
                }
                profiler.endSection();
            }
            profiler.startSection("capes");
            CapeHandler.onWorldTick(players);
            profiler.endSection();
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onWorldClientTick event");
        }
    }

    public static void onWorldClientDoPreChunk(WorldClient sender, int x2, int z2, boolean loading) {
        try {
            if (!WDL.downloading) {
                return;
            }
            profiler.startSection("wdl");
            if (!loading) {
                profiler.startSection("onChunkNoLongerNeeded");
                Chunk c2 = sender.getChunkFromChunkCoords(x2, z2);
                profiler.startSection("Core");
                WDLEvents.onChunkNoLongerNeeded(c2);
                profiler.endSection();
                profiler.endSection();
            }
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onWorldDoPreChunk event");
        }
    }

    public static void onWorldClientRemoveEntityFromWorld(WorldClient sender, int eid) {
        try {
            if (!WDL.downloading) {
                return;
            }
            profiler.startSection("wdl.onRemoveEntityFromWorld");
            Entity entity = sender.getEntityByID(eid);
            profiler.startSection("Core");
            WDLEvents.onRemoveEntityFromWorld(entity);
            profiler.endSection();
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onWorldRemoveEntityFromWorld event");
        }
    }

    public static void onNHPCHandleChat(NetHandlerPlayClient sender, S02PacketChat packet) {
        try {
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                return;
            }
            if (!WDL.downloading) {
                return;
            }
            profiler.startSection("wdl.onChatMessage");
            String chatMessage = packet.getChatComponent().getUnformattedText();
            profiler.startSection("Core");
            WDLEvents.onChatMessage(chatMessage);
            profiler.endSection();
            for (WDLApi.ModInfo<IChatMessageListener> info : WDLApi.getImplementingExtensions(IChatMessageListener.class)) {
                profiler.startSection(info.id);
                ((IChatMessageListener)info.mod).onChat(WDL.worldClient, chatMessage);
                profiler.endSection();
            }
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onNHPCHandleChat event");
        }
    }

    public static void onNHPCHandleMaps(NetHandlerPlayClient sender, S34PacketMaps packet) {
        try {
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                return;
            }
            if (!WDL.downloading) {
                return;
            }
            profiler.startSection("wdl.onMapDataLoaded");
            int id2 = packet.getMapId();
            MapData mapData = ItemMap.loadMapData(packet.getMapId(), WDL.worldClient);
            profiler.startSection("Core");
            WDLEvents.onMapDataLoaded(id2, mapData);
            profiler.endSection();
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onNHPCHandleMaps event");
        }
    }

    public static void onNHPCHandleCustomPayload(NetHandlerPlayClient sender, S3FPacketCustomPayload packet) {
        try {
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                return;
            }
            if (!packet.getBufferData().isReadable()) {
                return;
            }
            String channel = packet.getChannelName();
            byte[] payload = packet.getBufferData().array();
            profiler.startSection("wdl.onPluginMessage");
            profiler.startSection("Core");
            WDLEvents.onPluginChannelPacket(channel, payload);
            profiler.endSection();
            for (WDLApi.ModInfo<IPluginChannelListener> info : WDLApi.getImplementingExtensions(IPluginChannelListener.class)) {
                profiler.startSection(info.id);
                ((IPluginChannelListener)info.mod).onPluginChannelPacket(WDL.worldClient, channel, payload);
                profiler.endSection();
            }
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onNHPCHandleCustomPayload event");
        }
    }

    public static void onNHPCHandleBlockAction(NetHandlerPlayClient sender, S24PacketBlockAction packet) {
        try {
            if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
                return;
            }
            if (!WDL.downloading) {
                return;
            }
            profiler.startSection("wdl.onBlockEvent");
            BlockPos pos = packet.getBlockPosition();
            Block block = packet.getBlockType();
            int data1 = packet.getData1();
            int data2 = packet.getData2();
            profiler.startSection("Core");
            WDLEvents.onBlockEvent(pos, block, data1, data2);
            profiler.endSection();
            for (WDLApi.ModInfo<IBlockEventListener> info : WDLApi.getImplementingExtensions(IBlockEventListener.class)) {
                profiler.startSection(info.id);
                ((IBlockEventListener)info.mod).onBlockEvent(WDL.worldClient, pos, block, data1, data2);
                profiler.endSection();
            }
            profiler.endSection();
        }
        catch (Throwable e2) {
            WDL.crashed(e2, "WDL mod: exception in onNHPCHandleBlockAction event");
        }
    }

    public static void onCrashReportPopulateEnvironment(CrashReport report) {
        report.makeCategory("World Downloader Mod").addCrashSectionCallable("Info", new Callable<String>(){

            @Override
            public String call() {
                return WDL.getDebugInfo();
            }
        });
    }

    public static void injectWDLButtons(GuiIngameMenu gui, List buttonList) {
        GuiButton btn;
        int insertAtYPos = 0;
        for (Object obj : buttonList) {
            btn = (GuiButton)obj;
            if (btn.id != 5) continue;
            insertAtYPos = btn.yPosition + 24;
            break;
        }
        for (Object obj : buttonList) {
            btn = (GuiButton)obj;
            if (btn.yPosition < insertAtYPos) continue;
            btn.yPosition += 24;
        }
        GuiButton wdlDownload = new GuiButton(1464093811, GuiIngameMenu.width / 2 - 100, insertAtYPos, 170, 20, null);
        GuiButton wdlOptions = new GuiButton(1464093807, GuiIngameMenu.width / 2 + 72, insertAtYPos, 28, 20, I18n.format("wdl.gui.ingameMenu.settings", new Object[0]));
        if (WDL.minecraft.isIntegratedServerRunning()) {
            wdlDownload.displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.singlePlayer", new Object[0]);
            wdlDownload.enabled = false;
        } else if (!WDLPluginChannels.canDownloadAtAll()) {
            if (WDLPluginChannels.canRequestPermissions()) {
                wdlDownload.displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.request", new Object[0]);
            } else {
                wdlDownload.displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.disabled", new Object[0]);
                wdlDownload.enabled = false;
            }
        } else if (WDL.saving) {
            wdlDownload.displayString = I18n.format("wdl.gui.ingameMenu.downloadStatus.saving", new Object[0]);
            wdlDownload.enabled = false;
            wdlOptions.enabled = false;
        } else {
            wdlDownload.displayString = WDL.downloading ? I18n.format("wdl.gui.ingameMenu.downloadStatus.stop", new Object[0]) : I18n.format("wdl.gui.ingameMenu.downloadStatus.start", new Object[0]);
        }
        buttonList.add(wdlDownload);
        buttonList.add(wdlOptions);
    }

    public static void handleWDLButtonClick(GuiIngameMenu gui, GuiButton button) {
        if (!button.enabled) {
            return;
        }
        if (button.id == 1464093811) {
            if (WDL.minecraft.isIntegratedServerRunning()) {
                return;
            }
            if (WDL.downloading) {
                WDL.stopDownload();
            } else {
                if (!WDLPluginChannels.canDownloadAtAll()) {
                    if (WDLPluginChannels.canRequestPermissions()) {
                        WDL.minecraft.displayGuiScreen(new GuiWDLPermissions(gui));
                    } else {
                        button.enabled = false;
                    }
                    return;
                }
                if (WDLPluginChannels.hasChunkOverrides() && !WDLPluginChannels.canDownloadInGeneral()) {
                    WDL.minecraft.displayGuiScreen(new GuiWDLChunkOverrides(gui));
                } else {
                    WDL.startDownload();
                }
            }
        } else if (button.id == 1464093807) {
            if (WDL.minecraft.isIntegratedServerRunning()) {
                WDL.minecraft.displayGuiScreen(new GuiWDLAbout(gui));
            } else {
                WDL.minecraft.displayGuiScreen(new GuiWDL(gui));
            }
        } else if (button.id == 1) {
            WDL.stopDownload();
        }
    }
}

