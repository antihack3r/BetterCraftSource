/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBiome
extends GenLayer {
    private BiomeGenBase[] field_151623_c = new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna, BiomeGenBase.savanna, BiomeGenBase.plains};
    private BiomeGenBase[] field_151621_d = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.plains, BiomeGenBase.birchForest, BiomeGenBase.swampland};
    private BiomeGenBase[] field_151622_e = new BiomeGenBase[]{BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.taiga, BiomeGenBase.plains};
    private BiomeGenBase[] field_151620_f = new BiomeGenBase[]{BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.icePlains, BiomeGenBase.coldTaiga};
    private final ChunkProviderSettings field_175973_g;

    public GenLayerBiome(long p_i45560_1_, GenLayer p_i45560_3_, WorldType p_i45560_4_, String p_i45560_5_) {
        super(p_i45560_1_);
        this.parent = p_i45560_3_;
        if (p_i45560_4_ == WorldType.DEFAULT_1_1) {
            this.field_151623_c = new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills, BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga};
            this.field_175973_g = null;
        } else {
            this.field_175973_g = p_i45560_4_ == WorldType.CUSTOMIZED ? ChunkProviderSettings.Factory.jsonToFactory(p_i45560_5_).func_177864_b() : null;
        }
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        int i2 = 0;
        while (i2 < areaHeight) {
            int j2 = 0;
            while (j2 < areaWidth) {
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + i2 * areaWidth];
                int l2 = (k2 & 0xF00) >> 8;
                aint1[j2 + i2 * areaWidth] = this.field_175973_g != null && this.field_175973_g.fixedBiome >= 0 ? this.field_175973_g.fixedBiome : (GenLayerBiome.isBiomeOceanic(k2) ? k2 : (k2 == BiomeGenBase.mushroomIsland.biomeID ? k2 : (k2 == 1 ? (l2 > 0 ? (this.nextInt(3) == 0 ? BiomeGenBase.mesaPlateau.biomeID : BiomeGenBase.mesaPlateau_F.biomeID) : this.field_151623_c[this.nextInt((int)this.field_151623_c.length)].biomeID) : (k2 == 2 ? (l2 > 0 ? BiomeGenBase.jungle.biomeID : this.field_151621_d[this.nextInt((int)this.field_151621_d.length)].biomeID) : (k2 == 3 ? (l2 > 0 ? BiomeGenBase.megaTaiga.biomeID : this.field_151622_e[this.nextInt((int)this.field_151622_e.length)].biomeID) : ((k2 &= 0xFFFFF0FF) == 4 ? this.field_151620_f[this.nextInt((int)this.field_151620_f.length)].biomeID : BiomeGenBase.mushroomIsland.biomeID))))));
                ++j2;
            }
            ++i2;
        }
        return aint1;
    }
}

