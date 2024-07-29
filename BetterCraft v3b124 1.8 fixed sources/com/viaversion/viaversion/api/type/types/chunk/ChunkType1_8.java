/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.type.types.chunk;

import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;

public class ChunkType1_8
extends Type<Chunk> {
    private static final ChunkType1_8 WITH_SKYLIGHT = new ChunkType1_8(true);
    private static final ChunkType1_8 WITHOUT_SKYLIGHT = new ChunkType1_8(false);
    private final boolean hasSkyLight;

    public ChunkType1_8(boolean hasSkyLight) {
        super(Chunk.class);
        this.hasSkyLight = hasSkyLight;
    }

    public static ChunkType1_8 forEnvironment(Environment environment) {
        return environment == Environment.NORMAL ? WITH_SKYLIGHT : WITHOUT_SKYLIGHT;
    }

    @Override
    public Chunk read(ByteBuf input) throws Exception {
        int chunkX = input.readInt();
        int chunkZ = input.readInt();
        boolean fullChunk = input.readBoolean();
        int bitmask = input.readUnsignedShort();
        int dataLength = Type.VAR_INT.readPrimitive(input);
        byte[] data = new byte[dataLength];
        input.readBytes(data);
        if (fullChunk && bitmask == 0) {
            return new BaseChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], null, new ArrayList<CompoundTag>());
        }
        return ChunkType1_8.deserialize(chunkX, chunkZ, fullChunk, this.hasSkyLight, bitmask, data);
    }

    @Override
    public void write(ByteBuf output, Chunk chunk) throws Exception {
        output.writeInt(chunk.getX());
        output.writeInt(chunk.getZ());
        output.writeBoolean(chunk.isFullChunk());
        output.writeShort(chunk.getBitmask());
        byte[] data = ChunkType1_8.serialize(chunk);
        Type.VAR_INT.writePrimitive(output, data.length);
        output.writeBytes(data);
    }

    public static Chunk deserialize(int chunkX, int chunkZ, boolean fullChunk, boolean skyLight, int bitmask, byte[] data) throws Exception {
        int i2;
        ByteBuf input = Unpooled.wrappedBuffer(data);
        ChunkSection[] sections = new ChunkSection[16];
        int[] biomeData = null;
        for (i2 = 0; i2 < sections.length; ++i2) {
            if ((bitmask & 1 << i2) == 0) continue;
            sections[i2] = (ChunkSection)Types1_8.CHUNK_SECTION.read(input);
        }
        for (i2 = 0; i2 < sections.length; ++i2) {
            if ((bitmask & 1 << i2) == 0) continue;
            sections[i2].getLight().readBlockLight(input);
        }
        if (skyLight) {
            for (i2 = 0; i2 < sections.length; ++i2) {
                if ((bitmask & 1 << i2) == 0) continue;
                sections[i2].getLight().readSkyLight(input);
            }
        }
        if (fullChunk) {
            biomeData = new int[256];
            for (i2 = 0; i2 < 256; ++i2) {
                biomeData[i2] = input.readUnsignedByte();
            }
        }
        input.release();
        return new BaseChunk(chunkX, chunkZ, fullChunk, false, bitmask, sections, biomeData, new ArrayList<CompoundTag>());
    }

    public static byte[] serialize(Chunk chunk) throws Exception {
        int i2;
        ByteBuf output = Unpooled.buffer();
        for (i2 = 0; i2 < chunk.getSections().length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0) continue;
            Types1_8.CHUNK_SECTION.write(output, chunk.getSections()[i2]);
        }
        for (i2 = 0; i2 < chunk.getSections().length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0) continue;
            chunk.getSections()[i2].getLight().writeBlockLight(output);
        }
        for (i2 = 0; i2 < chunk.getSections().length; ++i2) {
            if ((chunk.getBitmask() & 1 << i2) == 0 || !chunk.getSections()[i2].getLight().hasSkyLight()) continue;
            chunk.getSections()[i2].getLight().writeSkyLight(output);
        }
        if (chunk.isFullChunk() && chunk.getBiomeData() != null) {
            for (int biome : chunk.getBiomeData()) {
                output.writeByte((byte)biome);
            }
        }
        byte[] data = new byte[output.readableBytes()];
        output.readBytes(data);
        output.release();
        return data;
    }
}

