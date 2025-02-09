/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class UnsignedByteType
extends Type<Short>
implements TypeConverter<Short> {
    public UnsignedByteType() {
        super("Unsigned Byte", Short.class);
    }

    @Override
    public Short read(ByteBuf buffer) {
        return buffer.readUnsignedByte();
    }

    @Override
    public void write(ByteBuf buffer, Short object) {
        buffer.writeByte(object.shortValue());
    }

    @Override
    public Short from(Object o2) {
        if (o2 instanceof Number) {
            return ((Number)o2).shortValue();
        }
        if (o2 instanceof Boolean) {
            return (Boolean)o2 != false ? (short)1 : 0;
        }
        return (Short)o2;
    }
}

