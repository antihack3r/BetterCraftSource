/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.util;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;

public interface Int2IntBiMap
extends Int2IntMap {
    public Int2IntBiMap inverse();

    @Override
    public int put(int var1, int var2);
}

