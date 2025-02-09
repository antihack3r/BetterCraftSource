/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.PointerBuffer;

public final class MemoryUtil {
    private static final Charset ascii;
    private static final Charset utf8;
    private static final Charset utf16;
    private static final Accessor memUtil;

    private MemoryUtil() {
    }

    public static long getAddress0(Buffer buffer) {
        return memUtil.getAddress(buffer);
    }

    public static long getAddress0Safe(Buffer buffer) {
        return buffer == null ? 0L : memUtil.getAddress(buffer);
    }

    public static long getAddress0(PointerBuffer buffer) {
        return memUtil.getAddress(buffer.getBuffer());
    }

    public static long getAddress0Safe(PointerBuffer buffer) {
        return buffer == null ? 0L : memUtil.getAddress(buffer.getBuffer());
    }

    public static long getAddress(ByteBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(ByteBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)position;
    }

    public static long getAddress(ShortBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(ShortBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 1);
    }

    public static long getAddress(CharBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(CharBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 1);
    }

    public static long getAddress(IntBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(IntBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 2);
    }

    public static long getAddress(FloatBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(FloatBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 2);
    }

    public static long getAddress(LongBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(LongBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 3);
    }

    public static long getAddress(DoubleBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(DoubleBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position << 3);
    }

    public static long getAddress(PointerBuffer buffer) {
        return MemoryUtil.getAddress(buffer, buffer.position());
    }

    public static long getAddress(PointerBuffer buffer, int position) {
        return MemoryUtil.getAddress0(buffer) + (long)(position * PointerBuffer.getPointerSize());
    }

