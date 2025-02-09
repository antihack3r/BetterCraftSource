// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.login.client;

import net.minecraft.network.INetHandler;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.Packet;

public class CPacketLoginStart implements Packet<INetHandlerLoginServer>
{
    private GameProfile profile;
    
    public CPacketLoginStart() {
    }
    
    public CPacketLoginStart(final GameProfile profileIn) {
        this.profile = profileIn;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.profile = new GameProfile(null, buf.readStringFromBuffer(16));
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.profile.getName());
    }
    
    @Override
    public void processPacket(final INetHandlerLoginServer handler) {
        handler.processLoginStart(this);
    }
    
    public GameProfile getProfile() {
        return this.profile;
    }
}
