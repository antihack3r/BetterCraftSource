/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.util;

import java.nio.ByteOrder;
import net.jpountz.util.Utils;

public enum SafeUtils {


    public static void checkRange(byte[] buf, int off) {
        if (off < 0 || off >= buf.length) {
            throw new ArrayIndexOutOfBoundsException(off);
        }
    }

    public static void checkRange(byte[] buf, int off, int len) {
        SafeUtils.checkLength(len);
        if (len > 0) {
            SafeUtils.checkRange(buf, off);
            SafeUtils.checkRange(buf, off + len - 1);
        }
    }

    public static void checkLength(int len) {
        if (len < 0) {
            throw new IllegalArgumentException("lengths must be >= 0");
        }
    }

    public static byte readByte(byte[] buf, int i2) {
        return buf[i2];
    }

    public static int readIntBE(byte[] buf, int i2) {
        return (buf[i2] & 0xFF) << 24 | (buf[i2 + 1] & 0xFF) << 16 | (buf[i2 + 2] & 0xFF) << 8 | buf[i2 + 3] & 0xFF;
    }

    public static int readIntLE(byte[] buf, int i2) {
        return buf[i2] & 0xFF | (buf[i2 + 1] & 0xFF) << 8 | (buf[i2 + 2] & 0xFF) << 16 | (buf[i2 + 3] & 0xFF) << 24;
    }

    public static int readInt(byte[] buf, int i2) {
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            return SafeUtils.readIntBE(buf, i2);
        }
        return SafeUtils.readIntLE(buf, i2);
    }

    public static long readLongLE(byte[] buf, int i2) {
        return (long)buf[i2] & 0xFFL | ((long)buf[i2 + 1] & 0xFFL) << 8 | ((long)buf[i2 + 2] & 0xFFL) << 16 | ((long)buf[i2 + 3] & 0xFFL) << 24 | ((long)buf[i2 + 4] & 0xFFL) << 32 | ((long)buf[i2 + 5] & 0xFFL) << 40 | ((long)buf[i2 + 6] & 0xFFL) << 48 | ((long)buf[i2 + 7] & 0xFFL) << 56;
    }

    public static void writeShortLE(byte[] buf, int off, int v2) {
        buf[off++] = (byte)v2;
        buf[off++] = (byte)(v2 >>> 8);
    }

    public static void writeInt(int[] buf, int off, int v2) {
        buf[off] = v2;
    }

    public static int readInt(int[] buf, int off) {
        return buf[off];
    }

    public static void writeByte(byte[] dest, int off, int i2) {
        dest[off] = (byte)i2;
    }

    public static void writeShort(short[] buf, int off, int v2) {
        buf[off] = (short)v2;
    }

    public static int readShortLE(byte[] buf, int i2) {
        return buf[i2] & 0xFF | (buf[i2 + 1] & 0xFF) << 8;
    }

    public static int readShort(short[] buf, int off) {
        return buf[off] & 0xFFFF;
    }
}

