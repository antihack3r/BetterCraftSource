/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.config;

import net.minecraft.world.World;

public enum Weather {
    CLEAR,
    RAIN,
    THUNDER;


    public static Weather getWeather(World world, float partialTicks) {
        float f2 = world.getThunderStrength(partialTicks);
        if (f2 > 0.5f) {
            return THUNDER;
        }
        float f1 = world.getRainStrength(partialTicks);
        return f1 > 0.5f ? RAIN : CLEAR;
    }
}

