/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.Comparator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

public class ChunkPosComparator
implements Comparator<ChunkCoordIntPair> {
    private int chunkPosX;
    private int chunkPosZ;
    private double yawRad;
    private double pitchNorm;

    public ChunkPosComparator(int chunkPosX, int chunkPosZ, double yawRad, double pitchRad) {
        this.chunkPosX = chunkPosX;
        this.chunkPosZ = chunkPosZ;
        this.yawRad = yawRad;
        this.pitchNorm = 1.0 - MathHelper.clamp_double(Math.abs(pitchRad) / 1.5707963267948966, 0.0, 1.0);
    }

    @Override
    public int compare(ChunkCoordIntPair cp1, ChunkCoordIntPair cp2) {
        int i2 = this.getDistSq(cp1);
        int j2 = this.getDistSq(cp2);
        return i2 - j2;
    }

    private int getDistSq(ChunkCoordIntPair cp2) {
        int i2 = cp2.chunkXPos - this.chunkPosX;
        int j2 = cp2.chunkZPos - this.chunkPosZ;
        int k2 = i2 * i2 + j2 * j2;
        double d0 = MathHelper.atan2(j2, i2);
        double d1 = Math.abs(d0 - this.yawRad);
        if (d1 > Math.PI) {
            d1 = Math.PI * 2 - d1;
        }
        k2 = (int)((double)k2 * 1000.0 * this.pitchNorm * d1 * d1);
        return k2;
    }
}

