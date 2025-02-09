/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.chunk;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.jpountz.xxhash.XXHashFactory;
import net.labymod.api.EventManager;
import net.labymod.api.events.PluginMessageEvent;
import net.labymod.api.protocol.chunk.CCPChannelHandler;
import net.labymod.api.protocol.chunk.ChunkHashMap;
import net.labymod.api.protocol.chunk.Extracted;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.support.util.Debug;
import net.labymod.utils.Consumer;
import net.labymod.utils.GZIPCompression;
import net.labymod.utils.ModUtils;
import net.labymod.utils.ServerData;
import net.minecraft.network.PacketBuffer;

public class ChunkCachingProtocol
extends ChannelInboundHandlerAdapter
implements PluginMessageEvent,
Consumer<ServerData> {
    private static final boolean MC18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    public static final String PM_CHANNEL = "CCP";
    public static final int CCP_VERSION = 2;
    private boolean cachingSupported = false;
    private AtomicLong currentlyCachedBytes = new AtomicLong(0L);
    private long maxChunkCacheSize;
    private XXHashFactory factory = XXHashFactory.fastestInstance();
    private ChunkHashMap<Integer, Extracted> chunkCache = new ChunkHashMap();
    private long loadedBytesInSession = 0L;
    private long downloadedBytesInSession = 0L;

    public ChunkCachingProtocol() {
        EventManager eventManager = LabyMod.getInstance().getLabyModAPI().getEventManager();
        eventManager.register(this);
        eventManager.registerOnQuit(this);
    }

    @Override
    public void accept(ServerData accepted) {
        if (accepted != null && accepted.getIp() != null) {
            this.disable(accepted.getIp(), false);
        }
    }

    @Override
    public void receiveMessage(String channelName, PacketBuffer packetBuffer) {
        if (!channelName.equals(PM_CHANNEL) || !LabyMod.getSettings().chunkCaching) {
            return;
        }
        if (!this.cachingSupported) {
            ServerData serverData;
            this.cachingSupported = true;
            long size = LabyMod.getSettings().chunkCachingSize * 1000000;
            long maxSize = Runtime.getRuntime().maxMemory();
            if (size > maxSize) {
                size = maxSize;
            }
            this.maxChunkCacheSize = size;
            Debug.log(Debug.EnumDebugMode.CCP, "Chunk caching is supported by the server! Available cache size: " + ModUtils.humanReadableByteCount(this.maxChunkCacheSize, true, true));
            if (!MC18) {
                try {
                    Channel channel = LabyMod.getInstance().getNettyChannel();
                    ChannelPipeline pipeline = channel.pipeline();
                    if (pipeline.context("ccp") == null) {
                        pipeline.addBefore("decoder", "ccp", new CCPChannelHandler(this));
                    }
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
            if (LabyMod.getSettings().chunkCachingStoreInFile && (serverData = LabyMod.getInstance().getCurrentServerData()) != null && serverData.getIp() != null && serverData.getIp().length() > 2) {
                this.loadInCurrentCache(serverData.getIp());
            }
        }
        try {
            this.handleCCPMessageProtocol(packetBuffer);
        }
        catch (Exception error2) {
            error2.printStackTrace();
        }
    }

    private void handleCCPMessageProtocol(PacketBuffer packetBuffer) {
        boolean isBulk = packetBuffer.readBoolean();
        boolean flag = packetBuffer.readBoolean();
        int arraySize = packetBuffer.readShort();
        int[][] responseList = new int[arraySize][4];
        Extracted[] allChunks = new Extracted[arraySize];
        int cachedChunkCount = 0;
        int i2 = 0;
        while (i2 < arraySize) {
            int hash = packetBuffer.readInt();
            int chunkX = packetBuffer.readInt();
            int chunkZ = packetBuffer.readInt();
            Extracted extracted = (Extracted)this.chunkCache.get(hash);
            boolean isCached = extracted != null;
            this.chunkCache.renewEntry(hash);
            responseList[i2][0] = isCached ? 1 : 0;
            responseList[i2][1] = hash;
            responseList[i2][2] = chunkX;
            responseList[i2][3] = chunkZ;
            if (isBulk) {
                allChunks[i2] = extracted;
                if (isCached) {
                    ++cachedChunkCount;
                }
            } else if (isCached) {
                LabyModCore.getCoreAdapter().getProtocolAdapter().loadChunk(this, extracted, chunkX, chunkZ, flag);
                this.loadedBytesInSession += (long)extracted.decompressedLength;
            }
            ++i2;
        }
        if (isBulk && cachedChunkCount > 0) {
            this.loadBulkChunks(responseList, allChunks, cachedChunkCount, flag);
        }
        this.sendResponseToServer(responseList);
    }

    protected void onReceive112ChunkData(byte[] array) {
        int hash = this.factory.hash32().hash(array, 9, array.length - 9, -42421337);
        byte[] compressed = GZIPCompression.compress(array);
        long count = this.currentlyCachedBytes.addAndGet(compressed.length);
        Extracted extracted = new Extracted(0, compressed, array.length);
        this.chunkCache.put(hash, extracted);
        count += (long)compressed.length;
        while (count > this.maxChunkCacheSize) {
            count = this.currentlyCachedBytes.addAndGet(-this.chunkCache.removeEldestEntry().data.length);
        }
        this.downloadedBytesInSession += (long)array.length;
    }

    public void onReceive18ChunkData(int x2, int z2, int dataSize, byte[] array) {
        if (dataSize == 0 || !this.cachingSupported) {
            return;
        }
        int hash = this.factory.hash32().hash(array, 0, array.length, -42421337);
        byte[] compressed = GZIPCompression.compress(array);
        long count = this.currentlyCachedBytes.addAndGet(compressed.length);
        Extracted extracted = new Extracted((short)dataSize, compressed, array.length);
        this.chunkCache.put(hash, extracted);
        count += (long)compressed.length;
        while (count > this.maxChunkCacheSize) {
            Extracted eldest = this.chunkCache.removeEldestEntry();
            count = this.currentlyCachedBytes.addAndGet(-eldest.data.length);
        }
        this.downloadedBytesInSession += (long)array.length;
    }

    private void sendResponseToServer(int[][] responseList) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeByte(33);
        packetBuffer.writeShort(responseList.length);
        int[][] nArray = responseList;
        int n2 = responseList.length;
        int n3 = 0;
        while (n3 < n2) {
            int[] entry = nArray[n3];
            int isCached = entry[0];
            int x2 = entry[2];
            int z2 = entry[3];
            packetBuffer.writeByte(isCached);
            packetBuffer.writeInt(x2);
            packetBuffer.writeInt(z2);
            ++n3;
        }
        LabyModCore.getMinecraft().sendPluginMessage(PM_CHANNEL, packetBuffer);
    }

    private void loadBulkChunks(int[][] responseList, Extracted[] allChunks, int cachedChunkCount, boolean flag) {
        Extracted[] cachedChunks = new Extracted[cachedChunkCount];
        int[] chunkXArray = new int[cachedChunkCount];
        int[] chunkZArray = new int[cachedChunkCount];
        int t2 = 0;
        int i2 = 0;
        while (i2 < responseList.length) {
            if (responseList[i2][0] == 1 && allChunks[i2] != null) {
                Extracted extracted;
                chunkXArray[t2] = responseList[i2][2];
                chunkZArray[t2] = responseList[i2][3];
                cachedChunks[t2] = extracted = allChunks[i2];
                this.loadedBytesInSession += (long)extracted.decompressedLength;
                ++t2;
            }
            ++i2;
        }
        LabyModCore.getCoreAdapter().getProtocolAdapter().loadChunkBulk(this, cachedChunks, chunkXArray, chunkZArray);
    }

    public void disable(String serverKey, boolean worldSwitch) {
        if (worldSwitch) {
            this.cachingSupported = false;
            return;
        }
        if (!this.cachingSupported) {
            return;
        }
        Debug.log(Debug.EnumDebugMode.CCP, "Removed " + this.chunkCache.size() + " (" + ModUtils.humanReadableByteCount(this.currentlyCachedBytes.get(), true, true) + ") chunks in cache!");
        if (LabyMod.getSettings().chunkCachingStoreInFile) {
            try {
                this.saveCurrentCache(serverKey);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.chunkCache.clear();
        this.currentlyCachedBytes.set(0L);
        this.loadedBytesInSession = 0L;
        this.downloadedBytesInSession = 0L;
    }

    private void saveCurrentCache(String key) throws Exception {
        File cacheFile = new File("LabyMod/ccp/", key);
        if (!cacheFile.getParentFile().exists()) {
            cacheFile.getParentFile().mkdir();
        }
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(cacheFile));
        dos.writeInt(this.chunkCache.size());
        for (Map.Entry entry : this.chunkCache.entrySet()) {
            dos.writeInt((Integer)entry.getKey());
            Extracted extracted = (Extracted)entry.getValue();
            dos.writeShort(extracted.dataSize);
            dos.writeInt(extracted.decompressedLength);
            dos.writeInt(extracted.data.length);
            dos.write(extracted.data);
        }
        dos.writeLong(this.currentlyCachedBytes.get());
        dos.writeLong(this.loadedBytesInSession);
        dos.writeLong(this.downloadedBytesInSession);
        dos.flush();
        dos.close();
    }

    private void loadInCurrentCache(String key) {
        File cacheFile = new File("LabyMod/ccp/", key);
        if (!cacheFile.exists()) {
            return;
        }
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(cacheFile));
            int len = dis.readInt();
            int i2 = 0;
            while (i2 < len) {
                int hash = dis.readInt();
                short dataSize = dis.readShort();
                int decompressedLength = dis.readInt();
                byte[] data = new byte[dis.readInt()];
                dis.read(data);
                Extracted extracted = new Extracted(dataSize, data, decompressedLength);
                this.chunkCache.put(hash, extracted);
                ++i2;
            }
            this.currentlyCachedBytes.set(dis.readLong());
            this.loadedBytesInSession = dis.readLong();
            this.downloadedBytesInSession = dis.readLong();
            dis.close();
        }
        catch (Exception error) {
            error.printStackTrace();
            cacheFile.delete();
        }
    }

    public boolean isCachingSupported() {
        return this.cachingSupported;
    }

    public AtomicLong getCurrentlyCachedBytes() {
        return this.currentlyCachedBytes;
    }

    public ChunkHashMap<Integer, Extracted> getChunkCache() {
        return this.chunkCache;
    }

    public long getLoadedBytesInSession() {
        return this.loadedBytesInSession;
    }

    public long getDownloadedBytesInSession() {
        return this.downloadedBytesInSession;
    }
}

