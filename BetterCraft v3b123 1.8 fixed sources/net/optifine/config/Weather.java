// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.config;

import net.minecraft.world.World;

public enum Weather
{
    CLEAR("CLEAR", 0), 
    RAIN("RAIN", 1), 
    THUNDER("THUNDER", 2);
    
    private Weather(final String s, final int n) {
    }
    
    public static Weather getWeather(final World world, final float partialTicks) {
        final float f = world.getThunderStrength(partialTicks);
        if (f > 0.5f) {
            return Weather.THUNDER;
        }
        final float f2 = world.getRainStrength(partialTicks);
        return (f2 > 0.5f) ? Weather.RAIN : Weather.CLEAR;
    }
}
