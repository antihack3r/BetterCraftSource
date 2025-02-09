/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRareBiome
extends GenLayer {
    public GenLayerRareBiome(long p_i45478_1_, GenLayer p_i45478_3_) {
        super(p_i45478_1_);
        this.parent = p_i45478_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        int i2 = 0;
        while (i2 < areaHeight) {
            int j2 = 0;
            while (j2 < areaWidth) {
                this.initChunkSeed(j2 + areaX, i2 + areaY);
                int k2 = aint[j2 + 1 + (i2 + 1) * (areaWidth + 2)];
                aint1[j2 + i2 * areaWidth] = this.nextInt(57) == 0 ? (k2 == BiomeGenBase.plains.biomeID ? BiomeGenBase.plains.biomeID + 128 : k2) : k2;
                ++j2;
            }
            ++i2;
        }
        return aint1;
    }
}

