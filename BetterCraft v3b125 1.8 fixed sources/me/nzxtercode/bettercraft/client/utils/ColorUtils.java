/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.utils;

import java.awt.Color;

public class ColorUtils {
    public static int rainbowEffect() {
        return Color.HSBtoRGB((float)(System.currentTimeMillis() % 3000L) / 3000.0f, 0.8f, 1.0f);
    }
}

