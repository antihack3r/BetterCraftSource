/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import com.TominoCZ.FBP.FBP;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.florianmichael.viamcp.fixes.FixedSoundEngine;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import me.nzxtercode.bettercraft.client.gui.section.GuiMisc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World
implements IBlockAccess {
    private int seaLevel = 63;
    protected boolean scheduledUpdatesAreImmediate;
    public final List<Entity> loadedEntityList = Lists.newArrayList();
    protected final List<Entity> unloadedEntityList = Lists.newArrayList();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    public final List<EntityPlayer> playerEntities = Lists.newArrayList();
    public final List<Entity> weatherEffects = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap();
    private long cloudColour = 0xFFFFFFL;
    private int skylightSubtracted;
    protected int updateLCG = new Random().nextInt();
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    private int lastLightningBolt;
    public final Random rand = new Random();
    public final WorldProvider provider;
    protected List<IWorldAccess> worldAccesses = Lists.newArrayList();
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;
    protected WorldInfo worldInfo;
    protected boolean findingSpawnPoint;
    protected MapStorage mapStorage;
    protected VillageCollection villageCollectionObj;
    public final Profiler theProfiler;
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();
    public final boolean isRemote;
    protected Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();
    private int ambientTickCountdown = this.rand.nextInt(12000);
    protected boolean spawnHostileMobs = true;
    protected boolean spawnPeacefulMobs = true;
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;
    int[] lightUpdateBlockList = new int[32768];

    protected World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        this.saveHandler = saveHandlerIn;
        this.theProfiler = profilerIn;
        this.worldInfo = info;
        this.provider = providerIn;
        this.isRemote = client;
        this.worldBorder = providerIn.getWorldBorder();
    }

    public World init() {
        return this;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos) {
        if (this.isBlockLoaded(pos)) {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            try {
                return chunk.getBiome(pos, this.provider.getWorldChunkManager());
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting biome");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Coordinates of biome request");
                crashreportcategory.addCrashSectionCallable("Location", new Callable<String>(){

                    @Override
                    public String call() throws Exception {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.provider.getWorldChunkManager();
    }

    protected abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings settings) {
        this.worldInfo.setServerInitialized(true);
    }

    public void setInitialSpawnLocation() {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public Block getGroundAboveSeaLevel(BlockPos pos) {
        BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        while (!this.isAirBlock(blockpos.up())) {
            blockpos = blockpos.up();
        }
        return this.getBlockState(blockpos).getBlock();
    }

    private boolean isValid(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public boolean isBlockLoaded(BlockPos pos) {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
        return !this.isValid(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos center, int radius) {
        return this.isAreaLoaded(center, radius, true);
    }

    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to2) {
        return this.isAreaLoaded(from, to2, true);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to2, boolean allowEmpty) {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to2.getX(), to2.getY(), to2.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(StructureBoundingBox box2) {
        return this.isAreaLoaded(box2, true);
    }

    public boolean isAreaLoaded(StructureBoundingBox box2, boolean allowEmpty) {
        return this.isAreaLoaded(box2.minX, box2.minY, box2.minZ, box2.maxX, box2.maxY, box2.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty) {
        if (yEnd >= 0 && yStart < 256) {
            zStart >>= 4;
            xEnd >>= 4;
            zEnd >>= 4;
            int i2 = xStart >>= 4;
            while (i2 <= xEnd) {
                int j2 = zStart;
                while (j2 <= zEnd) {
                    if (!this.isChunkLoaded(i2, j2, allowEmpty)) {
                        return false;
                    }
                    ++j2;
                }
                ++i2;
            }
            return true;
        }
        return false;
    }

    protected boolean isChunkLoaded(int x2, int z2, boolean allowEmpty) {
        return this.chunkProvider.chunkExists(x2, z2) && (allowEmpty || !this.chunkProvider.provideChunk(x2, z2).isEmpty());
    }

    public Chunk getChunkFromBlockCoords(BlockPos pos) {
        return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (!this.isValid(pos)) {
            return false;
        }
        if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        Block block = newState.getBlock();
        IBlockState iblockstate = chunk.setBlockState(pos, newState);
        if (iblockstate == null) {
            return false;
        }
        Block block1 = iblockstate.getBlock();
        if (block.getLightOpacity() != block1.getLightOpacity() || block.getLightValue() != block1.getLightValue()) {
            this.theProfiler.startSection("checkLight");
            this.checkLight(pos);
            this.theProfiler.endSection();
        }
        if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunk.isPopulated()) {
            this.markBlockForUpdate(pos);
        }
        if (!this.isRemote && (flags & 1) != 0) {
            this.notifyNeighborsRespectDebug(pos, iblockstate.getBlock());
            if (block.hasComparatorInputOverride()) {
                this.updateComparatorOutputLevel(pos, block);
            }
        }
        return true;
    }

    public boolean setBlockToAir(BlockPos pos) {
        return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
    }

    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return FixedSoundEngine.destroyBlock(this, pos, dropBlock);
    }

    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public void markBlockForUpdate(BlockPos pos) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).markBlockForUpdate(pos);
            ++i2;
        }
    }

    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType) {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
            this.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

    public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
        if (x2 > z2) {
            int i2 = z2;
            z2 = x2;
            x2 = i2;
        }
        if (!this.provider.getHasNoSky()) {
            int j2 = x2;
            while (j2 <= z2) {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, j2, z1));
                ++j2;
            }
        }
        this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
    }

    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
            ++i2;
        }
    }

    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
        this.notifyBlockOfStateChange(pos.west(), blockType);
        this.notifyBlockOfStateChange(pos.east(), blockType);
        this.notifyBlockOfStateChange(pos.down(), blockType);
        this.notifyBlockOfStateChange(pos.up(), blockType);
        this.notifyBlockOfStateChange(pos.north(), blockType);
        this.notifyBlockOfStateChange(pos.south(), blockType);
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        if (skipSide != EnumFacing.WEST) {
            this.notifyBlockOfStateChange(pos.west(), blockType);
        }
        if (skipSide != EnumFacing.EAST) {
            this.notifyBlockOfStateChange(pos.east(), blockType);
        }
        if (skipSide != EnumFacing.DOWN) {
            this.notifyBlockOfStateChange(pos.down(), blockType);
        }
        if (skipSide != EnumFacing.UP) {
            this.notifyBlockOfStateChange(pos.up(), blockType);
        }
        if (skipSide != EnumFacing.NORTH) {
            this.notifyBlockOfStateChange(pos.north(), blockType);
        }
        if (skipSide != EnumFacing.SOUTH) {
            this.notifyBlockOfStateChange(pos.south(), blockType);
        }
    }

    public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn) {
        if (!this.isRemote) {
            IBlockState iblockstate = this.getBlockState(pos);
            try {
                iblockstate.getBlock().onNeighborBlockChange(this, pos, iblockstate, blockIn);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.addCrashSectionCallable("Source block type", new Callable<String>(){

                    @Override
                    public String call() throws Exception {
                        try {
                            return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
                        }
                        catch (Throwable var2) {
                            return "ID #" + Block.getIdFromBlock(blockIn);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        return false;
    }

    public boolean canSeeSky(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(BlockPos pos) {
        if (pos.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(pos);
        }
        BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        if (!this.canSeeSky(blockpos)) {
            return false;
        }
        blockpos = blockpos.down();
        while (blockpos.getY() > pos.getY()) {
            Block block = this.getBlockState(blockpos).getBlock();
            if (block.getLightOpacity() > 0 && !block.getMaterial().isLiquid()) {
                return false;
            }
            blockpos = blockpos.down();
        }
        return true;
    }

    public int getLight(BlockPos pos) {
        if (pos.getY() < 0) {
            return 0;
        }
        if (pos.getY() >= 256) {
            pos = new BlockPos(pos.getX(), 255, pos.getZ());
        }
        return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
    }

    public int getLightFromNeighbors(BlockPos pos) {
        return this.getLight(pos, true);
    }

    public int getLight(BlockPos pos, boolean checkNeighbors) {
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
                int i1 = this.getLight(pos.up(), false);
                int i2 = this.getLight(pos.east(), false);
                int j2 = this.getLight(pos.west(), false);
                int k2 = this.getLight(pos.south(), false);
                int l2 = this.getLight(pos.north(), false);
                if (i2 > i1) {
                    i1 = i2;
                }
                if (j2 > i1) {
                    i1 = j2;
                }
                if (k2 > i1) {
                    i1 = k2;
                }
                if (l2 > i1) {
                    i1 = l2;
                }
                return i1;
            }
            if (pos.getY() < 0) {
                return 0;
            }
            if (pos.getY() >= 256) {
                pos = new BlockPos(pos.getX(), 255, pos.getZ());
            }
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            return chunk.getLightSubtracted(pos, this.skylightSubtracted);
        }
        return 15;
    }

    public BlockPos getHeight(BlockPos pos) {
        int i2 = pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 ? (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true) ? this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 0xF, pos.getZ() & 0xF) : 0) : this.getSeaLevel() + 1;
        return new BlockPos(pos.getX(), i2, pos.getZ());
    }

    public int getChunksLowestHorizon(int x2, int z2) {
        if (x2 >= -30000000 && z2 >= -30000000 && x2 < 30000000 && z2 < 30000000) {
            if (!this.isChunkLoaded(x2 >> 4, z2 >> 4, true)) {
                return 0;
            }
            Chunk chunk = this.getChunkFromChunkCoords(x2 >> 4, z2 >> 4);
            return chunk.getLowestHeight();
        }
        return this.getSeaLevel() + 1;
    }

    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        if (this.provider.getHasNoSky() && type == EnumSkyBlock.SKY) {
            return 0;
        }
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }
        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int i1 = this.getLightFor(type, pos.up());
            int i2 = this.getLightFor(type, pos.east());
            int j2 = this.getLightFor(type, pos.west());
            int k2 = this.getLightFor(type, pos.south());
            int l2 = this.getLightFor(type, pos.north());
            if (i2 > i1) {
                i1 = i2;
            }
            if (j2 > i1) {
                i1 = j2;
            }
            if (k2 > i1) {
                i1 = k2;
            }
            if (l2 > i1) {
                i1 = l2;
            }
            return i1;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getLightFor(type, pos);
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        if (pos.getY() < 0) {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }
        if (!this.isValid(pos)) {
            return type.defaultLightValue;
        }
        if (!this.isBlockLoaded(pos)) {
            return type.defaultLightValue;
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getLightFor(type, pos);
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
        if (this.isValid(pos) && this.isBlockLoaded(pos)) {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            chunk.setLightFor(type, pos, lightValue);
            this.notifyLightSet(pos);
        }
    }

    public void notifyLightSet(BlockPos pos) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).notifyLightSet(pos);
            ++i2;
        }
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i2 = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int j2 = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);
        if (j2 < lightValue) {
            j2 = lightValue;
        }
        return i2 << 20 | j2 << 4;
    }

    public float getLightBrightness(BlockPos pos) {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (!this.isValid(pos)) {
            return Blocks.air.getDefaultState();
        }
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        return chunk.getBlockState(pos);
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 p_72933_1_, Vec3 p_72933_2_) {
        return this.rayTraceBlocks(p_72933_1_, p_72933_2_, false, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 start, Vec3 end, boolean stopOnLiquid) {
        return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!(Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))) {
            if (!(Double.isNaN(vec32.xCoord) || Double.isNaN(vec32.yCoord) || Double.isNaN(vec32.zCoord))) {
                MovingObjectPosition movingobjectposition;
                int i2 = MathHelper.floor_double(vec32.xCoord);
                int j2 = MathHelper.floor_double(vec32.yCoord);
                int k2 = MathHelper.floor_double(vec32.zCoord);
                int l2 = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l2, i1, j1);
                IBlockState iblockstate = this.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(this, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid) && (movingobjectposition = block.collisionRayTrace(this, blockpos, vec31, vec32)) != null) {
                    return movingobjectposition;
                }
                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;
                while (k1-- >= 0) {
                    EnumFacing enumfacing;
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                        return null;
                    }
                    if (l2 == i2 && i1 == j2 && j1 == k2) {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }
                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0;
                    double d1 = 999.0;
                    double d2 = 999.0;
                    if (i2 > l2) {
                        d0 = (double)l2 + 1.0;
                    } else if (i2 < l2) {
                        d0 = (double)l2 + 0.0;
                    } else {
                        flag2 = false;
                    }
                    if (j2 > i1) {
                        d1 = (double)i1 + 1.0;
                    } else if (j2 < i1) {
                        d1 = (double)i1 + 0.0;
                    } else {
                        flag = false;
                    }
                    if (k2 > j1) {
                        d2 = (double)j1 + 1.0;
                    } else if (k2 < j1) {
                        d2 = (double)j1 + 0.0;
                    } else {
                        flag1 = false;
                    }
                    double d3 = 999.0;
                    double d4 = 999.0;
                    double d5 = 999.0;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;
                    if (flag2) {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }
                    if (flag) {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }
                    if (flag1) {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }
                    if (d3 == -0.0) {
                        d3 = -1.0E-4;
                    }
                    if (d4 == -0.0) {
                        d4 = -1.0E-4;
                    }
                    if (d5 == -0.0) {
                        d5 = -1.0E-4;
                    }
                    if (d3 < d4 && d3 < d5) {
                        enumfacing = i2 > l2 ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = j2 > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    } else {
                        enumfacing = k2 > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }
                    l2 = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l2, i1, j1);
                    IBlockState iblockstate1 = this.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();
                    if (ignoreBlockWithoutBoundingBox && block1.getCollisionBoundingBox(this, blockpos, iblockstate1) == null) continue;
                    if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                        MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, blockpos, vec31, vec32);
                        if (movingobjectposition1 == null) continue;
                        return movingobjectposition1;
                    }
                    movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                }
                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            return null;
        }
        return null;
    }

    public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
            ++i2;
        }
    }

    public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
            ++i2;
        }
    }

    public void playSoundEffect(double x2, double y2, double z2, String soundName, float volume, float pitch) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).playSound(soundName, x2, y2, z2, volume, pitch);
            ++i2;
        }
    }

    public void playSound(double x2, double y2, double z2, String soundName, float volume, float pitch, boolean distanceDelay) {
    }

    public void playRecord(BlockPos pos, String name) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).playRecord(name, pos);
            ++i2;
        }
    }

    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int ... p_175688_14_) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175688_14_);
    }

    public void spawnParticle(EnumParticleTypes particleType, boolean p_175682_2_, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int ... p_175682_15_) {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | p_175682_2_, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175682_15_);
    }

    private void spawnParticle(int particleID, boolean p_175720_2_, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int ... p_175720_15_) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).spawnParticle(particleID, p_175720_2_, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, p_175720_15_);
            ++i2;
        }
    }

    public boolean addWeatherEffect(Entity entityIn) {
        this.weatherEffects.add(entityIn);
        return true;
    }

    public boolean spawnEntityInWorld(Entity entityIn) {
        int i2 = MathHelper.floor_double(entityIn.posX / 16.0);
        int j2 = MathHelper.floor_double(entityIn.posZ / 16.0);
        boolean flag = entityIn.forceSpawn;
        if (entityIn instanceof EntityPlayer) {
            flag = true;
        }
        if (!flag && !this.isChunkLoaded(i2, j2, true)) {
            return false;
        }
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            this.playerEntities.add(entityplayer);
            this.updateAllPlayersSleepingFlag();
        }
        this.getChunkFromChunkCoords(i2, j2).addEntity(entityIn);
        this.loadedEntityList.add(entityIn);
        this.onEntityAdded(entityIn);
        if (GuiMisc.enabledMisc[1] && this instanceof WorldClient && FBP.enabled) {
            FBP.getInstance().eventHandler.onEntityJoinWorldEvent(entityIn, this);
        }
        return true;
    }

    protected void onEntityAdded(Entity entityIn) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).onEntityAdded(entityIn);
            ++i2;
        }
    }

    protected void onEntityRemoved(Entity entityIn) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).onEntityRemoved(entityIn);
            ++i2;
        }
    }

    public void removeEntity(Entity entityIn) {
        if (entityIn.riddenByEntity != null) {
            entityIn.riddenByEntity.mountEntity(null);
        }
        if (entityIn.ridingEntity != null) {
            entityIn.mountEntity(null);
        }
        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    public void removePlayerEntityDangerously(Entity entityIn) {
        entityIn.setDead();
        if (entityIn instanceof EntityPlayer) {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }
        int i2 = entityIn.chunkCoordX;
        int j2 = entityIn.chunkCoordZ;
        if (entityIn.addedToChunk && this.isChunkLoaded(i2, j2, true)) {
            this.getChunkFromChunkCoords(i2, j2).removeEntity(entityIn);
        }
        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    public void addWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.add(worldAccess);
    }

    public void removeWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.remove(worldAccess);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb2) {
        ArrayList<AxisAlignedBB> list = Lists.newArrayList();
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ + 1.0);
        WorldBorder worldborder = this.getWorldBorder();
        boolean flag = entityIn.isOutsideBorder();
        boolean flag1 = this.isInsideBorder(worldborder, entityIn);
        IBlockState iblockstate = Blocks.stone.getDefaultState();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 < j2) {
            int l1 = i1;
            while (l1 < j1) {
                if (this.isBlockLoaded(blockpos$mutableblockpos.set(k1, 64, l1))) {
                    int i22 = k2 - 1;
                    while (i22 < l2) {
                        blockpos$mutableblockpos.set(k1, i22, l1);
                        if (flag && flag1) {
                            entityIn.setOutsideBorder(false);
                        } else if (!flag && !flag1) {
                            entityIn.setOutsideBorder(true);
                        }
                        IBlockState iblockstate1 = iblockstate;
                        if (worldborder.contains(blockpos$mutableblockpos) || !flag1) {
                            iblockstate1 = this.getBlockState(blockpos$mutableblockpos);
                        }
                        iblockstate1.getBlock().addCollisionBoxesToList(this, blockpos$mutableblockpos, iblockstate1, bb2, list, entityIn);
                        ++i22;
                    }
                }
                ++l1;
            }
            ++k1;
        }
        double d0 = 0.25;
        List<Entity> list1 = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb2.expand(d0, d0, d0));
        int j22 = 0;
        while (j22 < list1.size()) {
            if (entityIn.riddenByEntity != list1 && entityIn.ridingEntity != list1) {
                AxisAlignedBB axisalignedbb = list1.get(j22).getCollisionBoundingBox();
                if (axisalignedbb != null && axisalignedbb.intersectsWith(bb2)) {
                    list.add(axisalignedbb);
                }
                if ((axisalignedbb = entityIn.getCollisionBox(list1.get(j22))) != null && axisalignedbb.intersectsWith(bb2)) {
                    list.add(axisalignedbb);
                }
            }
            ++j22;
        }
        return list;
    }

    public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn) {
        double d0 = worldBorderIn.minX();
        double d1 = worldBorderIn.minZ();
        double d2 = worldBorderIn.maxX();
        double d3 = worldBorderIn.maxZ();
        if (entityIn.isOutsideBorder()) {
            d0 += 1.0;
            d1 += 1.0;
            d2 -= 1.0;
            d3 -= 1.0;
        } else {
            d0 -= 1.0;
            d1 -= 1.0;
            d2 += 1.0;
            d3 += 1.0;
        }
        return entityIn.posX > d0 && entityIn.posX < d2 && entityIn.posZ > d1 && entityIn.posZ < d3;
    }

    public List<AxisAlignedBB> getCollisionBoxes(AxisAlignedBB bb2) {
        ArrayList<AxisAlignedBB> list = Lists.newArrayList();
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ + 1.0);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 < j2) {
            int l1 = i1;
            while (l1 < j1) {
                if (this.isBlockLoaded(blockpos$mutableblockpos.set(k1, 64, l1))) {
                    int i22 = k2 - 1;
                    while (i22 < l2) {
                        blockpos$mutableblockpos.set(k1, i22, l1);
                        IBlockState iblockstate = k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000 ? this.getBlockState(blockpos$mutableblockpos) : Blocks.bedrock.getDefaultState();
                        iblockstate.getBlock().addCollisionBoxesToList(this, blockpos$mutableblockpos, iblockstate, bb2, list, null);
                        ++i22;
                    }
                }
                ++l1;
            }
            ++k1;
        }
        return list;
    }

    public int calculateSkylightSubtracted(float p_72967_1_) {
        float f2 = this.getCelestialAngle(p_72967_1_);
        float f1 = 1.0f - (MathHelper.cos(f2 * (float)Math.PI * 2.0f) * 2.0f + 0.5f);
        f1 = MathHelper.clamp_float(f1, 0.0f, 1.0f);
        f1 = 1.0f - f1;
        f1 = (float)((double)f1 * (1.0 - (double)(this.getRainStrength(p_72967_1_) * 5.0f) / 16.0));
        f1 = (float)((double)f1 * (1.0 - (double)(this.getThunderStrength(p_72967_1_) * 5.0f) / 16.0));
        f1 = 1.0f - f1;
        return (int)(f1 * 11.0f);
    }

    public float getSunBrightness(float p_72971_1_) {
        float f2 = this.getCelestialAngle(p_72971_1_);
        float f1 = 1.0f - (MathHelper.cos(f2 * (float)Math.PI * 2.0f) * 2.0f + 0.2f);
        f1 = MathHelper.clamp_float(f1, 0.0f, 1.0f);
        f1 = 1.0f - f1;
        f1 = (float)((double)f1 * (1.0 - (double)(this.getRainStrength(p_72971_1_) * 5.0f) / 16.0));
        f1 = (float)((double)f1 * (1.0 - (double)(this.getThunderStrength(p_72971_1_) * 5.0f) / 16.0));
        return f1 * 0.8f + 0.2f;
    }

    public Vec3 getSkyColor(Entity entityIn, float partialTicks) {
        float f10;
        float f2 = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f2 * (float)Math.PI * 2.0f) * 2.0f + 0.5f;
        f1 = MathHelper.clamp_float(f1, 0.0f, 1.0f);
        int i2 = MathHelper.floor_double(entityIn.posX);
        int j2 = MathHelper.floor_double(entityIn.posY);
        int k2 = MathHelper.floor_double(entityIn.posZ);
        BlockPos blockpos = new BlockPos(i2, j2, k2);
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(blockpos);
        float f22 = biomegenbase.getFloatTemperature(blockpos);
        int l2 = biomegenbase.getSkyColorByTemp(f22);
        float f3 = (float)(l2 >> 16 & 0xFF) / 255.0f;
        float f4 = (float)(l2 >> 8 & 0xFF) / 255.0f;
        float f5 = (float)(l2 & 0xFF) / 255.0f;
        f3 *= f1;
        f4 *= f1;
        f5 *= f1;
        float f6 = this.getRainStrength(partialTicks);
        if (f6 > 0.0f) {
            float f7 = (f3 * 0.3f + f4 * 0.59f + f5 * 0.11f) * 0.6f;
            float f8 = 1.0f - f6 * 0.75f;
            f3 = f3 * f8 + f7 * (1.0f - f8);
            f4 = f4 * f8 + f7 * (1.0f - f8);
            f5 = f5 * f8 + f7 * (1.0f - f8);
        }
        if ((f10 = this.getThunderStrength(partialTicks)) > 0.0f) {
            float f11 = (f3 * 0.3f + f4 * 0.59f + f5 * 0.11f) * 0.2f;
            float f9 = 1.0f - f10 * 0.75f;
            f3 = f3 * f9 + f11 * (1.0f - f9);
            f4 = f4 * f9 + f11 * (1.0f - f9);
            f5 = f5 * f9 + f11 * (1.0f - f9);
        }
        if (this.lastLightningBolt > 0) {
            float f12 = (float)this.lastLightningBolt - partialTicks;
            if (f12 > 1.0f) {
                f12 = 1.0f;
            }
            f3 = f3 * (1.0f - (f12 *= 0.45f)) + 0.8f * f12;
            f4 = f4 * (1.0f - f12) + 0.8f * f12;
            f5 = f5 * (1.0f - f12) + 1.0f * f12;
        }
        return new Vec3(f3, f4, f5);
    }

    public float getCelestialAngle(float partialTicks) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
    }

    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    public float getCelestialAngleRadians(float partialTicks) {
        float f2 = this.getCelestialAngle(partialTicks);
        return f2 * (float)Math.PI * 2.0f;
    }

    public Vec3 getCloudColour(float partialTicks) {
        float f2 = this.getCelestialAngle(partialTicks);
        float f1 = MathHelper.cos(f2 * (float)Math.PI * 2.0f) * 2.0f + 0.5f;
        f1 = MathHelper.clamp_float(f1, 0.0f, 1.0f);
        float f22 = (float)(this.cloudColour >> 16 & 0xFFL) / 255.0f;
        float f3 = (float)(this.cloudColour >> 8 & 0xFFL) / 255.0f;
        float f4 = (float)(this.cloudColour & 0xFFL) / 255.0f;
        float f5 = this.getRainStrength(partialTicks);
        if (f5 > 0.0f) {
            float f6 = (f22 * 0.3f + f3 * 0.59f + f4 * 0.11f) * 0.6f;
            float f7 = 1.0f - f5 * 0.95f;
            f22 = f22 * f7 + f6 * (1.0f - f7);
            f3 = f3 * f7 + f6 * (1.0f - f7);
            f4 = f4 * f7 + f6 * (1.0f - f7);
        }
        f22 *= f1 * 0.9f + 0.1f;
        f3 *= f1 * 0.9f + 0.1f;
        f4 *= f1 * 0.85f + 0.15f;
        float f9 = this.getThunderStrength(partialTicks);
        if (f9 > 0.0f) {
            float f10 = (f22 * 0.3f + f3 * 0.59f + f4 * 0.11f) * 0.2f;
            float f8 = 1.0f - f9 * 0.95f;
            f22 = f22 * f8 + f10 * (1.0f - f8);
            f3 = f3 * f8 + f10 * (1.0f - f8);
            f4 = f4 * f8 + f10 * (1.0f - f8);
        }
        return new Vec3(f22, f3, f4);
    }

    public Vec3 getFogColor(float partialTicks) {
        float f2 = this.getCelestialAngle(partialTicks);
        return this.provider.getFogColor(f2, partialTicks);
    }

    public BlockPos getPrecipitationHeight(BlockPos pos) {
        return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
    }

    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
        Chunk chunk = this.getChunkFromBlockCoords(pos);
        BlockPos blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());
        while (blockpos.getY() >= 0) {
            BlockPos blockpos1 = blockpos.down();
            Material material = chunk.getBlock(blockpos1).getMaterial();
            if (material.blocksMovement() && material != Material.leaves) break;
            blockpos = blockpos1;
        }
        return blockpos;
    }

    public float getStarBrightness(float partialTicks) {
        float f2 = this.getCelestialAngle(partialTicks);
        float f1 = 1.0f - (MathHelper.cos(f2 * (float)Math.PI * 2.0f) * 2.0f + 0.25f);
        f1 = MathHelper.clamp_float(f1, 0.0f, 1.0f);
        return f1 * f1 * 0.5f;
    }

    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
    }

    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
    }

    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");
        int i2 = 0;
        while (i2 < this.weatherEffects.size()) {
            Entity entity = this.weatherEffects.get(i2);
            try {
                ++entity.ticksExisted;
                entity.onUpdate();
            }
            catch (Throwable throwable2) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable2, "Ticking entity");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
                if (entity == null) {
                    crashreportcategory.addCrashSection("Entity", "~~NULL~~");
                } else {
                    entity.addEntityCrashInfo(crashreportcategory);
                }
                throw new ReportedException(crashreport);
            }
            if (entity.isDead) {
                this.weatherEffects.remove(i2--);
            }
            ++i2;
        }
        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int k2 = 0;
        while (k2 < this.unloadedEntityList.size()) {
            Entity entity1 = this.unloadedEntityList.get(k2);
            int j2 = entity1.chunkCoordX;
            int l1 = entity1.chunkCoordZ;
            if (entity1.addedToChunk && this.isChunkLoaded(j2, l1, true)) {
                this.getChunkFromChunkCoords(j2, l1).removeEntity(entity1);
            }
            ++k2;
        }
        int l2 = 0;
        while (l2 < this.unloadedEntityList.size()) {
            this.onEntityRemoved(this.unloadedEntityList.get(l2));
            ++l2;
        }
        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");
        int i1 = 0;
        while (i1 < this.loadedEntityList.size()) {
            block27: {
                Entity entity2;
                block26: {
                    entity2 = this.loadedEntityList.get(i1);
                    if (entity2.ridingEntity == null) break block26;
                    if (!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) break block27;
                    entity2.ridingEntity.riddenByEntity = null;
                    entity2.ridingEntity = null;
                }
                this.theProfiler.startSection("tick");
                if (!entity2.isDead) {
                    try {
                        this.updateEntity(entity2);
                    }
                    catch (Throwable throwable1) {
                        CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Ticking entity");
                        CrashReportCategory crashreportcategory2 = crashreport1.makeCategory("Entity being ticked");
                        entity2.addEntityCrashInfo(crashreportcategory2);
                        throw new ReportedException(crashreport1);
                    }
                }
                this.theProfiler.endSection();
                this.theProfiler.startSection("remove");
                if (entity2.isDead) {
                    int k1 = entity2.chunkCoordX;
                    int i22 = entity2.chunkCoordZ;
                    if (entity2.addedToChunk && this.isChunkLoaded(k1, i22, true)) {
                        this.getChunkFromChunkCoords(k1, i22).removeEntity(entity2);
                    }
                    this.loadedEntityList.remove(i1--);
                    this.onEntityRemoved(entity2);
                }
                this.theProfiler.endSection();
            }
            ++i1;
        }
        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();
        while (iterator.hasNext()) {
            BlockPos blockpos;
            TileEntity tileentity = iterator.next();
            if (!tileentity.isInvalid() && tileentity.hasWorldObj() && this.isBlockLoaded(blockpos = tileentity.getPos()) && this.worldBorder.contains(blockpos)) {
                try {
                    ((ITickable)((Object)tileentity)).update();
                }
                catch (Throwable throwable) {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                    CrashReportCategory crashreportcategory1 = crashreport2.makeCategory("Block entity being ticked");
                    tileentity.addInfoToCrashReport(crashreportcategory1);
                    throw new ReportedException(crashreport2);
                }
            }
            if (!tileentity.isInvalid()) continue;
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (!this.isBlockLoaded(tileentity.getPos())) continue;
            this.getChunkFromBlockCoords(tileentity.getPos()).removeTileEntity(tileentity.getPos());
        }
        this.processingLoadedTiles = false;
        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }
        this.theProfiler.endStartSection("pendingBlockEntities");
        if (!this.addedTileEntityList.isEmpty()) {
            int j1 = 0;
            while (j1 < this.addedTileEntityList.size()) {
                TileEntity tileentity1 = this.addedTileEntityList.get(j1);
                if (!tileentity1.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity1)) {
                        this.addTileEntity(tileentity1);
                    }
                    if (this.isBlockLoaded(tileentity1.getPos())) {
                        this.getChunkFromBlockCoords(tileentity1.getPos()).addTileEntity(tileentity1.getPos(), tileentity1);
                    }
                    this.markBlockForUpdate(tileentity1.getPos());
                }
                ++j1;
            }
            this.addedTileEntityList.clear();
        }
        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public boolean addTileEntity(TileEntity tile) {
        boolean flag = this.loadedTileEntityList.add(tile);
        if (flag && tile instanceof ITickable) {
            this.tickableTileEntities.add(tile);
        }
        return flag;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            for (TileEntity tileentity : tileEntityCollection) {
                this.loadedTileEntityList.add(tileentity);
                if (!(tileentity instanceof ITickable)) continue;
                this.tickableTileEntities.add(tileentity);
            }
        }
    }

    public void updateEntity(Entity ent) {
        this.updateEntityWithOptionalForce(ent, true);
    }

    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
        int i2 = MathHelper.floor_double(entityIn.posX);
        int j2 = MathHelper.floor_double(entityIn.posZ);
        int k2 = 32;
        if (!forceUpdate || this.isAreaLoaded(i2 - k2, 0, j2 - k2, i2 + k2, 0, j2 + k2, true)) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;
            if (forceUpdate && entityIn.addedToChunk) {
                ++entityIn.ticksExisted;
                if (entityIn.ridingEntity != null) {
                    entityIn.updateRidden();
                } else {
                    entityIn.onUpdate();
                }
            }
            this.theProfiler.startSection("chunkCheck");
            if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX)) {
                entityIn.posX = entityIn.lastTickPosX;
            }
            if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY)) {
                entityIn.posY = entityIn.lastTickPosY;
            }
            if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ)) {
                entityIn.posZ = entityIn.lastTickPosZ;
            }
            if (Double.isNaN(entityIn.rotationPitch) || Double.isInfinite(entityIn.rotationPitch)) {
                entityIn.rotationPitch = entityIn.prevRotationPitch;
            }
            if (Double.isNaN(entityIn.rotationYaw) || Double.isInfinite(entityIn.rotationYaw)) {
                entityIn.rotationYaw = entityIn.prevRotationYaw;
            }
            int l2 = MathHelper.floor_double(entityIn.posX / 16.0);
            int i1 = MathHelper.floor_double(entityIn.posY / 16.0);
            int j1 = MathHelper.floor_double(entityIn.posZ / 16.0);
            if (!entityIn.addedToChunk || entityIn.chunkCoordX != l2 || entityIn.chunkCoordY != i1 || entityIn.chunkCoordZ != j1) {
                if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true)) {
                    this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
                }
                if (this.isChunkLoaded(l2, j1, true)) {
                    entityIn.addedToChunk = true;
                    this.getChunkFromChunkCoords(l2, j1).addEntity(entityIn);
                } else {
                    entityIn.addedToChunk = false;
                }
            }
            this.theProfiler.endSection();
            if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null) {
                if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn) {
                    this.updateEntity(entityIn.riddenByEntity);
                } else {
                    entityIn.riddenByEntity.ridingEntity = null;
                    entityIn.riddenByEntity = null;
                }
            }
        }
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb2) {
        return this.checkNoEntityCollision(bb2, null);
    }

    public boolean checkNoEntityCollision(AxisAlignedBB bb2, Entity entityIn) {
        List<Entity> list = this.getEntitiesWithinAABBExcludingEntity(null, bb2);
        int i2 = 0;
        while (i2 < list.size()) {
            Entity entity = list.get(i2);
            if (!entity.isDead && entity.preventEntitySpawning && entity != entityIn && (entityIn == null || entityIn.ridingEntity != entity && entityIn.riddenByEntity != entity)) {
                return false;
            }
            ++i2;
        }
        return true;
    }

    public boolean checkBlockCollision(AxisAlignedBB bb2) {
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 <= j2) {
            int l1 = k2;
            while (l1 <= l2) {
                int i22 = i1;
                while (i22 <= j1) {
                    Block block = this.getBlockState(blockpos$mutableblockpos.set(k1, l1, i22)).getBlock();
                    if (block.getMaterial() != Material.air) {
                        return true;
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        return false;
    }

    public boolean isAnyLiquid(AxisAlignedBB bb2) {
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 <= j2) {
            int l1 = k2;
            while (l1 <= l2) {
                int i22 = i1;
                while (i22 <= j1) {
                    Block block = this.getBlockState(blockpos$mutableblockpos.set(k1, l1, i22)).getBlock();
                    if (block.getMaterial().isLiquid()) {
                        return true;
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        return false;
    }

    public boolean isFlammableWithin(AxisAlignedBB bb2) {
        int j1;
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        if (this.isAreaLoaded(i2, k2, i1, j2, l2, j1 = MathHelper.floor_double(bb2.maxZ + 1.0), true)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            int k1 = i2;
            while (k1 < j2) {
                int l1 = k2;
                while (l1 < l2) {
                    int i22 = i1;
                    while (i22 < j1) {
                        Block block = this.getBlockState(blockpos$mutableblockpos.set(k1, l1, i22)).getBlock();
                        if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                            return true;
                        }
                        ++i22;
                    }
                    ++l1;
                }
                ++k1;
            }
        }
        return false;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB bb2, Material materialIn, Entity entityIn) {
        int j1;
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        if (!this.isAreaLoaded(i2, k2, i1, j2, l2, j1 = MathHelper.floor_double(bb2.maxZ + 1.0), true)) {
            return false;
        }
        boolean flag = false;
        Vec3 vec3 = new Vec3(0.0, 0.0, 0.0);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 < j2) {
            int l1 = k2;
            while (l1 < l2) {
                int i22 = i1;
                while (i22 < j1) {
                    double d0;
                    blockpos$mutableblockpos.set(k1, l1, i22);
                    IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos);
                    Block block = iblockstate.getBlock();
                    if (block.getMaterial() == materialIn && (double)l2 >= (d0 = (double)((float)(l1 + 1) - BlockLiquid.getLiquidHeightPercent(iblockstate.getValue(BlockLiquid.LEVEL))))) {
                        flag = true;
                        vec3 = block.modifyAcceleration(this, blockpos$mutableblockpos, entityIn, vec3);
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        if (vec3.lengthVector() > 0.0 && entityIn.isPushedByWater()) {
            vec3 = vec3.normalize();
            double d1 = 0.014;
            entityIn.motionX += vec3.xCoord * d1;
            entityIn.motionY += vec3.yCoord * d1;
            entityIn.motionZ += vec3.zCoord * d1;
        }
        return flag;
    }

    public boolean isMaterialInBB(AxisAlignedBB bb2, Material materialIn) {
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ + 1.0);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 < j2) {
            int l1 = k2;
            while (l1 < l2) {
                int i22 = i1;
                while (i22 < j1) {
                    if (this.getBlockState(blockpos$mutableblockpos.set(k1, l1, i22)).getBlock().getMaterial() == materialIn) {
                        return true;
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        return false;
    }

    public boolean isAABBInMaterial(AxisAlignedBB bb2, Material materialIn) {
        int i2 = MathHelper.floor_double(bb2.minX);
        int j2 = MathHelper.floor_double(bb2.maxX + 1.0);
        int k2 = MathHelper.floor_double(bb2.minY);
        int l2 = MathHelper.floor_double(bb2.maxY + 1.0);
        int i1 = MathHelper.floor_double(bb2.minZ);
        int j1 = MathHelper.floor_double(bb2.maxZ + 1.0);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int k1 = i2;
        while (k1 < j2) {
            int l1 = k2;
            while (l1 < l2) {
                int i22 = i1;
                while (i22 < j1) {
                    IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos.set(k1, l1, i22));
                    Block block = iblockstate.getBlock();
                    if (block.getMaterial() == materialIn) {
                        int j22 = iblockstate.getValue(BlockLiquid.LEVEL);
                        double d0 = l1 + 1;
                        if (j22 < 8) {
                            d0 = (double)(l1 + 1) - (double)j22 / 8.0;
                        }
                        if (d0 >= bb2.minY) {
                            return true;
                        }
                    }
                    ++i22;
                }
                ++l1;
            }
            ++k1;
        }
        return false;
    }

    public Explosion createExplosion(Entity entityIn, double x2, double y2, double z2, float strength, boolean isSmoking) {
        return this.newExplosion(entityIn, x2, y2, z2, strength, false, isSmoking);
    }

    public Explosion newExplosion(Entity entityIn, double x2, double y2, double z2, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entityIn, x2, y2, z2, strength, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    public float getBlockDensity(Vec3 vec, AxisAlignedBB bb2) {
        double d0 = 1.0 / ((bb2.maxX - bb2.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((bb2.maxY - bb2.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb2.maxZ - bb2.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
            int i2 = 0;
            int j2 = 0;
            float f2 = 0.0f;
            while (f2 <= 1.0f) {
                float f1 = 0.0f;
                while (f1 <= 1.0f) {
                    float f22 = 0.0f;
                    while (f22 <= 1.0f) {
                        double d5 = bb2.minX + (bb2.maxX - bb2.minX) * (double)f2;
                        double d6 = bb2.minY + (bb2.maxY - bb2.minY) * (double)f1;
                        double d7 = bb2.minZ + (bb2.maxZ - bb2.minZ) * (double)f22;
                        if (this.rayTraceBlocks(new Vec3(d5 + d3, d6, d7 + d4), vec) == null) {
                            ++i2;
                        }
                        ++j2;
                        f22 = (float)((double)f22 + d2);
                    }
                    f1 = (float)((double)f1 + d1);
                }
                f2 = (float)((double)f2 + d0);
            }
            return (float)i2 / (float)j2;
        }
        return 0.0f;
    }

    public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side) {
        if (this.getBlockState(pos = pos.offset(side)).getBlock() == Blocks.fire) {
            this.playAuxSFXAtEntity(player, 1004, pos, 0);
            this.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if (!this.isValid(pos)) {
            return null;
        }
        TileEntity tileentity = null;
        if (this.processingLoadedTiles) {
            int i2 = 0;
            while (i2 < this.addedTileEntityList.size()) {
                TileEntity tileentity1 = this.addedTileEntityList.get(i2);
                if (!tileentity1.isInvalid() && tileentity1.getPos().equals(pos)) {
                    tileentity = tileentity1;
                    break;
                }
                ++i2;
            }
        }
        if (tileentity == null) {
            tileentity = this.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
        }
        if (tileentity == null) {
            int j2 = 0;
            while (j2 < this.addedTileEntityList.size()) {
                TileEntity tileentity2 = this.addedTileEntityList.get(j2);
                if (!tileentity2.isInvalid() && tileentity2.getPos().equals(pos)) {
                    tileentity = tileentity2;
                    break;
                }
                ++j2;
            }
        }
        return tileentity;
    }

    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.setPos(pos);
                Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();
                while (iterator.hasNext()) {
                    TileEntity tileentity = iterator.next();
                    if (!tileentity.getPos().equals(pos)) continue;
                    tileentity.invalidate();
                    iterator.remove();
                }
                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.addTileEntity(tileEntityIn);
                this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
            }
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);
        if (tileentity != null && this.processingLoadedTiles) {
            tileentity.invalidate();
            this.addedTileEntityList.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.addedTileEntityList.remove(tileentity);
                this.loadedTileEntityList.remove(tileentity);
                this.tickableTileEntities.remove(tileentity);
            }
            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
    }

    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(BlockPos pos) {
        IBlockState iblockstate = this.getBlockState(pos);
        AxisAlignedBB axisalignedbb = iblockstate.getBlock().getCollisionBoundingBox(this, pos, iblockstate);
        return axisalignedbb != null && axisalignedbb.getAverageEdgeLength() >= 1.0;
    }

    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState iblockstate = blockAccess.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block.getMaterial().isOpaque() && block.isFullCube() ? true : (block instanceof BlockStairs ? iblockstate.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP : (block instanceof BlockSlab ? iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP : (block instanceof BlockHopper ? true : (block instanceof BlockSnow ? iblockstate.getValue(BlockSnow.LAYERS) == 7 : false))));
    }

    public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
        if (!this.isValid(pos)) {
            return _default;
        }
        Chunk chunk = this.chunkProvider.provideChunk(pos);
        if (chunk.isEmpty()) {
            return _default;
        }
        Block block = this.getBlockState(pos).getBlock();
        return block.getMaterial().isOpaque() && block.isFullCube();
    }

    public void calculateInitialSkylight() {
        int i2 = this.calculateSkylightSubtracted(1.0f);
        if (i2 != this.skylightSubtracted) {
            this.skylightSubtracted = i2;
        }
    }

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    public void tick() {
        this.updateWeather();
    }

    protected void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0f;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0f;
            }
        }
    }

    protected void updateWeather() {
        if (!this.provider.getHasNoSky() && !this.isRemote) {
            int j2;
            int i2 = this.worldInfo.getCleanWeatherTime();
            if (i2 > 0) {
                this.worldInfo.setCleanWeatherTime(--i2);
                this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
            }
            if ((j2 = this.worldInfo.getThunderTime()) <= 0) {
                if (this.worldInfo.isThundering()) {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                } else {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setThunderTime(--j2);
                if (j2 <= 0) {
                    this.worldInfo.setThundering(!this.worldInfo.isThundering());
                }
            }
            this.prevThunderingStrength = this.thunderingStrength;
            this.thunderingStrength = this.worldInfo.isThundering() ? (float)((double)this.thunderingStrength + 0.01) : (float)((double)this.thunderingStrength - 0.01);
            this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0f, 1.0f);
            int k2 = this.worldInfo.getRainTime();
            if (k2 <= 0) {
                if (this.worldInfo.isRaining()) {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                } else {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setRainTime(--k2);
                if (k2 <= 0) {
                    this.worldInfo.setRaining(!this.worldInfo.isRaining());
                }
            }
            this.prevRainingStrength = this.rainingStrength;
            this.rainingStrength = this.worldInfo.isRaining() ? (float)((double)this.rainingStrength + 0.01) : (float)((double)this.rainingStrength - 0.01);
            this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0f, 1.0f);
        }
    }

    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");
        int i2 = 0;
        while (i2 < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i2);
            int j2 = MathHelper.floor_double(entityplayer.posX / 16.0);
            int k2 = MathHelper.floor_double(entityplayer.posZ / 16.0);
            int l2 = this.getRenderDistanceChunks();
            int i1 = -l2;
            while (i1 <= l2) {
                int j1 = -l2;
                while (j1 <= l2) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(i1 + j2, j1 + k2));
                    ++j1;
                }
                ++i1;
            }
            ++i2;
        }
        this.theProfiler.endSection();
        if (this.ambientTickCountdown > 0) {
            --this.ambientTickCountdown;
        }
        this.theProfiler.startSection("playerCheckLight");
        if (!this.playerEntities.isEmpty()) {
            int k1 = this.rand.nextInt(this.playerEntities.size());
            EntityPlayer entityplayer1 = this.playerEntities.get(k1);
            int l1 = MathHelper.floor_double(entityplayer1.posX) + this.rand.nextInt(11) - 5;
            int i22 = MathHelper.floor_double(entityplayer1.posY) + this.rand.nextInt(11) - 5;
            int j2 = MathHelper.floor_double(entityplayer1.posZ) + this.rand.nextInt(11) - 5;
            this.checkLight(new BlockPos(l1, i22, j2));
        }
        this.theProfiler.endSection();
    }

    protected abstract int getRenderDistanceChunks();

    protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn) {
        this.theProfiler.endStartSection("moodSound");
        if (this.ambientTickCountdown == 0 && !this.isRemote) {
            EntityPlayer entityplayer;
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int i2 = this.updateLCG >> 2;
            int j2 = i2 & 0xF;
            int k2 = i2 >> 8 & 0xF;
            int l2 = i2 >> 16 & 0xFF;
            BlockPos blockpos = new BlockPos(j2, l2, k2);
            Block block = chunkIn.getBlock(blockpos);
            if (block.getMaterial() == Material.air && this.getLight(blockpos) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, blockpos) <= 0 && (entityplayer = this.getClosestPlayer((double)(j2 += p_147467_1_) + 0.5, (double)l2 + 0.5, (double)(k2 += p_147467_2_) + 0.5, 8.0)) != null && entityplayer.getDistanceSq((double)j2 + 0.5, (double)l2 + 0.5, (double)k2 + 0.5) > 4.0) {
                this.playSoundEffect((double)j2 + 0.5, (double)l2 + 0.5, (double)k2 + 0.5, "ambient.cave.cave", 0.7f, 0.8f + this.rand.nextFloat() * 0.2f);
                this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
            }
        }
        this.theProfiler.endStartSection("checkLight");
        chunkIn.enqueueRelightChecks();
    }

    protected void updateBlocks() {
        this.setActivePlayerChunksAndCheckLight();
    }

    public void forceBlockUpdateTick(Block blockType, BlockPos pos, Random random) {
        this.scheduledUpdatesAreImmediate = true;
        blockType.updateTick(this, pos, this.getBlockState(pos), random);
        this.scheduledUpdatesAreImmediate = false;
    }

    public boolean canBlockFreezeWater(BlockPos pos) {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(BlockPos pos) {
        return this.canBlockFreeze(pos, true);
    }

    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
        IBlockState iblockstate;
        Block block;
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        float f2 = biomegenbase.getFloatTemperature(pos);
        if (f2 > 0.15f) {
            return false;
        }
        if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10 && ((block = (iblockstate = this.getBlockState(pos)).getBlock()) == Blocks.water || block == Blocks.flowing_water) && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
            boolean flag;
            if (!noWaterAdj) {
                return true;
            }
            boolean bl2 = flag = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    private boolean isWater(BlockPos pos) {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    public boolean canSnowAt(BlockPos pos, boolean checkLight) {
        Block block;
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        float f2 = biomegenbase.getFloatTemperature(pos);
        if (f2 > 0.15f) {
            return false;
        }
        if (!checkLight) {
            return true;
        }
        return pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10 && (block = this.getBlockState(pos).getBlock()).getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, pos);
    }

    public boolean checkLight(BlockPos pos) {
        boolean flag = false;
        if (!this.provider.getHasNoSky()) {
            flag |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }
        return flag |= this.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }

    private int getRawLight(BlockPos pos, EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos)) {
            return 15;
        }
        Block block = this.getBlockState(pos).getBlock();
        int i2 = lightType == EnumSkyBlock.SKY ? 0 : block.getLightValue();
        int j2 = block.getLightOpacity();
        if (j2 >= 15 && block.getLightValue() > 0) {
            j2 = 1;
        }
        if (j2 < 1) {
            j2 = 1;
        }
        if (j2 >= 15) {
            return 0;
        }
        if (i2 >= 14) {
            return i2;
        }
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n2 = enumFacingArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            BlockPos blockpos = pos.offset(enumfacing);
            int k2 = this.getLightFor(lightType, blockpos) - j2;
            if (k2 > i2) {
                i2 = k2;
            }
            if (i2 >= 14) {
                return i2;
            }
            ++n3;
        }
        return i2;
    }

    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
        if (!this.isAreaLoaded(pos, 17, false)) {
            return false;
        }
        int i2 = 0;
        int j2 = 0;
        this.theProfiler.startSection("getBrightness");
        int k2 = this.getLightFor(lightType, pos);
        int l2 = this.getRawLight(pos, lightType);
        int i1 = pos.getX();
        int j1 = pos.getY();
        int k1 = pos.getZ();
        if (l2 > k2) {
            this.lightUpdateBlockList[j2++] = 133152;
        } else if (l2 < k2) {
            this.lightUpdateBlockList[j2++] = 0x20820 | k2 << 18;
            while (i2 < j2) {
                int l3;
                int k3;
                int j3;
                int l1 = this.lightUpdateBlockList[i2++];
                int i22 = (l1 & 0x3F) - 32 + i1;
                int j22 = (l1 >> 6 & 0x3F) - 32 + j1;
                int k22 = (l1 >> 12 & 0x3F) - 32 + k1;
                int l22 = l1 >> 18 & 0xF;
                BlockPos blockpos = new BlockPos(i22, j22, k22);
                int i3 = this.getLightFor(lightType, blockpos);
                if (i3 != l22) continue;
                this.setLightFor(lightType, blockpos, 0);
                if (l22 <= 0 || (j3 = MathHelper.abs_int(i22 - i1)) + (k3 = MathHelper.abs_int(j22 - j1)) + (l3 = MathHelper.abs_int(k22 - k1)) >= 17) continue;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                EnumFacing[] enumFacingArray = EnumFacing.values();
                int n2 = enumFacingArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    EnumFacing enumfacing = enumFacingArray[n3];
                    int i4 = i22 + enumfacing.getFrontOffsetX();
                    int j4 = j22 + enumfacing.getFrontOffsetY();
                    int k4 = k22 + enumfacing.getFrontOffsetZ();
                    blockpos$mutableblockpos.set(i4, j4, k4);
                    int l4 = Math.max(1, this.getBlockState(blockpos$mutableblockpos).getBlock().getLightOpacity());
                    i3 = this.getLightFor(lightType, blockpos$mutableblockpos);
                    if (i3 == l22 - l4 && j2 < this.lightUpdateBlockList.length) {
                        this.lightUpdateBlockList[j2++] = i4 - i1 + 32 | j4 - j1 + 32 << 6 | k4 - k1 + 32 << 12 | l22 - l4 << 18;
                    }
                    ++n3;
                }
            }
            i2 = 0;
        }
        this.theProfiler.endSection();
        this.theProfiler.startSection("checkedPosition < toCheckCount");
        while (i2 < j2) {
            boolean flag;
            int i5 = this.lightUpdateBlockList[i2++];
            int j5 = (i5 & 0x3F) - 32 + i1;
            int k5 = (i5 >> 6 & 0x3F) - 32 + j1;
            int l5 = (i5 >> 12 & 0x3F) - 32 + k1;
            BlockPos blockpos1 = new BlockPos(j5, k5, l5);
            int i6 = this.getLightFor(lightType, blockpos1);
            int j6 = this.getRawLight(blockpos1, lightType);
            if (j6 == i6) continue;
            this.setLightFor(lightType, blockpos1, j6);
            if (j6 <= i6) continue;
            int k6 = Math.abs(j5 - i1);
            int l6 = Math.abs(k5 - j1);
            int i7 = Math.abs(l5 - k1);
            boolean bl2 = flag = j2 < this.lightUpdateBlockList.length - 6;
            if (k6 + l6 + i7 >= 17 || !flag) continue;
            if (this.getLightFor(lightType, blockpos1.west()) < j6) {
                this.lightUpdateBlockList[j2++] = j5 - 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.east()) < j6) {
                this.lightUpdateBlockList[j2++] = j5 + 1 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.down()) < j6) {
                this.lightUpdateBlockList[j2++] = j5 - i1 + 32 + (k5 - 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.up()) < j6) {
                this.lightUpdateBlockList[j2++] = j5 - i1 + 32 + (k5 + 1 - j1 + 32 << 6) + (l5 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.north()) < j6) {
                this.lightUpdateBlockList[j2++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 - 1 - k1 + 32 << 12);
            }
            if (this.getLightFor(lightType, blockpos1.south()) >= j6) continue;
            this.lightUpdateBlockList[j2++] = j5 - i1 + 32 + (k5 - j1 + 32 << 6) + (l5 + 1 - k1 + 32 << 12);
        }
        this.theProfiler.endSection();
        return true;
    }

    public boolean tickUpdates(boolean p_72955_1_) {
        return false;
    }

    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_) {
        return null;
    }

    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean p_175712_2_) {
        return null;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb2) {
        return this.getEntitiesInAABBexcluding(entityIn, bb2, EntitySelectors.NOT_SPECTATING);
    }

    public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        ArrayList<Entity> list = Lists.newArrayList();
        int i2 = MathHelper.floor_double((boundingBox.minX - 2.0) / 16.0);
        int j2 = MathHelper.floor_double((boundingBox.maxX + 2.0) / 16.0);
        int k2 = MathHelper.floor_double((boundingBox.minZ - 2.0) / 16.0);
        int l2 = MathHelper.floor_double((boundingBox.maxZ + 2.0) / 16.0);
        int i1 = i2;
        while (i1 <= j2) {
            int j1 = k2;
            while (j1 <= l2) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
                }
                ++j1;
            }
            ++i1;
        }
        return list;
    }

    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
        ArrayList<Entity> list = Lists.newArrayList();
        for (Entity entity : this.loadedEntityList) {
            if (!entityType.isAssignableFrom(entity.getClass()) || !filter.apply(entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
        ArrayList<Entity> list = Lists.newArrayList();
        for (Entity entity : this.playerEntities) {
            if (!playerType.isAssignableFrom(entity.getClass()) || !filter.apply(entity)) continue;
            list.add(entity);
        }
        return list;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb2) {
        return this.getEntitiesWithinAABB(classEntity, bb2, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
        int i2 = MathHelper.floor_double((aabb.minX - 2.0) / 16.0);
        int j2 = MathHelper.floor_double((aabb.maxX + 2.0) / 16.0);
        int k2 = MathHelper.floor_double((aabb.minZ - 2.0) / 16.0);
        int l2 = MathHelper.floor_double((aabb.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        int i1 = i2;
        while (i1 <= j2) {
            int j1 = k2;
            while (j1 <= l2) {
                if (this.isChunkLoaded(i1, j1, true)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(clazz, aabb, list, filter);
                }
                ++j1;
            }
            ++i1;
        }
        return list;
    }

    public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
        List<T> list = this.getEntitiesWithinAABB(entityType, aabb);
        Entity t2 = null;
        double d0 = Double.MAX_VALUE;
        int i2 = 0;
        while (i2 < list.size()) {
            double d1;
            Entity t1 = (Entity)list.get(i2);
            if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t1) && (d1 = closestTo.getDistanceSqToEntity(t1)) <= d0) {
                t2 = t1;
                d0 = d1;
            }
            ++i2;
        }
        return (T)t2;
    }

    public Entity getEntityByID(int id2) {
        return this.entitiesById.lookup(id2);
    }

    public List<Entity> getLoadedEntityList() {
        return this.loadedEntityList;
    }

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getChunkFromBlockCoords(pos).setChunkModified();
        }
    }

    public int countEntities(Class<?> entityType) {
        int i2 = 0;
        for (Entity entity : this.loadedEntityList) {
            if (entity instanceof EntityLiving && ((EntityLiving)entity).isNoDespawnRequired() || !entityType.isAssignableFrom(entity.getClass())) continue;
            ++i2;
        }
        return i2;
    }

    public void loadEntities(Collection<Entity> entityCollection) {
        this.loadedEntityList.addAll(entityCollection);
        for (Entity entity : entityCollection) {
            this.onEntityAdded(entity);
        }
    }

    public void unloadEntities(Collection<Entity> entityCollection) {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean canBlockBePlaced(Block blockIn, BlockPos pos, boolean p_175716_3_, EnumFacing side, Entity entityIn, ItemStack itemStackIn) {
        AxisAlignedBB axisalignedbb;
        Block block = this.getBlockState(pos).getBlock();
        AxisAlignedBB axisAlignedBB = axisalignedbb = p_175716_3_ ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
        return axisalignedbb != null && !this.checkNoEntityCollision(axisalignedbb, entityIn) ? false : (block.getMaterial() == Material.circuits && blockIn == Blocks.anvil ? true : block.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn));
    }

    public int getSeaLevel() {
        return this.seaLevel;
    }

    public void setSeaLevel(int p_181544_1_) {
        this.seaLevel = p_181544_1_;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.getBlock().getStrongPower(this, pos, iblockstate, direction);
    }

    @Override
    public WorldType getWorldType() {
        return this.worldInfo.getTerrainType();
    }

    public int getStrongPower(BlockPos pos) {
        int i2 = 0;
        if ((i2 = Math.max(i2, this.getStrongPower(pos.down(), EnumFacing.DOWN))) >= 15) {
            return i2;
        }
        if ((i2 = Math.max(i2, this.getStrongPower(pos.up(), EnumFacing.UP))) >= 15) {
            return i2;
        }
        if ((i2 = Math.max(i2, this.getStrongPower(pos.north(), EnumFacing.NORTH))) >= 15) {
            return i2;
        }
        if ((i2 = Math.max(i2, this.getStrongPower(pos.south(), EnumFacing.SOUTH))) >= 15) {
            return i2;
        }
        if ((i2 = Math.max(i2, this.getStrongPower(pos.west(), EnumFacing.WEST))) >= 15) {
            return i2;
        }
        return (i2 = Math.max(i2, this.getStrongPower(pos.east(), EnumFacing.EAST))) >= 15 ? i2 : i2;
    }

    public boolean isSidePowered(BlockPos pos, EnumFacing side) {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, EnumFacing facing) {
        IBlockState iblockstate = this.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return block.isNormalCube() ? this.getStrongPower(pos) : block.getWeakPower(this, pos, iblockstate, facing);
    }

    public boolean isBlockPowered(BlockPos pos) {
        return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0 ? true : (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0 ? true : (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0 ? true : (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0 ? true : (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0))));
    }

    public int isBlockIndirectlyGettingPowered(BlockPos pos) {
        int i2 = 0;
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n2 = enumFacingArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing enumfacing = enumFacingArray[n3];
            int j2 = this.getRedstonePower(pos.offset(enumfacing), enumfacing);
            if (j2 >= 15) {
                return 15;
            }
            if (j2 > i2) {
                i2 = j2;
            }
            ++n3;
        }
        return i2;
    }

    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    public EntityPlayer getClosestPlayer(double x2, double y2, double z2, double distance) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;
        int i2 = 0;
        while (i2 < this.playerEntities.size()) {
            EntityPlayer entityplayer1 = this.playerEntities.get(i2);
            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer1)) {
                double d1 = entityplayer1.getDistanceSq(x2, y2, z2);
                if ((distance < 0.0 || d1 < distance * distance) && (d0 == -1.0 || d1 < d0)) {
                    d0 = d1;
                    entityplayer = entityplayer1;
                }
            }
            ++i2;
        }
        return entityplayer;
    }

    public boolean isAnyPlayerWithinRangeAt(double x2, double y2, double z2, double range) {
        int i2 = 0;
        while (i2 < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i2);
            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer)) {
                double d0 = entityplayer.getDistanceSq(x2, y2, z2);
                if (range < 0.0 || d0 < range * range) {
                    return true;
                }
            }
            ++i2;
        }
        return false;
    }

    public EntityPlayer getPlayerEntityByName(String name) {
        int i2 = 0;
        while (i2 < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i2);
            if (name.equals(entityplayer.getName())) {
                return entityplayer;
            }
            ++i2;
        }
        return null;
    }

    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        int i2 = 0;
        while (i2 < this.playerEntities.size()) {
            EntityPlayer entityplayer = this.playerEntities.get(i2);
            if (uuid.equals(entityplayer.getUniqueID())) {
                return entityplayer;
            }
            ++i2;
        }
        return null;
    }

    public void sendQuittingDisconnectingPacket() {
    }

    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    public void setTotalWorldTime(long worldTime) {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    public long getSeed() {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    public void setWorldTime(long time) {
        this.worldInfo.setWorldTime(time);
    }

    public BlockPos getSpawnPoint() {
        BlockPos blockpos = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
        if (!this.getWorldBorder().contains(blockpos)) {
            blockpos = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockpos;
    }

    public void setSpawnPoint(BlockPos pos) {
        this.worldInfo.setSpawn(pos);
    }

    public void joinEntityInSurroundings(Entity entityIn) {
        int i2 = MathHelper.floor_double(entityIn.posX / 16.0);
        int j2 = MathHelper.floor_double(entityIn.posZ / 16.0);
        int k2 = 2;
        int l2 = i2 - k2;
        while (l2 <= i2 + k2) {
            int i1 = j2 - k2;
            while (i1 <= j2 + k2) {
                this.getChunkFromChunkCoords(l2, i1);
                ++i1;
            }
            ++l2;
        }
        if (!this.loadedEntityList.contains(entityIn)) {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
        return true;
    }

    public void setEntityState(Entity entityIn, byte state) {
    }

    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
    }

    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    public void updateAllPlayersSleepingFlag() {
    }

    public float getThunderStrength(float delta) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    public void setThunderStrength(float strength) {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    public float getRainStrength(float delta) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    public void setRainStrength(float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    public boolean isThundering() {
        return (double)this.getThunderStrength(1.0f) > 0.9;
    }

    public boolean isRaining() {
        return (double)this.getRainStrength(1.0f) > 0.2;
    }

    public boolean isRainingAt(BlockPos strikePosition) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky(strikePosition)) {
            return false;
        }
        if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY()) {
            return false;
        }
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(strikePosition);
        return biomegenbase.getEnableSnow() ? false : (this.canSnowAt(strikePosition, false) ? false : biomegenbase.canRain());
    }

    public boolean isBlockinHighHumidity(BlockPos pos) {
        BiomeGenBase biomegenbase = this.getBiomeGenForCoords(pos);
        return biomegenbase.isHighHumidity();
    }

    public MapStorage getMapStorage() {
        return this.mapStorage;
    }

    public void setItemData(String dataID, WorldSavedData worldSavedDataIn) {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    public WorldSavedData loadItemData(Class<? extends WorldSavedData> clazz, String dataID) {
        return this.mapStorage.loadData(clazz, dataID);
    }

    public int getUniqueDataId(String key) {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(int p_175669_1_, BlockPos pos, int p_175669_3_) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            this.worldAccesses.get(i2).broadcastSound(p_175669_1_, pos, p_175669_3_);
            ++i2;
        }
    }

    public void playAuxSFX(int p_175718_1_, BlockPos pos, int p_175718_3_) {
        this.playAuxSFXAtEntity(null, p_175718_1_, pos, p_175718_3_);
    }

    public void playAuxSFXAtEntity(EntityPlayer player, int sfxType, BlockPos pos, int p_180498_4_) {
        try {
            int i2 = 0;
            while (i2 < this.worldAccesses.size()) {
                this.worldAccesses.get(i2).playAuxSFX(player, sfxType, pos, p_180498_4_);
                ++i2;
            }
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Playing level event");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Level event being played");
            crashreportcategory.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            crashreportcategory.addCrashSection("Event source", player);
            crashreportcategory.addCrashSection("Event type", sfxType);
            crashreportcategory.addCrashSection("Event data", p_180498_4_);
            throw new ReportedException(crashreport);
        }
    }

    public int getHeight() {
        return 256;
    }

    public int getActualHeight() {
        return this.provider.getHasNoSky() ? 128 : 256;
    }

    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_) {
        long i2 = (long)p_72843_1_ * 341873128712L + (long)p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + (long)p_72843_3_;
        this.rand.setSeed(i2);
        return this.rand;
    }

    public BlockPos getStrongholdPos(String name, BlockPos pos) {
        return this.getChunkProvider().getStrongholdGen(this, name, pos);
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    public double getHorizon() {
        return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : 63.0;
    }

    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        CrashReportCategory crashreportcategory = report.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        crashreportcategory.addCrashSectionCallable("All players", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(World.this.playerEntities.size()) + " total; " + World.this.playerEntities.toString();
            }
        });
        crashreportcategory.addCrashSectionCallable("Chunk stats", new Callable<String>(){

            @Override
            public String call() {
                return World.this.chunkProvider.makeString();
            }
        });
        try {
            this.worldInfo.addToCrashReport(crashreportcategory);
        }
        catch (Throwable throwable) {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }
        return crashreportcategory;
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        int i2 = 0;
        while (i2 < this.worldAccesses.size()) {
            IWorldAccess iworldaccess = this.worldAccesses.get(i2);
            iworldaccess.sendBlockBreakProgress(breakerId, pos, progress);
            ++i2;
        }
    }

    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }
        return this.theCalendar;
    }

    public void makeFireworks(double x2, double y2, double z2, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            if (!this.isBlockLoaded(blockpos)) continue;
            IBlockState iblockstate = this.getBlockState(blockpos);
            if (Blocks.unpowered_comparator.isAssociated(iblockstate.getBlock())) {
                iblockstate.getBlock().onNeighborBlockChange(this, blockpos, iblockstate, blockIn);
                continue;
            }
            if (!iblockstate.getBlock().isNormalCube() || !Blocks.unpowered_comparator.isAssociated((iblockstate = this.getBlockState(blockpos = blockpos.offset(enumfacing))).getBlock())) continue;
            iblockstate.getBlock().onNeighborBlockChange(this, blockpos, iblockstate, blockIn);
        }
    }

    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        long i2 = 0L;
        float f2 = 0.0f;
        if (this.isBlockLoaded(pos)) {
            f2 = this.getCurrentMoonPhaseFactor();
            i2 = this.getChunkFromBlockCoords(pos).getInhabitedTime();
        }
        return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), i2, f2);
    }

    public EnumDifficulty getDifficulty() {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted() {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(int newSkylightSubtracted) {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    public int getLastLightningBolt() {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(int lastLightningBoltIn) {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public boolean isFindingSpawnPoint() {
        return this.findingSpawnPoint;
    }

    public VillageCollection getVillageCollection() {
        return this.villageCollectionObj;
    }

    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public boolean isSpawnChunk(int x2, int z2) {
        BlockPos blockpos = this.getSpawnPoint();
        int i2 = x2 * 16 + 8 - blockpos.getX();
        int j2 = z2 * 16 + 8 - blockpos.getZ();
        int k2 = 128;
        return i2 >= -k2 && i2 <= k2 && j2 >= -k2 && j2 <= k2;
    }
}

