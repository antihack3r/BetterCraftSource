/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomGuis;
import net.optifine.DynamicLights;
import net.optifine.override.PlayerControllerOF;
import net.optifine.reflect.Reflector;
import wdl.WDLHooks;

public class WorldClient
extends World {
    private NetHandlerPlayClient sendQueue;
    private ChunkProviderClient clientChunkProvider;
    private final Set<Entity> entityList = Sets.newHashSet();
    private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();
    private boolean playerUpdate = false;

    public WorldClient(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn) {
        super(new SaveHandlerMP(), new WorldInfo(settings, "MpServer"), WorldProvider.getProviderForDimension(dimension), profilerIn, true);
        this.sendQueue = netHandler;
        this.getWorldInfo().setDifficulty(difficulty);
        this.provider.registerWorld(this);
        this.setSpawnPoint(new BlockPos(8, 64, 8));
        this.chunkProvider = this.createChunkProvider();
        this.mapStorage = new SaveDataMemoryStorage();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);
        if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
            this.mc.playerController = new PlayerControllerOF(this.mc, netHandler);
            CustomGuis.setPlayerControllerOF((PlayerControllerOF)this.mc.playerController);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
        if (this.getGameRules().getBoolean("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }
        this.theProfiler.startSection("reEntryProcessing");
        int i2 = 0;
        while (i2 < 10 && !this.entitySpawnQueue.isEmpty()) {
            Entity entity = this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);
            if (!this.loadedEntityList.contains(entity)) {
                this.spawnEntityInWorld(entity);
            }
            ++i2;
        }
        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.updateBlocks();
        this.theProfiler.endSection();
        WDLHooks.onWorldClientTick(this);
    }

    public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    @Override
    protected void updateBlocks() {
        super.updateBlocks();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);
        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
            this.previousActiveChunkSet.clear();
        }
        int i2 = 0;
        for (ChunkCoordIntPair chunkcoordintpair : this.activeChunkSet) {
            if (this.previousActiveChunkSet.contains(chunkcoordintpair)) continue;
            int j2 = chunkcoordintpair.chunkXPos * 16;
            int k2 = chunkcoordintpair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
            this.playMoodSoundAndCheckLight(j2, k2, chunk);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(chunkcoordintpair);
            if (++i2 < 10) continue;
            return;
        }
    }

    public void doPreChunk(int chuncX, int chuncZ, boolean loadChunk) {
        WDLHooks.onWorldClientDoPreChunk(this, chuncX, chuncZ, loadChunk);
        if (loadChunk) {
            this.clientChunkProvider.loadChunk(chuncX, chuncZ);
        } else {
            this.clientChunkProvider.unloadChunk(chuncX, chuncZ);
        }
        if (!loadChunk) {
            this.markBlockRangeForRenderUpdate(chuncX * 16, 0, chuncZ * 16, chuncX * 16 + 15, 256, chuncZ * 16 + 15);
        }
    }

    @Override
    public boolean spawnEntityInWorld(Entity entityIn) {
        boolean flag = super.spawnEntityInWorld(entityIn);
        this.entityList.add(entityIn);
        if (!flag) {
            this.entitySpawnQueue.add(entityIn);
        } else if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
        }
        return flag;
    }

    @Override
    public void removeEntity(Entity entityIn) {
        super.removeEntity(entityIn);
        this.entityList.remove(entityIn);
    }

    @Override
    protected void onEntityAdded(Entity entityIn) {
        super.onEntityAdded(entityIn);
        if (this.entitySpawnQueue.contains(entityIn)) {
            this.entitySpawnQueue.remove(entityIn);
        }
    }

    @Override
    protected void onEntityRemoved(Entity entityIn) {
        super.onEntityRemoved(entityIn);
        boolean flag = false;
        if (this.entityList.contains(entityIn)) {
            if (entityIn.isEntityAlive()) {
                this.entitySpawnQueue.add(entityIn);
                flag = true;
            } else {
                this.entityList.remove(entityIn);
            }
        }
    }

    public void addEntityToWorld(int entityID, Entity entityToSpawn) {
        Entity entity = this.getEntityByID(entityID);
        if (entity != null) {
            this.removeEntity(entity);
        }
        this.entityList.add(entityToSpawn);
        entityToSpawn.setEntityId(entityID);
        if (!this.spawnEntityInWorld(entityToSpawn)) {
            this.entitySpawnQueue.add(entityToSpawn);
        }
        this.entitiesById.addKey(entityID, entityToSpawn);
    }

    @Override
    public Entity getEntityByID(int id2) {
        return id2 == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id2);
    }

    public Entity removeEntityFromWorld(int entityID) {
        WDLHooks.onWorldClientRemoveEntityFromWorld(this, entityID);
        Entity entity = (Entity)this.entitiesById.removeObject(entityID);
        if (entity != null) {
            this.entityList.remove(entity);
            this.removeEntity(entity);
        }
        return entity;
    }

    public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state) {
        int i2 = pos.getX();
        int j2 = pos.getY();
        int k2 = pos.getZ();
        this.invalidateBlockReceiveRegion(i2, j2, k2, i2, j2, k2);
        return super.setBlockState(pos, state, 3);
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    @Override
    protected void updateWeather() {
    }

    @Override
    protected int getRenderDistanceChunks() {
        return this.mc.gameSettings.renderDistanceChunks;
    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        int i2 = 16;
        Random random = new Random();
        ItemStack itemstack = this.mc.thePlayer.getHeldItem();
        boolean flag = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE && itemstack != null && Block.getBlockFromItem(itemstack.getItem()) == Blocks.barrier;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int j2 = 0;
        while (j2 < 1000) {
            int k2 = posX + this.rand.nextInt(i2) - this.rand.nextInt(i2);
            int l2 = posY + this.rand.nextInt(i2) - this.rand.nextInt(i2);
            int i1 = posZ + this.rand.nextInt(i2) - this.rand.nextInt(i2);
            blockpos$mutableblockpos.set(k2, l2, i1);
            IBlockState iblockstate = this.getBlockState(blockpos$mutableblockpos);
            iblockstate.getBlock().randomDisplayTick(this, blockpos$mutableblockpos, iblockstate, random);
            if (flag && iblockstate.getBlock() == Blocks.barrier) {
                this.spawnParticle(EnumParticleTypes.BARRIER, (float)k2 + 0.5f, (double)((float)l2 + 0.5f), (double)((float)i1 + 0.5f), 0.0, 0.0, 0.0, new int[0]);
            }
            ++j2;
        }
    }

    public void removeAllEntities() {
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int i2 = 0;
        while (i2 < this.unloadedEntityList.size()) {
            Entity entity = (Entity)this.unloadedEntityList.get(i2);
            int j2 = entity.chunkCoordX;
            int k2 = entity.chunkCoordZ;
            if (entity.addedToChunk && this.isChunkLoaded(j2, k2, true)) {
                this.getChunkFromChunkCoords(j2, k2).removeEntity(entity);
            }
            ++i2;
        }
        int l2 = 0;
        while (l2 < this.unloadedEntityList.size()) {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(l2));
            ++l2;
        }
        this.unloadedEntityList.clear();
        int i1 = 0;
        while (i1 < this.loadedEntityList.size()) {
            block10: {
                Entity entity1;
                block9: {
                    entity1 = (Entity)this.loadedEntityList.get(i1);
                    if (entity1.ridingEntity == null) break block9;
                    if (!entity1.ridingEntity.isDead && entity1.ridingEntity.riddenByEntity == entity1) break block10;
                    entity1.ridingEntity.riddenByEntity = null;
                    entity1.ridingEntity = null;
                }
                if (entity1.isDead) {
                    int j1 = entity1.chunkCoordX;
                    int k1 = entity1.chunkCoordZ;
                    if (entity1.addedToChunk && this.isChunkLoaded(j1, k1, true)) {
                        this.getChunkFromChunkCoords(j1, k1).removeEntity(entity1);
                    }
                    this.loadedEntityList.remove(i1--);
                    this.onEntityRemoved(entity1);
                }
            }
            ++i1;
        }
    }

    @Override
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        CrashReportCategory crashreportcategory = super.addWorldInfoToCrashReport(report);
        crashreportcategory.addCrashSectionCallable("Forced entities", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(WorldClient.this.entityList.size()) + " total; " + WorldClient.this.entityList.toString();
            }
        });
        crashreportcategory.addCrashSectionCallable("Retry entities", new Callable<String>(){

            @Override
            public String call() {
                return String.valueOf(WorldClient.this.entitySpawnQueue.size()) + " total; " + WorldClient.this.entitySpawnQueue.toString();
            }
        });
        crashreportcategory.addCrashSectionCallable("Server brand", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return ((WorldClient)WorldClient.this).mc.thePlayer.getClientBrand();
            }
        });
        crashreportcategory.addCrashSectionCallable("Server type", new Callable<String>(){

            @Override
            public String call() throws Exception {
                return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }
        });
        return crashreportcategory;
    }

    public void playSoundAtPos(BlockPos pos, String soundName, float volume, float pitch, boolean distanceDelay) {
        this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, soundName, volume, pitch, distanceDelay);
    }

    @Override
    public void playSound(double x2, double y2, double z2, String soundName, float volume, float pitch, boolean distanceDelay) {
        double d0 = this.mc.getRenderViewEntity().getDistanceSq(x2, y2, z2);
        PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float)x2, (float)y2, (float)z2);
        if (distanceDelay && d0 > 100.0) {
            double d1 = Math.sqrt(d0) / 40.0;
            this.mc.getSoundHandler().playDelayedSound(positionedsoundrecord, (int)(d1 * 20.0));
        } else {
            this.mc.getSoundHandler().playSound(positionedsoundrecord);
        }
    }

    @Override
    public void makeFireworks(double x2, double y2, double z2, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
        this.mc.effectRenderer.addEffect(new EntityFirework.StarterFX(this, x2, y2, z2, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
    }

    public void setWorldScoreboard(Scoreboard scoreboardIn) {
        this.worldScoreboard = scoreboardIn;
    }

    @Override
    public void setWorldTime(long time) {
        if (time < 0L) {
            time = -time;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        } else {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }
        super.setWorldTime(time);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i2 = super.getCombinedLight(pos, lightValue);
        if (Config.isDynamicLights()) {
            i2 = DynamicLights.getCombinedLight(pos, i2);
        }
        return i2;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        this.playerUpdate = this.isPlayerActing();
        boolean flag = super.setBlockState(pos, newState, flags);
        this.playerUpdate = false;
        return flag;
    }

    private boolean isPlayerActing() {
        if (this.mc.playerController instanceof PlayerControllerOF) {
            PlayerControllerOF playercontrollerof = (PlayerControllerOF)this.mc.playerController;
            return playercontrollerof.isActing();
        }
        return false;
    }

    public boolean isPlayerUpdate() {
        return this.playerUpdate;
    }
}

