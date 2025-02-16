/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.xxhash;

import net.jpountz.util.SafeUtils;
import net.jpountz.xxhash.AbstractStreamingXXHash64Java;
import net.jpountz.xxhash.StreamingXXHash64;

final class StreamingXXHash64JavaSafe
extends AbstractStreamingXXHash64Java {
    StreamingXXHash64JavaSafe(long seed) {
        super(seed);
    }

    @Override
    public long getValue() {
        int off;
        long h64;
        if (this.totalLen >= 32L) {
            long v1 = this.v1;
            long v2 = this.v2;
            long v3 = this.v3;
            long v4 = this.v4;
            h64 = Long.rotateLeft(v1, 1) + Long.rotateLeft(v2, 7) + Long.rotateLeft(v3, 12) + Long.rotateLeft(v4, 18);
            v1 *= -4417276706812531889L;
            v1 = Long.rotateLeft(v1, 31);
            h64 ^= (v1 *= -7046029288634856825L);
            h64 = h64 * -7046029288634856825L + -8796714831421723037L;
            v2 *= -4417276706812531889L;
            v2 = Long.rotateLeft(v2, 31);
            h64 ^= (v2 *= -7046029288634856825L);
            h64 = h64 * -7046029288634856825L + -8796714831421723037L;
            v3 *= -4417276706812531889L;
            v3 = Long.rotateLeft(v3, 31);
            h64 ^= (v3 *= -7046029288634856825L);
            h64 = h64 * -7046029288634856825L + -8796714831421723037L;
            v4 *= -4417276706812531889L;
            v4 = Long.rotateLeft(v4, 31);
            h64 ^= (v4 *= -7046029288634856825L);
            h64 = h64 * -7046029288634856825L + -8796714831421723037L;
        } else {
            h64 = this.seed + 2870177450012600261L;
        }
        h64 += this.totalLen;
        for (off = 0; off <= this.memSize - 8; off += 8) {
            long k1 = SafeUtils.readLongLE(this.memory, off);
            k1 *= -4417276706812531889L;
            k1 = Long.rotateLeft(k1, 31);
            h64 ^= (k1 *= -7046029288634856825L);
            h64 = Long.rotateLeft(h64, 27) * -7046029288634856825L + -8796714831421723037L;
        }
        if (off <= this.memSize - 4) {
            h64 ^= ((long)SafeUtils.readIntLE(this.memory, off) & 0xFFFFFFFFL) * -7046029288634856825L;
            h64 = Long.rotateLeft(h64, 23) * -4417276706812531889L + 1609587929392839161L;
            off += 4;
        }
        while (off < this.memSize) {
            h64 ^= (long)(this.memory[off] & 0xFF) * 2870177450012600261L;
            h64 = Long.rotateLeft(h64, 11) * -7046029288634856825L;
            ++off;
        }
        h64 ^= h64 >>> 33;
        h64 *= -4417276706812531889L;
        h64 ^= h64 >>> 29;
        h64 *= 1609587929392839161L;
        h64 ^= h64 >>> 32;
        return h64;
    }

    @Override
    public void update(byte[] buf, int off, int len) {
        SafeUtils.checkRange(buf, off, len);
        this.totalLen += (long)len;
        if (this.memSize + len < 32) {
            System.arraycopy(buf, off, this.memory, this.memSize, len);
            this.memSize += len;
            return;
        }
        int end = off + len;
        if (this.memSize > 0) {
            System.arraycopy(buf, off, this.memory, this.memSize, 32 - this.memSize);
            this.v1 += SafeUtils.readLongLE(this.memory, 0) * -4417276706812531889L;
            this.v1 = Long.rotateLeft(this.v1, 31);
            this.v1 *= -7046029288634856825L;
            this.v2 += SafeUtils.readLongLE(this.memory, 8) * -4417276706812531889L;
            this.v2 = Long.rotateLeft(this.v2, 31);
            this.v2 *= -7046029288634856825L;
            this.v3 += SafeUtils.readLongLE(this.memory, 16) * -4417276706812531889L;
            this.v3 = Long.rotateLeft(this.v3, 31);
            this.v3 *= -7046029288634856825L;
            this.v4 += SafeUtils.readLongLE(this.memory, 24) * -4417276706812531889L;
            this.v4 = Long.rotateLeft(this.v4, 31);
            this.v4 *= -7046029288634856825L;
            off += 32 - this.memSize;
            this.memSize = 0;
        }
        int limit = end - 32;
        long v1 = this.v1;
        long v2 = this.v2;
        long v3 = this.v3;
        long v4 = this.v4;
        while (off <= limit) {
            v1 += SafeUtils.readLongLE(buf, off) * -4417276706812531889L;
            v1 = Long.rotateLeft(v1, 31);
            v1 *= -7046029288634856825L;
            v2 += SafeUtils.readLongLE(buf, off += 8) * -4417276706812531889L;
            v2 = Long.rotateLeft(v2, 31);
            v2 *= -7046029288634856825L;
            v3 += SafeUtils.readLongLE(buf, off += 8) * -4417276706812531889L;
            v3 = Long.rotateLeft(v3, 31);
            v3 *= -7046029288634856825L;
            v4 += SafeUtils.readLongLE(buf, off += 8) * -4417276706812531889L;
            v4 = Long.rotateLeft(v4, 31);
            v4 *= -7046029288634856825L;
            off += 8;
        }
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        if (off < end) {
            System.arraycopy(buf, off, this.memory, 0, end - off);
            this.memSize = end - off;
        }
    }

    static class Factory
    implements StreamingXXHash64.Factory {
        public static final StreamingXXHash64.Factory INSTANCE = new Factory();

        Factory() {
        }

        @Override
        public StreamingXXHash64 newStreamingHash(long seed) {
            return new StreamingXXHash64JavaSafe(seed);
        }
    }
}

