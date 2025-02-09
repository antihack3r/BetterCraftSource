/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.data;

import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.io.IOException;

public final class MappingData
extends MappingDataBase {
    private CompoundTag damageTypesRegistry;

    public MappingData() {
        super("1.19.3", "1.19.4");
    }

    @Override
    protected void loadExtras(CompoundTag data) {
        try {
            this.damageTypesRegistry = NBTIO.readTag(MappingDataLoader.getResource("damage-types-1.19.4.nbt"));
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public CompoundTag damageTypesRegistry() {
        return this.damageTypesRegistry.clone();
    }
}

