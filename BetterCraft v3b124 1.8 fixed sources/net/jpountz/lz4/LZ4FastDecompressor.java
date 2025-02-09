/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.lz4;

import java.nio.ByteBuffer;
import net.jpountz.lz4.LZ4Decompressor;

public abstract class LZ4FastDecompressor
implements LZ4Decompressor {
    @Override
    public abstract int decompress(byte[] var1, int var2, byte[] var3, int var4, int var5);

    public abstract int decompress(ByteBuffer var1, int var2, ByteBuffer var3, int var4, int var5);

    public final int decompress(byte[] src, byte[] dest, int destLen) {
        return this.decompress(src, 0, dest, 0, destLen);
    }

    public final int decompress(byte[] src, byte[] dest) {
        return this.decompress(src, dest, dest.length);
    }

    public final byte[] decompress(byte[] src, int srcOff, int destLen) {
        byte[] decompressed = new byte[destLen];
        this.decompress(src, srcOff, decompressed, 0, destLen);
        return decompressed;
    }

    public final byte[] decompress(byte[] src, int destLen) {
        return this.decompress(src, 0, destLen);
    }

    public final void decompress(ByteBuffer src, ByteBuffer dest) {
        int read = this.decompress(src, src.position(), dest, dest.position(), dest.remaining());
        dest.position(dest.limit());
        src.position(src.position() + read);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

