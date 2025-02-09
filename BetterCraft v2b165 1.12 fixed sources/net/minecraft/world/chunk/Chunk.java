// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.chunk;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.IChunkGenerator;
import java.util.Random;
import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.crash.CrashReport;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldType;
import net.minecraft.world.EnumSkyBlock;
import java.util.Iterator;
import net.minecraft.util.EnumFacing;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import java.util.Arrays;
import com.google.common.collect.Queues;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.logging.log4j.Logger;

public class Chunk
{
    private static final Logger LOGGER;
    public static final ExtendedBlockStorage NULL_BLOCK_STORAGE;
    private final ExtendedBlockStorage[] storageArrays;
    private final byte[] blockBiomeArray;
    private final int[] precipitationHeightMap;
    private final boolean[] updateSkylightColumns;
    private boolean isChunkLoaded;
    private final World worldObj;
    private final int[] heightMap;
    public final int xPosition;
    public final int zPosition;
    private boolean isGapLightingUpdated;
    private final Map<BlockPos, TileEntity> chunkTileEntityMap;
    private final ClassInheritanceMultiMap<Entity>[] entityLists;
    private boolean isTerrainPopulated;
    private boolean isLightPopulated;
    private boolean chunkTicked;
    private boolean isModified;
    private boolean hasEntities;
    private long lastSaveTime;
    private int heightMapMinimum;
    private long inhabitedTime;
    private int queuedLightChecks;
    private final ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;
    public boolean unloaded;
    
    static {
        LOGGER = LogManager.getLogger();
        NULL_BLOCK_STORAGE = null;
    }
    
