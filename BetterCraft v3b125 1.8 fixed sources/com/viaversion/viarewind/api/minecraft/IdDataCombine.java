/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.api.minecraft;

public class IdDataCombine {
    public static int idFromCombined(int combined) {
        return combined >> 4;
    }

    public static int dataFromCombined(int combined) {
        return combined & 0xF;
    }

    public static int toCombined(int id2, int data) {
        return id2 << 4 | data & 0xF;
    }
}

