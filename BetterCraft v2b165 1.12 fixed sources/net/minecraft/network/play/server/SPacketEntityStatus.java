// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketEntityStatus implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private byte logicOpcode;
    
    public SPacketEntityStatus() {
    }
    
    public SPacketEntityStatus(final Entity entityIn, final byte opcodeIn) {
        this.entityId = entityIn.getEntityId();
        this.logicOpcode = opcodeIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.entityId = buf.readInt();
        this.logicOpcode = buf.readByte();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeInt(this.entityId);
        buf.writeByte(this.logicOpcode);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleEntityStatus(this);
    }
    
    public Entity getEntity(final World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }
    
    public byte getOpCode() {
        return this.logicOpcode;
    }
}
