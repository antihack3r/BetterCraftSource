// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.network;

import net.minecraft.util.IChatComponent;
import net.minecraft.network.INetHandler;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer
{
    private final MinecraftServer mcServer;
    private final NetworkManager networkManager;
    
    public NetHandlerHandshakeMemory(final MinecraftServer mcServerIn, final NetworkManager networkManagerIn) {
        this.mcServer = mcServerIn;
        this.networkManager = networkManagerIn;
    }
    
    @Override
    public void processHandshake(final C00Handshake packetIn) {
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
    }
    
    @Override
    public void onDisconnect(final IChatComponent reason) {
    }
}
