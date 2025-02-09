/*
 * Decompiled with CFR 0.152.
 */
package org.lwjgl;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ReadOnlyBufferException;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerWrapper;

public class PointerBuffer
implements Comparable {
    private static final boolean is64Bit;
    protected final ByteBuffer pointers;
    protected final Buffer view;
    protected final IntBuffer view32;
    protected final LongBuffer view64;

    public PointerBuffer(int capacity) {
        this(BufferUtils.createByteBuffer(capacity * PointerBuffer.getPointerSize()));
    }

    public PointerBuffer(ByteBuffer source) {
        if (LWJGLUtil.CHECKS) {
            PointerBuffer.checkSource(source);
        }
        this.pointers = source.slice().order(source.order());
        if (is64Bit) {
            this.view32 = null;
            this.view64 = this.pointers.asLongBuffer();
            this.view = this.view64;
        } else {
            this.view32 = this.pointers.asIntBuffer();
            this.view = this.view32;
            this.view64 = null;
        }
    }

    private static void checkSource(ByteBuffer source) {
        int alignment;
        if (!source.isDirect()) {
            throw new IllegalArgumentException("The source buffer is not direct.");
        }
        int n2 = alignment = is64Bit ? 8 : 4;
        if ((MemoryUtil.getAddress0(source) + (long)source.position()) % (long)alignment != 0L || source.remaining() % alignment != 0) {
            throw new IllegalArgumentException("The source buffer is not aligned to " + alignment + " bytes.");
        }
    }

    public ByteBuffer getBuffer() {
        return this.pointers;
    }

    public static boolean is64Bit() {
        return is64Bit;
    }

    public static int getPointerSize() {
        return is64Bit ? 8 : 4;
    }

    public final int capacity() {
        return this.view.capacity();
    }

    public final int position() {
        return this.view.position();
    }

    public final int positionByte() {
        return this.position() * PointerBuffer.getPointerSize();
    }

    public final PointerBuffer position(int newPosition) {
        this.view.position(newPosition);
        return this;
    }

    public final int limit() {
        return this.view.limit();
    }

    public final PointerBuffer limit(int newLimit) {
        this.view.limit(newLimit);
        return this;
    }

    public final PointerBuffer mark() {
        this.view.mark();
        return this;
    }

    public final PointerBuffer reset() {
        this.view.reset();
        return this;
    }

    public final PointerBuffer clear() {
        this.view.clear();
        return this;
    }

    public final PointerBuffer flip() {
        this.view.flip();
        return this;
    }

    public final PointerBuffer rewind() {
        this.view.rewind();
        return this;
    }

    public final int remaining() {
        return this.view.remaining();
    }

    public final int remainingByte() {
        return this.remaining() * PointerBuffer.getPointerSize();
    }

    public final boolean hasRemaining() {
        return this.view.hasRemaining();
    }

    public static PointerBuffer allocateDirect(int capacity) {
        return new PointerBuffer(capacity);
    }

    protected PointerBuffer newInstance(ByteBuffer source) {
        return new PointerBuffer(source);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PointerBuffer slice() {
        int pointerSize = PointerBuffer.getPointerSize();
        this.pointers.position(this.view.position() * pointerSize);
        this.pointers.limit(this.view.limit() * pointerSize);
        try {
            PointerBuffer pointerBuffer = this.newInstance(this.pointers);
            return pointerBuffer;
        }
        finally {
            this.pointers.clear();
        }
    }

    public PointerBuffer duplicate() {
        PointerBuffer buffer = this.newInstance(this.pointers);
        buffer.position(this.view.position());
        buffer.limit(this.view.limit());
        return buffer;
    }

    public PointerBuffer asReadOnlyBuffer() {
        PointerBufferR buffer = new PointerBufferR(this.pointers);
        buffer.position(this.view.position());
        buffer.limit(this.view.limit());
        return buffer;
    }

    public boolean isReadOnly() {
        return false;
    }

    public long get() {
        if (is64Bit) {
            return this.view64.get();
        }
        return (long)this.view32.get() & 0xFFFFFFFFL;
    }

    public PointerBuffer put(long l2) {
        if (is64Bit) {
            this.view64.put(l2);
        } else {
            this.view32.put((int)l2);
        }
        return this;
    }

    public PointerBuffer put(PointerWrapper pointer) {
        return this.put(pointer.getPointer());
    }

    public static void put(ByteBuffer target, long l2) {
        if (is64Bit) {
            target.putLong(l2);
        } else {
            target.putInt((int)l2);
        }
    }

    public long get(int index) {
        if (is64Bit) {
            return this.view64.get(index);
        }
        return (long)this.view32.get(index) & 0xFFFFFFFFL;
    }

    public PointerBuffer put(int index, long l2) {
        if (is64Bit) {
            this.view64.put(index, l2);
        } else {
            this.view32.put(index, (int)l2);
        }
        return this;
    }

    public PointerBuffer put(int index, PointerWrapper pointer) {
        return this.put(index, pointer.getPointer());
    }

    public static void put(ByteBuffer target, int index, long l2) {
        if (is64Bit) {
            target.putLong(index, l2);
        } else {
            target.putInt(index, (int)l2);
        }
    }

    public PointerBuffer get(long[] dst, int offset, int length) {
        if (is64Bit) {
            this.view64.get(dst, offset, length);
        } else {
            PointerBuffer.checkBounds(offset, length, dst.length);
            if (length > this.view32.remaining()) {
                throw new BufferUnderflowException();
            }
            int end = offset + length;
            for (int i2 = offset; i2 < end; ++i2) {
                dst[i2] = (long)this.view32.get() & 0xFFFFFFFFL;
            }
        }
        return this;
    }

    public PointerBuffer get(long[] dst) {
        return this.get(dst, 0, dst.length);
    }

    public PointerBuffer put(PointerBuffer src) {
        if (is64Bit) {
            this.view64.put(src.view64);
        } else {
            this.view32.put(src.view32);
        }
        return this;
    }

    public PointerBuffer put(long[] src, int offset, int length) {
        if (is64Bit) {
            this.view64.put(src, offset, length);
        } else {
            PointerBuffer.checkBounds(offset, length, src.length);
            if (length > this.view32.remaining()) {
                throw new BufferOverflowException();
            }
            int end = offset + length;
            for (int i2 = offset; i2 < end; ++i2) {
                this.view32.put((int)src[i2]);
            }
        }
        return this;
    }

    public final PointerBuffer put(long[] src) {
        return this.put(src, 0, src.length);
    }

    public PointerBuffer compact() {
        if (is64Bit) {
            this.view64.compact();
        } else {
            this.view32.compact();
        }
        return this;
    }

    public ByteOrder order() {
        if (is64Bit) {
            return this.view64.order();
        }
        return this.view32.order();
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder(48);
        sb2.append(this.getClass().getName());
        sb2.append("[pos=");
        sb2.append(this.position());
        sb2.append(" lim=");
        sb2.append(this.limit());
        sb2.append(" cap=");
        sb2.append(this.capacity());
        sb2.append("]");
        return sb2.toString();
    }

    public int hashCode() {
        int h2 = 1;
        int p2 = this.position();
        for (int i2 = this.limit() - 1; i2 >= p2; --i2) {
            h2 = 31 * h2 + (int)this.get(i2);
        }
        return h2;
    }

    public boolean equals(Object ob2) {
        if (!(ob2 instanceof PointerBuffer)) {
            return false;
        }
        PointerBuffer that = (PointerBuffer)ob2;
        if (this.remaining() != that.remaining()) {
            return false;
        }
        int p2 = this.position();
        int i2 = this.limit() - 1;
        int j2 = that.limit() - 1;
        while (i2 >= p2) {
            long v2;
            long v1 = this.get(i2);
            if (v1 != (v2 = that.get(j2))) {
                return false;
            }
            --i2;
            --j2;
        }
        return true;
    }

    public int compareTo(Object o2) {
        PointerBuffer that = (PointerBuffer)o2;
        int n2 = this.position() + Math.min(this.remaining(), that.remaining());
        int i2 = this.position();
        int j2 = that.position();
        while (i2 < n2) {
            long v2;
            long v1 = this.get(i2);
            if (v1 != (v2 = that.get(j2))) {
                if (v1 < v2) {
                    return -1;
                }
                return 1;
            }
            ++i2;
            ++j2;
        }
        return this.remaining() - that.remaining();
    }

    private static void checkBounds(int off, int len, int size) {
        if ((off | len | off + len | size - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        boolean is64 = false;
        try {
            Method m2 = Class.forName("org.lwjgl.Sys").getDeclaredMethod("is64Bit", null);
            is64 = (Boolean)m2.invoke(null, (Object[])null);
        }
        catch (Throwable throwable) {
        }
        finally {
            is64Bit = is64;
        }
    }

    private static final class PointerBufferR
    extends PointerBuffer {
        PointerBufferR(ByteBuffer source) {
            super(source);
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        protected PointerBuffer newInstance(ByteBuffer source) {
            return new PointerBufferR(source);
        }

        @Override
        public PointerBuffer asReadOnlyBuffer() {
            return this.duplicate();
        }

        @Override
        public PointerBuffer put(long l2) {
            throw new ReadOnlyBufferException();
        }

        @Override
        public PointerBuffer put(int index, long l2) {
            throw new ReadOnlyBufferException();
        }

        @Override
        public PointerBuffer put(PointerBuffer src) {
            throw new ReadOnlyBufferException();
        }

        @Override
        public PointerBuffer put(long[] src, int offset, int length) {
            throw new ReadOnlyBufferException();
        }

        @Override
        public PointerBuffer compact() {
            throw new ReadOnlyBufferException();
        }
    }
}

