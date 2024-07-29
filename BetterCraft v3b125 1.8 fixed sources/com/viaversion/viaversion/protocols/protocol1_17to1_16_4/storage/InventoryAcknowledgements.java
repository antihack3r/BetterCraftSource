/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;

public final class InventoryAcknowledgements
implements StorableObject {
    private final IntList ids = new IntArrayList();

    public void addId(int id2) {
        this.ids.add(id2);
    }

    public boolean removeId(int id2) {
        return this.ids.rem(id2);
    }
}

