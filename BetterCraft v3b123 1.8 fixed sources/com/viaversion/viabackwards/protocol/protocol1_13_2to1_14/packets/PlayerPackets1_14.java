// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.packets;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.storage.DifficultyStorage;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viabackwards.protocol.protocol1_13_2to1_14.Protocol1_13_2To1_14;
import com.viaversion.viaversion.api.rewriter.RewriterBase;

public class PlayerPackets1_14 extends RewriterBase<Protocol1_13_2To1_14>
{
    public PlayerPackets1_14(final Protocol1_13_2To1_14 protocol) {
        super(protocol);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_14, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_14.SERVER_DIFFICULTY, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.BOOLEAN, Type.NOTHING);
                this.handler(wrapper -> {
                    final byte difficulty = wrapper.get((Type<Short>)Type.UNSIGNED_BYTE, 0).byteValue();
                    wrapper.user().get(DifficultyStorage.class).setDifficulty(difficulty);
                });
            }
        });
        ((AbstractProtocol<ClientboundPackets1_14, CM, SM, SU>)this.protocol).registerClientbound(ClientboundPackets1_14.OPEN_SIGN_EDITOR, new PacketHandlers() {
            public void register() {
                this.map(Type.POSITION1_14, Type.POSITION);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.QUERY_BLOCK_NBT, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.PLAYER_DIGGING, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.RECIPE_BOOK_DATA, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int type = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    if (type == 0) {
                        wrapper.passthrough(Type.STRING);
                    }
                    else if (type == 1) {
                        wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                        wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                        wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                        wrapper.passthrough((Type<Object>)Type.BOOLEAN);
                        wrapper.write(Type.BOOLEAN, false);
                        wrapper.write(Type.BOOLEAN, false);
                        wrapper.write(Type.BOOLEAN, false);
                        wrapper.write(Type.BOOLEAN, false);
                    }
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.UPDATE_COMMAND_BLOCK, new PacketHandlers() {
            public void register() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.UPDATE_STRUCTURE_BLOCK, new PacketHandlers() {
            public void register() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.UPDATE_SIGN, new PacketHandlers() {
            public void register() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_13>)this.protocol).registerServerbound(ServerboundPackets1_13.PLAYER_BLOCK_PLACEMENT, wrapper -> {
            final Position position = wrapper.read(Type.POSITION);
            final int face = wrapper.read((Type<Integer>)Type.VAR_INT);
            final int hand = wrapper.read((Type<Integer>)Type.VAR_INT);
            final float x = wrapper.read((Type<Float>)Type.FLOAT);
            final float y = wrapper.read((Type<Float>)Type.FLOAT);
            final float z = wrapper.read((Type<Float>)Type.FLOAT);
            wrapper.write(Type.VAR_INT, hand);
            wrapper.write(Type.POSITION1_14, position);
            wrapper.write(Type.VAR_INT, face);
            wrapper.write(Type.FLOAT, x);
            wrapper.write(Type.FLOAT, y);
            wrapper.write(Type.FLOAT, z);
            wrapper.write(Type.BOOLEAN, false);
        });
    }
}
