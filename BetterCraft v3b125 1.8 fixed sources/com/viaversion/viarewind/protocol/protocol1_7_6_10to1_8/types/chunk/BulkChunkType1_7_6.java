/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.chunk;

import com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.chunk.ChunkType1_7_6;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.Pair;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

public class BulkChunkType1_7_6
extends Type<Chunk[]> {
    public static final BulkChunkType1_7_6 TYPE = new BulkChunkType1_7_6();

    public BulkChunkType1_7_6() {
        super(Chunk[].class);
    }

    @Override
    public Chunk[] read(ByteBuf byteBuf) throws Exception {
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ByteBuf byteBuf, Chunk[] chunks) throws Exception {
        int compressedSize;
        byte[] compressedData;
        int chunkCount = chunks.length;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int[] chunkX = new int[chunkCount];
        int[] chunkZ = new int[chunkCount];
        short[] primaryBitMask = new short[chunkCount];
        short[] additionalBitMask = new short[chunkCount];
        for (int i2 = 0; i2 < chunkCount; ++i2) {
            Chunk chunk = chunks[i2];
            Pair<byte[], Short> chunkData = ChunkType1_7_6.serialize(chunk);
            output.write(chunkData.key());
            chunkX[i2] = chunk.getX();
            chunkZ[i2] = chunk.getZ();
            primaryBitMask[i2] = (short)chunk.getBitmask();
            additionalBitMask[i2] = chunkData.value();
        }
        byte[] data = output.toByteArray();
        Deflater deflater = new Deflater();
        try {
            deflater.setInput(data, 0, data.length);
            deflater.finish();
            compressedData = new byte[data.length];
            compressedSize = deflater.deflate(compressedData);
        }
        finally {
            deflater.end();
        }
        byteBuf.writeShort(chunkCount);
        byteBuf.writeInt(compressedSize);
        byteBuf.writeBoolean(true);
        byteBuf.writeBytes(compressedData, 0, compressedSize);
        for (int i3 = 0; i3 < chunkCount; ++i3) {
            byteBuf.writeInt(chunkX[i3]);
            byteBuf.writeInt(chunkZ[i3]);
            byteBuf.writeShort(primaryBitMask[i3]);
            byteBuf.writeShort(additionalBitMask[i3]);
        }
    }
}

