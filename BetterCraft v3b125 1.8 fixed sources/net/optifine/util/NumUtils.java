/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

public class NumUtils {
    public static float limit(float val, float min, float max) {
        return val < min ? min : (val > max ? max : val);
    }

    public static int mod(int x2, int y2) {
        int i2 = x2 % y2;
        if (i2 < 0) {
            i2 += y2;
        }
        return i2;
    }
}

