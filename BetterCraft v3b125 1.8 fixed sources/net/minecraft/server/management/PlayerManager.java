/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkPosComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
    private static final Logger pmLogger = LogManager.getLogger();
    private final WorldServer theWorldServer;
    private final List<EntityPlayerMP> players = Lists.newArrayList();
    private final LongHashMap<PlayerInstance> playerInstances = new LongHashMap();
    private final List<PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
    private final List<PlayerInstance> playerInstanceList = Lists.newArrayList();
    private int playerViewRadius;
    private long previousTotalWorldTime;
    private final int[][] xzDirectionsConst;
    private final Map<EntityPlayerMP, Set<ChunkCoordIntPair>> mapPlayerPendingEntries;

    public PlayerManager(WorldServer serverWorld) {
        int[][] nArrayArray = new int[4][];
        int[] nArray = new int[2];
        nArray[0] = 1;
        nArrayArray[0] = nArray;
        int[] nArray2 = new int[2];
        nArray2[1] = 1;
        nArrayArray[1] = nArray2;
        int[] nArray3 = new int[2];
        nArray3[0] = -1;
        nArrayArray[2] = nArray3;
        int[] nArray4 = new int[2];
        nArray4[1] = -1;
        nArrayArray[3] = nArray4;
        this.xzDirectionsConst = nArrayArray;
        this.mapPlayerPendingEntries = new HashMap<EntityPlayerMP, Set<ChunkCoordIntPair>>();
        this.theWorldServer = serverWorld;
        this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
    }

    public WorldServer getWorldServer() {
        return this.theWorldServer;
    }

    public void updatePlayerInstances() {
        WorldProvider worldprovider;
        Set<Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>>> set = this.mapPlayerPendingEntries.entrySet();
        Iterator<Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntityPlayerMP, Set<ChunkCoordIntPair>> entry = iterator.next();
            Set<ChunkCoordIntPair> set1 = entry.getValue();
            if (set1.isEmpty()) continue;
            EntityPlayerMP entityplayermp = entry.getKey();
            if (entityplayermp.worldObj != this.theWorldServer) {
                iterator.remove();
                continue;
            }
            int i2 = this.playerViewRadius / 3 + 1;
            if (!Config.isLazyChunkLoading()) {
                i2 = this.playerViewRadius * 2 + 1;
            }
            for (ChunkCoordIntPair chunkcoordintpair : this.getNearest(set1, entityplayermp, i2)) {
                PlayerInstance playermanager$playerinstance = this.getPlayerInstance(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos, true);
                playermanager$playerinstance.addPlayer(entityplayermp);
                set1.remove(chunkcoordintpair);
            }
        }
        long j2 = this.theWorldServer.getTotalWorldTime();
        if (j2 - this.previousTotalWorldTime > 8000L) {
            this.previousTotalWorldTime = j2;
            int k2 = 0;
            while (k2 < this.playerInstanceList.size()) {
                PlayerInstance playermanager$playerinstance1 = this.playerInstanceList.get(k2);
                playermanager$playerinstance1.onUpdate();
                playermanager$playerinstance1.processChunk();
                ++k2;
            }
        } else {
            int l2 = 0;
            while (l2 < this.playerInstancesToUpdate.size()) {
                PlayerInstance playermanager$playerinstance2 = this.playerInstancesToUpdate.get(l2);
                playermanager$playerinstance2.onUpdate();
                ++l2;
            }
        }
        this.playerInstancesToUpdate.clear();
        if (this.players.isEmpty() && !(worldprovider = this.theWorldServer.provider).canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
        }
    }

    public boolean hasPlayerInstance(int chunkX, int chunkZ) {
        long i2 = (long)chunkX + Integer.MAX_VALUE | (long)chunkZ + Integer.MAX_VALUE << 32;
        return this.playerInstances.getValueByKey(i2) != null;
    }

    private PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent) {
        long i2 = (long)chunkX + Integer.MAX_VALUE | (long)chunkZ + Integer.MAX_VALUE << 32;
        PlayerInstance playermanager$playerinstance = this.playerInstances.getValueByKey(i2);
        if (playermanager$playerinstance == null && createIfAbsent) {
            playermanager$playerinstance = new PlayerInstance(chunkX, chunkZ);
            this.playerInstances.add(i2, playermanager$playerinstance);
            this.playerInstanceList.add(playermanager$playerinstance);
        }
        return playermanager$playerinstance;
    }

    public void markBlockForUpdate(BlockPos pos) {
        int j2;
        int i2 = pos.getX() >> 4;
        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(i2, j2 = pos.getZ() >> 4, false);
        if (playermanager$playerinstance != null) {
            playermanager$playerinstance.flagChunkForUpdate(pos.getX() & 0xF, pos.getY(), pos.getZ() & 0xF);
        }
    }

    public void addPlayer(EntityPlayerMP player) {
        int i2 = (int)player.posX >> 4;
        int j2 = (int)player.posZ >> 4;
        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;
        int k2 = Math.min(this.playerViewRadius, 8);
        int l2 = i2 - k2;
        int i1 = i2 + k2;
        int j1 = j2 - k2;
        int k1 = j2 + k2;
        Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);
        int l1 = i2 - this.playerViewRadius;
        while (l1 <= i2 + this.playerViewRadius) {
            int i22 = j2 - this.playerViewRadius;
            while (i22 <= j2 + this.playerViewRadius) {
                if (l1 >= l2 && l1 <= i1 && i22 >= j1 && i22 <= k1) {
                    this.getPlayerInstance(l1, i22, true).addPlayer(player);
                } else {
                    set.add(new ChunkCoordIntPair(l1, i22));
                }
                ++i22;
            }
            ++l1;
        }
        this.players.add(player);
        this.filterChunkLoadQueue(player);
    }

    public void filterChunkLoadQueue(EntityPlayerMP player) {
        ArrayList<ChunkCoordIntPair> list = Lists.newArrayList(player.loadedChunks);
        int i2 = 0;
        int j2 = this.playerViewRadius;
        int k2 = (int)player.posX >> 4;
        int l2 = (int)player.posZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.getPlayerInstance(k2, l2, true).chunkCoords;
        player.loadedChunks.clear();
        if (list.contains(chunkcoordintpair)) {
            player.loadedChunks.add(chunkcoordintpair);
        }
        int k1 = 1;
        while (k1 <= j2 * 2) {
            int l1 = 0;
            while (l1 < 2) {
                int[] aint = this.xzDirectionsConst[i2++ % 4];
                int i22 = 0;
                while (i22 < k1) {
                    chunkcoordintpair = this.getPlayerInstance(k2 + (i1 += aint[0]), l2 + (j1 += aint[1]), true).chunkCoords;
                    if (list.contains(chunkcoordintpair)) {
                        player.loadedChunks.add(chunkcoordintpair);
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        i2 %= 4;
        int j22 = 0;
        while (j22 < j2 * 2) {
            chunkcoordintpair = this.getPlayerInstance(k2 + (i1 += this.xzDirectionsConst[i2][0]), l2 + (j1 += this.xzDirectionsConst[i2][1]), true).chunkCoords;
            if (list.contains(chunkcoordintpair)) {
                player.loadedChunks.add(chunkcoordintpair);
            }
            ++j22;
        }
    }

    public void removePlayer(EntityPlayerMP player) {
        this.mapPlayerPendingEntries.remove(player);
        int i2 = (int)player.managedPosX >> 4;
        int j2 = (int)player.managedPosZ >> 4;
        int k2 = i2 - this.playerViewRadius;
        while (k2 <= i2 + this.playerViewRadius) {
            int l2 = j2 - this.playerViewRadius;
            while (l2 <= j2 + this.playerViewRadius) {
                PlayerInstance playermanager$playerinstance = this.getPlayerInstance(k2, l2, false);
                if (playermanager$playerinstance != null) {
                    playermanager$playerinstance.removePlayer(player);
                }
                ++l2;
            }
            ++k2;
        }
        this.players.remove(player);
    }

    private boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
        int i2 = x1 - x2;
        int j2 = z1 - z2;
        return i2 >= -radius && i2 <= radius ? j2 >= -radius && j2 <= radius : false;
    }

    public void updateMountedMovingPlayer(EntityPlayerMP player) {
        int i2 = (int)player.posX >> 4;
        int j2 = (int)player.posZ >> 4;
        double d0 = player.managedPosX - player.posX;
        double d1 = player.managedPosZ - player.posZ;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 >= 64.0) {
            int k2 = (int)player.managedPosX >> 4;
            int l2 = (int)player.managedPosZ >> 4;
            int i1 = this.playerViewRadius;
            int j1 = i2 - k2;
            int k1 = j2 - l2;
            if (j1 != 0 || k1 != 0) {
                Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);
                int l1 = i2 - i1;
                while (l1 <= i2 + i1) {
                    int i22 = j2 - i1;
                    while (i22 <= j2 + i1) {
                        if (!this.overlaps(l1, i22, k2, l2, i1)) {
                            if (Config.isLazyChunkLoading()) {
                                set.add(new ChunkCoordIntPair(l1, i22));
                            } else {
                                this.getPlayerInstance(l1, i22, true).addPlayer(player);
                            }
                        }
                        if (!this.overlaps(l1 - j1, i22 - k1, i2, j2, i1)) {
                            set.remove(new ChunkCoordIntPair(l1 - j1, i22 - k1));
                            PlayerInstance playermanager$playerinstance = this.getPlayerInstance(l1 - j1, i22 - k1, false);
                            if (playermanager$playerinstance != null) {
                                playermanager$playerinstance.removePlayer(player);
                            }
                        }
                        ++i22;
                    }
                    ++l1;
                }
                this.filterChunkLoadQueue(player);
                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ) {
        PlayerInstance playermanager$playerinstance = this.getPlayerInstance(chunkX, chunkZ, false);
        return playermanager$playerinstance != null && playermanager$playerinstance.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(playermanager$playerinstance.chunkCoords);
    }

    public void setPlayerViewRadius(int radius) {
        if ((radius = MathHelper.clamp_int(radius, 3, 64)) != this.playerViewRadius) {
            int i2 = radius - this.playerViewRadius;
            for (EntityPlayerMP entityplayermp : Lists.newArrayList(this.players)) {
                int j2 = (int)entityplayermp.posX >> 4;
                int k2 = (int)entityplayermp.posZ >> 4;
                Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(entityplayermp);
                if (i2 > 0) {
                    int j1 = j2 - radius;
                    while (j1 <= j2 + radius) {
                        int k1 = k2 - radius;
                        while (k1 <= k2 + radius) {
                            if (Config.isLazyChunkLoading()) {
                                set.add(new ChunkCoordIntPair(j1, k1));
                            } else {
                                PlayerInstance playermanager$playerinstance1 = this.getPlayerInstance(j1, k1, true);
                                if (!playermanager$playerinstance1.playersWatchingChunk.contains(entityplayermp)) {
                                    playermanager$playerinstance1.addPlayer(entityplayermp);
                                }
                            }
                            ++k1;
                        }
                        ++j1;
                    }
                    continue;
                }
                int l2 = j2 - this.playerViewRadius;
                while (l2 <= j2 + this.playerViewRadius) {
                    int i1 = k2 - this.playerViewRadius;
                    while (i1 <= k2 + this.playerViewRadius) {
                        if (!this.overlaps(l2, i1, j2, k2, radius)) {
                            set.remove(new ChunkCoordIntPair(l2, i1));
                            PlayerInstance playermanager$playerinstance = this.getPlayerInstance(l2, i1, true);
                            if (playermanager$playerinstance != null) {
                                playermanager$playerinstance.removePlayer(entityplayermp);
                            }
                        }
                        ++i1;
                    }
                    ++l2;
                }
            }
            this.playerViewRadius = radius;
        }
    }

    public static int getFurthestViewableBlock(int distance) {
        return distance * 16 - 16;
    }

    private PriorityQueue<ChunkCoordIntPair> getNearest(Set<ChunkCoordIntPair> p_getNearest_1_, EntityPlayerMP p_getNearest_2_, int p_getNearest_3_) {
        float f2 = p_getNearest_2_.rotationYaw + 90.0f;
        while (f2 <= -180.0f) {
            f2 += 360.0f;
        }
        while (f2 > 180.0f) {
            f2 -= 360.0f;
        }
        double d0 = (double)f2 * (Math.PI / 180);
        double d1 = p_getNearest_2_.rotationPitch;
        double d2 = d1 * (Math.PI / 180);
        ChunkPosComparator chunkposcomparator = new ChunkPosComparator(p_getNearest_2_.chunkCoordX, p_getNearest_2_.chunkCoordZ, d0, d2);
        Comparator<ChunkCoordIntPair> comparator = Collections.reverseOrder(chunkposcomparator);
        PriorityQueue<ChunkCoordIntPair> priorityqueue = new PriorityQueue<ChunkCoordIntPair>(p_getNearest_3_, comparator);
        for (ChunkCoordIntPair chunkcoordintpair : p_getNearest_1_) {
            if (priorityqueue.size() < p_getNearest_3_) {
                priorityqueue.add(chunkcoordintpair);
                continue;
            }
            ChunkCoordIntPair chunkcoordintpair1 = priorityqueue.peek();
            if (chunkposcomparator.compare(chunkcoordintpair, chunkcoordintpair1) >= 0) continue;
            priorityqueue.remove();
            priorityqueue.add(chunkcoordintpair);
        }
        return priorityqueue;
    }

    private Set<ChunkCoordIntPair> getPendingEntriesSafe(EntityPlayerMP p_getPendingEntriesSafe_1_) {
        Set<ChunkCoordIntPair> set = this.mapPlayerPendingEntries.get(p_getPendingEntriesSafe_1_);
        if (set != null) {
            return set;
        }
        int i2 = Math.min(this.playerViewRadius, 8);
        int j2 = this.playerViewRadius * 2 + 1;
        int k2 = i2 * 2 + 1;
        int l2 = j2 * j2 - k2 * k2;
        l2 = Math.max(l2, 16);
        HashSet<ChunkCoordIntPair> hashset = new HashSet<ChunkCoordIntPair>(l2);
        this.mapPlayerPendingEntries.put(p_getPendingEntriesSafe_1_, hashset);
        return hashset;
    }

    class PlayerInstance {
        private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
        private final ChunkCoordIntPair chunkCoords;
        private short[] locationOfBlockChange = new short[64];
        private int numBlocksToUpdate;
        private int flagsYAreasToUpdate;
        private long previousWorldTime;

        public PlayerInstance(int chunkX, int chunkZ) {
            this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP player) {
            if (this.playersWatchingChunk.contains(player)) {
                pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", player, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            } else {
                if (this.playersWatchingChunk.isEmpty()) {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }
                this.playersWatchingChunk.add(player);
                player.loadedChunks.add(this.chunkCoords);
            }
        }

        public void removePlayer(EntityPlayerMP player) {
            if (this.playersWatchingChunk.contains(player)) {
                Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                if (chunk.isPopulated()) {
                    player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0));
                }
                this.playersWatchingChunk.remove(player);
                player.loadedChunks.remove(this.chunkCoords);
                if (this.playersWatchingChunk.isEmpty()) {
                    long i2 = (long)this.chunkCoords.chunkXPos + Integer.MAX_VALUE | (long)this.chunkCoords.chunkZPos + Integer.MAX_VALUE << 32;
                    this.increaseInhabitedTime(chunk);
                    PlayerManager.this.playerInstances.remove(i2);
                    PlayerManager.this.playerInstanceList.remove(this);
                    if (this.numBlocksToUpdate > 0) {
                        PlayerManager.this.playerInstancesToUpdate.remove(this);
                    }
                    PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                }
            }
        }

        public void processChunk() {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
        }

        private void increaseInhabitedTime(Chunk theChunk) {
            theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int x2, int y2, int z2) {
            if (this.numBlocksToUpdate == 0) {
                PlayerManager.this.playerInstancesToUpdate.add(this);
            }
            this.flagsYAreasToUpdate |= 1 << (y2 >> 4);
            if (this.numBlocksToUpdate < 64) {
                short short1 = (short)(x2 << 12 | z2 << 8 | y2);
                int i2 = 0;
                while (i2 < this.numBlocksToUpdate) {
                    if (this.locationOfBlockChange[i2] == short1) {
                        return;
                    }
                    ++i2;
                }
                this.locationOfBlockChange[this.numBlocksToUpdate++] = short1;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet thePacket) {
            int i2 = 0;
            while (i2 < this.playersWatchingChunk.size()) {
                EntityPlayerMP entityplayermp = this.playersWatchingChunk.get(i2);
                if (!entityplayermp.loadedChunks.contains(this.chunkCoords)) {
                    entityplayermp.playerNetServerHandler.sendPacket(thePacket);
                }
                ++i2;
            }
        }

        public void onUpdate() {
            if (this.numBlocksToUpdate != 0) {
                if (this.numBlocksToUpdate == 1) {
                    int k1 = (this.locationOfBlockChange[0] >> 12 & 0xF) + this.chunkCoords.chunkXPos * 16;
                    int i2 = this.locationOfBlockChange[0] & 0xFF;
                    int k2 = (this.locationOfBlockChange[0] >> 8 & 0xF) + this.chunkCoords.chunkZPos * 16;
                    BlockPos blockpos = new BlockPos(k1, i2, k2);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, blockpos));
                    if (PlayerManager.this.theWorldServer.getBlockState(blockpos).getBlock().hasTileEntity()) {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockpos));
                    }
                } else if (this.numBlocksToUpdate != 64) {
                    this.sendToAllPlayersWatchingChunk(new S22PacketMultiBlockChange(this.numBlocksToUpdate, this.locationOfBlockChange, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)));
                    int j1 = 0;
                    while (j1 < this.numBlocksToUpdate) {
                        int l1 = (this.locationOfBlockChange[j1] >> 12 & 0xF) + this.chunkCoords.chunkXPos * 16;
                        int j2 = this.locationOfBlockChange[j1] & 0xFF;
                        int l2 = (this.locationOfBlockChange[j1] >> 8 & 0xF) + this.chunkCoords.chunkZPos * 16;
                        BlockPos blockpos1 = new BlockPos(l1, j2, l2);
                        if (PlayerManager.this.theWorldServer.getBlockState(blockpos1).getBlock().hasTileEntity()) {
                            this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockpos1));
                        }
                        ++j1;
                    }
                } else {
                    int i2 = this.chunkCoords.chunkXPos * 16;
                    int j2 = this.chunkCoords.chunkZPos * 16;
                    this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos), false, this.flagsYAreasToUpdate));
                    int k2 = 0;
                    while (k2 < 16) {
                        if ((this.flagsYAreasToUpdate & 1 << k2) != 0) {
                            int l2 = k2 << 4;
                            List<TileEntity> list = PlayerManager.this.theWorldServer.getTileEntitiesIn(i2, l2, j2, i2 + 16, l2 + 16, j2 + 16);
                            int i1 = 0;
                            while (i1 < list.size()) {
                                this.sendTileToAllPlayersWatchingChunk(list.get(i1));
                                ++i1;
                            }
                        }
                        ++k2;
                    }
                }
                this.numBlocksToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity) {
            Packet packet;
            if (theTileEntity != null && (packet = theTileEntity.getDescriptionPacket()) != null) {
                this.sendToAllPlayersWatchingChunk(packet);
            }
        }
    }
}

