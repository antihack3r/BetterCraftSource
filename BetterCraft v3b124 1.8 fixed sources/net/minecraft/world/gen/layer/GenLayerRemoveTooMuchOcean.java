/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRemoveTooMuchOcean
extends GenLayer {
    public GenLayerRemoveTooMuchOcean(long p_i45480_1_, GenLayer p_i45480_3_) {
        super(p_i45480_1_);
        this.parent = p_i45480_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i2 = areaX - 1;
        int j2 = areaY - 1;
        int k2 = areaWidth + 2;
        int l2 = areaHeight + 2;
        int[] aint = this.parent.getInts(i2, j2, k2, l2);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);
        int i1 = 0;
        while (i1 < areaHeight) {
            int j1 = 0;
            while (j1 < areaWidth) {
                int k22;
                int k1 = aint[j1 + 1 + (i1 + 1 - 1) * (areaWidth + 2)];
                int l1 = aint[j1 + 1 + 1 + (i1 + 1) * (areaWidth + 2)];
                int i22 = aint[j1 + 1 - 1 + (i1 + 1) * (areaWidth + 2)];
                int j22 = aint[j1 + 1 + (i1 + 1 + 1) * (areaWidth + 2)];
                aint1[j1 + i1 * areaWidth] = k22 = aint[j1 + 1 + (i1 + 1) * k2];
                this.initChunkSeed(j1 + areaX, i1 + areaY);
                if (k22 == 0 && k1 == 0 && l1 == 0 && i22 == 0 && j22 == 0 && this.nextInt(2) == 0) {
                    aint1[j1 + i1 * areaWidth] = 1;
                }
                ++j1;
            }
            ++i1;
        }
        return aint1;
    }
}

