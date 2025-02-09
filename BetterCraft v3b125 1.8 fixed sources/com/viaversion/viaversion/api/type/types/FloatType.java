/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types;

import com.viaversion.viaversion.api.type.OptionalType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.TypeConverter;
import io.netty.buffer.ByteBuf;

public class FloatType
extends Type<Float>
implements TypeConverter<Float> {
    public FloatType() {
        super(Float.class);
    }

    public float readPrimitive(ByteBuf buffer) {
        return buffer.readFloat();
    }

    public void writePrimitive(ByteBuf buffer, float object) {
        buffer.writeFloat(object);
    }

    @Override
    @Deprecated
    public Float read(ByteBuf buffer) {
        return Float.valueOf(buffer.readFloat());
    }

    @Override
    @Deprecated
    public void write(ByteBuf buffer, Float object) {
        buffer.writeFloat(object.floatValue());
    }

    @Override
    public Float from(Object o2) {
        if (o2 instanceof Number) {
            return Float.valueOf(((Number)o2).floatValue());
        }
        if (o2 instanceof Boolean) {
            return Float.valueOf((Boolean)o2 != false ? 1.0f : 0.0f);
        }
        return (Float)o2;
    }

    public static final class OptionalFloatType
    extends OptionalType<Float> {
        public OptionalFloatType() {
            super(Type.FLOAT);
        }
    }
}

