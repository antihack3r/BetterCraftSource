/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.Vec3;

public class CustomColorFader {
    private Vec3 color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vec3 getColor(double x2, double y2, double z2) {
        if (this.color == null) {
            this.color = new Vec3(x2, y2, z2);
            return this.color;
        }
        long i2 = System.currentTimeMillis();
        long j2 = i2 - this.timeUpdate;
        if (j2 == 0L) {
            return this.color;
        }
        this.timeUpdate = i2;
        if (Math.abs(x2 - this.color.xCoord) < 0.004 && Math.abs(y2 - this.color.yCoord) < 0.004 && Math.abs(z2 - this.color.zCoord) < 0.004) {
            return this.color;
        }
        double d0 = (double)j2 * 0.001;
        d0 = Config.limit(d0, 0.0, 1.0);
        double d1 = x2 - this.color.xCoord;
        double d2 = y2 - this.color.yCoord;
        double d3 = z2 - this.color.zCoord;
        double d4 = this.color.xCoord + d1 * d0;
        double d5 = this.color.yCoord + d2 * d0;
        double d6 = this.color.zCoord + d3 * d0;
        this.color = new Vec3(d4, d5, d6);
        return this.color;
    }
}

