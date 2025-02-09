// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.layer;

import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.List;

public class IntCache
{
    private static int intCacheSize;
    private static final List<int[]> freeSmallArrays;
    private static final List<int[]> inUseSmallArrays;
    private static final List<int[]> freeLargeArrays;
    private static final List<int[]> inUseLargeArrays;
    
    static {
        IntCache.intCacheSize = 256;
        freeSmallArrays = Lists.newArrayList();
        inUseSmallArrays = Lists.newArrayList();
        freeLargeArrays = Lists.newArrayList();
        inUseLargeArrays = Lists.newArrayList();
    }
    
    public static synchronized int[] getIntCache(final int p_76445_0_) {
        if (p_76445_0_ <= 256) {
            if (IntCache.freeSmallArrays.isEmpty()) {
                final int[] aint4 = new int[256];
                IntCache.inUseSmallArrays.add(aint4);
                return aint4;
            }
            final int[] aint5 = IntCache.freeSmallArrays.remove(IntCache.freeSmallArrays.size() - 1);
            IntCache.inUseSmallArrays.add(aint5);
            return aint5;
        }
        else {
            if (p_76445_0_ > IntCache.intCacheSize) {
                IntCache.intCacheSize = p_76445_0_;
                IntCache.freeLargeArrays.clear();
                IntCache.inUseLargeArrays.clear();
                final int[] aint6 = new int[IntCache.intCacheSize];
                IntCache.inUseLargeArrays.add(aint6);
                return aint6;
            }
            if (IntCache.freeLargeArrays.isEmpty()) {
                final int[] aint7 = new int[IntCache.intCacheSize];
                IntCache.inUseLargeArrays.add(aint7);
                return aint7;
            }
            final int[] aint8 = IntCache.freeLargeArrays.remove(IntCache.freeLargeArrays.size() - 1);
            IntCache.inUseLargeArrays.add(aint8);
            return aint8;
        }
    }
    
    public static synchronized void resetIntCache() {
        if (!IntCache.freeLargeArrays.isEmpty()) {
            IntCache.freeLargeArrays.remove(IntCache.freeLargeArrays.size() - 1);
        }
        if (!IntCache.freeSmallArrays.isEmpty()) {
            IntCache.freeSmallArrays.remove(IntCache.freeSmallArrays.size() - 1);
        }
        IntCache.freeLargeArrays.addAll(IntCache.inUseLargeArrays);
        IntCache.freeSmallArrays.addAll(IntCache.inUseSmallArrays);
        IntCache.inUseLargeArrays.clear();
        IntCache.inUseSmallArrays.clear();
    }
    
    public static synchronized String getCacheSizes() {
        return "cache: " + IntCache.freeLargeArrays.size() + ", tcache: " + IntCache.freeSmallArrays.size() + ", allocated: " + IntCache.inUseLargeArrays.size() + ", tallocated: " + IntCache.inUseSmallArrays.size();
    }
}
