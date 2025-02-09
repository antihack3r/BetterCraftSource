/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class ByteType
extends Type<Byte>
implements TypeConverter<Byte> {
    public ByteType() {
        super(Byte.class);
    }

    public byte readPrimitive(ByteBuf buffer) {
        return buffer.readByte();
    }

    public void writePrimitive(ByteBuf buffer, byte object) {
        buffer.writeByte(object);
    }

    @Override
    @Deprecated
    public Byte read(ByteBuf buffer) {
        return buffer.readByte();
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Byte object) {
        buffer.writeByte(object.byteValue());
    }

    @Override
    public Byte from(Object o2) {
        if (o2 instanceof Number) {
            return ((Number)o2).byteValue();
        }
        if (o2 instanceof Boolean) {
            return (Boolean)o2 != false ? (byte)1 : 0;
        }
        return (Byte)o2;
    }
}

