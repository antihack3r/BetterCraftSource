/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerIsland
extends GenLayer {
    public GenLayerIsland(long p_i2124_1_) {
        super(p_i2124_1_);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = IntCache.getIntCache(areaWidth * areaHeight);
        int i2 = 0;
        while (i2 < areaHeight) {
            int j2 = 0;
            while (j2 < areaWidth) {
                this.initChunkSeed(areaX + j2, areaY + i2);
                aint[j2 + i2 * areaWidth] = this.nextInt(10) == 0 ? 1 : 0;
                ++j2;
            }
            ++i2;
        }
        if (areaX > -areaWidth && areaX <= 0 && areaY > -areaHeight && areaY <= 0) {
            aint[-areaX + -areaY * areaWidth] = 1;
        }
        return aint;
    }
}

