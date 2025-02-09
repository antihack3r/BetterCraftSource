/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_16_2;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_16_2;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_17;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import java.util.ArrayList;
import java.util.BitSet;

public final class WorldPackets {
    public static void register(Protocol1_17To1_16_4 protocol) {
        BlockRewriter<ClientboundPackets1_16_2> blockRewriter = BlockRewriter.for1_14(protocol);
        blockRewriter.registerBlockAction(ClientboundPackets1_16_2.BLOCK_ACTION);
        blockRewriter.registerBlockChange(ClientboundPackets1_16_2.BLOCK_CHANGE);
        blockRewriter.registerVarLongMultiBlockChange(ClientboundPackets1_16_2.MULTI_BLOCK_CHANGE);
        blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_16_2.ACKNOWLEDGE_PLAYER_DIGGING);
        protocol.registerClientbound(ClientboundPackets1_16_2.WORLD_BORDER, null, wrapper -> {
            ClientboundPackets1_17 packetType;
            int type = wrapper.read(Type.VAR_INT);
            switch (type) {
                case 0: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_SIZE;
                    break;
                }
                case 1: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_LERP_SIZE;
                    break;
                }
                case 2: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_CENTER;
                    break;
                }
                case 3: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_INIT;
                    break;
                }
                case 4: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_WARNING_DELAY;
                    break;
                }
                case 5: {
                    packetType = ClientboundPackets1_17.WORLD_BORDER_WARNING_DISTANCE;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid world border type received: " + type);
                }
            }
            wrapper.setPacketType(packetType);
        });
        protocol.registerClientbound(ClientboundPackets1_16_2.UPDATE_LIGHT, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(Type.BOOLEAN);
                this.handler(wrapper -> {
                    int skyLightMask = wrapper.read(Type.VAR_INT);
                    int blockLightMask = wrapper.read(Type.VAR_INT);
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, this.toBitSetLongArray(skyLightMask));
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, this.toBitSetLongArray(blockLightMask));
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, this.toBitSetLongArray(wrapper.read(Type.VAR_INT)));
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, this.toBitSetLongArray(wrapper.read(Type.VAR_INT)));
                    this.writeLightArrays(wrapper, skyLightMask);
                    this.writeLightArrays(wrapper, blockLightMask);
                });
            }

            private void writeLightArrays(PacketWrapper wrapper, int bitMask) throws Exception {
                ArrayList<byte[]> light = new ArrayList<byte[]>();
                for (int i2 = 0; i2 < 18; ++i2) {
                    if (!this.isSet(bitMask, i2)) continue;
                    light.add(wrapper.read(Type.BYTE_ARRAY_PRIMITIVE));
                }
                wrapper.write(Type.VAR_INT, light.size());
                for (byte[] bytes : light) {
                    wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, bytes);
                }
            }

            private long[] toBitSetLongArray(int bitmask) {
                return new long[]{bitmask};
            }

            private boolean isSet(int mask, int i2) {
                return (mask & 1 << i2) != 0;
            }
        });
        protocol.registerClientbound(ClientboundPackets1_16_2.CHUNK_DATA, wrapper -> {
            Chunk chunk = wrapper.read(ChunkType1_16_2.TYPE);
            if (!chunk.isFullChunk()) {
                WorldPackets.writeMultiBlockChangePacket(wrapper, chunk);
                wrapper.cancel();
                return;
            }
            wrapper.write(new ChunkType1_17(chunk.getSections().length), chunk);
            chunk.setChunkMask(BitSet.valueOf(new long[]{chunk.getBitmask()}));
            for (int s2 = 0; s2 < chunk.getSections().length; ++s2) {
                ChunkSection section = chunk.getSections()[s2];
                if (section == null) continue;
                DataPalette palette = section.palette(PaletteType.BLOCKS);
                for (int i2 = 0; i2 < palette.size(); ++i2) {
                    int mappedBlockStateId = protocol.getMappingData().getNewBlockStateId(palette.idByIndex(i2));
                    palette.setIdByIndex(i2, mappedBlockStateId);
                }
            }
        });
        blockRewriter.registerEffect(ClientboundPackets1_16_2.EFFECT, 1010, 2001);
    }

    private static void writeMultiBlockChangePacket(PacketWrapper wrapper, Chunk chunk) throws Exception {
        long chunkPosition = ((long)chunk.getX() & 0x3FFFFFL) << 42;
        chunkPosition |= ((long)chunk.getZ() & 0x3FFFFFL) << 20;
        ChunkSection[] sections = chunk.getSections();
        for (int chunkY = 0; chunkY < sections.length; ++chunkY) {
            ChunkSection section = sections[chunkY];
            if (section == null) continue;
            PacketWrapper blockChangePacket = wrapper.create(ClientboundPackets1_17.MULTI_BLOCK_CHANGE);
            blockChangePacket.write(Type.LONG, chunkPosition | (long)chunkY & 0xFFFFFL);
            blockChangePacket.write(Type.BOOLEAN, true);
            BlockChangeRecord[] blockChangeRecords = new BlockChangeRecord[4096];
            DataPalette palette = section.palette(PaletteType.BLOCKS);
            int j2 = 0;
            for (int x2 = 0; x2 < 16; ++x2) {
                for (int y2 = 0; y2 < 16; ++y2) {
                    for (int z2 = 0; z2 < 16; ++z2) {
                        int blockStateId = Protocol1_17To1_16_4.MAPPINGS.getNewBlockStateId(palette.idAt(x2, y2, z2));
                        blockChangeRecords[j2++] = new BlockChangeRecord1_16_2(x2, y2, z2, blockStateId);
                    }
                }
            }
            blockChangePacket.write(Type.VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY, blockChangeRecords);
            blockChangePacket.send(Protocol1_17To1_16_4.class);
        }
    }
}

