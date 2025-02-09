/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.util;

public class FBPMathUtil {
    public static double add(double d2, double add2) {
        if (d2 < 0.0) {
            return d2 - add2;
        }
        return d2 + add2;
    }

    public static double round(double d2, int decimals) {
        int i2 = (int)Math.round(d2 * Math.pow(10.0, decimals));
        return (double)i2 / Math.pow(10.0, decimals);
    }
}

