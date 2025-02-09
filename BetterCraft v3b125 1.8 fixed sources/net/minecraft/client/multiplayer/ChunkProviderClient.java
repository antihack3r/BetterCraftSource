/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient
implements IChunkProvider {
    private static final Logger logger = LogManager.getLogger();
    private Chunk blankChunk;
    private LongHashMap<Chunk> chunkMapping = new LongHashMap();
    private List<Chunk> chunkListing = Lists.newArrayList();
    private World worldObj;

    public ChunkProviderClient(World worldIn) {
        this.blankChunk = new EmptyChunk(worldIn, 0, 0);
        this.worldObj = worldIn;
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return true;
    }

    public void unloadChunk(int x2, int z2) {
        Chunk chunk = this.provideChunk(x2, z2);
        if (!chunk.isEmpty()) {
            chunk.onChunkUnload();
        }
        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
        this.chunkListing.remove(chunk);
    }

    public Chunk loadChunk(int chunkX, int chunkZ) {
        Chunk chunk = new Chunk(this.worldObj, chunkX, chunkZ);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ), chunk);
        this.chunkListing.add(chunk);
        chunk.setChunkLoaded(true);
        return chunk;
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        Chunk chunk = this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
        return chunk == null ? this.blankChunk : chunk;
    }

    @Override
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        long i2 = System.currentTimeMillis();
        for (Chunk chunk : this.chunkListing) {
            chunk.func_150804_b(System.currentTimeMillis() - i2 > 5L);
        }
        if (System.currentTimeMillis() - i2 > 100L) {
            logger.info("Warning: Clientside chunk ticking took {} ms", System.currentTimeMillis() - i2);
        }
        return false;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
    }

    @Override
    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x2, int z2) {
        return false;
    }

    @Override
    public String makeString() {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return this.chunkListing.size();
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

