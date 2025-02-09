/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.math;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.OptionalType;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class PositionType1_8
extends Type<Position> {
    public PositionType1_8() {
        super(Position.class);
    }

    @Override
    public Position read(ByteBuf buffer) {
        long val = buffer.readLong();
        long x2 = val >> 38;
        long y2 = val << 26 >> 52;
        long z2 = val << 38 >> 38;
        return new Position((int)x2, (short)y2, (int)z2);
    }

    @Override
    public void write(ByteBuf buffer, Position object) {
        buffer.writeLong(((long)object.x() & 0x3FFFFFFL) << 38 | ((long)object.y() & 0xFFFL) << 26 | (long)object.z() & 0x3FFFFFFL);
    }

    public static final class OptionalPositionType
    extends OptionalType<Position> {
        public OptionalPositionType() {
            super(Type.POSITION1_8);
        }
    }
}

