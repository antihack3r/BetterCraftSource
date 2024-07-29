/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.xxhash;

import java.io.Closeable;
import java.util.zip.Checksum;

public abstract class StreamingXXHash64
implements Closeable {
    final long seed;

    StreamingXXHash64(long seed) {
        this.seed = seed;
    }

    public abstract long getValue();

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
                return StreamingXXHash64.this.getValue();
            }

            @Override
            public void reset() {
                StreamingXXHash64.this.reset();
            }

            @Override
            public void update(int b2) {
                StreamingXXHash64.this.update(new byte[]{(byte)b2}, 0, 1);
            }

            @Override
            public void update(byte[] b2, int off, int len) {
                StreamingXXHash64.this.update(b2, off, len);
            }

            public String toString() {
                return StreamingXXHash64.this.toString();
            }
        };
    }

    static interface Factory {
        public StreamingXXHash64 newStreamingHash(long var1);
    }
}

