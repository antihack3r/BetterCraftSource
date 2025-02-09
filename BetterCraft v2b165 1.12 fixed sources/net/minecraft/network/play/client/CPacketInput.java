// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.Packet;

public class CPacketInput implements Packet<INetHandlerPlayServer>
{
    private float strafeSpeed;
    private float field_192621_b;
    private boolean jumping;
    private boolean sneaking;
    
    public CPacketInput() {
    }
    
    public CPacketInput(final float strafeSpeedIn, final float forwardSpeedIn, final boolean jumpingIn, final boolean sneakingIn) {
        this.strafeSpeed = strafeSpeedIn;
        this.field_192621_b = forwardSpeedIn;
        this.jumping = jumpingIn;
        this.sneaking = sneakingIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.strafeSpeed = buf.readFloat();
        this.field_192621_b = buf.readFloat();
        final byte b0 = buf.readByte();
        this.jumping = ((b0 & 0x1) > 0);
        this.sneaking = ((b0 & 0x2) > 0);
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeFloat(this.strafeSpeed);
        buf.writeFloat(this.field_192621_b);
        byte b0 = 0;
        if (this.jumping) {
            b0 |= 0x1;
        }
        if (this.sneaking) {
            b0 |= 0x2;
        }
        buf.writeByte(b0);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processInput(this);
    }
    
    public float getStrafeSpeed() {
        return this.strafeSpeed;
    }
    
    public float func_192620_b() {
        return this.field_192621_b;
    }
    
    public boolean isJumping() {
        return this.jumping;
    }
    
    public boolean isSneaking() {
        return this.sneaking;
    }
}
