/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.math;

import com.viaversion.viaversion.api.minecraft.Quaternion;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class QuaternionType
extends Type<Quaternion> {
    public QuaternionType() {
        super(Quaternion.class);
    }

    @Override
    public Quaternion read(ByteBuf buffer) throws Exception {
        float x2 = buffer.readFloat();
        float y2 = buffer.readFloat();
        float z2 = buffer.readFloat();
        float w2 = buffer.readFloat();
        return new Quaternion(x2, y2, z2, w2);
    }

    @Override
    public void write(ByteBuf buffer, Quaternion object) throws Exception {
        buffer.writeFloat(object.x());
        buffer.writeFloat(object.y());
        buffer.writeFloat(object.z());
        buffer.writeFloat(object.w());
    }
}

