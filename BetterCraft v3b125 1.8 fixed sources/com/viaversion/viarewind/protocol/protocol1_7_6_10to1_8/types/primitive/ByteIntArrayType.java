/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.primitive;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ByteIntArrayType
extends Type<int[]> {
    public ByteIntArrayType() {
        super(int[].class);
    }

    @Override
    public int[] read(ByteBuf byteBuf) throws Exception {
        byte size = byteBuf.readByte();
        int[] array = new int[size];
        for (byte i2 = 0; i2 < size; i2 = (byte)(i2 + 1)) {
            array[i2] = byteBuf.readInt();
        }
        return array;
    }

    @Override
    public void write(ByteBuf byteBuf, int[] array) throws Exception {
        byteBuf.writeByte(array.length);
        for (int i2 : array) {
            byteBuf.writeInt(i2);
        }
    }
}

