/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_19to1_19_1.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.Protocol1_19To1_18_2;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ChatRegistryStorage
implements StorableObject {
    private final Int2ObjectMap<CompoundTag> chatTypes = new Int2ObjectOpenHashMap<CompoundTag>();

    public @Nullable CompoundTag chatType(int id2) {
        return this.chatTypes.isEmpty() ? Protocol1_19To1_18_2.MAPPINGS.chatType(id2) : (CompoundTag)this.chatTypes.get(id2);
    }

    public void addChatType(int id2, CompoundTag chatType) {
        this.chatTypes.put(id2, chatType);
    }

    public void clear() {
        this.chatTypes.clear();
    }

    @Override
    public boolean clearOnServerSwitch() {
        return false;
    }
}

