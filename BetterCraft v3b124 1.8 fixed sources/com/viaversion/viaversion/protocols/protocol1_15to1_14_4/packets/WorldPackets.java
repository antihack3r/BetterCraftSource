/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets;

import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_14;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_15;
import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.ClientboundPackets1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import com.viaversion.viaversion.rewriter.BlockRewriter;

public final class WorldPackets {
    public static void register(final Protocol1_15To1_14_4 protocol) {
        BlockRewriter<ClientboundPackets1_14_4> blockRewriter = BlockRewriter.for1_14(protocol);
        blockRewriter.registerBlockAction(ClientboundPackets1_14_4.BLOCK_ACTION);
        blockRewriter.registerBlockChange(ClientboundPackets1_14_4.BLOCK_CHANGE);
        blockRewriter.registerMultiBlockChange(ClientboundPackets1_14_4.MULTI_BLOCK_CHANGE);
        blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_14_4.ACKNOWLEDGE_PLAYER_DIGGING);
        protocol.registerClientbound(ClientboundPackets1_14_4.CHUNK_DATA, wrapper -> {
            Chunk chunk = wrapper.read(ChunkType1_14.TYPE);
            wrapper.write(ChunkType1_15.TYPE, chunk);
            if (chunk.isFullChunk()) {
                int[] biomeData = chunk.getBiomeData();
                int[] newBiomeData = new int[1024];
                if (biomeData != null) {
                    int i2;
                    for (i2 = 0; i2 < 4; ++i2) {
                        for (int j2 = 0; j2 < 4; ++j2) {
                            int x2 = (j2 << 2) + 2;
                            int z2 = (i2 << 2) + 2;
                            int oldIndex = z2 << 4 | x2;
                            newBiomeData[i2 << 2 | j2] = biomeData[oldIndex];
                        }
                    }
                    for (i2 = 1; i2 < 64; ++i2) {
                        System.arraycopy(newBiomeData, 0, newBiomeData, i2 * 16, 16);
                    }
                }
                chunk.setBiomeData(newBiomeData);
            }
            for (int s2 = 0; s2 < chunk.getSections().length; ++s2) {
                ChunkSection section = chunk.getSections()[s2];
                if (section == null) continue;
                DataPalette palette = section.palette(PaletteType.BLOCKS);
                for (int i3 = 0; i3 < palette.size(); ++i3) {
                    int mappedBlockStateId = protocol.getMappingData().getNewBlockStateId(palette.idByIndex(i3));
                    palette.setIdByIndex(i3, mappedBlockStateId);
                }
            }
        });
        blockRewriter.registerEffect(ClientboundPackets1_14_4.EFFECT, 1010, 2001);
        protocol.registerClientbound(ClientboundPackets1_14_4.SPAWN_PARTICLE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map((Type)Type.FLOAT, Type.DOUBLE);
                this.map((Type)Type.FLOAT, Type.DOUBLE);
                this.map((Type)Type.FLOAT, Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int id2 = wrapper.get(Type.INT, 0);
                    if (id2 == 3 || id2 == 23) {
                        int data = wrapper.passthrough(Type.VAR_INT);
                        wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockStateId(data));
                    } else if (id2 == 32) {
                        protocol.getItemRewriter().handleItemToClient(wrapper.passthrough(Type.ITEM1_13_2));
                    }
                });
            }
        });
    }
}

