/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCavesHell;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell
implements IChunkProvider {
    private final World worldObj;
    private final boolean field_177466_i;
    private final Random hellRNG;
    private double[] slowsandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] netherrackExclusivityNoise = new double[256];
    private double[] noiseField;
    private final NoiseGeneratorOctaves netherNoiseGen1;
    private final NoiseGeneratorOctaves netherNoiseGen2;
    private final NoiseGeneratorOctaves netherNoiseGen3;
    private final NoiseGeneratorOctaves slowsandGravelNoiseGen;
    private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
    public final NoiseGeneratorOctaves netherNoiseGen6;
    public final NoiseGeneratorOctaves netherNoiseGen7;
    private final WorldGenFire field_177470_t = new WorldGenFire();
    private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
    private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
    private final WorldGenerator field_177467_w = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
    private final WorldGenHellLava field_177473_x = new WorldGenHellLava(Blocks.flowing_lava, true);
    private final WorldGenHellLava field_177472_y = new WorldGenHellLava(Blocks.flowing_lava, false);
    private final GeneratorBushFeature field_177471_z = new GeneratorBushFeature(Blocks.brown_mushroom);
    private final GeneratorBushFeature field_177465_A = new GeneratorBushFeature(Blocks.red_mushroom);
    private final MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    private final MapGenBase netherCaveGenerator = new MapGenCavesHell();
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;

    public ChunkProviderHell(World worldIn, boolean p_i45637_2_, long seed) {
        this.worldObj = worldIn;
        this.field_177466_i = p_i45637_2_;
        this.hellRNG = new Random(seed);
        this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        worldIn.setSeaLevel(63);
    }

    public void func_180515_a(int p_180515_1_, int p_180515_2_, ChunkPrimer p_180515_3_) {
        int i2 = 4;
        int j2 = this.worldObj.getSeaLevel() / 2 + 1;
        int k2 = i2 + 1;
        int l2 = 17;
        int i1 = i2 + 1;
        this.noiseField = this.initializeNoiseField(this.noiseField, p_180515_1_ * i2, 0, p_180515_2_ * i2, k2, l2, i1);
        int j1 = 0;
        while (j1 < i2) {
            int k1 = 0;
            while (k1 < i2) {
                int l1 = 0;
                while (l1 < 16) {
                    double d0 = 0.125;
                    double d1 = this.noiseField[((j1 + 0) * i1 + k1 + 0) * l2 + l1 + 0];
                    double d2 = this.noiseField[((j1 + 0) * i1 + k1 + 1) * l2 + l1 + 0];
                    double d3 = this.noiseField[((j1 + 1) * i1 + k1 + 0) * l2 + l1 + 0];
                    double d4 = this.noiseField[((j1 + 1) * i1 + k1 + 1) * l2 + l1 + 0];
                    double d5 = (this.noiseField[((j1 + 0) * i1 + k1 + 0) * l2 + l1 + 1] - d1) * d0;
                    double d6 = (this.noiseField[((j1 + 0) * i1 + k1 + 1) * l2 + l1 + 1] - d2) * d0;
                    double d7 = (this.noiseField[((j1 + 1) * i1 + k1 + 0) * l2 + l1 + 1] - d3) * d0;
                    double d8 = (this.noiseField[((j1 + 1) * i1 + k1 + 1) * l2 + l1 + 1] - d4) * d0;
                    int i22 = 0;
                    while (i22 < 8) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;
                        int j22 = 0;
                        while (j22 < 4) {
                            double d14 = 0.25;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;
                            int k22 = 0;
                            while (k22 < 4) {
                                IBlockState iblockstate = null;
                                if (l1 * 8 + i22 < j2) {
                                    iblockstate = Blocks.lava.getDefaultState();
                                }
                                if (d15 > 0.0) {
                                    iblockstate = Blocks.netherrack.getDefaultState();
                                }
                                int l22 = j22 + j1 * 4;
                                int i3 = i22 + l1 * 8;
                                int j3 = k22 + k1 * 4;
                                p_180515_3_.setBlockState(l22, i3, j3, iblockstate);
                                d15 += d16;
                                ++k22;
                            }
                            d10 += d12;
                            d11 += d13;
                            ++j22;
                        }
                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                        ++i22;
                    }
                    ++l1;
                }
                ++k1;
            }
            ++j1;
        }
    }

    public void func_180516_b(int p_180516_1_, int p_180516_2_, ChunkPrimer p_180516_3_) {
        int i2 = this.worldObj.getSeaLevel() + 1;
        double d0 = 0.03125;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, d0, d0, 1.0);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, p_180516_1_ * 16, 109, p_180516_2_ * 16, 16, 1, 16, d0, 1.0, d0);
        this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, d0 * 2.0, d0 * 2.0, d0 * 2.0);
        int j2 = 0;
        while (j2 < 16) {
            int k2 = 0;
            while (k2 < 16) {
                boolean flag = this.slowsandNoise[j2 + k2 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                boolean flag1 = this.gravelNoise[j2 + k2 * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                int l2 = (int)(this.netherrackExclusivityNoise[j2 + k2 * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
                int i1 = -1;
                IBlockState iblockstate = Blocks.netherrack.getDefaultState();
                IBlockState iblockstate1 = Blocks.netherrack.getDefaultState();
                int j1 = 127;
                while (j1 >= 0) {
                    if (j1 < 127 - this.hellRNG.nextInt(5) && j1 > this.hellRNG.nextInt(5)) {
                        IBlockState iblockstate2 = p_180516_3_.getBlockState(k2, j1, j2);
                        if (iblockstate2.getBlock() != null && iblockstate2.getBlock().getMaterial() != Material.air) {
                            if (iblockstate2.getBlock() == Blocks.netherrack) {
                                if (i1 == -1) {
                                    if (l2 <= 0) {
                                        iblockstate = null;
                                        iblockstate1 = Blocks.netherrack.getDefaultState();
                                    } else if (j1 >= i2 - 4 && j1 <= i2 + 1) {
                                        iblockstate = Blocks.netherrack.getDefaultState();
                                        iblockstate1 = Blocks.netherrack.getDefaultState();
                                        if (flag1) {
                                            iblockstate = Blocks.gravel.getDefaultState();
                                            iblockstate1 = Blocks.netherrack.getDefaultState();
                                        }
                                        if (flag) {
                                            iblockstate = Blocks.soul_sand.getDefaultState();
                                            iblockstate1 = Blocks.soul_sand.getDefaultState();
                                        }
                                    }
                                    if (j1 < i2 && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air)) {
                                        iblockstate = Blocks.lava.getDefaultState();
                                    }
                                    i1 = l2;
                                    if (j1 >= i2 - 1) {
                                        p_180516_3_.setBlockState(k2, j1, j2, iblockstate);
                                    } else {
                                        p_180516_3_.setBlockState(k2, j1, j2, iblockstate1);
                                    }
                                } else if (i1 > 0) {
                                    --i1;
                                    p_180516_3_.setBlockState(k2, j1, j2, iblockstate1);
                                }
                            }
                        } else {
                            i1 = -1;
                        }
                    } else {
                        p_180516_3_.setBlockState(k2, j1, j2, Blocks.bedrock.getDefaultState());
                    }
                    --j1;
                }
                ++k2;
            }
            ++j2;
        }
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        this.hellRNG.setSeed((long)x2 * 341873128712L + (long)z2 * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.func_180515_a(x2, z2, chunkprimer);
        this.func_180516_b(x2, z2, chunkprimer);
        this.netherCaveGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        if (this.field_177466_i) {
            this.genNetherBridge.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x2, z2);
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, x2 * 16, z2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();
        int i2 = 0;
        while (i2 < abyte.length) {
            abyte[i2] = (byte)abiomegenbase[i2].biomeID;
            ++i2;
        }
        chunk.resetRelightChecks();
        return chunk;
    }

    private double[] initializeNoiseField(double[] p_73164_1_, int p_73164_2_, int p_73164_3_, int p_73164_4_, int p_73164_5_, int p_73164_6_, int p_73164_7_) {
        if (p_73164_1_ == null) {
            p_73164_1_ = new double[p_73164_5_ * p_73164_6_ * p_73164_7_];
        }
        double d0 = 684.412;
        double d1 = 2053.236;
        this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 1.0, 0.0, 1.0);
        this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 100.0, 0.0, 100.0);
        this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0 / 80.0, d1 / 60.0, d0 / 80.0);
        this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
        this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
        int i2 = 0;
        double[] adouble = new double[p_73164_6_];
        int j2 = 0;
        while (j2 < p_73164_6_) {
            adouble[j2] = Math.cos((double)j2 * Math.PI * 6.0 / (double)p_73164_6_) * 2.0;
            double d2 = j2;
            if (j2 > p_73164_6_ / 2) {
                d2 = p_73164_6_ - 1 - j2;
            }
            if (d2 < 4.0) {
                d2 = 4.0 - d2;
                int n2 = j2;
                adouble[n2] = adouble[n2] - d2 * d2 * d2 * 10.0;
            }
            ++j2;
        }
        int l2 = 0;
        while (l2 < p_73164_5_) {
            int i1 = 0;
            while (i1 < p_73164_7_) {
                double d3 = 0.0;
                int k2 = 0;
                while (k2 < p_73164_6_) {
                    double d4 = 0.0;
                    double d5 = adouble[k2];
                    double d6 = this.noiseData2[i2] / 512.0;
                    double d7 = this.noiseData3[i2] / 512.0;
                    double d8 = (this.noiseData1[i2] / 10.0 + 1.0) / 2.0;
                    d4 = d8 < 0.0 ? d6 : (d8 > 1.0 ? d7 : d6 + (d7 - d6) * d8);
                    d4 -= d5;
                    if (k2 > p_73164_6_ - 4) {
                        double d9 = (float)(k2 - (p_73164_6_ - 4)) / 3.0f;
                        d4 = d4 * (1.0 - d9) + -10.0 * d9;
                    }
                    if ((double)k2 < d3) {
                        double d10 = (d3 - (double)k2) / 4.0;
                        d10 = MathHelper.clamp_double(d10, 0.0, 1.0);
                        d4 = d4 * (1.0 - d10) + -10.0 * d10;
                    }
                    p_73164_1_[i2] = d4;
                    ++i2;
                    ++k2;
                }
                ++i1;
            }
            ++l2;
        }
        return p_73164_1_;
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
        BlockFalling.fallInstantly = true;
        BlockPos blockpos = new BlockPos(x2 * 16, 0, z2 * 16);
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x2, z2);
        this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, chunkcoordintpair);
        int i2 = 0;
        while (i2 < 8) {
            this.field_177472_y.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1) {
            this.field_177470_t.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
            ++j2;
        }
        int k2 = 0;
        while (k2 < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1)) {
            this.field_177469_u.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
            ++k2;
        }
        int l2 = 0;
        while (l2 < 10) {
            this.field_177468_v.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
            ++l2;
        }
        if (this.hellRNG.nextBoolean()) {
            this.field_177471_z.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
        }
        if (this.hellRNG.nextBoolean()) {
            this.field_177465_A.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
        }
        int i1 = 0;
        while (i1 < 16) {
            this.field_177467_w.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
            ++i1;
        }
        int j1 = 0;
        while (j1 < 16) {
            this.field_177473_x.generate(this.worldObj, this.hellRNG, blockpos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
            ++j1;
        }
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x2, int z2) {
        return false;
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
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "HellRandomLevelSource";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        if (creatureType == EnumCreatureType.MONSTER) {
            if (this.genNetherBridge.func_175795_b(pos)) {
                return this.genNetherBridge.getSpawnList();
            }
            if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
                return this.genNetherBridge.getSpawnList();
            }
        }
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
        this.genNetherBridge.generate(this, this.worldObj, x2, z2, null);
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

