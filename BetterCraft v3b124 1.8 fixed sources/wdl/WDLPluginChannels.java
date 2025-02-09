/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.Unpooled;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;

public class WDLPluginChannels {
    private static Logger logger = LogManager.getLogger();
    private static HashSet<Integer> receivedPackets = new HashSet();
    private static boolean canUseFunctionsUnknownToServer = true;
    private static boolean canDownloadInGeneral = true;
    private static int saveRadius = -1;
    private static boolean canCacheChunks = true;
    private static boolean canSaveEntities = true;
    private static boolean canSaveTileEntities = true;
    private static boolean canSaveContainers = true;
    private static Map<String, Integer> entityRanges = new HashMap<String, Integer>();
    private static boolean canRequestPermissions = false;
    private static String requestMessage = "";
    private static Map<String, Multimap<String, ChunkRange>> chunkOverrides = new HashMap<String, Multimap<String, ChunkRange>>();
    private static Map<String, String> requests = new HashMap<String, String>();
    public static final List<String> BOOLEAN_REQUEST_FIELDS = Arrays.asList("downloadInGeneral", "cacheChunks", "saveEntities", "saveTileEntities", "saveContainers", "getEntityRanges");
    public static final List<String> INTEGER_REQUEST_FIELDS = Arrays.asList("saveRadius");
    private static List<ChunkRange> chunkOverrideRequests = new ArrayList<ChunkRange>();

    public static boolean canUseFunctionsUnknownToServer() {
        if (receivedPackets.contains(0)) {
            return canUseFunctionsUnknownToServer;
        }
        return true;
    }

    public static boolean canDownloadAtAll() {
        if (WDLPluginChannels.hasChunkOverrides()) {
            return true;
        }
        return WDLPluginChannels.canDownloadInGeneral();
    }

