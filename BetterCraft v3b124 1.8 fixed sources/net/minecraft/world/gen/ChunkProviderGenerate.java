/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderGenerate
implements IChunkProvider {
    private Random rand;
    private NoiseGeneratorOctaves field_147431_j;
    private NoiseGeneratorOctaves field_147432_k;
    private NoiseGeneratorOctaves field_147429_l;
    private NoiseGeneratorPerlin field_147430_m;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private World worldObj;
    private final boolean mapFeaturesEnabled;
    private WorldType field_177475_o;
    private final double[] field_147434_q;
    private final float[] parabolicField;
    private ChunkProviderSettings settings;
    private Block oceanBlockTmpl = Blocks.water;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private MapGenVillage villageGenerator = new MapGenVillage();
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    private MapGenBase ravineGenerator = new MapGenRavine();
    private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();
    private BiomeGenBase[] biomesForGeneration;
    double[] mainNoiseArray;
    double[] lowerLimitNoiseArray;
    double[] upperLimitNoiseArray;
    double[] depthNoiseArray;

    public ChunkProviderGenerate(World worldIn, long seed, boolean generateStructures, String structuresJson) {
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = generateStructures;
        this.field_177475_o = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];
        int i2 = -2;
        while (i2 <= 2) {
            int j2 = -2;
            while (j2 <= 2) {
                float f2;
                this.parabolicField[i2 + 2 + (j2 + 2) * 5] = f2 = 10.0f / MathHelper.sqrt_float((float)(i2 * i2 + j2 * j2) + 0.2f);
                ++j2;
            }
            ++i2;
        }
        if (structuresJson != null) {
            this.settings = ChunkProviderSettings.Factory.jsonToFactory(structuresJson).func_177864_b();
            this.oceanBlockTmpl = this.settings.useLavaOceans ? Blocks.lava : Blocks.water;
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }

    public void setBlocksInChunk(int x2, int z2, ChunkPrimer primer) {
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x2 * 4 - 2, z2 * 4 - 2, 10, 10);
        this.func_147423_a(x2 * 4, 0, z2 * 4);
        int i2 = 0;
        while (i2 < 4) {
            int j2 = i2 * 5;
            int k2 = (i2 + 1) * 5;
            int l2 = 0;
            while (l2 < 4) {
                int i1 = (j2 + l2) * 33;
                int j1 = (j2 + l2 + 1) * 33;
                int k1 = (k2 + l2) * 33;
                int l1 = (k2 + l2 + 1) * 33;
                int i22 = 0;
                while (i22 < 32) {
                    double d0 = 0.125;
                    double d1 = this.field_147434_q[i1 + i22];
                    double d2 = this.field_147434_q[j1 + i22];
                    double d3 = this.field_147434_q[k1 + i22];
                    double d4 = this.field_147434_q[l1 + i22];
                    double d5 = (this.field_147434_q[i1 + i22 + 1] - d1) * d0;
                    double d6 = (this.field_147434_q[j1 + i22 + 1] - d2) * d0;
                    double d7 = (this.field_147434_q[k1 + i22 + 1] - d3) * d0;
                    double d8 = (this.field_147434_q[l1 + i22 + 1] - d4) * d0;
                    int j22 = 0;
                    while (j22 < 8) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;
                        int k22 = 0;
                        while (k22 < 4) {
                            double d14 = 0.25;
                            double d16 = (d11 - d10) * d14;
                            double lvt_45_1_ = d10 - d16;
                            int l22 = 0;
                            while (l22 < 4) {
                                double d15;
                                lvt_45_1_ += d16;
                                if (d15 > 0.0) {
                                    primer.setBlockState(i2 * 4 + k22, i22 * 8 + j22, l2 * 4 + l22, Blocks.stone.getDefaultState());
                                } else if (i22 * 8 + j22 < this.settings.seaLevel) {
                                    primer.setBlockState(i2 * 4 + k22, i22 * 8 + j22, l2 * 4 + l22, this.oceanBlockTmpl.getDefaultState());
                                }
                                ++l22;
                            }
                            d10 += d12;
                            d11 += d13;
                            ++k22;
                        }
                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                        ++j22;
                    }
                    ++i22;
                }
                ++l2;
            }
            ++i2;
        }
    }

    public void replaceBlocksForBiome(int x2, int z2, ChunkPrimer primer, BiomeGenBase[] biomeGens) {
        double d0 = 0.03125;
        this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, x2 * 16, z2 * 16, 16, 16, d0 * 2.0, d0 * 2.0, 1.0);
        int i2 = 0;
        while (i2 < 16) {
            int j2 = 0;
            while (j2 < 16) {
                BiomeGenBase biomegenbase = biomeGens[j2 + i2 * 16];
                biomegenbase.genTerrainBlocks(this.worldObj, this.rand, primer, x2 * 16 + i2, z2 * 16 + j2, this.stoneNoise[j2 + i2 * 16]);
                ++j2;
            }
            ++i2;
        }
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        this.rand.setSeed((long)x2 * 341873128712L + (long)z2 * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.setBlocksInChunk(x2, z2, chunkprimer);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x2 * 16, z2 * 16, 16, 16);
        this.replaceBlocksForBiome(x2, z2, chunkprimer, this.biomesForGeneration);
        if (this.settings.useCaves) {
            this.caveGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useRavines) {
            this.ravineGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x2, z2);
        byte[] abyte = chunk.getBiomeArray();
        int i2 = 0;
        while (i2 < abyte.length) {
            abyte[i2] = (byte)this.biomesForGeneration[i2].biomeID;
            ++i2;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    private void func_147423_a(int x2, int y2, int z2) {
        this.depthNoiseArray = this.noiseGen6.generateNoiseOctaves(this.depthNoiseArray, x2, z2, 5, 5, this.settings.depthNoiseScaleX, this.settings.depthNoiseScaleZ, this.settings.depthNoiseScaleExponent);
        float f2 = this.settings.coordinateScale;
        float f1 = this.settings.heightScale;
        this.mainNoiseArray = this.field_147429_l.generateNoiseOctaves(this.mainNoiseArray, x2, y2, z2, 5, 33, 5, f2 / this.settings.mainNoiseScaleX, f1 / this.settings.mainNoiseScaleY, f2 / this.settings.mainNoiseScaleZ);
        this.lowerLimitNoiseArray = this.field_147431_j.generateNoiseOctaves(this.lowerLimitNoiseArray, x2, y2, z2, 5, 33, 5, f2, f1, f2);
        this.upperLimitNoiseArray = this.field_147432_k.generateNoiseOctaves(this.upperLimitNoiseArray, x2, y2, z2, 5, 33, 5, f2, f1, f2);
        z2 = 0;
        x2 = 0;
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        while (k2 < 5) {
            int l2 = 0;
            while (l2 < 5) {
                float f22 = 0.0f;
                float f3 = 0.0f;
                float f4 = 0.0f;
                int i1 = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[k2 + 2 + (l2 + 2) * 10];
                int j1 = -i1;
                while (j1 <= i1) {
                    int k1 = -i1;
                    while (k1 <= i1) {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[k2 + j1 + 2 + (l2 + k1 + 2) * 10];
                        float f5 = this.settings.biomeDepthOffSet + biomegenbase1.minHeight * this.settings.biomeDepthWeight;
                        float f6 = this.settings.biomeScaleOffset + biomegenbase1.maxHeight * this.settings.biomeScaleWeight;
                        if (this.field_177475_o == WorldType.AMPLIFIED && f5 > 0.0f) {
                            f5 = 1.0f + f5 * 2.0f;
                            f6 = 1.0f + f6 * 4.0f;
                        }
                        float f7 = this.parabolicField[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0f);
                        if (biomegenbase1.minHeight > biomegenbase.minHeight) {
                            f7 /= 2.0f;
                        }
                        f22 += f6 * f7;
                        f3 += f5 * f7;
                        f4 += f7;
                        ++k1;
                    }
                    ++j1;
                }
                f22 /= f4;
                f3 /= f4;
                f22 = f22 * 0.9f + 0.1f;
                f3 = (f3 * 4.0f - 1.0f) / 8.0f;
                double d7 = this.depthNoiseArray[j2] / 8000.0;
                if (d7 < 0.0) {
                    d7 = -d7 * 0.3;
                }
                if ((d7 = d7 * 3.0 - 2.0) < 0.0) {
                    if ((d7 /= 2.0) < -1.0) {
                        d7 = -1.0;
                    }
                    d7 /= 1.4;
                    d7 /= 2.0;
                } else {
                    if (d7 > 1.0) {
                        d7 = 1.0;
                    }
                    d7 /= 8.0;
                }
                ++j2;
                double d8 = f3;
                double d9 = f22;
                d8 += d7 * 0.2;
                d8 = d8 * (double)this.settings.baseSize / 8.0;
                double d0 = (double)this.settings.baseSize + d8 * 4.0;
                int l1 = 0;
                while (l1 < 33) {
                    double d1 = ((double)l1 - d0) * (double)this.settings.stretchY * 128.0 / 256.0 / d9;
                    if (d1 < 0.0) {
                        d1 *= 4.0;
                    }
                    double d2 = this.lowerLimitNoiseArray[i2] / (double)this.settings.lowerLimitScale;
                    double d3 = this.upperLimitNoiseArray[i2] / (double)this.settings.upperLimitScale;
                    double d4 = (this.mainNoiseArray[i2] / 10.0 + 1.0) / 2.0;
                    double d5 = MathHelper.denormalizeClamp(d2, d3, d4) - d1;
                    if (l1 > 29) {
                        double d6 = (float)(l1 - 29) / 3.0f;
                        d5 = d5 * (1.0 - d6) + -10.0 * d6;
                    }
                    this.field_147434_q[i2] = d5;
                    ++i2;
                    ++l1;
                }
                ++l2;
            }
            ++k2;
        }
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
        BlockFalling.fallInstantly = true;
        int i2 = x2 * 16;
        int j2 = z2 * 16;
        BlockPos blockpos = new BlockPos(i2, 0, j2);
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long k2 = this.rand.nextLong() / 2L * 2L + 1L;
        long l2 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x2 * k2 + (long)z2 * l2 ^ this.worldObj.getSeed());
        boolean flag = false;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x2, z2);
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, chunkcoordintpair);
        }
        if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && this.settings.useWaterLakes && !flag && this.rand.nextInt(this.settings.waterLakeChance) == 0) {
            int i1 = this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(256);
            int k1 = this.rand.nextInt(16) + 8;
            new WorldGenLakes(Blocks.water).generate(this.worldObj, this.rand, blockpos.add(i1, j1, k1));
        }
        if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes) {
            int i22 = this.rand.nextInt(16) + 8;
            int l22 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int k3 = this.rand.nextInt(16) + 8;
            if (l22 < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) {
                new WorldGenLakes(Blocks.lava).generate(this.worldObj, this.rand, blockpos.add(i22, l22, k3));
            }
        }
        if (this.settings.useDungeons) {
            int j22 = 0;
            while (j22 < this.settings.dungeonChance) {
                int i3 = this.rand.nextInt(16) + 8;
                int l3 = this.rand.nextInt(256);
                int l1 = this.rand.nextInt(16) + 8;
                new WorldGenDungeons().generate(this.worldObj, this.rand, blockpos.add(i3, l3, l1));
                ++j22;
            }
        }
        biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(i2, 0, j2));
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, i2 + 8, j2 + 8, 16, 16, this.rand);
        blockpos = blockpos.add(8, 0, 8);
        int k22 = 0;
        while (k22 < 16) {
            int j3 = 0;
            while (j3 < 16) {
                BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k22, 0, j3));
                BlockPos blockpos2 = blockpos1.down();
                if (this.worldObj.canBlockFreezeWater(blockpos2)) {
                    this.worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(), 2);
                }
                if (this.worldObj.canSnowAt(blockpos1, true)) {
                    this.worldObj.setBlockState(blockpos1, Blocks.snow_layer.getDefaultState(), 2);
                }
                ++j3;
            }
            ++k22;
        }
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x2, int z2) {
        boolean flag = false;
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L) {
            flag |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x2, z2));
        }
        return flag;
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
        return "RandomLevelSource";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        if (this.mapFeaturesEnabled) {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.func_175798_a(pos)) {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }
            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos)) {
                return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
            }
        }
        return biomegenbase.getSpawnableList(creatureType);
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
        if (this.settings.useMineShafts && this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, x2, z2, null);
        }
        if (this.settings.useVillages && this.mapFeaturesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, x2, z2, null);
        }
        if (this.settings.useStrongholds && this.mapFeaturesEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, x2, z2, null);
        }
        if (this.settings.useTemples && this.mapFeaturesEnabled) {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x2, z2, null);
        }
        if (this.settings.useMonuments && this.mapFeaturesEnabled) {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x2, z2, null);
        }
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

