/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_19to1_18_2.data;

import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MappingData
extends MappingDataBase {
    private final Int2ObjectMap<CompoundTag> defaultChatTypes = new Int2ObjectOpenHashMap<CompoundTag>();

    public MappingData() {
        super("1.18", "1.19");
    }

    @Override
    protected void loadExtras(CompoundTag daata) {
        try {
            ListTag chatTypes = (ListTag)NBTIO.readTag(MappingDataLoader.getResource("chat-types-1.19.nbt")).get("values");
            for (Tag chatType : chatTypes) {
                CompoundTag chatTypeCompound = (CompoundTag)chatType;
                NumberTag idTag = (NumberTag)chatTypeCompound.get("id");
                this.defaultChatTypes.put(idTag.asInt(), chatTypeCompound);
            }
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public @Nullable CompoundTag chatType(int id2) {
        return (CompoundTag)this.defaultChatTypes.get(id2);
    }
}

