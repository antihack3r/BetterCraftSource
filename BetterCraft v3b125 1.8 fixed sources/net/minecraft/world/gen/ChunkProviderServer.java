/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer
implements IChunkProvider {
    private static final Logger logger = LogManager.getLogger();
    private Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap());
    private Chunk dummyChunk;
    private IChunkProvider serverChunkGenerator;
    private IChunkLoader chunkLoader;
    public boolean chunkLoadOverride = true;
    private LongHashMap<Chunk> id2ChunkMap = new LongHashMap();
    private List<Chunk> loadedChunks = Lists.newArrayList();
    private WorldServer worldObj;

    public ChunkProviderServer(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_) {
        this.dummyChunk = new EmptyChunk(p_i1520_1_, 0, 0);
        this.worldObj = p_i1520_1_;
        this.chunkLoader = p_i1520_2_;
        this.serverChunkGenerator = p_i1520_3_;
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
    }

    public List<Chunk> func_152380_a() {
        return this.loadedChunks;
    }

    public void dropChunk(int x2, int z2) {
        if (this.worldObj.provider.canRespawnHere()) {
            if (!this.worldObj.isSpawnChunk(x2, z2)) {
                this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
            }
        } else {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
        }
    }

    public void unloadAllChunks() {
        for (Chunk chunk : this.loadedChunks) {
            this.dropChunk(chunk.xPosition, chunk.zPosition);
        }
    }

    public Chunk loadChunk(int chunkX, int chunkZ) {
        long i2 = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        this.droppedChunksSet.remove(i2);
        Chunk chunk = this.id2ChunkMap.getValueByKey(i2);
        if (chunk == null) {
            chunk = this.loadChunkFromFile(chunkX, chunkZ);
            if (chunk == null) {
                if (this.serverChunkGenerator == null) {
                    chunk = this.dummyChunk;
                } else {
                    try {
                        chunk = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
                    }
                    catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", chunkX, chunkZ));
                        crashreportcategory.addCrashSection("Position hash", i2);
                        crashreportcategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                        throw new ReportedException(crashreport);
                    }
                }
            }
            this.id2ChunkMap.add(i2, chunk);
            this.loadedChunks.add(chunk);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this, chunkX, chunkZ);
        }
        return chunk;
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        Chunk chunk = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x2, z2));
        return chunk == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x2, z2)) : chunk;
    }

    private Chunk loadChunkFromFile(int x2, int z2) {
        if (this.chunkLoader == null) {
            return null;
        }
        try {
            Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x2, z2);
            if (chunk != null) {
                chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
                if (this.serverChunkGenerator != null) {
                    this.serverChunkGenerator.recreateStructures(chunk, x2, z2);
                }
            }
            return chunk;
        }
        catch (Exception exception) {
            logger.error("Couldn't load chunk", (Throwable)exception);
            return null;
        }
    }

    private void saveChunkExtraData(Chunk chunkIn) {
        if (this.chunkLoader != null) {
            try {
                this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
            }
            catch (Exception exception) {
                logger.error("Couldn't save entities", (Throwable)exception);
            }
        }
    }

    private void saveChunkData(Chunk chunkIn) {
        if (this.chunkLoader != null) {
            try {
                chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
                this.chunkLoader.saveChunk(this.worldObj, chunkIn);
            }
            catch (IOException ioexception) {
                logger.error("Couldn't save chunk", (Throwable)ioexception);
            }
            catch (MinecraftException minecraftexception) {
                logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)minecraftexception);
            }
        }
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
        Chunk chunk = this.provideChunk(x2, z2);
        if (!chunk.isTerrainPopulated()) {
            chunk.func_150809_p();
            if (this.serverChunkGenerator != null) {
                this.serverChunkGenerator.populate(chunkProvider, x2, z2);
                chunk.setChunkModified();
            }
        }
    }

    @Override
    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x2, int z2) {
        if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x2, z2)) {
            Chunk chunk = this.provideChunk(x2, z2);
            chunk.setChunkModified();
            return true;
        }
        return false;
    }

    @Override
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
        int i2 = 0;
        ArrayList<Chunk> list = Lists.newArrayList(this.loadedChunks);
        int j2 = 0;
        while (j2 < list.size()) {
            Chunk chunk = (Chunk)list.get(j2);
            if (saveAllChunks) {
                this.saveChunkExtraData(chunk);
            }
            if (chunk.needsSaving(saveAllChunks)) {
                this.saveChunkData(chunk);
                chunk.setModified(false);
                if (++i2 == 24 && !saveAllChunks) {
                    return false;
                }
            }
            ++j2;
        }
        return true;
    }

    @Override
    public void saveExtraData() {
        if (this.chunkLoader != null) {
            this.chunkLoader.saveExtraData();
        }
    }

    @Override
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.disableLevelSaving) {
            int i2 = 0;
            while (i2 < 100) {
                if (!this.droppedChunksSet.isEmpty()) {
                    Long olong = this.droppedChunksSet.iterator().next();
                    Chunk chunk = this.id2ChunkMap.getValueByKey(olong);
                    if (chunk != null) {
                        chunk.onChunkUnload();
                        this.saveChunkData(chunk);
                        this.saveChunkExtraData(chunk);
                        this.id2ChunkMap.remove(olong);
                        this.loadedChunks.remove(chunk);
                    }
                    this.droppedChunksSet.remove(olong);
                }
                ++i2;
            }
            if (this.chunkLoader != null) {
                this.chunkLoader.chunkTick();
            }
        }
        return this.serverChunkGenerator.unloadQueuedChunks();
    }

    @Override
    public boolean canSave() {
        return !this.worldObj.disableLevelSaving;
    }

    @Override
    public String makeString() {
        return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
    }

    @Override
    public int getLoadedChunkCount() {
        return this.id2ChunkMap.getNumHashElements();
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

