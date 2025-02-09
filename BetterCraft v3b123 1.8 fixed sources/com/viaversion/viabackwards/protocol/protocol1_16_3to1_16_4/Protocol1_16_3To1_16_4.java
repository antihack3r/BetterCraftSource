// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viabackwards.protocol.protocol1_16_3to1_16_4;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viabackwards.protocol.protocol1_16_3to1_16_4.storage.PlayerHandStorage;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viabackwards.api.BackwardsProtocol;

public class Protocol1_16_3To1_16_4 extends BackwardsProtocol<ClientboundPackets1_16_2, ClientboundPackets1_16_2, ServerboundPackets1_16_2, ServerboundPackets1_16_2>
{
    public Protocol1_16_3To1_16_4() {
        super(ClientboundPackets1_16_2.class, ClientboundPackets1_16_2.class, ServerboundPackets1_16_2.class, ServerboundPackets1_16_2.class);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_16_2>)this).registerServerbound(ServerboundPackets1_16_2.EDIT_BOOK, new PacketHandlers() {
            public void register() {
                this.map(Type.FLAT_VAR_INT_ITEM);
                this.map((Type<Object>)Type.BOOLEAN);
                this.handler(wrapper -> {
                    final int slot = wrapper.read((Type<Integer>)Type.VAR_INT);
                    if (slot == 1) {
                        wrapper.write(Type.VAR_INT, 40);
                    }
                    else {
                        wrapper.write(Type.VAR_INT, wrapper.user().get(PlayerHandStorage.class).getCurrentHand());
                    }
                });
            }
        });
        ((AbstractProtocol<CU, CM, SM, ServerboundPackets1_16_2>)this).registerServerbound(ServerboundPackets1_16_2.HELD_ITEM_CHANGE, wrapper -> {
            final short slot = wrapper.passthrough((Type<Short>)Type.SHORT);
            wrapper.user().get(PlayerHandStorage.class).setCurrentHand(slot);
        });
    }
    
    @Override
    public void init(final UserConnection user) {
        user.put(new PlayerHandStorage());
    }
}
