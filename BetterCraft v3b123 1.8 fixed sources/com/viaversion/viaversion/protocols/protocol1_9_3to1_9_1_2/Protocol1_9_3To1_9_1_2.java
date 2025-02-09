// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.chunks.DataPalette;
import java.util.List;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.chunks.FakeTileEntity;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class Protocol1_9_3To1_9_1_2 extends AbstractProtocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3>
{
    public static final ValueTransformer<Short, Short> ADJUST_PITCH;
    
    public Protocol1_9_3To1_9_1_2() {
        super(ClientboundPackets1_9.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9.class, ServerboundPackets1_9_3.class);
    }
    
    @Override
    protected void registerPackets() {
        ((Protocol<ClientboundPackets1_9, ClientboundPackets1_9_3, SM, SU>)this).registerClientbound(ClientboundPackets1_9.UPDATE_SIGN, null, wrapper -> {
            final Position position = wrapper.read(Type.POSITION);
            final JsonElement[] lines = new JsonElement[4];
            for (int i = 0; i < 4; ++i) {
                lines[i] = wrapper.read(Type.COMPONENT);
            }
            wrapper.clearInputBuffer();
            wrapper.setPacketType(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA);
            wrapper.write(Type.POSITION, position);
            wrapper.write(Type.UNSIGNED_BYTE, (Short)9);
            final CompoundTag tag = new CompoundTag();
            tag.put("id", new StringTag("Sign"));
            tag.put("x", new IntTag(position.x()));
            tag.put("y", new IntTag(position.y()));
            tag.put("z", new IntTag(position.z()));
            for (int j = 0; j < lines.length; ++j) {
                tag.put("Text" + (j + 1), new StringTag(lines[j].toString()));
            }
            wrapper.write(Type.NBT, tag);
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.CHUNK_DATA, wrapper -> {
            final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
            final Chunk chunk = wrapper.read((Type<Chunk>)new Chunk1_9_1_2Type(clientWorld));
            wrapper.write((Type<Chunk>)new Chunk1_9_3_4Type(clientWorld), chunk);
            final List<CompoundTag> tags = chunk.getBlockEntities();
            for (int s = 0; s < chunk.getSections().length; ++s) {
                final ChunkSection section = chunk.getSections()[s];
                if (section != null) {
                    final DataPalette blocks = section.palette(PaletteType.BLOCKS);
                    for (int idx = 0; idx < 4096; ++idx) {
                        final int id = blocks.idAt(idx) >> 4;
                        if (FakeTileEntity.isTileEntity(id)) {
                            tags.add(FakeTileEntity.createTileEntity(ChunkSection.xFromIndex(idx) + (chunk.getX() << 4), ChunkSection.yFromIndex(idx) + (s << 4), ChunkSection.zFromIndex(idx) + (chunk.getZ() << 4), id));
                        }
                    }
                }
            }
            return;
        });
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Integer>)Type.INT, 1);
                    clientWorld.setEnvironment(dimensionId);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.RESPAWN, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final int dimensionId = wrapper.get((Type<Integer>)Type.INT, 0);
                    clientWorld.setEnvironment(dimensionId);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.SOUND, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.VAR_INT);
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.FLOAT);
                this.map(Protocol1_9_3To1_9_1_2.ADJUST_PITCH);
            }
        });
    }
    
    @Override
    public void init(final UserConnection user) {
        if (!user.has(ClientWorld.class)) {
            user.put(new ClientWorld(user));
        }
    }
    
    static {
        ADJUST_PITCH = new ValueTransformer<Short, Short>(Type.UNSIGNED_BYTE, Type.UNSIGNED_BYTE) {
            @Override
            public Short transform(final PacketWrapper wrapper, final Short inputValue) throws Exception {
                return (short)Math.round(inputValue / 63.5f * 63.0f);
            }
        };
    }
}
