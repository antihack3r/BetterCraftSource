// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.chunks.BlockEntity;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class Protocol1_9_1_2To1_9_3_4 extends AbstractProtocol<ClientboundPackets1_9_3, ClientboundPackets1_9, ServerboundPackets1_9_3, ServerboundPackets1_9>
{
    public Protocol1_9_1_2To1_9_3_4() {
        super(ClientboundPackets1_9_3.class, ClientboundPackets1_9.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9.class);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_9_3, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketHandlers() {
            public void register() {
                this.map(Type.POSITION);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.NBT);
                this.handler(wrapper -> {
                    if (wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0) == 9) {
                        final Position position = wrapper.get(Type.POSITION, 0);
                        final CompoundTag tag = wrapper.get(Type.NBT, 0);
                        wrapper.clearPacket();
                        wrapper.setPacketType(ClientboundPackets1_9.UPDATE_SIGN);
                        wrapper.write(Type.POSITION, position);
                        for (int i = 1; i < 5; ++i) {
                            final Tag textTag = tag.get("Text" + i);
                            final String line = (textTag instanceof StringTag) ? ((StringTag)textTag).getValue() : "";
                            wrapper.write(Type.STRING, line);
                        }
                    }
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_9_3, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9_3.CHUNK_DATA, wrapper -> {
            final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
            final Chunk1_9_3_4Type newType = new Chunk1_9_3_4Type(clientWorld);
            final Chunk1_9_1_2Type oldType = new Chunk1_9_1_2Type(clientWorld);
            final Chunk chunk = wrapper.read((Type<Chunk>)newType);
            wrapper.write((Type<Chunk>)oldType, chunk);
            BlockEntity.handle(chunk.getBlockEntities(), wrapper.user());
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_9_3, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9_3.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Integer>)Type.INT, 1);
                    clientChunks.setEnvironment(dimensionId);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_9_3, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9_3.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Integer>)Type.INT, 0);
                    clientWorld.setEnvironment(dimensionId);
                });
            }
        });
    }
    
    @Override
    public void init(final UserConnection userConnection) {
        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
    }
}
