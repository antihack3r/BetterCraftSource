// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer
{
    public int elapsedTicks;
    public float field_194147_b;
    public float field_194148_c;
    private long lastSyncSysClock;
    private float field_194149_e;
    
    public Timer(final float tps) {
        this.field_194149_e = 1000.0f / tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
    }
    
    public void updateTimer() {
        final long i = Minecraft.getSystemTime();
        this.field_194148_c = (i - this.lastSyncSysClock) / this.field_194149_e;
        this.lastSyncSysClock = i;
        this.field_194147_b += this.field_194148_c;
        this.elapsedTicks = (int)this.field_194147_b;
        this.field_194147_b -= this.elapsedTicks;
    }
}
