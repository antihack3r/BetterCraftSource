/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerVoronoiZoom
extends GenLayer {
    public GenLayerVoronoiZoom(long p_i2133_1_, GenLayer p_i2133_3_) {
        super(p_i2133_1_);
        this.parent = p_i2133_3_;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int i2 = (areaX -= 2) >> 2;
        int j2 = (areaY -= 2) >> 2;
        int k2 = (areaWidth >> 2) + 2;
        int l2 = (areaHeight >> 2) + 2;
        int[] aint = this.parent.getInts(i2, j2, k2, l2);
        int i1 = k2 - 1 << 2;
        int j1 = l2 - 1 << 2;
        int[] aint1 = IntCache.getIntCache(i1 * j1);
        int k1 = 0;
        while (k1 < l2 - 1) {
            int l1 = 0;
            int i22 = aint[l1 + 0 + (k1 + 0) * k2];
            int j22 = aint[l1 + 0 + (k1 + 1) * k2];
            while (l1 < k2 - 1) {
                double d0 = 3.6;
                this.initChunkSeed(l1 + i2 << 2, k1 + j2 << 2);
                double d1 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                double d2 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                this.initChunkSeed(l1 + i2 + 1 << 2, k1 + j2 << 2);
                double d3 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                double d4 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                this.initChunkSeed(l1 + i2 << 2, k1 + j2 + 1 << 2);
                double d5 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6;
                double d6 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                this.initChunkSeed(l1 + i2 + 1 << 2, k1 + j2 + 1 << 2);
                double d7 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                double d8 = ((double)this.nextInt(1024) / 1024.0 - 0.5) * 3.6 + 4.0;
                int k22 = aint[l1 + 1 + (k1 + 0) * k2] & 0xFF;
                int l22 = aint[l1 + 1 + (k1 + 1) * k2] & 0xFF;
                int i3 = 0;
                while (i3 < 4) {
                    int j3 = ((k1 << 2) + i3) * i1 + (l1 << 2);
                    int k3 = 0;
                    while (k3 < 4) {
                        double d9 = ((double)i3 - d2) * ((double)i3 - d2) + ((double)k3 - d1) * ((double)k3 - d1);
                        double d10 = ((double)i3 - d4) * ((double)i3 - d4) + ((double)k3 - d3) * ((double)k3 - d3);
                        double d11 = ((double)i3 - d6) * ((double)i3 - d6) + ((double)k3 - d5) * ((double)k3 - d5);
                        double d12 = ((double)i3 - d8) * ((double)i3 - d8) + ((double)k3 - d7) * ((double)k3 - d7);
                        aint1[j3++] = d9 < d10 && d9 < d11 && d9 < d12 ? i22 : (d10 < d9 && d10 < d11 && d10 < d12 ? k22 : (d11 < d9 && d11 < d10 && d11 < d12 ? j22 : l22));
                        ++k3;
                    }
                    ++i3;
                }
                i22 = k22;
                j22 = l22;
                ++l1;
            }
            ++k1;
        }
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        int l3 = 0;
        while (l3 < areaHeight) {
            System.arraycopy(aint1, (l3 + (areaY & 3)) * i1 + (areaX & 3), aint2, l3 * areaWidth, areaWidth);
            ++l3;
        }
        return aint2;
    }
}

