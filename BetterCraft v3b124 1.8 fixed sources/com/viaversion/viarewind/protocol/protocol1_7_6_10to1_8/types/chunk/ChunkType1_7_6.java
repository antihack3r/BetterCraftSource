/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.types.chunk;

import com.viaversion.viarewind.api.minecraft.ExtendedBlockStorage;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.Pair;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class ChunkType1_7_6
extends Type<Chunk> {
    public static final ChunkType1_7_6 TYPE = new ChunkType1_7_6();

    public ChunkType1_7_6() {
        super(Chunk.class);
    }

    @Override
    public Chunk read(ByteBuf byteBuf) throws Exception {
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ByteBuf output, Chunk chunk) throws Exception {
        int compressedSize;
        byte[] compressedData;
        Pair<byte[], Short> chunkData = ChunkType1_7_6.serialize(chunk);
        byte[] data = chunkData.key();
        short additionalBitMask = chunkData.value();
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
        output.writeInt(chunk.getX());
        output.writeInt(chunk.getZ());
        output.writeBoolean(chunk.isFullChunk());
        output.writeShort(chunk.getBitmask());
        output.writeShort(additionalBitMask);
        output.writeInt(compressedSize);
        output.writeBytes(compressedData, 0, compressedSize);
    }

    public static Pair<byte[], Short> serialize(Chunk chunk) throws IOException {
        int i2;
        ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
        for (int i3 = 0; i3 < storageArrays.length; ++i3) {
            ChunkSection section = chunk.getSections()[i3];
            if (section == null) continue;
            ExtendedBlockStorage storage = storageArrays[i3] = new ExtendedBlockStorage(section.getLight().hasSkyLight());
            for (int x2 = 0; x2 < 16; ++x2) {
                for (int z2 = 0; z2 < 16; ++z2) {
                    for (int y2 = 0; y2 < 16; ++y2) {
                        int flatBlock = section.palette(PaletteType.BLOCKS).idAt(x2, y2, z2);
                        storage.setBlockId(x2, y2, z2, flatBlock >> 4);
                        storage.setBlockMetadata(x2, y2, z2, flatBlock & 0xF);
                    }
                }
            }
            storage.getBlockLightArray().setHandle(section.getLight().getBlockLight());
            if (!section.getLight().hasSkyLight()) continue;
            storage.getSkyLightArray().setHandle(section.getLight().getSkyLight());
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (i2 = 0; i2 < storageArrays.length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0) continue;
            output.write(storageArrays[i2].getBlockLSBArray());
        }
        for (i2 = 0; i2 < storageArrays.length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0) continue;
            output.write(storageArrays[i2].getBlockMetadataArray().getHandle());
        }
        for (i2 = 0; i2 < storageArrays.length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0) continue;
            output.write(storageArrays[i2].getBlockLightArray().getHandle());
        }
        for (i2 = 0; i2 < storageArrays.length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0 || storageArrays[i2].getSkyLightArray() == null) continue;
            output.write(storageArrays[i2].getSkyLightArray().getHandle());
        }
        short additionalBitMask = 0;
        for (int i4 = 0; i4 < storageArrays.length; ++i4) {
            if ((chunk.getBitmask() & 1 << i4) == 0 || !storageArrays[i4].hasBlockMSBArray()) continue;
            additionalBitMask = (short)(additionalBitMask | (short)(1 << i4));
            output.write(storageArrays[i4].getOrCreateBlockMSBArray().getHandle());
        }
        if (chunk.isFullChunk() && chunk.getBiomeData() != null) {
            for (int biome : chunk.getBiomeData()) {
                output.write(biome);
            }
        }
        return new Pair<byte[], Short>(output.toByteArray(), additionalBitMask);
    }
}

