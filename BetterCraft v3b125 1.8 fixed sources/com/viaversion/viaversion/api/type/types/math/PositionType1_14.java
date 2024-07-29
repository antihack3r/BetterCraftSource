/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.math;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.OptionalType;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class PositionType1_14
extends Type<Position> {
    public PositionType1_14() {
        super(Position.class);
    }

    @Override
    public Position read(ByteBuf buffer) {
        long val = buffer.readLong();
        long x2 = val >> 38;
        long y2 = val << 52 >> 52;
        long z2 = val << 26 >> 38;
        return new Position((int)x2, (int)y2, (int)z2);
    }

    @Override
    public void write(ByteBuf buffer, Position object) {
        buffer.writeLong(((long)object.x() & 0x3FFFFFFL) << 38 | (long)(object.y() & 0xFFF) | ((long)object.z() & 0x3FFFFFFL) << 12);
    }

    public static final class OptionalPosition1_14Type
    extends OptionalType<Position> {
        public OptionalPosition1_14Type() {
            super(Type.POSITION1_14);
        }
    }
}

