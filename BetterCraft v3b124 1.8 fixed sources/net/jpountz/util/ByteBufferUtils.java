/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import net.jpountz.util.SafeUtils;
import net.jpountz.util.Utils;

public final class ByteBufferUtils
extends Enum<ByteBufferUtils> {
    private static final /* synthetic */ ByteBufferUtils[] $VALUES;

    public static ByteBufferUtils[] values() {
        return (ByteBufferUtils[])$VALUES.clone();
    }

    public static ByteBufferUtils valueOf(String name) {
        return Enum.valueOf(ByteBufferUtils.class, name);
    }

    public static void checkRange(ByteBuffer buf, int off, int len) {
        SafeUtils.checkLength(len);
        if (len > 0) {
            ByteBufferUtils.checkRange(buf, off);
            ByteBufferUtils.checkRange(buf, off + len - 1);
        }
    }

    public static void checkRange(ByteBuffer buf, int off) {
        if (off < 0 || off >= buf.capacity()) {
            throw new ArrayIndexOutOfBoundsException(off);
        }
    }

    public static ByteBuffer inLittleEndianOrder(ByteBuffer buf) {
        if (buf.order().equals(ByteOrder.LITTLE_ENDIAN)) {
            return buf;
        }
        return buf.duplicate().order(ByteOrder.LITTLE_ENDIAN);
    }

    public static ByteBuffer inNativeByteOrder(ByteBuffer buf) {
        if (buf.order().equals(Utils.NATIVE_BYTE_ORDER)) {
            return buf;
        }
        return buf.duplicate().order(Utils.NATIVE_BYTE_ORDER);
    }

    public static byte readByte(ByteBuffer buf, int i2) {
        return buf.get(i2);
    }

    public static void writeInt(ByteBuffer buf, int i2, int v2) {
        assert (buf.order() == Utils.NATIVE_BYTE_ORDER);
        buf.putInt(i2, v2);
    }

    public static int readInt(ByteBuffer buf, int i2) {
        assert (buf.order() == Utils.NATIVE_BYTE_ORDER);
        return buf.getInt(i2);
    }

    public static int readIntLE(ByteBuffer buf, int i2) {
        assert (buf.order() == ByteOrder.LITTLE_ENDIAN);
        return buf.getInt(i2);
    }

    public static void writeLong(ByteBuffer buf, int i2, long v2) {
        assert (buf.order() == Utils.NATIVE_BYTE_ORDER);
        buf.putLong(i2, v2);
    }

    public static long readLong(ByteBuffer buf, int i2) {
        assert (buf.order() == Utils.NATIVE_BYTE_ORDER);
        return buf.getLong(i2);
    }

    public static long readLongLE(ByteBuffer buf, int i2) {
        assert (buf.order() == ByteOrder.LITTLE_ENDIAN);
        return buf.getLong(i2);
    }

    public static void writeByte(ByteBuffer dest, int off, int i2) {
        dest.put(off, (byte)i2);
    }

    public static void writeShortLE(ByteBuffer dest, int off, int i2) {
        dest.put(off, (byte)i2);
        dest.put(off + 1, (byte)(i2 >>> 8));
    }

    public static void checkNotReadOnly(ByteBuffer buffer) {
        if (buffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
    }

    public static int readShortLE(ByteBuffer buf, int i2) {
        return buf.get(i2) & 0xFF | (buf.get(i2 + 1) & 0xFF) << 8;
    }

    static {
        $VALUES = new ByteBufferUtils[0];
    }
}

