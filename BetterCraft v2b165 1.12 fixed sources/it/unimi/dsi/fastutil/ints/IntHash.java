// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.ints;

public interface IntHash
{
    public interface Strategy
    {
        int hashCode(final int p0);
        
        boolean equals(final int p0, final int p1);
    }
}
