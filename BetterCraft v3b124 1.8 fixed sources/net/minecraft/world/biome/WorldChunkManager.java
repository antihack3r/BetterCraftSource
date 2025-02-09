/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager {
    private GenLayer genBiomes;
    private GenLayer biomeIndexLayer;
    private BiomeCache biomeCache = new BiomeCache(this);
    private List<BiomeGenBase> biomesToSpawnIn = Lists.newArrayList();
    private String generatorOptions = "";

    protected WorldChunkManager() {
        this.biomesToSpawnIn.add(BiomeGenBase.forest);
        this.biomesToSpawnIn.add(BiomeGenBase.plains);
        this.biomesToSpawnIn.add(BiomeGenBase.taiga);
        this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
        this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
        this.biomesToSpawnIn.add(BiomeGenBase.jungle);
        this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
    }

    public WorldChunkManager(long seed, WorldType worldTypeIn, String options) {
        this();
        this.generatorOptions = options;
        GenLayer[] agenlayer = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, options);
        this.genBiomes = agenlayer[0];
        this.biomeIndexLayer = agenlayer[1];
    }

    public WorldChunkManager(World worldIn) {
        this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
    }

    public List<BiomeGenBase> getBiomesToSpawnIn() {
        return this.biomesToSpawnIn;
    }

    public BiomeGenBase getBiomeGenerator(BlockPos pos) {
        return this.getBiomeGenerator(pos, null);
    }

    public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn) {
        return this.biomeCache.func_180284_a(pos.getX(), pos.getZ(), biomeGenBaseIn);
    }

    public float[] getRainfall(float[] listToReuse, int x2, int z2, int width, int length) {
        IntCache.resetIntCache();
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new float[width * length];
        }
        int[] aint = this.biomeIndexLayer.getInts(x2, z2, width, length);
        int i2 = 0;
        while (i2 < width * length) {
            try {
                float f2 = (float)BiomeGenBase.getBiomeFromBiomeList(aint[i2], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0f;
                if (f2 > 1.0f) {
                    f2 = 1.0f;
                }
                listToReuse[i2] = f2;
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
                crashreportcategory.addCrashSection("biome id", i2);
                crashreportcategory.addCrashSection("downfalls[] size", listToReuse.length);
                crashreportcategory.addCrashSection("x", x2);
                crashreportcategory.addCrashSection("z", z2);
                crashreportcategory.addCrashSection("w", width);
                crashreportcategory.addCrashSection("h", length);
                throw new ReportedException(crashreport);
            }
            ++i2;
        }
        return listToReuse;
    }

    public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_) {
        return p_76939_1_;
    }

    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x2, int z2, int width, int height) {
        IntCache.resetIntCache();
        if (biomes == null || biomes.length < width * height) {
            biomes = new BiomeGenBase[width * height];
        }
        int[] aint = this.genBiomes.getInts(x2, z2, width, height);
        try {
            int i2 = 0;
            while (i2 < width * height) {
                biomes[i2] = BiomeGenBase.getBiomeFromBiomeList(aint[i2], BiomeGenBase.field_180279_ad);
                ++i2;
            }
            return biomes;
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", biomes.length);
            crashreportcategory.addCrashSection("x", x2);
            crashreportcategory.addCrashSection("z", z2);
            crashreportcategory.addCrashSection("w", width);
            crashreportcategory.addCrashSection("h", height);
            throw new ReportedException(crashreport);
        }
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x2, int z2, int width, int depth) {
        return this.getBiomeGenAt(oldBiomeList, x2, z2, width, depth, true);
    }

    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x2, int z2, int width, int length, boolean cacheFlag) {
        IntCache.resetIntCache();
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new BiomeGenBase[width * length];
        }
        if (cacheFlag && width == 16 && length == 16 && (x2 & 0xF) == 0 && (z2 & 0xF) == 0) {
            BiomeGenBase[] abiomegenbase = this.biomeCache.getCachedBiomes(x2, z2);
            System.arraycopy(abiomegenbase, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        int[] aint = this.biomeIndexLayer.getInts(x2, z2, width, length);
        int i2 = 0;
        while (i2 < width * length) {
            listToReuse[i2] = BiomeGenBase.getBiomeFromBiomeList(aint[i2], BiomeGenBase.field_180279_ad);
            ++i2;
        }
        return listToReuse;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_) {
        IntCache.resetIntCache();
        int i2 = p_76940_1_ - p_76940_3_ >> 2;
        int j2 = p_76940_2_ - p_76940_3_ >> 2;
        int k2 = p_76940_1_ + p_76940_3_ >> 2;
        int l2 = p_76940_2_ + p_76940_3_ >> 2;
        int i1 = k2 - i2 + 1;
        int j1 = l2 - j2 + 1;
        int[] aint = this.genBiomes.getInts(i2, j2, i1, j1);
        try {
            int k1 = 0;
            while (true) {
                if (k1 >= i1 * j1) {
                    return true;
                }
                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[k1]);
                if (!p_76940_4_.contains(biomegenbase)) {
                    return false;
                }
                ++k1;
            }
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", p_76940_1_);
            crashreportcategory.addCrashSection("z", p_76940_2_);
            crashreportcategory.addCrashSection("radius", p_76940_3_);
            crashreportcategory.addCrashSection("allowed", p_76940_4_);
            throw new ReportedException(crashreport);
        }
    }

    public BlockPos findBiomePosition(int x2, int z2, int range, List<BiomeGenBase> biomes, Random random) {
        IntCache.resetIntCache();
        int i2 = x2 - range >> 2;
        int j2 = z2 - range >> 2;
        int k2 = x2 + range >> 2;
        int l2 = z2 + range >> 2;
        int i1 = k2 - i2 + 1;
        int j1 = l2 - j2 + 1;
        int[] aint = this.genBiomes.getInts(i2, j2, i1, j1);
        BlockPos blockpos = null;
        int k1 = 0;
        int l1 = 0;
        while (l1 < i1 * j1) {
            int i22 = i2 + l1 % i1 << 2;
            int j22 = j2 + l1 / i1 << 2;
            BiomeGenBase biomegenbase = BiomeGenBase.getBiome(aint[l1]);
            if (biomes.contains(biomegenbase) && (blockpos == null || random.nextInt(k1 + 1) == 0)) {
                blockpos = new BlockPos(i22, 0, j22);
                ++k1;
            }
            ++l1;
        }
        return blockpos;
    }

    public void cleanupCache() {
        this.biomeCache.cleanupCache();
    }
}

