// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_9_1to1_9;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class Protocol1_9_1To1_9 extends AbstractProtocol<ClientboundPackets1_9, ClientboundPackets1_9, ServerboundPackets1_9, ServerboundPackets1_9>
{
    public Protocol1_9_1To1_9() {
        super(ClientboundPackets1_9.class, ClientboundPackets1_9.class, ServerboundPackets1_9.class, ServerboundPackets1_9.class);
    }
    
    @Override
    protected void registerPackets() {
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.JOIN_GAME, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.BYTE, Type.INT);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map((Type<Object>)Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map((Type<Object>)Type.BOOLEAN);
            }
        });
        ((AbstractProtocol<ClientboundPackets1_9, CM, SM, SU>)this).registerClientbound(ClientboundPackets1_9.SOUND, new PacketHandlers() {
            public void register() {
                this.map((Type<Object>)Type.VAR_INT);
                this.handler(wrapper -> {
                    final int sound = wrapper.get((Type<Integer>)Type.VAR_INT, 0);
                    if (sound >= 415) {
                        wrapper.set(Type.VAR_INT, 0, sound + 1);
                    }
                });
            }
        });
    }
}
