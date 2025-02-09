/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.misc;

import com.viaversion.viaversion.api.type.OptionalType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.misc.NamedCompoundTagType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;

public class CompoundTagType
extends Type<CompoundTag> {
    public CompoundTagType() {
        super(CompoundTag.class);
    }

    @Override
    public CompoundTag read(ByteBuf buffer) throws Exception {
        return NamedCompoundTagType.read(buffer, false);
    }

    @Override
    public void write(ByteBuf buffer, CompoundTag object) throws Exception {
        NamedCompoundTagType.write(buffer, object, null);
    }

    public static final class OptionalCompoundTagType
    extends OptionalType<CompoundTag> {
        public OptionalCompoundTagType() {
            super(Type.COMPOUND_TAG);
        }
    }
}