    public static long getAddressSafe(ByteBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(ByteBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(ShortBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(ShortBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(CharBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(CharBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(IntBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(IntBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(FloatBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(FloatBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(LongBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(LongBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(DoubleBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(DoubleBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static long getAddressSafe(PointerBuffer buffer) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer);
    }

    public static long getAddressSafe(PointerBuffer buffer, int position) {
        return buffer == null ? 0L : MemoryUtil.getAddress(buffer, position);
    }

    public static ByteBuffer encodeASCII(CharSequence text) {
        return MemoryUtil.encode(text, ascii);
    }

    public static ByteBuffer encodeUTF8(CharSequence text) {
        return MemoryUtil.encode(text, utf8);
    }

    public static ByteBuffer encodeUTF16(CharSequence text) {
        return MemoryUtil.encode(text, utf16);
    }

    private static ByteBuffer encode(CharSequence text, Charset charset) {
        if (text == null) {
            return null;
        }
        return MemoryUtil.encode(CharBuffer.wrap(new CharSequenceNT(text)), charset);
    }

    private static ByteBuffer encode(CharBuffer in2, Charset charset) {
        CharsetEncoder encoder = charset.newEncoder();
        int n2 = (int)((float)in2.remaining() * encoder.averageBytesPerChar());
        ByteBuffer out = BufferUtils.createByteBuffer(n2);
        if (n2 == 0 && in2.remaining() == 0) {
            return out;
        }
        encoder.reset();
        while (true) {
            CoderResult cr2;
            CoderResult coderResult = cr2 = in2.hasRemaining() ? encoder.encode(in2, out, true) : CoderResult.UNDERFLOW;
            if (cr2.isUnderflow()) {
                cr2 = encoder.flush(out);
            }
            if (cr2.isUnderflow()) break;
            if (cr2.isOverflow()) {
                n2 = 2 * n2 + 1;
                ByteBuffer o2 = BufferUtils.createByteBuffer(n2);
                out.flip();
                o2.put(out);
                out = o2;
                continue;
            }
            try {
                cr2.throwException();
            }
            catch (CharacterCodingException e2) {
                throw new RuntimeException(e2);
            }
        }
        out.flip();
        return out;
    }

    public static String decodeASCII(ByteBuffer buffer) {
        return MemoryUtil.decode(buffer, ascii);
    }

    public static String decodeUTF8(ByteBuffer buffer) {
        return MemoryUtil.decode(buffer, utf8);
    }

    public static String decodeUTF16(ByteBuffer buffer) {
        return MemoryUtil.decode(buffer, utf16);
    }

    private static String decode(ByteBuffer buffer, Charset charset) {
        if (buffer == null) {
            return null;
        }
        return MemoryUtil.decodeImpl(buffer, charset);
    }

    private static String decodeImpl(ByteBuffer in2, Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        int n2 = (int)((float)in2.remaining() * decoder.averageCharsPerByte());
        CharBuffer out = BufferUtils.createCharBuffer(n2);
        if (n2 == 0 && in2.remaining() == 0) {
            return "";
        }
        decoder.reset();
        while (true) {
            CoderResult cr2;
            CoderResult coderResult = cr2 = in2.hasRemaining() ? decoder.decode(in2, out, true) : CoderResult.UNDERFLOW;
            if (cr2.isUnderflow()) {
                cr2 = decoder.flush(out);
            }
            if (cr2.isUnderflow()) break;
            if (cr2.isOverflow()) {
                n2 = 2 * n2 + 1;
                CharBuffer o2 = BufferUtils.createCharBuffer(n2);
                out.flip();
                o2.put(out);
                out = o2;
                continue;
            }
            try {
                cr2.throwException();
            }
            catch (CharacterCodingException e2) {
                throw new RuntimeException(e2);
            }
        }
        out.flip();
        return out.toString();
    }

    private static Accessor loadAccessor(String className) throws Exception {
        return (Accessor)Class.forName(className).newInstance();
    }

    static Field getAddressField() throws NoSuchFieldException {
        return MemoryUtil.getDeclaredFieldRecursive(ByteBuffer.class, "address");
    }

    private static Field getDeclaredFieldRecursive(Class<?> root, String fieldName) throws NoSuchFieldException {
        Class<?> type = root;
        while (true) {
            try {
                return type.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e2) {
                if ((type = type.getSuperclass()) != null) continue;
                throw new NoSuchFieldException(fieldName + " does not exist in " + root.getSimpleName() + " or any of its superclasses.");
            }
            break;
        }
    }

    static {
        Accessor util;
        ascii = Charset.forName("ISO-8859-1");
        utf8 = Charset.forName("UTF-8");
        utf16 = Charset.forName("UTF-16LE");
        try {
            util = MemoryUtil.loadAccessor("org.lwjgl.MemoryUtilSun$AccessorUnsafe");
        }
        catch (Exception e0) {
            try {
                util = MemoryUtil.loadAccessor("org.lwjgl.MemoryUtilSun$AccessorReflectFast");
            }
            catch (Exception e1) {
                try {
                    util = new AccessorReflect();
                }
                catch (Exception e2) {
                    LWJGLUtil.log("Unsupported JVM detected, this will likely result in low performance. Please inform LWJGL developers.");
                    util = new AccessorJNI();
                }
            }
        }
        LWJGLUtil.log("MemoryUtil Accessor: " + util.getClass().getSimpleName());
        memUtil = util;
    }

    private static class AccessorReflect
    implements Accessor {
        private final Field address;

        AccessorReflect() {
            try {
                this.address = MemoryUtil.getAddressField();
            }
            catch (NoSuchFieldException e2) {
                throw new UnsupportedOperationException(e2);
            }
            this.address.setAccessible(true);
        }

        @Override
        public long getAddress(Buffer buffer) {
            try {
                return this.address.getLong(buffer);
            }
            catch (IllegalAccessException e2) {
                return 0L;
            }
        }
    }

    private static class AccessorJNI
    implements Accessor {
        private AccessorJNI() {
        }

        @Override
        public long getAddress(Buffer buffer) {
            return BufferUtils.getBufferAddress(buffer);
        }
    }

    static interface Accessor {
        public long getAddress(Buffer var1);
    }

    private static class CharSequenceNT
    implements CharSequence {
        final CharSequence source;

        CharSequenceNT(CharSequence source) {
            this.source = source;
        }

        @Override
        public int length() {
            return this.source.length() + 1;
        }

        @Override
        public char charAt(int index) {
            return index == this.source.length() ? (char)'\u0000' : this.source.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new CharSequenceNT(this.source.subSequence(start, Math.min(end, this.source.length())));
        }
    }
}

