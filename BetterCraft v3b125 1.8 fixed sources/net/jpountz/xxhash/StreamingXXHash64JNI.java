/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.xxhash;

import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashJNI;

final class StreamingXXHash64JNI
extends StreamingXXHash64 {
    private long state;

    StreamingXXHash64JNI(long seed) {
        super(seed);
        this.state = XXHashJNI.XXH64_init(seed);
    }

    private void checkState() {
        if (this.state == 0L) {
            throw new AssertionError((Object)"Already finalized");
        }
    }

    @Override
    public synchronized void reset() {
        this.checkState();
        XXHashJNI.XXH64_free(this.state);
        this.state = XXHashJNI.XXH64_init(this.seed);
    }

    @Override
    public synchronized long getValue() {
        this.checkState();
        return XXHashJNI.XXH64_digest(this.state);
    }

    @Override
    public synchronized void update(byte[] bytes, int off, int len) {
        this.checkState();
        XXHashJNI.XXH64_update(this.state, bytes, off, len);
    }

    @Override
    public synchronized void close() {
        if (this.state != 0L) {
            super.close();
            XXHashJNI.XXH64_free(this.state);
            this.state = 0L;
        }
    }

    protected synchronized void finalize() throws Throwable {
        super.finalize();
        if (this.state != 0L) {
            XXHashJNI.XXH64_free(this.state);
            this.state = 0L;
        }
    }

    static class Factory
    implements StreamingXXHash64.Factory {
        public static final StreamingXXHash64.Factory INSTANCE = new Factory();

        Factory() {
        }

        @Override
        public StreamingXXHash64 newStreamingHash(long seed) {
            return new StreamingXXHash64JNI(seed);
        }
    }
}