    public static boolean canDownloadInGeneral() {
        if (receivedPackets.contains(1)) {
            return canDownloadInGeneral;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static boolean canSaveChunk(Chunk chunk) {
        if (WDLPluginChannels.isChunkOverridden(chunk)) {
            return true;
        }
        if (!WDLPluginChannels.canDownloadInGeneral()) {
            return false;
        }
        if (receivedPackets.contains(1)) {
            if (!canCacheChunks && saveRadius >= 0) {
                int distanceX = chunk.xPosition - WDL.thePlayer.chunkCoordX;
                int distanceZ = chunk.zPosition - WDL.thePlayer.chunkCoordZ;
                if (Math.abs(distanceX) > saveRadius || Math.abs(distanceZ) > saveRadius) {
                    return false;
                }
            }
            return true;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static boolean canSaveEntities() {
        if (!WDLPluginChannels.canDownloadInGeneral()) {
            return false;
        }
        if (receivedPackets.contains(1)) {
            return canSaveEntities;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static boolean canSaveEntities(Chunk chunk) {
        if (WDLPluginChannels.isChunkOverridden(chunk)) {
            return true;
        }
        return WDLPluginChannels.canSaveEntities();
    }

    public static boolean canSaveEntities(int chunkX, int chunkZ) {
        if (WDLPluginChannels.isChunkOverridden(chunkX, chunkZ)) {
            return true;
        }
        return WDLPluginChannels.canSaveEntities();
    }

    public static boolean canSaveTileEntities() {
        if (!WDLPluginChannels.canDownloadInGeneral()) {
            return false;
        }
        if (receivedPackets.contains(1)) {
            return canSaveTileEntities;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static boolean canSaveTileEntities(Chunk chunk) {
        if (WDLPluginChannels.isChunkOverridden(chunk)) {
            return true;
        }
        return WDLPluginChannels.canSaveTileEntities();
    }

    public static boolean canSaveTileEntities(int chunkX, int chunkZ) {
        if (WDLPluginChannels.isChunkOverridden(chunkX, chunkZ)) {
            return true;
        }
        return WDLPluginChannels.canSaveTileEntities();
    }

    public static boolean canSaveContainers() {
        if (!WDLPluginChannels.canDownloadInGeneral()) {
            return false;
        }
        if (!WDLPluginChannels.canSaveTileEntities()) {
            return false;
        }
        if (receivedPackets.contains(1)) {
            return canSaveContainers;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static boolean canSaveContainers(Chunk chunk) {
        if (WDLPluginChannels.isChunkOverridden(chunk)) {
            return true;
        }
        return WDLPluginChannels.canSaveContainers();
    }

    public static boolean canSaveContainers(int chunkX, int chunkZ) {
        if (WDLPluginChannels.isChunkOverridden(chunkX, chunkZ)) {
            return true;
        }
        return WDLPluginChannels.canSaveContainers();
    }

    public static boolean canSaveMaps() {
        if (!WDLPluginChannels.canDownloadInGeneral()) {
            return false;
        }
        if (receivedPackets.contains(1)) {
            return canSaveTileEntities;
        }
        return WDLPluginChannels.canUseFunctionsUnknownToServer();
    }

    public static int getEntityRange(String entity) {
        if (!WDLPluginChannels.canSaveEntities(null)) {
            return -1;
        }
        if (receivedPackets.contains(2)) {
            if (entityRanges.containsKey(entity)) {
                return entityRanges.get(entity);
            }
            return -1;
        }
        return -1;
    }

    public static int getSaveRadius() {
        return saveRadius;
    }

    public static boolean canCacheChunks() {
        return canCacheChunks;
    }

    public static boolean hasServerEntityRange() {
        return receivedPackets.contains(2) && entityRanges.size() > 0;
    }

    public static Map<String, Integer> getEntityRanges() {
        return new HashMap<String, Integer>(entityRanges);
    }

    public static boolean hasPermissions() {
        return receivedPackets != null && !receivedPackets.isEmpty();
    }

    public static boolean canRequestPermissions() {
        return receivedPackets.contains(3) && canRequestPermissions;
    }

    public static String getRequestMessage() {
        if (receivedPackets.contains(3)) {
            return requestMessage;
        }
        return null;
    }

    public static boolean isChunkOverridden(Chunk chunk) {
        if (chunk == null) {
            return false;
        }
        return WDLPluginChannels.isChunkOverridden(chunk.xPosition, chunk.zPosition);
    }

    public static boolean isChunkOverridden(int x2, int z2) {
        for (Multimap<String, ChunkRange> map : chunkOverrides.values()) {
            for (ChunkRange range : map.values()) {
                if (x2 < range.x1 || x2 > range.x2 || z2 < range.z1 || z2 > range.z2) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean hasChunkOverrides() {
        if (!receivedPackets.contains(4)) {
            return false;
        }
        if (chunkOverrides == null || chunkOverrides.isEmpty()) {
            return false;
        }
        for (Multimap<String, ChunkRange> m2 : chunkOverrides.values()) {
            if (m2.isEmpty()) continue;
            return true;
        }
        return false;
    }

    public static Map<String, Multimap<String, ChunkRange>> getChunkOverrides() {
        HashMap<String, ImmutableMultimap<String, ChunkRange>> returned = new HashMap<String, ImmutableMultimap<String, ChunkRange>>();
        for (Map.Entry<String, Multimap<String, ChunkRange>> e2 : chunkOverrides.entrySet()) {
            ImmutableMultimap<String, ChunkRange> map = ImmutableMultimap.copyOf(e2.getValue());
            returned.put(e2.getKey(), map);
        }
        return ImmutableMap.copyOf(returned);
    }

    public static void addRequest(String key, String value) {
        if (!WDLPluginChannels.isValidRequest(key, value)) {
            return;
        }
        requests.put(key, value);
    }

    public static Map<String, String> getRequests() {
        return ImmutableMap.copyOf(requests);
    }

    public static boolean isValidRequest(String key, String value) {
        if (key == null || value == null) {
            return false;
        }
        if (BOOLEAN_REQUEST_FIELDS.contains(key)) {
            return value.equals("true") || value.equals("false");
        }
        if (INTEGER_REQUEST_FIELDS.contains(key)) {
            try {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e2) {
                return false;
            }
        }
        return false;
    }

    public static List<ChunkRange> getChunkOverrideRequests() {
        return ImmutableList.copyOf(chunkOverrideRequests);
    }

    public static void addChunkOverrideRequest(ChunkRange range) {
        chunkOverrideRequests.add(range);
    }

    public static void sendRequests() {
        if (requests.isEmpty() && chunkOverrideRequests.isEmpty()) {
            return;
        }
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("REQUEST REASON WILL GO HERE");
        output.writeInt(requests.size());
        for (Map.Entry<String, String> request : requests.entrySet()) {
            output.writeUTF(request.getKey());
            output.writeUTF(request.getValue());
        }
        output.writeInt(chunkOverrideRequests.size());
        for (ChunkRange range : chunkOverrideRequests) {
            range.writeToOutput(output);
        }
        PacketBuffer requestBuffer = new PacketBuffer(Unpooled.buffer());
        requestBuffer.writeBytes(output.toByteArray());
        C17PacketCustomPayload requestPacket = new C17PacketCustomPayload("WDL|REQUEST", requestBuffer);
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(requestPacket);
    }

    static void onWorldLoad() {
        Minecraft minecraft = Minecraft.getMinecraft();
        receivedPackets = new HashSet();
        requests = new HashMap<String, String>();
        chunkOverrideRequests = new ArrayList<ChunkRange>();
        canUseFunctionsUnknownToServer = true;
        WDLMessages.chatMessageTranslated(WDLMessageTypes.PLUGIN_CHANNEL_MESSAGE, "wdl.messages.permissions.init", new Object[0]);
        PacketBuffer registerPacketBuffer = new PacketBuffer(Unpooled.buffer());
        byte[] byArray = new byte[33];
        byArray[0] = 87;
        byArray[1] = 68;
        byArray[2] = 76;
        byArray[3] = 124;
        byArray[4] = 73;
        byArray[5] = 78;
        byArray[6] = 73;
        byArray[7] = 84;
        byArray[9] = 87;
        byArray[10] = 68;
        byArray[11] = 76;
        byArray[12] = 124;
        byArray[13] = 67;
        byArray[14] = 79;
        byArray[15] = 78;
        byArray[16] = 84;
        byArray[17] = 82;
        byArray[18] = 79;
        byArray[19] = 76;
        byArray[21] = 87;
        byArray[22] = 68;
        byArray[23] = 76;
        byArray[24] = 124;
        byArray[25] = 82;
        byArray[26] = 69;
        byArray[27] = 81;
        byArray[28] = 85;
        byArray[29] = 69;
        byArray[30] = 83;
        byArray[31] = 84;
        registerPacketBuffer.writeBytes(byArray);
        C17PacketCustomPayload registerPacket = new C17PacketCustomPayload("REGISTER", registerPacketBuffer);
        try {
            C17PacketCustomPayload initPacket = new C17PacketCustomPayload("WDL|INIT", new PacketBuffer(Unpooled.copiedBuffer("1.8.9a-beta2".getBytes("UTF-8"))));
        }
        catch (UnsupportedEncodingException e2) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.noUTF8", e2);
            C17PacketCustomPayload initPacket = new C17PacketCustomPayload("WDL|INIT", new PacketBuffer(Unpooled.buffer()));
        }
    }

    static void onPluginChannelPacket(String channel, byte[] bytes) {
    }

    public static class ChunkRange {
        public final String tag;
        public final int x1;
        public final int z1;
        public final int x2;
        public final int z2;

        public ChunkRange(String tag, int x1, int z1, int x2, int z2) {
            this.tag = tag;
            if (x1 > x2) {
                this.x1 = x2;
                this.x2 = x1;
            } else {
                this.x1 = x1;
                this.x2 = x2;
            }
            if (z1 > z2) {
                this.z1 = z2;
                this.z2 = z1;
            } else {
                this.z1 = z1;
                this.z2 = z2;
            }
        }

        public static ChunkRange readFromInput(ByteArrayDataInput input) {
            String tag = input.readUTF();
            int x1 = input.readInt();
            int z1 = input.readInt();
            int x2 = input.readInt();
            int z2 = input.readInt();
            return new ChunkRange(tag, x1, z1, x2, z2);
        }

        public void writeToOutput(ByteArrayDataOutput output) {
            output.writeUTF(this.tag);
            output.writeInt(this.x1);
            output.writeInt(this.z1);
            output.writeInt(this.x2);
        }

        public String toString() {
            return "ChunkRange [tag=" + this.tag + ", x1=" + this.x1 + ", z1=" + this.z1 + ", x2=" + this.x2 + ", z2=" + this.z2 + "]";
        }
    }
}

