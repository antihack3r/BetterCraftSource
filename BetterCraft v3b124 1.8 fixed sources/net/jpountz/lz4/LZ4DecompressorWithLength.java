/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.lz4;

import java.nio.ByteBuffer;
import net.jpountz.lz4.LZ4FastDecompressor;

public class LZ4DecompressorWithLength {
    private final LZ4FastDecompressor decompressor;

    public static int getDecompressedLength(byte[] src) {
        return LZ4DecompressorWithLength.getDecompressedLength(src, 0);
    }

    public static int getDecompressedLength(byte[] src, int srcOff) {
        return src[srcOff] & 0xFF | (src[srcOff + 1] & 0xFF) << 8 | (src[srcOff + 2] & 0xFF) << 16 | src[srcOff + 3] << 24;
    }

    public static int getDecompressedLength(ByteBuffer src) {
        return LZ4DecompressorWithLength.getDecompressedLength(src, src.position());
    }

    public static int getDecompressedLength(ByteBuffer src, int srcOff) {
        return src.get(srcOff) & 0xFF | (src.get(srcOff + 1) & 0xFF) << 8 | (src.get(srcOff + 2) & 0xFF) << 16 | src.get(srcOff + 3) << 24;
    }

    public LZ4DecompressorWithLength(LZ4FastDecompressor decompressor) {
        this.decompressor = decompressor;
    }

    public int decompress(byte[] src, byte[] dest) {
        return this.decompress(src, 0, dest, 0);
    }

    public int decompress(byte[] src, int srcOff, byte[] dest, int destOff) {
        int destLen = LZ4DecompressorWithLength.getDecompressedLength(src, srcOff);
        return this.decompressor.decompress(src, srcOff + 4, dest, destOff, destLen) + 4;
    }

    public byte[] decompress(byte[] src) {
        return this.decompress(src, 0);
    }

    public byte[] decompress(byte[] src, int srcOff) {
        int destLen = LZ4DecompressorWithLength.getDecompressedLength(src, srcOff);
        return this.decompressor.decompress(src, srcOff + 4, destLen);
    }

    public void decompress(ByteBuffer src, ByteBuffer dest) {
        int destLen = LZ4DecompressorWithLength.getDecompressedLength(src, src.position());
        int read = this.decompressor.decompress(src, src.position() + 4, dest, dest.position(), destLen);
        src.position(src.position() + 4 + read);
        dest.position(dest.position() + destLen);
    }

    public int decompress(ByteBuffer src, int srcOff, ByteBuffer dest, int destOff) {
        int destLen = LZ4DecompressorWithLength.getDecompressedLength(src, srcOff);
        return this.decompressor.decompress(src, srcOff + 4, dest, destOff, destLen) + 4;
    }
}

