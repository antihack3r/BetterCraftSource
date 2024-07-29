/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.xxhash;

import java.io.Closeable;
import java.util.zip.Checksum;

public abstract class StreamingXXHash32
implements Closeable {
    final int seed;

    StreamingXXHash32(int seed) {
        this.seed = seed;
    }

    public abstract int getValue();

    public abstract void update(byte[] var1, int var2, int var3);

    public abstract void reset();

    @Override
    public void close() {
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(seed=" + this.seed + ")";
    }

    public final Checksum asChecksum() {
        return new Checksum(){

            @Override
            public long getValue() {
                return (long)StreamingXXHash32.this.getValue() & 0xFFFFFFFL;
            }

            @Override
            public void reset() {
                StreamingXXHash32.this.reset();
            }

            @Override
            public void update(int b2) {
                StreamingXXHash32.this.update(new byte[]{(byte)b2}, 0, 1);
            }

            @Override
            public void update(byte[] b2, int off, int len) {
                StreamingXXHash32.this.update(b2, off, len);
            }

            public String toString() {
                return StreamingXXHash32.this.toString();
            }
        };
    }

    static interface Factory {
        public StreamingXXHash32 newStreamingHash(int var1);
    }
}

