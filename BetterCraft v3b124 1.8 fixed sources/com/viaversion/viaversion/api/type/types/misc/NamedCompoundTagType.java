/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.misc;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NamedCompoundTagType
extends Type<CompoundTag> {
    public static final int MAX_NBT_BYTES = 0x200000;
    public static final int MAX_NESTING_LEVEL = 512;

    public NamedCompoundTagType() {
        super(CompoundTag.class);
    }

    @Override
    public CompoundTag read(ByteBuf buffer) throws Exception {
        return NamedCompoundTagType.read(buffer, true);
    }

    @Override
    public void write(ByteBuf buffer, CompoundTag object) throws Exception {
        NamedCompoundTagType.write(buffer, object, "");
    }

    public static CompoundTag read(ByteBuf buffer, boolean readName) throws Exception {
        byte id2 = buffer.readByte();
        if (id2 == 0) {
            return null;
        }
        if (id2 != 10) {
            throw new IOException(String.format("Expected root tag to be a CompoundTag, was %s", id2));
        }
        if (readName) {
            buffer.skipBytes(buffer.readUnsignedShort());
        }
        TagLimiter tagLimiter = TagLimiter.create(0x200000, 512);
        CompoundTag tag = new CompoundTag();
        tag.read(new ByteBufInputStream(buffer), tagLimiter);
        return tag;
    }

    public static void write(ByteBuf buffer, Tag tag, @Nullable String name) throws Exception {
        if (tag == null) {
            buffer.writeByte(0);
            return;
        }
        ByteBufOutputStream out = new ByteBufOutputStream(buffer);
        out.writeByte(tag.getTagId());
        if (name != null) {
            out.writeUTF(name);
        }
        tag.write(out);
    }
}

