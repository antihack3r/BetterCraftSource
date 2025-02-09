// 
// Decompiled by Procyon v0.6.0
// 

package me.amkgre.bettercraft.client.mods.chunkanimator.easing;

public class Back
{
    public static float easeIn(float t, final float b, final float c, final float d) {
        final float s = 1.70158f;
        return c * (t /= d) * t * ((s + 1.0f) * t - s) + b;
    }
    
    public static float easeIn(float t, final float b, final float c, final float d, final float s) {
        return c * (t /= d) * t * ((s + 1.0f) * t - s) + b;
    }
    
    public static float easeOut(float t, final float b, final float c, final float d) {
        final float s = 1.70158f;
        return c * ((t = t / d - 1.0f) * t * ((s + 1.0f) * t + s) + 1.0f) + b;
    }
    
    public static float easeOut(float t, final float b, final float c, final float d, final float s) {
        return c * ((t = t / d - 1.0f) * t * ((s + 1.0f) * t + s) + 1.0f) + b;
    }
    
    public static float easeInOut(float t, final float b, final float c, final float d) {
        float s = 1.70158f;
        if ((t /= d / 2.0f) < 1.0f) {
            return c / 2.0f * (t * t * (((s *= 1.525f) + 1.0f) * t - s)) + b;
        }
        return c / 2.0f * ((t -= 2.0f) * t * (((s *= 1.525f) + 1.0f) * t + s) + 2.0f) + b;
    }
    
    public static float easeInOut(float t, final float b, final float c, final float d, float s) {
        if ((t /= d / 2.0f) < 1.0f) {
            return c / 2.0f * (t * t * (((s *= 1.525f) + 1.0f) * t - s)) + b;
        }
        return c / 2.0f * ((t -= 2.0f) * t * (((s *= 1.525f) + 1.0f) * t + s) + 2.0f) + b;
    }
}
