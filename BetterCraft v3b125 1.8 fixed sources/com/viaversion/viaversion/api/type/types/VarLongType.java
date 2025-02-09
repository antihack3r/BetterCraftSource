/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class VarLongType
extends Type<Long>
implements TypeConverter<Long> {
    public VarLongType() {
        super("VarLong", Long.class);
    }

    public long readPrimitive(ByteBuf buffer) {
        byte in2;
        long out = 0L;
        int bytes = 0;
        do {
            in2 = buffer.readByte();
            out |= (long)(in2 & 0x7F) << bytes++ * 7;
            if (bytes <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((in2 & 0x80) == 128);
        return out;
    }

    public void writePrimitive(ByteBuf buffer, long object) {
        do {
            int part = (int)(object & 0x7FL);
            if ((object >>>= 7) != 0L) {
                part |= 0x80;
            }
            buffer.writeByte(part);
        } while (object != 0L);
    }

    @Override
    @Deprecated
    public Long read(ByteBuf buffer) {
        return this.readPrimitive(buffer);
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Long object) {
        this.writePrimitive(buffer, object);
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
}

