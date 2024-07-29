/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.Mappings;
import java.util.Arrays;

public class IntArrayMappings
implements Mappings {
    private final int[] mappings;
    private final int mappedIds;

    protected IntArrayMappings(int[] mappings, int mappedIds) {
        this.mappings = mappings;
        this.mappedIds = mappedIds;
    }

    public static IntArrayMappings of(int[] mappings, int mappedIds) {
        return new IntArrayMappings(mappings, mappedIds);
    }

    @Deprecated
    public static Mappings.Builder<IntArrayMappings> builder() {
        return Mappings.builder(IntArrayMappings::new);
    }

    @Override
    public int getNewId(int id2) {
        return id2 >= 0 && id2 < this.mappings.length ? this.mappings[id2] : -1;
    }

    @Override
    public void setNewId(int id2, int mappedId) {
        this.mappings[id2] = mappedId;
    }

    @Override
    public int size() {
        return this.mappings.length;
    }

    @Override
    public int mappedSize() {
        return this.mappedIds;
    }

    @Override
    public Mappings inverse() {
        int[] inverse = new int[this.mappedIds];
        Arrays.fill(inverse, -1);
        for (int id2 = 0; id2 < this.mappings.length; ++id2) {
            int mappedId = this.mappings[id2];
            if (mappedId == -1 || inverse[mappedId] != -1) continue;
            inverse[mappedId] = id2;
        }
        return IntArrayMappings.of(inverse, this.mappings.length);
    }

    public int[] raw() {
        return this.mappings;
    }
}

