// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.Packet;

public class CPacketCloseWindow implements Packet<INetHandlerPlayServer>
{
    private int windowId;
    
    public CPacketCloseWindow() {
    }
    
    public CPacketCloseWindow(final int windowIdIn) {
        this.windowId = windowIdIn;
    }
    
    @Override
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processCloseWindow(this);
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.windowId = buf.readByte();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
    }
}
