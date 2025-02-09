/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;

public class CacheObjectArray {
    private static ArrayDeque<int[]> arrays = new ArrayDeque();
    private static int maxCacheSize = 10;

    private static synchronized int[] allocateArray(int size) {
        int[] aint = arrays.pollLast();
        if (aint == null || aint.length < size) {
            aint = new int[size];
        }
        return aint;
    }

    public static synchronized void freeArray(int[] ints) {
        if (arrays.size() < maxCacheSize) {
            arrays.add(ints);
        }
    }

    public static void main(String[] args) throws Exception {
        int i2 = 4096;
        int j2 = 500000;
        CacheObjectArray.testNew(i2, j2);
        CacheObjectArray.testClone(i2, j2);
        CacheObjectArray.testNewObj(i2, j2);
        CacheObjectArray.testCloneObj(i2, j2);
        CacheObjectArray.testNewObjDyn(IBlockState.class, i2, j2);
        long k2 = CacheObjectArray.testNew(i2, j2);
        long l2 = CacheObjectArray.testClone(i2, j2);
        long i1 = CacheObjectArray.testNewObj(i2, j2);
        long j1 = CacheObjectArray.testCloneObj(i2, j2);
        long k1 = CacheObjectArray.testNewObjDyn(IBlockState.class, i2, j2);
        Config.dbg("New: " + k2);
        Config.dbg("Clone: " + l2);
        Config.dbg("NewObj: " + i1);
        Config.dbg("CloneObj: " + j1);
        Config.dbg("NewObjDyn: " + k1);
    }

    private static long testClone(int size, int count) {
        long i2 = System.currentTimeMillis();
        int[] aint = new int[size];
        int j2 = 0;
        while (j2 < count) {
            int[] nArray = (int[])aint.clone();
            ++j2;
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNew(int size, int count) {
        long i2 = System.currentTimeMillis();
        int j2 = 0;
        while (j2 < count) {
            int[] nArray = (int[])Array.newInstance(Integer.TYPE, size);
            ++j2;
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testCloneObj(int size, int count) {
        long i2 = System.currentTimeMillis();
        IBlockState[] aiblockstate = new IBlockState[size];
        int j2 = 0;
        while (j2 < count) {
            IBlockState[] iBlockStateArray = (IBlockState[])aiblockstate.clone();
            ++j2;
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNewObj(int size, int count) {
        long i2 = System.currentTimeMillis();
        int j2 = 0;
        while (j2 < count) {
            IBlockState[] iBlockStateArray = new IBlockState[size];
            ++j2;
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }

    private static long testNewObjDyn(Class cls, int size, int count) {
        long i2 = System.currentTimeMillis();
        int j2 = 0;
        while (j2 < count) {
            Object[] objectArray = (Object[])Array.newInstance(cls, size);
            ++j2;
        }
        long k2 = System.currentTimeMillis();
        return k2 - i2;
    }
}

