/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public class IntegerCache {
    private static final Integer[] CACHE = new Integer[65535];

    static {
        int i2 = 0;
        int j2 = CACHE.length;
        while (i2 < j2) {
            IntegerCache.CACHE[i2] = i2;
            ++i2;
        }
    }

    public static Integer getInteger(int value) {
        return value >= 0 && value < CACHE.length ? CACHE[value] : new Integer(value);
    }
}

