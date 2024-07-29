/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.misc;

import com.viaversion.viaversion.api.type.OptionalType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.misc.NamedCompoundTagType;
import com.viaversion.viaversion.libs.opennbt.tag.TagRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

public class TagType
extends Type<Tag> {
    public TagType() {
        super(Tag.class);
    }

    @Override
    public Tag read(ByteBuf buffer) throws Exception {
        byte id2 = buffer.readByte();
        if (id2 == 0) {
            return null;
        }
        TagLimiter tagLimiter = TagLimiter.create(0x200000, 512);
        Tag tag = TagRegistry.createInstance(id2);
        tag.read(new ByteBufInputStream(buffer), tagLimiter);
        return tag;
    }

    @Override
    public void write(ByteBuf buffer, Tag tag) throws Exception {
        NamedCompoundTagType.write(buffer, tag, null);
    }

    public static final class OptionalTagType
    extends OptionalType<Tag> {
        public OptionalTagType() {
            super(Type.TAG);
        }
    }
}

