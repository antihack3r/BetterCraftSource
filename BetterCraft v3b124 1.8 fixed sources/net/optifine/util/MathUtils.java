/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtils {
    public static final float PI = (float)Math.PI;
    public static final float PI2 = (float)Math.PI * 2;
    public static final float PId2 = 1.5707964f;
    private static final float[] ASIN_TABLE = new float[65536];

    static {
        int i2 = 0;
        while (i2 < 65536) {
            MathUtils.ASIN_TABLE[i2] = (float)Math.asin((double)i2 / 32767.5 - 1.0);
            ++i2;
        }
        int j2 = -1;
        while (j2 < 2) {
            MathUtils.ASIN_TABLE[(int)(((double)j2 + 1.0) * 32767.5) & 0xFFFF] = (float)Math.asin(j2);
            ++j2;
        }
    }

    public static float asin(float value) {
        return ASIN_TABLE[(int)((double)(value + 1.0f) * 32767.5) & 0xFFFF];
    }

    public static float acos(float value) {
        return 1.5707964f - ASIN_TABLE[(int)((double)(value + 1.0f) * 32767.5) & 0xFFFF];
    }

    public static int getAverage(int[] vals) {
        if (vals.length <= 0) {
            return 0;
        }
        int i2 = MathUtils.getSum(vals);
        int j2 = i2 / vals.length;
        return j2;
    }

    public static int getSum(int[] vals) {
        if (vals.length <= 0) {
            return 0;
        }
        int i2 = 0;
        int j2 = 0;
        while (j2 < vals.length) {
            int k2 = vals[j2];
            i2 += k2;
            ++j2;
        }
        return i2;
    }

    public static int roundDownToPowerOfTwo(int val) {
        int i2 = MathHelper.roundUpToPowerOfTwo(val);
        return val == i2 ? i2 : i2 / 2;
    }

    public static boolean equalsDelta(float f1, float f2, float delta) {
        return Math.abs(f1 - f2) <= delta;
    }

    public static float toDeg(float angle) {
        return angle * 180.0f / MathHelper.PI;
    }

    public static float toRad(float angle) {
        return angle / 180.0f * MathHelper.PI;
    }

    public static float roundToFloat(double d2) {
        return (float)((double)Math.round(d2 * 1.0E8) / 1.0E8);
    }
}

