/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.xxhash;

import java.nio.ByteBuffer;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.UnsafeUtils;
import net.jpountz.xxhash.XXHash32;

final class XXHash32JavaUnsafe
extends XXHash32 {
    public static final XXHash32 INSTANCE = new XXHash32JavaUnsafe();

    XXHash32JavaUnsafe() {
    }

    @Override
    public int hash(byte[] buf, int off, int len, int seed) {
        int h32;
        UnsafeUtils.checkRange(buf, off, len);
        int end = off + len;
        if (len >= 16) {
            int limit = end - 16;
            int v1 = seed + -1640531535 + -2048144777;
            int v2 = seed + -2048144777;
            int v3 = seed + 0;
            int v4 = seed - -1640531535;
            do {
                v1 += UnsafeUtils.readIntLE(buf, off) * -2048144777;
                v1 = Integer.rotateLeft(v1, 13);
                v1 *= -1640531535;
                v2 += UnsafeUtils.readIntLE(buf, off += 4) * -2048144777;
                v2 = Integer.rotateLeft(v2, 13);
                v2 *= -1640531535;
                v3 += UnsafeUtils.readIntLE(buf, off += 4) * -2048144777;
                v3 = Integer.rotateLeft(v3, 13);
                v3 *= -1640531535;
                v4 += UnsafeUtils.readIntLE(buf, off += 4) * -2048144777;
                v4 = Integer.rotateLeft(v4, 13);
                v4 *= -1640531535;
            } while ((off += 4) <= limit);
            h32 = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7) + Integer.rotateLeft(v3, 12) + Integer.rotateLeft(v4, 18);
        } else {
            h32 = seed + 374761393;
        }
        h32 += len;
        while (off <= end - 4) {
            h32 += UnsafeUtils.readIntLE(buf, off) * -1028477379;
            h32 = Integer.rotateLeft(h32, 17) * 668265263;
            off += 4;
        }
        while (off < end) {
            h32 += (UnsafeUtils.readByte(buf, off) & 0xFF) * 374761393;
            h32 = Integer.rotateLeft(h32, 11) * -1640531535;
            ++off;
        }
        h32 ^= h32 >>> 15;
        h32 *= -2048144777;
        h32 ^= h32 >>> 13;
        h32 *= -1028477379;
        h32 ^= h32 >>> 16;
        return h32;
    }

    @Override
    public int hash(ByteBuffer buf, int off, int len, int seed) {
        int h32;
        if (buf.hasArray()) {
            return this.hash(buf.array(), off + buf.arrayOffset(), len, seed);
        }
        ByteBufferUtils.checkRange(buf, off, len);
        buf = ByteBufferUtils.inLittleEndianOrder(buf);
        int end = off + len;
        if (len >= 16) {
            int limit = end - 16;
            int v1 = seed + -1640531535 + -2048144777;
            int v2 = seed + -2048144777;
            int v3 = seed + 0;
            int v4 = seed - -1640531535;
            do {
                v1 += ByteBufferUtils.readIntLE(buf, off) * -2048144777;
                v1 = Integer.rotateLeft(v1, 13);
                v1 *= -1640531535;
                v2 += ByteBufferUtils.readIntLE(buf, off += 4) * -2048144777;
                v2 = Integer.rotateLeft(v2, 13);
                v2 *= -1640531535;
                v3 += ByteBufferUtils.readIntLE(buf, off += 4) * -2048144777;
                v3 = Integer.rotateLeft(v3, 13);
                v3 *= -1640531535;
                v4 += ByteBufferUtils.readIntLE(buf, off += 4) * -2048144777;
                v4 = Integer.rotateLeft(v4, 13);
                v4 *= -1640531535;
            } while ((off += 4) <= limit);
            h32 = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7) + Integer.rotateLeft(v3, 12) + Integer.rotateLeft(v4, 18);
        } else {
            h32 = seed + 374761393;
        }
        h32 += len;
        while (off <= end - 4) {
            h32 += ByteBufferUtils.readIntLE(buf, off) * -1028477379;
            h32 = Integer.rotateLeft(h32, 17) * 668265263;
            off += 4;
        }
        while (off < end) {
            h32 += (ByteBufferUtils.readByte(buf, off) & 0xFF) * 374761393;
            h32 = Integer.rotateLeft(h32, 11) * -1640531535;
            ++off;
        }
        h32 ^= h32 >>> 15;
        h32 *= -2048144777;
        h32 ^= h32 >>> 13;
        h32 *= -1028477379;
        h32 ^= h32 >>> 16;
        return h32;
    }
}

