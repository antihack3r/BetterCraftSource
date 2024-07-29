/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

final class ShadyPines {
    private ShadyPines() {
    }

    static int floor(double dv2) {
        int iv2 = (int)dv2;
        return dv2 < (double)iv2 ? iv2 - 1 : iv2;
    }

    static int floor(float fv2) {
        int iv2 = (int)fv2;
        return fv2 < (float)iv2 ? iv2 - 1 : iv2;
    }
}

