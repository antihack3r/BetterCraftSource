/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;

public final class SpawnerAnimals {
    private static final int MOB_COUNT_DIV = (int)Math.pow(17.0, 2.0);
    private final Set<ChunkCoordIntPair> eligibleChunksForSpawning = Sets.newHashSet();
    private Map<Class, EntityLiving> mapSampleEntitiesByClass = new HashMap<Class, EntityLiving>();
    private int lastPlayerChunkX = Integer.MAX_VALUE;
    private int lastPlayerChunkZ = Integer.MAX_VALUE;
    private int countChunkPos;

    public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean p_77192_4_) {
        if (!spawnHostileMobs && !spawnPeacefulMobs) {
            return 0;
        }
        boolean flag = true;
        EntityPlayer entityplayer = null;
        if (worldServerIn.playerEntities.size() == 1) {
            entityplayer = (EntityPlayer)worldServerIn.playerEntities.get(0);
            if (this.eligibleChunksForSpawning.size() > 0 && entityplayer != null && entityplayer.chunkCoordX == this.lastPlayerChunkX && entityplayer.chunkCoordZ == this.lastPlayerChunkZ) {
                flag = false;
            }
        }
        if (flag) {
            this.eligibleChunksForSpawning.clear();
            int i2 = 0;
            for (EntityPlayer entityplayer1 : worldServerIn.playerEntities) {
                if (entityplayer1.isSpectator()) continue;
                int j2 = MathHelper.floor_double(entityplayer1.posX / 16.0);
                int k2 = MathHelper.floor_double(entityplayer1.posZ / 16.0);
                int l2 = 8;
                int i1 = -l2;
                while (i1 <= l2) {
                    int j1 = -l2;
                    while (j1 <= l2) {
                        boolean flag1 = i1 == -l2 || i1 == l2 || j1 == -l2 || j1 == l2;
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + j2, j1 + k2);
                        if (!this.eligibleChunksForSpawning.contains(chunkcoordintpair)) {
                            ++i2;
                            if (!flag1 && worldServerIn.getWorldBorder().contains(chunkcoordintpair)) {
                                this.eligibleChunksForSpawning.add(chunkcoordintpair);
                            }
                        }
                        ++j1;
                    }
                    ++i1;
                }
            }
            this.countChunkPos = i2;
            if (entityplayer != null) {
                this.lastPlayerChunkX = entityplayer.chunkCoordX;
                this.lastPlayerChunkZ = entityplayer.chunkCoordZ;
            }
        }
        int j4 = 0;
        BlockPos blockpos2 = worldServerIn.getSpawnPoint();
        BlockPosM blockposm = new BlockPosM(0, 0, 0);
        new BlockPos.MutableBlockPos();
        EnumCreatureType[] enumCreatureTypeArray = EnumCreatureType.values();
        int n2 = enumCreatureTypeArray.length;
        int n3 = 0;
        while (n3 < n2) {
            int l4;
            int k4;
            EnumCreatureType enumcreaturetype = enumCreatureTypeArray[n3];
            if (!(enumcreaturetype.getPeacefulCreature() && !spawnPeacefulMobs || !enumcreaturetype.getPeacefulCreature() && !spawnHostileMobs || enumcreaturetype.getAnimal() && !p_77192_4_ || (k4 = Reflector.ForgeWorld_countEntities.exists() ? Reflector.callInt(worldServerIn, Reflector.ForgeWorld_countEntities, new Object[]{enumcreaturetype, true}) : worldServerIn.countEntities(enumcreaturetype.getCreatureClass())) > (l4 = enumcreaturetype.getMaxNumberOfCreature() * this.countChunkPos / MOB_COUNT_DIV))) {
                Collection<ChunkCoordIntPair> collection = this.eligibleChunksForSpawning;
                if (Reflector.ForgeHooksClient.exists()) {
                    ArrayList<ChunkCoordIntPair> arraylist = Lists.newArrayList(collection);
                    Collections.shuffle(arraylist);
                    collection = arraylist;
                }
                block6: for (ChunkCoordIntPair chunkcoordintpair1 : collection) {
                    BlockPosM blockpos = SpawnerAnimals.getRandomChunkPosition(worldServerIn, chunkcoordintpair1.chunkXPos, chunkcoordintpair1.chunkZPos, blockposm);
                    int k1 = ((Vec3i)blockpos).getX();
                    int l1 = ((Vec3i)blockpos).getY();
                    int i2 = ((Vec3i)blockpos).getZ();
                    Block block = worldServerIn.getBlockState(blockpos).getBlock();
                    if (block.isNormalCube()) continue;
                    int j2 = 0;
                    int k2 = 0;
                    while (k2 < 3) {
                        int l2 = k1;
                        int i3 = l1;
                        int j3 = i2;
                        int k3 = 6;
                        BiomeGenBase.SpawnListEntry biomegenbase$spawnlistentry = null;
                        IEntityLivingData ientitylivingdata = null;
                        int l3 = 0;
                        while (l3 < 4) {
                            BlockPos blockpos1 = new BlockPos(l2 += worldServerIn.rand.nextInt(k3) - worldServerIn.rand.nextInt(k3), i3 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1), j3 += worldServerIn.rand.nextInt(k3) - worldServerIn.rand.nextInt(k3));
                            float f2 = (float)l2 + 0.5f;
                            float f1 = (float)j3 + 0.5f;
                            if (!worldServerIn.isAnyPlayerWithinRangeAt(f2, i3, f1, 24.0) && blockpos2.distanceSq(f2, i3, f1) >= 576.0) {
                                if (biomegenbase$spawnlistentry == null && (biomegenbase$spawnlistentry = worldServerIn.getSpawnListEntryForTypeAt(enumcreaturetype, blockpos1)) == null) break;
                                if (worldServerIn.canCreatureTypeSpawnHere(enumcreaturetype, biomegenbase$spawnlistentry, blockpos1) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(biomegenbase$spawnlistentry.entityClass), worldServerIn, blockpos1)) {
                                    boolean flag2;
                                    EntityLiving entityliving;
                                    try {
                                        entityliving = this.mapSampleEntitiesByClass.get(biomegenbase$spawnlistentry.entityClass);
                                        if (entityliving == null) {
                                            entityliving = biomegenbase$spawnlistentry.entityClass.getConstructor(World.class).newInstance(worldServerIn);
                                            this.mapSampleEntitiesByClass.put(biomegenbase$spawnlistentry.entityClass, entityliving);
                                        }
                                    }
                                    catch (Exception exception1) {
                                        exception1.printStackTrace();
                                        return j4;
                                    }
                                    entityliving.setLocationAndAngles(f2, i3, f1, worldServerIn.rand.nextFloat() * 360.0f, 0.0f);
                                    boolean bl2 = Reflector.ForgeEventFactory_canEntitySpawn.exists() ? ReflectorForge.canEntitySpawn(entityliving, worldServerIn, f2, i3, f1) : (flag2 = entityliving.getCanSpawnHere() && entityliving.isNotColliding());
                                    if (flag2) {
                                        int i4;
                                        this.mapSampleEntitiesByClass.remove(biomegenbase$spawnlistentry.entityClass);
                                        if (!ReflectorForge.doSpecialSpawn(entityliving, worldServerIn, f2, i3, f1)) {
                                            ientitylivingdata = entityliving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);
                                        }
                                        if (entityliving.isNotColliding()) {
                                            ++j2;
                                            worldServerIn.spawnEntityInWorld(entityliving);
                                        }
                                        int n4 = i4 = Reflector.ForgeEventFactory_getMaxSpawnPackSize.exists() ? Reflector.callInt(Reflector.ForgeEventFactory_getMaxSpawnPackSize, entityliving) : entityliving.getMaxSpawnedInChunk();
                                        if (j2 >= i4) continue block6;
                                    }
                                    j4 += j2;
                                }
                            }
                            ++l3;
                        }
                        ++k2;
                    }
                }
            }
            ++n3;
        }
        return j4;
    }

    protected static BlockPos getRandomChunkPosition(World worldIn, int x2, int z2) {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x2, z2);
        int i2 = x2 * 16 + worldIn.rand.nextInt(16);
        int j2 = z2 * 16 + worldIn.rand.nextInt(16);
        int k2 = MathHelper.roundUp(chunk.getHeight(new BlockPos(i2, 0, j2)) + 1, 16);
        int l2 = worldIn.rand.nextInt(k2 > 0 ? k2 : chunk.getTopFilledSegment() + 16 - 1);
        return new BlockPos(i2, l2, j2);
    }

    private static BlockPosM getRandomChunkPosition(World p_getRandomChunkPosition_0_, int p_getRandomChunkPosition_1_, int p_getRandomChunkPosition_2_, BlockPosM p_getRandomChunkPosition_3_) {
        Chunk chunk = p_getRandomChunkPosition_0_.getChunkFromChunkCoords(p_getRandomChunkPosition_1_, p_getRandomChunkPosition_2_);
        int i2 = p_getRandomChunkPosition_1_ * 16 + p_getRandomChunkPosition_0_.rand.nextInt(16);
        int j2 = p_getRandomChunkPosition_2_ * 16 + p_getRandomChunkPosition_0_.rand.nextInt(16);
        int k2 = MathHelper.roundUp(chunk.getHeightValue(i2 & 0xF, j2 & 0xF) + 1, 16);
        int l2 = p_getRandomChunkPosition_0_.rand.nextInt(k2 > 0 ? k2 : chunk.getTopFilledSegment() + 16 - 1);
        p_getRandomChunkPosition_3_.setXyz(i2, l2, j2);
        return p_getRandomChunkPosition_3_;
    }

    public static boolean canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
        boolean flag1;
        boolean flag;
        if (!worldIn.getWorldBorder().contains(pos)) {
            return false;
        }
        if (spawnPlacementTypeIn == null) {
            return false;
        }
        Block block = worldIn.getBlockState(pos).getBlock();
        if (spawnPlacementTypeIn == EntityLiving.SpawnPlacementType.IN_WATER) {
            return block.getMaterial().isLiquid() && worldIn.getBlockState(pos.down()).getBlock().getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
        }
        BlockPos blockpos = pos.down();
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        boolean bl2 = flag = Reflector.ForgeBlock_canCreatureSpawn.exists() ? Reflector.callBoolean(iblockstate.getBlock(), Reflector.ForgeBlock_canCreatureSpawn, new Object[]{worldIn, blockpos, spawnPlacementTypeIn}) : World.doesBlockHaveSolidTopSurface(worldIn, blockpos);
        if (!flag) {
            return false;
        }
        Block block1 = worldIn.getBlockState(blockpos).getBlock();
        boolean bl3 = flag1 = block1 != Blocks.bedrock && block1 != Blocks.barrier;
        return flag1 && !block.isNormalCube() && !block.getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    public static void performWorldGenSpawning(World worldIn, BiomeGenBase biomeIn, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random randomIn) {
        block9: {
            List<BiomeGenBase.SpawnListEntry> list = biomeIn.getSpawnableList(EnumCreatureType.CREATURE);
            if (list.isEmpty()) break block9;
            while (randomIn.nextFloat() < biomeIn.getSpawningChance()) {
                BiomeGenBase.SpawnListEntry biomegenbase$spawnlistentry = WeightedRandom.getRandomItem(worldIn.rand, list);
                int i2 = biomegenbase$spawnlistentry.minGroupCount + randomIn.nextInt(1 + biomegenbase$spawnlistentry.maxGroupCount - biomegenbase$spawnlistentry.minGroupCount);
                IEntityLivingData ientitylivingdata = null;
                int j2 = p_77191_2_ + randomIn.nextInt(p_77191_4_);
                int k2 = p_77191_3_ + randomIn.nextInt(p_77191_5_);
                int l2 = j2;
                int i1 = k2;
                int j1 = 0;
                while (j1 < i2) {
                    boolean flag = false;
                    int k1 = 0;
                    while (!flag && k1 < 4) {
                        block8: {
                            block10: {
                                Object object;
                                EntityLiving entityliving;
                                BlockPos blockpos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(j2, 0, k2));
                                if (!SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, worldIn, blockpos)) break block10;
                                try {
                                    entityliving = biomegenbase$spawnlistentry.entityClass.getConstructor(World.class).newInstance(worldIn);
                                }
                                catch (Exception exception1) {
                                    exception1.printStackTrace();
                                    break block8;
                                }
                                if (Reflector.ForgeEventFactory_canEntitySpawn.exists() && (object = Reflector.call(Reflector.ForgeEventFactory_canEntitySpawn, entityliving, worldIn, Float.valueOf((float)j2 + 0.5f), blockpos.getY(), Float.valueOf((float)k2 + 0.5f))) == ReflectorForge.EVENT_RESULT_DENY) break block8;
                                entityliving.setLocationAndAngles((float)j2 + 0.5f, blockpos.getY(), (float)k2 + 0.5f, randomIn.nextFloat() * 360.0f, 0.0f);
                                worldIn.spawnEntityInWorld(entityliving);
                                ientitylivingdata = entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), ientitylivingdata);
                                flag = true;
                            }
                            j2 += randomIn.nextInt(5) - randomIn.nextInt(5);
                            k2 += randomIn.nextInt(5) - randomIn.nextInt(5);
                            while (j2 < p_77191_2_ || j2 >= p_77191_2_ + p_77191_4_ || k2 < p_77191_3_ || k2 >= p_77191_3_ + p_77191_4_) {
                                j2 = l2 + randomIn.nextInt(5) - randomIn.nextInt(5);
                                k2 = i1 + randomIn.nextInt(5) - randomIn.nextInt(5);
                            }
                        }
                        ++k1;
                    }
                    ++j1;
                }
            }
        }
    }
}

