// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.profiler;

public interface ISnooperInfo
{
    void addServerStatsToSnooper(final Snooper p0);
    
    void addServerTypeToSnooper(final Snooper p0);
    
    boolean isSnooperEnabled();
}
