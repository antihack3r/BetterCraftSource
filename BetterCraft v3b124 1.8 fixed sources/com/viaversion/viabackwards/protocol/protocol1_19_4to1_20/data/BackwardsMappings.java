/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_19_4to1_20.data;

import com.viaversion.viabackwards.api.data.VBMappingDataLoader;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_20to1_19_4.Protocol1_20To1_19_4;
import java.io.IOException;

public class BackwardsMappings
extends com.viaversion.viabackwards.api.data.BackwardsMappings {
    private CompoundTag trimPatternRegistry;

    public BackwardsMappings() {
        super("1.20", "1.19.4", Protocol1_20To1_19_4.class);
    }

    @Override
    protected void loadExtras(CompoundTag data) {
        super.loadExtras(data);
        try {
            this.trimPatternRegistry = NBTIO.readTag(VBMappingDataLoader.getResource("trim_pattern-1.19.4.nbt"));
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public CompoundTag getTrimPatternRegistry() {
        return this.trimPatternRegistry;
    }
}

