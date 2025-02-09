/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MappingData
extends MappingDataBase {
    private final Map<String, CompoundTag> dimensionDataMap = new HashMap<String, CompoundTag>();
    private CompoundTag dimensionRegistry;

    public MappingData() {
        super("1.16", "1.16.2");
    }

    @Override
    public void loadExtras(CompoundTag data) {
        try {
            this.dimensionRegistry = NBTIO.readTag(MappingDataLoader.getResource("dimension-registry-1.16.2.nbt"));
        }
        catch (IOException e2) {
            Via.getPlatform().getLogger().severe("Error loading dimension registry:");
            e2.printStackTrace();
        }
        ListTag dimensions = (ListTag)((CompoundTag)this.dimensionRegistry.get("minecraft:dimension_type")).get("value");
        for (Tag dimension : dimensions) {
            CompoundTag dimensionCompound = (CompoundTag)dimension;
            CompoundTag dimensionData = new CompoundTag((Map<String, Tag>)((CompoundTag)dimensionCompound.get("element")).getValue());
            this.dimensionDataMap.put(((StringTag)dimensionCompound.get("name")).getValue(), dimensionData);
        }
    }

    public Map<String, CompoundTag> getDimensionDataMap() {
        return this.dimensionDataMap;
    }

    public CompoundTag getDimensionRegistry() {
        return this.dimensionRegistry.clone();
    }
}

