/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.packets;

import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_14_4to1_15.Protocol1_14_4To1_15;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_14;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_15;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.rewriter.RecipeRewriter;

public class BlockItemPackets1_15
extends ItemRewriter<ClientboundPackets1_15, ServerboundPackets1_14, Protocol1_14_4To1_15> {
    public BlockItemPackets1_15(Protocol1_14_4To1_15 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        BlockRewriter<ClientboundPackets1_15> blockRewriter = BlockRewriter.for1_14(this.protocol);
        new RecipeRewriter<ClientboundPackets1_15>(this.protocol).register(ClientboundPackets1_15.DECLARE_RECIPES);
        ((Protocol1_14_4To1_15)this.protocol).registerServerbound(ServerboundPackets1_14.EDIT_BOOK, wrapper -> this.handleItemToServer(wrapper.passthrough(Type.ITEM1_13_2)));
        this.registerSetCooldown(ClientboundPackets1_15.COOLDOWN);
        this.registerWindowItems(ClientboundPackets1_15.WINDOW_ITEMS, Type.ITEM1_13_2_SHORT_ARRAY);
        this.registerSetSlot(ClientboundPackets1_15.SET_SLOT, Type.ITEM1_13_2);
        this.registerTradeList(ClientboundPackets1_15.TRADE_LIST);
        this.registerEntityEquipment(ClientboundPackets1_15.ENTITY_EQUIPMENT, Type.ITEM1_13_2);
        this.registerAdvancements(ClientboundPackets1_15.ADVANCEMENTS, Type.ITEM1_13_2);
        this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.ITEM1_13_2);
        this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.ITEM1_13_2);
        blockRewriter.registerAcknowledgePlayerDigging(ClientboundPackets1_15.ACKNOWLEDGE_PLAYER_DIGGING);
        blockRewriter.registerBlockAction(ClientboundPackets1_15.BLOCK_ACTION);
        blockRewriter.registerBlockChange(ClientboundPackets1_15.BLOCK_CHANGE);
        blockRewriter.registerMultiBlockChange(ClientboundPackets1_15.MULTI_BLOCK_CHANGE);
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.CHUNK_DATA, wrapper -> {
            int j2;
            Chunk chunk = wrapper.read(ChunkType1_15.TYPE);
            wrapper.write(ChunkType1_14.TYPE, chunk);
            if (chunk.isFullChunk()) {
                int[] biomeData = chunk.getBiomeData();
                int[] newBiomeData = new int[256];
                for (int i2 = 0; i2 < 4; ++i2) {
                    for (j2 = 0; j2 < 4; ++j2) {
                        int x2 = j2 << 2;
                        int z2 = i2 << 2;
                        int newIndex = z2 << 4 | x2;
                        int oldIndex = i2 << 2 | j2;
                        int biome = biomeData[oldIndex];
                        for (int k2 = 0; k2 < 4; ++k2) {
                            int offX = newIndex + (k2 << 4);
                            for (int l2 = 0; l2 < 4; ++l2) {
                                newBiomeData[offX + l2] = biome;
                            }
                        }
                    }
                }
                chunk.setBiomeData(newBiomeData);
            }
            for (int i3 = 0; i3 < chunk.getSections().length; ++i3) {
                ChunkSection section = chunk.getSections()[i3];
                if (section == null) continue;
                DataPalette palette = section.palette(PaletteType.BLOCKS);
                for (j2 = 0; j2 < palette.size(); ++j2) {
                    int mappedBlockStateId = ((Protocol1_14_4To1_15)this.protocol).getMappingData().getNewBlockStateId(palette.idByIndex(j2));
                    palette.setIdByIndex(j2, mappedBlockStateId);
                }
            }
        });
        blockRewriter.registerEffect(ClientboundPackets1_15.EFFECT, 1010, 2001);
        ((Protocol1_14_4To1_15)this.protocol).registerClientbound(ClientboundPackets1_15.SPAWN_PARTICLE, new PacketHandlers(){

            @Override
            public void register() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map((Type)Type.DOUBLE, Type.FLOAT);
                this.map((Type)Type.DOUBLE, Type.FLOAT);
                this.map((Type)Type.DOUBLE, Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int id2 = wrapper.get(Type.INT, 0);
                    if (id2 == 3 || id2 == 23) {
                        int data = wrapper.passthrough(Type.VAR_INT);
                        wrapper.set(Type.VAR_INT, 0, ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewBlockStateId(data));
                    } else if (id2 == 32) {
                        Item item = BlockItemPackets1_15.this.handleItemToClient(wrapper.read(Type.ITEM1_13_2));
                        wrapper.write(Type.ITEM1_13_2, item);
                    }
                    int mappedId = ((Protocol1_14_4To1_15)BlockItemPackets1_15.this.protocol).getMappingData().getNewParticleId(id2);
                    if (id2 != mappedId) {
                        wrapper.set(Type.INT, 0, mappedId);
                    }
                });
            }
        });
    }
}

