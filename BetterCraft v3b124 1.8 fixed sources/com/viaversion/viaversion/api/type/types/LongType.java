/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class LongType
extends Type<Long>
implements TypeConverter<Long> {
    public LongType() {
        super(Long.class);
    }

    @Override
    @Deprecated
    public Long read(ByteBuf buffer) {
        return buffer.readLong();
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Long object) {
        buffer.writeLong(object);
    }

    @Override
    public Long from(Object o2) {
        if (o2 instanceof Number) {
            return ((Number)o2).longValue();
        }
        if (o2 instanceof Boolean) {
            return (Boolean)o2 != false ? 1L : 0L;
        }
        return (Long)o2;
    }

    public long readPrimitive(ByteBuf buffer) {
        return buffer.readLong();
    }

    public void writePrimitive(ByteBuf buffer, long object) {
        buffer.writeLong(object);
    }
}

