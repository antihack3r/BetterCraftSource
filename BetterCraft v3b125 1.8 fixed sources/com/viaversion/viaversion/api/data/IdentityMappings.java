/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.Mappings;

public class IdentityMappings
implements Mappings {
    private final int size;
    private final int mappedSize;

    public IdentityMappings(int size, int mappedSize) {
        this.size = size;
        this.mappedSize = mappedSize;
    }

    @Override
    public int getNewId(int id2) {
        return id2 >= 0 && id2 < this.size ? id2 : -1;
    }

    @Override
    public void setNewId(int id2, int mappedId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public int mappedSize() {
        return this.mappedSize;
    }

    @Override
    public Mappings inverse() {
        return new IdentityMappings(this.mappedSize, this.size);
    }
}

