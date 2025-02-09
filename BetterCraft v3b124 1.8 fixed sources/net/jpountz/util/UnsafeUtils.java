/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.util;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import net.jpountz.util.SafeUtils;
import net.jpountz.util.Utils;
import sun.misc.Unsafe;

public enum UnsafeUtils {

    private static final Unsafe UNSAFE;
    private static final long BYTE_ARRAY_OFFSET;
    private static final int BYTE_ARRAY_SCALE;
    private static final long INT_ARRAY_OFFSET;
    private static final int INT_ARRAY_SCALE;
    private static final long SHORT_ARRAY_OFFSET;
    private static final int SHORT_ARRAY_SCALE;

    public static void checkRange(byte[] buf, int off) {
        SafeUtils.checkRange(buf, off);
    }

    public static void checkRange(byte[] buf, int off, int len) {
        SafeUtils.checkRange(buf, off, len);
    }

    public static void checkLength(int len) {
        SafeUtils.checkLength(len);
    }

    public static byte readByte(byte[] src, int srcOff) {
        return UNSAFE.getByte(src, BYTE_ARRAY_OFFSET + (long)(BYTE_ARRAY_SCALE * srcOff));
    }

    public static void writeByte(byte[] src, int srcOff, byte value) {
        UNSAFE.putByte(src, BYTE_ARRAY_OFFSET + (long)(BYTE_ARRAY_SCALE * srcOff), value);
    }

    public static void writeByte(byte[] src, int srcOff, int value) {
        UnsafeUtils.writeByte(src, srcOff, (byte)value);
    }

    public static long readLong(byte[] src, int srcOff) {
        return UNSAFE.getLong(src, BYTE_ARRAY_OFFSET + (long)srcOff);
    }

    public static long readLongLE(byte[] src, int srcOff) {
        long i2 = UnsafeUtils.readLong(src, srcOff);
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            i2 = Long.reverseBytes(i2);
        }
        return i2;
    }

    public static void writeLong(byte[] dest, int destOff, long value) {
        UNSAFE.putLong(dest, BYTE_ARRAY_OFFSET + (long)destOff, value);
    }

    public static int readInt(byte[] src, int srcOff) {
        return UNSAFE.getInt(src, BYTE_ARRAY_OFFSET + (long)srcOff);
    }

    public static int readIntLE(byte[] src, int srcOff) {
        int i2 = UnsafeUtils.readInt(src, srcOff);
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            i2 = Integer.reverseBytes(i2);
        }
        return i2;
    }

    public static void writeInt(byte[] dest, int destOff, int value) {
        UNSAFE.putInt(dest, BYTE_ARRAY_OFFSET + (long)destOff, value);
    }

    public static short readShort(byte[] src, int srcOff) {
        return UNSAFE.getShort(src, BYTE_ARRAY_OFFSET + (long)srcOff);
    }

    public static int readShortLE(byte[] src, int srcOff) {
        short s2 = UnsafeUtils.readShort(src, srcOff);
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            s2 = Short.reverseBytes(s2);
        }
        return s2 & 0xFFFF;
    }

    public static void writeShort(byte[] dest, int destOff, short value) {
        UNSAFE.putShort(dest, BYTE_ARRAY_OFFSET + (long)destOff, value);
    }

    public static void writeShortLE(byte[] buf, int off, int v2) {
        UnsafeUtils.writeByte(buf, off, (byte)v2);
        UnsafeUtils.writeByte(buf, off + 1, (byte)(v2 >>> 8));
    }

    public static int readInt(int[] src, int srcOff) {
        return UNSAFE.getInt(src, INT_ARRAY_OFFSET + (long)(INT_ARRAY_SCALE * srcOff));
    }

    public static void writeInt(int[] dest, int destOff, int value) {
        UNSAFE.putInt(dest, INT_ARRAY_OFFSET + (long)(INT_ARRAY_SCALE * destOff), value);
    }

    public static int readShort(short[] src, int srcOff) {
        return UNSAFE.getShort(src, SHORT_ARRAY_OFFSET + (long)(SHORT_ARRAY_SCALE * srcOff)) & 0xFFFF;
    }

    public static void writeShort(short[] dest, int destOff, int value) {
        UNSAFE.putShort(dest, SHORT_ARRAY_OFFSET + (long)(SHORT_ARRAY_SCALE * destOff), (short)value);
    }

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
            BYTE_ARRAY_SCALE = UNSAFE.arrayIndexScale(byte[].class);
            INT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(int[].class);
            INT_ARRAY_SCALE = UNSAFE.arrayIndexScale(int[].class);
            SHORT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(short[].class);
            SHORT_ARRAY_SCALE = UNSAFE.arrayIndexScale(short[].class);
        }
        catch (IllegalAccessException e2) {
            throw new ExceptionInInitializerError("Cannot access Unsafe");
        }
        catch (NoSuchFieldException e3) {
            throw new ExceptionInInitializerError("Cannot access Unsafe");
        }
        catch (SecurityException e4) {
            throw new ExceptionInInitializerError("Cannot access Unsafe");
        }
    }
}

