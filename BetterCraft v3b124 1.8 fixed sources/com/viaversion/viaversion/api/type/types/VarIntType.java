/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class VarIntType
extends Type<Integer>
implements TypeConverter<Integer> {
    private static final int CONTINUE_BIT = 128;
    private static final int VALUE_BITS = 127;
    private static final int MULTI_BYTE_BITS = -128;
    private static final int MAX_BYTES = 5;

    public VarIntType() {
        super("VarInt", Integer.class);
    }

    public int readPrimitive(ByteBuf buffer) {
        byte in2;
        int value = 0;
        int bytes = 0;
        do {
            in2 = buffer.readByte();
            value |= (in2 & 0x7F) << bytes++ * 7;
            if (bytes <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((in2 & 0x80) == 128);
        return value;
    }

    public void writePrimitive(ByteBuf buffer, int value) {
        while ((value & 0xFFFFFF80) != 0) {
            buffer.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
        buffer.writeByte(value);
    }

    @Override
    @Deprecated
    public Integer read(ByteBuf buffer) {
        return this.readPrimitive(buffer);
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Integer object) {
        this.writePrimitive(buffer, object);
    }

    @Override
    public Integer from(Object o2) {
        if (o2 instanceof Number) {
            return ((Number)o2).intValue();
        }
        if (o2 instanceof Boolean) {
            return (Boolean)o2 != false ? 1 : 0;
        }
        return (Integer)o2;
    }
}

