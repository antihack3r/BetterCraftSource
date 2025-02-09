/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import net.labymod.support.util.Debug;

@Deprecated
public class Debug {
    @Deprecated
    public static void log(EnumDebugMode debugMode, String message) {
        net.labymod.support.util.Debug.log(Debug.EnumDebugMode.ADDON, message);
    }

    @Deprecated
    public static enum EnumDebugMode {
        ADDON;

    }
}

