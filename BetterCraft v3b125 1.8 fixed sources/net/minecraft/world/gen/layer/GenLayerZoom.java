/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerZoom
extends GenLayer {
    public GenLayerZoom(long p_i2134_1_, GenLayer p_i2134_3_) {
        super(p_i2134_1_);
        this.parent = p_i2134_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i2 = areaX >> 1;
        int j2 = areaY >> 1;
        int k2 = (areaWidth >> 1) + 2;
        int l2 = (areaHeight >> 1) + 2;
        int[] aint = this.parent.getInts(i2, j2, k2, l2);
        int i1 = k2 - 1 << 1;
        int j1 = l2 - 1 << 1;
        int[] aint1 = IntCache.getIntCache(i1 * j1);
        int k1 = 0;
        while (k1 < l2 - 1) {
            int l1 = (k1 << 1) * i1;
            int i22 = 0;
            int j22 = aint[i22 + 0 + (k1 + 0) * k2];
            int k22 = aint[i22 + 0 + (k1 + 1) * k2];
            while (i22 < k2 - 1) {
                this.initChunkSeed(i22 + i2 << 1, k1 + j2 << 1);
                int l22 = aint[i22 + 1 + (k1 + 0) * k2];
                int i3 = aint[i22 + 1 + (k1 + 1) * k2];
                aint1[l1] = j22;
                aint1[l1++ + i1] = this.selectRandom2(j22, k22);
                aint1[l1] = this.selectRandom2(j22, l22);
                aint1[l1++ + i1] = this.selectModeOrRandom(j22, l22, k22, i3);
                j22 = l22;
                k22 = i3;
                ++i22;
            }
            ++k1;
        }
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        int j3 = 0;
        while (j3 < areaHeight) {
            System.arraycopy(aint1, (j3 + (areaY & 1)) * i1 + (areaX & 1), aint2, j3 * areaWidth, areaWidth);
            ++j3;
        }
        return aint2;
    }

    public static GenLayer magnify(long p_75915_0_, GenLayer p_75915_2_, int p_75915_3_) {
        GenLayer genlayer = p_75915_2_;
        int i2 = 0;
        while (i2 < p_75915_3_) {
            genlayer = new GenLayerZoom(p_75915_0_ + (long)i2, genlayer);
            ++i2;
        }
        return genlayer;
    }

    protected int selectRandom2(int p_selectRandom2_1_, int p_selectRandom2_2_) {
        int i2 = this.nextInt(2);
        return i2 == 0 ? p_selectRandom2_1_ : p_selectRandom2_2_;
    }
}

