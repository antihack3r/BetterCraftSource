/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.storage;

import com.viaversion.viabackwards.protocol.protocol1_18_2to1_19.Protocol1_18_2To1_19;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DimensionRegistryStorage
implements StorableObject {
    private final Map<String, CompoundTag> dimensions = new HashMap<String, CompoundTag>();
    private final Int2ObjectMap<CompoundTag> chatTypes = new Int2ObjectOpenHashMap<CompoundTag>();

    public @Nullable CompoundTag dimension(String dimensionKey) {
        CompoundTag compoundTag = this.dimensions.get(dimensionKey);
        return compoundTag != null ? compoundTag.clone() : null;
    }

    public void addDimension(String dimensionKey, CompoundTag dimension) {
        this.dimensions.put(dimensionKey, dimension);
    }

    public @Nullable CompoundTag chatType(int id2) {
        return this.chatTypes.isEmpty() ? Protocol1_18_2To1_19.MAPPINGS.chatType(id2) : (CompoundTag)this.chatTypes.get(id2);
    }

    public void addChatType(int id2, CompoundTag chatType) {
        this.chatTypes.put(id2, chatType);
    }

    public void clear() {
        this.dimensions.clear();
        this.chatTypes.clear();
    }

    @Override
    public boolean clearOnServerSwitch() {
        return false;
    }
}