    public Chunk(final World worldIn, final int x, final int z) {
        this.storageArrays = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.precipitationHeightMap = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.chunkTileEntityMap = (Map<BlockPos, TileEntity>)Maps.newHashMap();
        this.queuedLightChecks = 4096;
        this.tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
        this.entityLists = new ClassInheritanceMultiMap[16];
        this.worldObj = worldIn;
        this.xPosition = x;
        this.zPosition = z;
        this.heightMap = new int[256];
        for (int i = 0; i < this.entityLists.length; ++i) {
            this.entityLists[i] = new ClassInheritanceMultiMap<Entity>(Entity.class);
        }
        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.blockBiomeArray, (byte)(-1));
    }
    
    public Chunk(final World worldIn, final ChunkPrimer primer, final int x, final int z) {
        this(worldIn, x, z);
        final int i = 256;
        final boolean flag = worldIn.provider.func_191066_m();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 256; ++l) {
                    final IBlockState iblockstate = primer.getBlockState(j, l, k);
                    if (iblockstate.getMaterial() != Material.AIR) {
                        final int i2 = l >> 4;
                        if (this.storageArrays[i2] == Chunk.NULL_BLOCK_STORAGE) {
                            this.storageArrays[i2] = new ExtendedBlockStorage(i2 << 4, flag);
                        }
                        this.storageArrays[i2].set(j, l & 0xF, k, iblockstate);
                    }
                }
            }
        }
    }
    
    public boolean isAtLocation(final int x, final int z) {
        return x == this.xPosition && z == this.zPosition;
    }
    
    public int getHeight(final BlockPos pos) {
        return this.getHeightValue(pos.getX() & 0xF, pos.getZ() & 0xF);
    }
    
    public int getHeightValue(final int x, final int z) {
        return this.heightMap[z << 4 | x];
    }
    
    @Nullable
    private ExtendedBlockStorage getLastExtendedBlockStorage() {
        for (int i = this.storageArrays.length - 1; i >= 0; --i) {
            if (this.storageArrays[i] != Chunk.NULL_BLOCK_STORAGE) {
                return this.storageArrays[i];
            }
        }
        return null;
    }
    
    public int getTopFilledSegment() {
        final ExtendedBlockStorage extendedblockstorage = this.getLastExtendedBlockStorage();
        return (extendedblockstorage == null) ? 0 : extendedblockstorage.getYLocation();
    }
    
    public ExtendedBlockStorage[] getBlockStorageArray() {
        return this.storageArrays;
    }
    
    protected void generateHeightMap() {
        final int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                this.precipitationHeightMap[j + (k << 4)] = -999;
                int l = i + 16;
                while (l > 0) {
                    final IBlockState iblockstate = this.getBlockState(j, l - 1, k);
                    if (iblockstate.getLightOpacity() != 0) {
                        if ((this.heightMap[k << 4 | j] = l) < this.heightMapMinimum) {
                            this.heightMapMinimum = l;
                            break;
                        }
                        break;
                    }
                    else {
                        --l;
                    }
                }
            }
        }
        this.isModified = true;
    }
    
    public void generateSkylightMap() {
        final int i = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                this.precipitationHeightMap[j + (k << 4)] = -999;
                int l = i + 16;
                while (l > 0) {
                    if (this.getBlockLightOpacity(j, l - 1, k) != 0) {
                        if ((this.heightMap[k << 4 | j] = l) < this.heightMapMinimum) {
                            this.heightMapMinimum = l;
                            break;
                        }
                        break;
                    }
                    else {
                        --l;
                    }
                }
                if (this.worldObj.provider.func_191066_m()) {
                    int k2 = 15;
                    int i2 = i + 16 - 1;
                    do {
                        int j2 = this.getBlockLightOpacity(j, i2, k);
                        if (j2 == 0 && k2 != 15) {
                            j2 = 1;
                        }
                        k2 -= j2;
                        if (k2 > 0) {
                            final ExtendedBlockStorage extendedblockstorage = this.storageArrays[i2 >> 4];
                            if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
                                continue;
                            }
                            extendedblockstorage.setExtSkylightValue(j, i2 & 0xF, k, k2);
                            this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + j, i2, (this.zPosition << 4) + k));
                        }
                    } while (--i2 > 0 && k2 > 0);
                }
            }
        }
        this.isModified = true;
    }
    
    private void propagateSkylightOcclusion(final int x, final int z) {
        this.updateSkylightColumns[x + z * 16] = true;
        this.isGapLightingUpdated = true;
    }
    
    private void recheckGaps(final boolean p_150803_1_) {
        this.worldObj.theProfiler.startSection("recheckGaps");
        if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.updateSkylightColumns[i + j * 16]) {
                        this.updateSkylightColumns[i + j * 16] = false;
                        final int k = this.getHeightValue(i, j);
                        final int l = this.xPosition * 16 + i;
                        final int i2 = this.zPosition * 16 + j;
                        int j2 = Integer.MAX_VALUE;
                        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                            j2 = Math.min(j2, this.worldObj.getChunksLowestHorizon(l + enumfacing.getFrontOffsetX(), i2 + enumfacing.getFrontOffsetZ()));
                        }
                        this.checkSkylightNeighborHeight(l, i2, j2);
                        for (final EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                            this.checkSkylightNeighborHeight(l + enumfacing2.getFrontOffsetX(), i2 + enumfacing2.getFrontOffsetZ(), k);
                        }
                        if (p_150803_1_) {
                            this.worldObj.theProfiler.endSection();
                            return;
                        }
                    }
                }
            }
            this.isGapLightingUpdated = false;
        }
        this.worldObj.theProfiler.endSection();
    }
    
    private void checkSkylightNeighborHeight(final int x, final int z, final int maxValue) {
        final int i = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();
        if (i > maxValue) {
            this.updateSkylightNeighborHeight(x, z, maxValue, i + 1);
        }
        else if (i < maxValue) {
            this.updateSkylightNeighborHeight(x, z, i, maxValue + 1);
        }
    }
    
    private void updateSkylightNeighborHeight(final int x, final int z, final int startY, final int endY) {
        if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16)) {
            for (int i = startY; i < endY; ++i) {
                this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, i, z));
            }
            this.isModified = true;
        }
    }
    
    private void relightBlock(final int x, final int y, final int z) {
        int j;
        final int i = j = (this.heightMap[z << 4 | x] & 0xFF);
        if (y > i) {
            j = y;
        }
        while (j > 0 && this.getBlockLightOpacity(x, j - 1, z) == 0) {
            --j;
        }
        if (j != i) {
            this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, j, i);
            this.heightMap[z << 4 | x] = j;
            final int k = this.xPosition * 16 + x;
            final int l = this.zPosition * 16 + z;
            if (this.worldObj.provider.func_191066_m()) {
                if (j < i) {
                    for (int j2 = j; j2 < i; ++j2) {
                        final ExtendedBlockStorage extendedblockstorage2 = this.storageArrays[j2 >> 4];
                        if (extendedblockstorage2 != Chunk.NULL_BLOCK_STORAGE) {
                            extendedblockstorage2.setExtSkylightValue(x, j2 & 0xF, z, 15);
                            this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, j2, (this.zPosition << 4) + z));
                        }
                    }
                }
                else {
                    for (int i2 = i; i2 < j; ++i2) {
                        final ExtendedBlockStorage extendedblockstorage3 = this.storageArrays[i2 >> 4];
                        if (extendedblockstorage3 != Chunk.NULL_BLOCK_STORAGE) {
                            extendedblockstorage3.setExtSkylightValue(x, i2 & 0xF, z, 0);
                            this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, i2, (this.zPosition << 4) + z));
                        }
                    }
                }
                int k2 = 15;
                while (j > 0 && k2 > 0) {
                    --j;
                    int i3 = this.getBlockLightOpacity(x, j, z);
                    if (i3 == 0) {
                        i3 = 1;
                    }
                    k2 -= i3;
                    if (k2 < 0) {
                        k2 = 0;
                    }
                    final ExtendedBlockStorage extendedblockstorage4 = this.storageArrays[j >> 4];
                    if (extendedblockstorage4 != Chunk.NULL_BLOCK_STORAGE) {
                        extendedblockstorage4.setExtSkylightValue(x, j & 0xF, z, k2);
                    }
                }
            }
            final int l2 = this.heightMap[z << 4 | x];
            int k3;
            int j3;
            if ((k3 = l2) < (j3 = i)) {
                j3 = l2;
                k3 = i;
            }
            if (l2 < this.heightMapMinimum) {
                this.heightMapMinimum = l2;
            }
            if (this.worldObj.provider.func_191066_m()) {
                for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    this.updateSkylightNeighborHeight(k + enumfacing.getFrontOffsetX(), l + enumfacing.getFrontOffsetZ(), j3, k3);
                }
                this.updateSkylightNeighborHeight(k, l, j3, k3);
            }
            this.isModified = true;
        }
    }
    
    public int getBlockLightOpacity(final BlockPos pos) {
        return this.getBlockState(pos).getLightOpacity();
    }
    
    private int getBlockLightOpacity(final int x, final int y, final int z) {
        return this.getBlockState(x, y, z).getLightOpacity();
    }
    
    public IBlockState getBlockState(final BlockPos pos) {
        return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public IBlockState getBlockState(final int x, final int y, final int z) {
        if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD) {
            IBlockState iblockstate = null;
            if (y == 60) {
                iblockstate = Blocks.BARRIER.getDefaultState();
            }
            if (y == 70) {
                iblockstate = ChunkGeneratorDebug.getBlockStateFor(x, z);
            }
            return (iblockstate == null) ? Blocks.AIR.getDefaultState() : iblockstate;
        }
        try {
            if (y >= 0 && y >> 4 < this.storageArrays.length) {
                final ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];
                if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE) {
                    return extendedblockstorage.get(x & 0xF, y & 0xF, z & 0xF);
                }
            }
            return Blocks.AIR.getDefaultState();
        }
        catch (final Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.setDetail("Location", new ICrashReportDetail<String>() {
                @Override
                public String call() throws Exception {
                    return CrashReportCategory.getCoordinateInfo(x, y, z);
                }
            });
            throw new ReportedException(crashreport);
        }
    }
    
    @Nullable
    public IBlockState setBlockState(final BlockPos pos, final IBlockState state) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getY();
        final int k = pos.getZ() & 0xF;
        final int l = k << 4 | i;
        if (j >= this.precipitationHeightMap[l] - 1) {
            this.precipitationHeightMap[l] = -999;
        }
        final int i2 = this.heightMap[l];
        final IBlockState iblockstate = this.getBlockState(pos);
        if (iblockstate == state) {
            return null;
        }
        final Block block = state.getBlock();
        final Block block2 = iblockstate.getBlock();
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        boolean flag = false;
        if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
            if (block == Blocks.AIR) {
                return null;
            }
            extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.worldObj.provider.func_191066_m());
            this.storageArrays[j >> 4] = extendedblockstorage;
            flag = (j >= i2);
        }
        extendedblockstorage.set(i, j & 0xF, k, state);
        if (block2 != block) {
            if (!this.worldObj.isRemote) {
                block2.breakBlock(this.worldObj, pos, iblockstate);
            }
            else if (block2 instanceof ITileEntityProvider) {
                this.worldObj.removeTileEntity(pos);
            }
        }
        if (extendedblockstorage.get(i, j & 0xF, k).getBlock() != block) {
            return null;
        }
        if (flag) {
            this.generateSkylightMap();
        }
        else {
            final int j2 = state.getLightOpacity();
            final int k2 = iblockstate.getLightOpacity();
            if (j2 > 0) {
                if (j >= i2) {
                    this.relightBlock(i, j + 1, k);
                }
            }
            else if (j == i2 - 1) {
                this.relightBlock(i, j, k);
            }
            if (j2 != k2 && (j2 < k2 || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0)) {
                this.propagateSkylightOcclusion(i, k);
            }
        }
        if (block2 instanceof ITileEntityProvider) {
            final TileEntity tileentity = this.getTileEntity(pos, EnumCreateEntityType.CHECK);
            if (tileentity != null) {
                tileentity.updateContainingBlockInfo();
            }
        }
        if (!this.worldObj.isRemote && block2 != block) {
            block.onBlockAdded(this.worldObj, pos, state);
        }
        if (block instanceof ITileEntityProvider) {
            TileEntity tileentity2 = this.getTileEntity(pos, EnumCreateEntityType.CHECK);
            if (tileentity2 == null) {
                tileentity2 = ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, block.getMetaFromState(state));
                this.worldObj.setTileEntity(pos, tileentity2);
            }
            if (tileentity2 != null) {
                tileentity2.updateContainingBlockInfo();
            }
        }
        this.isModified = true;
        return iblockstate;
    }
    
    public int getLightFor(final EnumSkyBlock p_177413_1_, final BlockPos pos) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getY();
        final int k = pos.getZ() & 0xF;
        final ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
            return this.canSeeSky(pos) ? p_177413_1_.defaultLightValue : 0;
        }
        if (p_177413_1_ == EnumSkyBlock.SKY) {
            return this.worldObj.provider.func_191066_m() ? extendedblockstorage.getExtSkylightValue(i, j & 0xF, k) : 0;
        }
        return (p_177413_1_ == EnumSkyBlock.BLOCK) ? extendedblockstorage.getExtBlocklightValue(i, j & 0xF, k) : p_177413_1_.defaultLightValue;
    }
    
    public void setLightFor(final EnumSkyBlock p_177431_1_, final BlockPos pos, final int value) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getY();
        final int k = pos.getZ() & 0xF;
        ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
            extendedblockstorage = new ExtendedBlockStorage(j >> 4 << 4, this.worldObj.provider.func_191066_m());
            this.storageArrays[j >> 4] = extendedblockstorage;
            this.generateSkylightMap();
        }
        this.isModified = true;
        if (p_177431_1_ == EnumSkyBlock.SKY) {
            if (this.worldObj.provider.func_191066_m()) {
                extendedblockstorage.setExtSkylightValue(i, j & 0xF, k, value);
            }
        }
        else if (p_177431_1_ == EnumSkyBlock.BLOCK) {
            extendedblockstorage.setExtBlocklightValue(i, j & 0xF, k, value);
        }
    }
    
    public int getLightSubtracted(final BlockPos pos, final int amount) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getY();
        final int k = pos.getZ() & 0xF;
        final ExtendedBlockStorage extendedblockstorage = this.storageArrays[j >> 4];
        if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
            return (this.worldObj.provider.func_191066_m() && amount < EnumSkyBlock.SKY.defaultLightValue) ? (EnumSkyBlock.SKY.defaultLightValue - amount) : 0;
        }
        int l = this.worldObj.provider.func_191066_m() ? extendedblockstorage.getExtSkylightValue(i, j & 0xF, k) : 0;
        l -= amount;
        final int i2 = extendedblockstorage.getExtBlocklightValue(i, j & 0xF, k);
        if (i2 > l) {
            l = i2;
        }
        return l;
    }
    
    public void addEntity(final Entity entityIn) {
        this.hasEntities = true;
        final int i = MathHelper.floor(entityIn.posX / 16.0);
        final int j = MathHelper.floor(entityIn.posZ / 16.0);
        if (i != this.xPosition || j != this.zPosition) {
            Chunk.LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", (Object)i, j, this.xPosition, this.zPosition, entityIn);
            entityIn.setDead();
        }
        int k = MathHelper.floor(entityIn.posY / 16.0);
        if (k < 0) {
            k = 0;
        }
        if (k >= this.entityLists.length) {
            k = this.entityLists.length - 1;
        }
        entityIn.addedToChunk = true;
        entityIn.chunkCoordX = this.xPosition;
        entityIn.chunkCoordY = k;
        entityIn.chunkCoordZ = this.zPosition;
        this.entityLists[k].add(entityIn);
    }
    
    public void removeEntity(final Entity entityIn) {
        this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
    }
    
    public void removeEntityAtIndex(final Entity entityIn, int index) {
        if (index < 0) {
            index = 0;
        }
        if (index >= this.entityLists.length) {
            index = this.entityLists.length - 1;
        }
        this.entityLists[index].remove(entityIn);
    }
    
    public boolean canSeeSky(final BlockPos pos) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getY();
        final int k = pos.getZ() & 0xF;
        return j >= this.heightMap[k << 4 | i];
    }
    
    @Nullable
    private TileEntity createNewTileEntity(final BlockPos pos) {
        final IBlockState iblockstate = this.getBlockState(pos);
        final Block block = iblockstate.getBlock();
        return block.hasTileEntity() ? ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, iblockstate.getBlock().getMetaFromState(iblockstate)) : null;
    }
    
    @Nullable
    public TileEntity getTileEntity(final BlockPos pos, final EnumCreateEntityType p_177424_2_) {
        TileEntity tileentity = this.chunkTileEntityMap.get(pos);
        if (tileentity == null) {
            if (p_177424_2_ == EnumCreateEntityType.IMMEDIATE) {
                tileentity = this.createNewTileEntity(pos);
                this.worldObj.setTileEntity(pos, tileentity);
            }
            else if (p_177424_2_ == EnumCreateEntityType.QUEUED) {
                this.tileEntityPosQueue.add(pos);
            }
        }
        else if (tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(pos);
            return null;
        }
        return tileentity;
    }
    
    public void addTileEntity(final TileEntity tileEntityIn) {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
        if (this.isChunkLoaded) {
            this.worldObj.addTileEntity(tileEntityIn);
        }
    }
    
    public void addTileEntity(final BlockPos pos, final TileEntity tileEntityIn) {
        tileEntityIn.setWorldObj(this.worldObj);
        tileEntityIn.setPos(pos);
        if (this.getBlockState(pos).getBlock() instanceof ITileEntityProvider) {
            if (this.chunkTileEntityMap.containsKey(pos)) {
                this.chunkTileEntityMap.get(pos).invalidate();
            }
            tileEntityIn.validate();
            this.chunkTileEntityMap.put(pos, tileEntityIn);
        }
    }
    
    public void removeTileEntity(final BlockPos pos) {
        if (this.isChunkLoaded) {
            final TileEntity tileentity = this.chunkTileEntityMap.remove(pos);
            if (tileentity != null) {
                tileentity.invalidate();
            }
        }
    }
    
    public void onChunkLoad() {
        this.isChunkLoaded = true;
        this.worldObj.addTileEntities(this.chunkTileEntityMap.values());
        ClassInheritanceMultiMap<Entity>[] entityLists;
        for (int length = (entityLists = this.entityLists).length, i = 0; i < length; ++i) {
            final ClassInheritanceMultiMap<Entity> classinheritancemultimap = entityLists[i];
            this.worldObj.loadEntities(classinheritancemultimap);
        }
    }
    
    public void onChunkUnload() {
        this.isChunkLoaded = false;
        for (final TileEntity tileentity : this.chunkTileEntityMap.values()) {
            this.worldObj.markTileEntityForRemoval(tileentity);
        }
        ClassInheritanceMultiMap<Entity>[] entityLists;
        for (int length = (entityLists = this.entityLists).length, i = 0; i < length; ++i) {
            final ClassInheritanceMultiMap<Entity> classinheritancemultimap = entityLists[i];
            this.worldObj.unloadEntities(classinheritancemultimap);
        }
    }
    
    public void setChunkModified() {
        this.isModified = true;
    }
    
    public void getEntitiesWithinAABBForEntity(@Nullable final Entity entityIn, final AxisAlignedBB aabb, final List<Entity> listToFill, final Predicate<? super Entity> p_177414_4_) {
        int i = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);
        for (int k = i; k <= j; ++k) {
            if (!this.entityLists[k].isEmpty()) {
                for (final Entity entity : this.entityLists[k]) {
                    if (entity.getEntityBoundingBox().intersectsWith(aabb) && entity != entityIn) {
                        if (p_177414_4_ == null || p_177414_4_.apply(entity)) {
                            listToFill.add(entity);
                        }
                        final Entity[] aentity = entity.getParts();
                        if (aentity == null) {
                            continue;
                        }
                        Entity[] array;
                        for (int length = (array = aentity).length, l = 0; l < length; ++l) {
                            final Entity entity2 = array[l];
                            if (entity2 != entityIn && entity2.getEntityBoundingBox().intersectsWith(aabb) && (p_177414_4_ == null || p_177414_4_.apply(entity2))) {
                                listToFill.add(entity2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(final Class<? extends T> entityClass, final AxisAlignedBB aabb, final List<T> listToFill, final Predicate<? super T> filter) {
        int i = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);
        for (int k = i; k <= j; ++k) {
            for (final T t : this.entityLists[k].getByClass(entityClass)) {
                if (t.getEntityBoundingBox().intersectsWith(aabb) && (filter == null || filter.apply(t))) {
                    listToFill.add(t);
                }
            }
        }
    }
    
    public boolean needsSaving(final boolean p_76601_1_) {
        if (p_76601_1_) {
            if ((this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime) || this.isModified) {
                return true;
            }
        }
        else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
            return true;
        }
        return this.isModified;
    }
    
    public Random getRandomWithSeed(final long seed) {
        return new Random(this.worldObj.getSeed() + this.xPosition * this.xPosition * 4987142 + this.xPosition * 5947611 + this.zPosition * this.zPosition * 4392871L + this.zPosition * 389711 ^ seed);
    }
    
    public boolean isEmpty() {
        return false;
    }
    
    public void populateChunk(final IChunkProvider chunkProvider, final IChunkGenerator chunkGenrator) {
        final Chunk chunk = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition - 1);
        final Chunk chunk2 = chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition);
        final Chunk chunk3 = chunkProvider.getLoadedChunk(this.xPosition, this.zPosition + 1);
        final Chunk chunk4 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition);
        if (chunk2 != null && chunk3 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition + 1) != null) {
            this.populateChunk(chunkGenrator);
        }
        if (chunk4 != null && chunk3 != null && chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition + 1) != null) {
            chunk4.populateChunk(chunkGenrator);
        }
        if (chunk != null && chunk2 != null && chunkProvider.getLoadedChunk(this.xPosition + 1, this.zPosition - 1) != null) {
            chunk.populateChunk(chunkGenrator);
        }
        if (chunk != null && chunk4 != null) {
            final Chunk chunk5 = chunkProvider.getLoadedChunk(this.xPosition - 1, this.zPosition - 1);
            if (chunk5 != null) {
                chunk5.populateChunk(chunkGenrator);
            }
        }
    }
    
    protected void populateChunk(final IChunkGenerator generator) {
        if (this.isTerrainPopulated()) {
            if (generator.generateStructures(this, this.xPosition, this.zPosition)) {
                this.setChunkModified();
            }
        }
        else {
            this.checkLight();
            generator.populate(this.xPosition, this.zPosition);
            this.setChunkModified();
        }
    }
    
    public BlockPos getPrecipitationHeight(final BlockPos pos) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getZ() & 0xF;
        final int k = i | j << 4;
        BlockPos blockpos = new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
        if (blockpos.getY() == -999) {
            final int l = this.getTopFilledSegment() + 15;
            blockpos = new BlockPos(pos.getX(), l, pos.getZ());
            int i2 = -1;
            while (blockpos.getY() > 0 && i2 == -1) {
                final IBlockState iblockstate = this.getBlockState(blockpos);
                final Material material = iblockstate.getMaterial();
                if (!material.blocksMovement() && !material.isLiquid()) {
                    blockpos = blockpos.down();
                }
                else {
                    i2 = blockpos.getY() + 1;
                }
            }
            this.precipitationHeightMap[k] = i2;
        }
        return new BlockPos(pos.getX(), this.precipitationHeightMap[k], pos.getZ());
    }
    
    public void onTick(final boolean p_150804_1_) {
        if (this.isGapLightingUpdated && this.worldObj.provider.func_191066_m() && !p_150804_1_) {
            this.recheckGaps(this.worldObj.isRemote);
        }
        this.chunkTicked = true;
        if (!this.isLightPopulated && this.isTerrainPopulated) {
            this.checkLight();
        }
        while (!this.tileEntityPosQueue.isEmpty()) {
            final BlockPos blockpos = this.tileEntityPosQueue.poll();
            if (this.getTileEntity(blockpos, EnumCreateEntityType.CHECK) == null && this.getBlockState(blockpos).getBlock().hasTileEntity()) {
                final TileEntity tileentity = this.createNewTileEntity(blockpos);
                this.worldObj.setTileEntity(blockpos, tileentity);
                this.worldObj.markBlockRangeForRenderUpdate(blockpos, blockpos);
            }
        }
    }
    
    public boolean isPopulated() {
        return this.chunkTicked && this.isTerrainPopulated && this.isLightPopulated;
    }
    
    public boolean isChunkTicked() {
        return this.chunkTicked;
    }
    
    public ChunkPos getChunkCoordIntPair() {
        return new ChunkPos(this.xPosition, this.zPosition);
    }
    
    public boolean getAreLevelsEmpty(int startY, int endY) {
        if (startY < 0) {
            startY = 0;
        }
        if (endY >= 256) {
            endY = 255;
        }
        for (int i = startY; i <= endY; i += 16) {
            final ExtendedBlockStorage extendedblockstorage = this.storageArrays[i >> 4];
            if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && !extendedblockstorage.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public void setStorageArrays(final ExtendedBlockStorage[] newStorageArrays) {
        if (this.storageArrays.length != newStorageArrays.length) {
            Chunk.LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)newStorageArrays.length, this.storageArrays.length);
        }
        else {
            System.arraycopy(newStorageArrays, 0, this.storageArrays, 0, this.storageArrays.length);
        }
    }
    
    public void fillChunk(final PacketBuffer buf, final int p_186033_2_, final boolean p_186033_3_) {
        final boolean flag = this.worldObj.provider.func_191066_m();
        for (int i = 0; i < this.storageArrays.length; ++i) {
            ExtendedBlockStorage extendedblockstorage = this.storageArrays[i];
            if ((p_186033_2_ & 1 << i) == 0x0) {
                if (p_186033_3_ && extendedblockstorage != Chunk.NULL_BLOCK_STORAGE) {
                    this.storageArrays[i] = Chunk.NULL_BLOCK_STORAGE;
                }
            }
            else {
                if (extendedblockstorage == Chunk.NULL_BLOCK_STORAGE) {
                    extendedblockstorage = new ExtendedBlockStorage(i << 4, flag);
                    this.storageArrays[i] = extendedblockstorage;
                }
                extendedblockstorage.getData().read(buf);
                buf.readBytes(extendedblockstorage.getBlocklightArray().getData());
                if (flag) {
                    buf.readBytes(extendedblockstorage.getSkylightArray().getData());
                }
            }
        }
        if (p_186033_3_) {
            buf.readBytes(this.blockBiomeArray);
        }
        for (int j = 0; j < this.storageArrays.length; ++j) {
            if (this.storageArrays[j] != Chunk.NULL_BLOCK_STORAGE && (p_186033_2_ & 1 << j) != 0x0) {
                this.storageArrays[j].removeInvalidBlocks();
            }
        }
        this.isLightPopulated = true;
        this.isTerrainPopulated = true;
        this.generateHeightMap();
        for (final TileEntity tileentity : this.chunkTileEntityMap.values()) {
            tileentity.updateContainingBlockInfo();
        }
    }
    
    public Biome getBiome(final BlockPos pos, final BiomeProvider provider) {
        final int i = pos.getX() & 0xF;
        final int j = pos.getZ() & 0xF;
        int k = this.blockBiomeArray[j << 4 | i] & 0xFF;
        if (k == 255) {
            final Biome biome = provider.getBiome(pos, Biomes.PLAINS);
            k = Biome.getIdForBiome(biome);
            this.blockBiomeArray[j << 4 | i] = (byte)(k & 0xFF);
        }
        final Biome biome2 = Biome.getBiome(k);
        return (biome2 == null) ? Biomes.PLAINS : biome2;
    }
    
    public byte[] getBiomeArray() {
        return this.blockBiomeArray;
    }
    
    public void setBiomeArray(final byte[] biomeArray) {
        if (this.blockBiomeArray.length != biomeArray.length) {
            Chunk.LOGGER.warn("Could not set level chunk biomes, array length is {} instead of {}", (Object)biomeArray.length, this.blockBiomeArray.length);
        }
        else {
            System.arraycopy(biomeArray, 0, this.blockBiomeArray, 0, this.blockBiomeArray.length);
        }
    }
    
    public void resetRelightChecks() {
        this.queuedLightChecks = 0;
    }
    
    public void enqueueRelightChecks() {
        if (this.queuedLightChecks < 4096) {
            final BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
            for (int i = 0; i < 8; ++i) {
                if (this.queuedLightChecks >= 4096) {
                    return;
                }
                final int j = this.queuedLightChecks % 16;
                final int k = this.queuedLightChecks / 16 % 16;
                final int l = this.queuedLightChecks / 256;
                ++this.queuedLightChecks;
                for (int i2 = 0; i2 < 16; ++i2) {
                    final BlockPos blockpos2 = blockpos.add(k, (j << 4) + i2, l);
                    final boolean flag = i2 == 0 || i2 == 15 || k == 0 || k == 15 || l == 0 || l == 15;
                    if ((this.storageArrays[j] == Chunk.NULL_BLOCK_STORAGE && flag) || (this.storageArrays[j] != Chunk.NULL_BLOCK_STORAGE && this.storageArrays[j].get(k, i2, l).getMaterial() == Material.AIR)) {
                        EnumFacing[] values;
                        for (int length = (values = EnumFacing.values()).length, n = 0; n < length; ++n) {
                            final EnumFacing enumfacing = values[n];
                            final BlockPos blockpos3 = blockpos2.offset(enumfacing);
                            if (this.worldObj.getBlockState(blockpos3).getLightValue() > 0) {
                                this.worldObj.checkLight(blockpos3);
                            }
                        }
                        this.worldObj.checkLight(blockpos2);
                    }
                }
            }
        }
    }
    
    public void checkLight() {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;
        final BlockPos blockpos = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);
        if (this.worldObj.provider.func_191066_m()) {
            if (this.worldObj.isAreaLoaded(blockpos.add(-1, 0, -1), blockpos.add(16, this.worldObj.getSeaLevel(), 16))) {
            Label_0121:
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (!this.checkLight(i, j)) {
                            this.isLightPopulated = false;
                            break Label_0121;
                        }
                    }
                }
                if (this.isLightPopulated) {
                    for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                        final int k = (enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) ? 16 : 1;
                        this.worldObj.getChunkFromBlockCoords(blockpos.offset(enumfacing, k)).checkLightSide(enumfacing.getOpposite());
                    }
                    this.setSkylightUpdated();
                }
            }
            else {
                this.isLightPopulated = false;
            }
        }
    }
    
    private void setSkylightUpdated() {
        for (int i = 0; i < this.updateSkylightColumns.length; ++i) {
            this.updateSkylightColumns[i] = true;
        }
        this.recheckGaps(false);
    }
    
    private void checkLightSide(final EnumFacing facing) {
        if (this.isTerrainPopulated) {
            if (facing == EnumFacing.EAST) {
                for (int i = 0; i < 16; ++i) {
                    this.checkLight(15, i);
                }
            }
            else if (facing == EnumFacing.WEST) {
                for (int j = 0; j < 16; ++j) {
                    this.checkLight(0, j);
                }
            }
            else if (facing == EnumFacing.SOUTH) {
                for (int k = 0; k < 16; ++k) {
                    this.checkLight(k, 15);
                }
            }
            else if (facing == EnumFacing.NORTH) {
                for (int l = 0; l < 16; ++l) {
                    this.checkLight(l, 0);
                }
            }
        }
    }
    
    private boolean checkLight(final int x, final int z) {
        final int i = this.getTopFilledSegment();
        boolean flag = false;
        boolean flag2 = false;
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);
        for (int j = i + 16 - 1; j > this.worldObj.getSeaLevel() || (j > 0 && !flag2); --j) {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), j, blockpos$mutableblockpos.getZ());
            final int k = this.getBlockLightOpacity(blockpos$mutableblockpos);
            if (k == 255 && blockpos$mutableblockpos.getY() < this.worldObj.getSeaLevel()) {
                flag2 = true;
            }
            if (!flag && k > 0) {
                flag = true;
            }
            else if (flag && k == 0 && !this.worldObj.checkLight(blockpos$mutableblockpos)) {
                return false;
            }
        }
        for (int l = blockpos$mutableblockpos.getY(); l > 0; --l) {
            blockpos$mutableblockpos.setPos(blockpos$mutableblockpos.getX(), l, blockpos$mutableblockpos.getZ());
            if (this.getBlockState(blockpos$mutableblockpos).getLightValue() > 0) {
                this.worldObj.checkLight(blockpos$mutableblockpos);
            }
        }
        return true;
    }
    
    public boolean isLoaded() {
        return this.isChunkLoaded;
    }
    
    public void setChunkLoaded(final boolean loaded) {
        this.isChunkLoaded = loaded;
    }
    
    public World getWorld() {
        return this.worldObj;
    }
    
    public int[] getHeightMap() {
        return this.heightMap;
    }
    
    public void setHeightMap(final int[] newHeightMap) {
        if (this.heightMap.length != newHeightMap.length) {
            Chunk.LOGGER.warn("Could not set level chunk heightmap, array length is {} instead of {}", (Object)newHeightMap.length, this.heightMap.length);
        }
        else {
            System.arraycopy(newHeightMap, 0, this.heightMap, 0, this.heightMap.length);
        }
    }
    
    public Map<BlockPos, TileEntity> getTileEntityMap() {
        return this.chunkTileEntityMap;
    }
    
    public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
        return this.entityLists;
    }
    
    public boolean isTerrainPopulated() {
        return this.isTerrainPopulated;
    }
    
    public void setTerrainPopulated(final boolean terrainPopulated) {
        this.isTerrainPopulated = terrainPopulated;
    }
    
    public boolean isLightPopulated() {
        return this.isLightPopulated;
    }
    
    public void setLightPopulated(final boolean lightPopulated) {
        this.isLightPopulated = lightPopulated;
    }
    
    public void setModified(final boolean modified) {
        this.isModified = modified;
    }
    
    public void setHasEntities(final boolean hasEntitiesIn) {
        this.hasEntities = hasEntitiesIn;
    }
    
    public void setLastSaveTime(final long saveTime) {
        this.lastSaveTime = saveTime;
    }
    
    public int getLowestHeight() {
        return this.heightMapMinimum;
    }
    
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }
    
    public void setInhabitedTime(final long newInhabitedTime) {
        this.inhabitedTime = newInhabitedTime;
    }
    
    public enum EnumCreateEntityType
    {
        IMMEDIATE("IMMEDIATE", 0), 
        QUEUED("QUEUED", 1), 
        CHECK("CHECK", 2);
        
        private EnumCreateEntityType(final String s, final int n) {
        }
    }
}
