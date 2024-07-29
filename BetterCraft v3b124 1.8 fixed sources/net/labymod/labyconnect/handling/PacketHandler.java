/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.handling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketAddonMessage;
import net.labymod.labyconnect.packets.PacketBanned;
import net.labymod.labyconnect.packets.PacketChatVisibilityChange;
import net.labymod.labyconnect.packets.PacketDisconnect;
import net.labymod.labyconnect.packets.PacketEncryptionRequest;
import net.labymod.labyconnect.packets.PacketEncryptionResponse;
import net.labymod.labyconnect.packets.PacketHelloPing;
import net.labymod.labyconnect.packets.PacketHelloPong;
import net.labymod.labyconnect.packets.PacketKick;
import net.labymod.labyconnect.packets.PacketLoginComplete;
import net.labymod.labyconnect.packets.PacketLoginData;
import net.labymod.labyconnect.packets.PacketLoginFriend;
import net.labymod.labyconnect.packets.PacketLoginOptions;
import net.labymod.labyconnect.packets.PacketLoginRequest;
import net.labymod.labyconnect.packets.PacketLoginTime;
import net.labymod.labyconnect.packets.PacketLoginVersion;
import net.labymod.labyconnect.packets.PacketMessage;
import net.labymod.labyconnect.packets.PacketMojangStatus;
import net.labymod.labyconnect.packets.PacketPing;
import net.labymod.labyconnect.packets.PacketPlayChangeOptions;
import net.labymod.labyconnect.packets.PacketPlayDenyFriendRequest;
import net.labymod.labyconnect.packets.PacketPlayFriendPlayingOn;
import net.labymod.labyconnect.packets.PacketPlayFriendRemove;
import net.labymod.labyconnect.packets.PacketPlayFriendStatus;
import net.labymod.labyconnect.packets.PacketPlayPlayerOnline;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriend;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriendResponse;
import net.labymod.labyconnect.packets.PacketPlayRequestRemove;
import net.labymod.labyconnect.packets.PacketPlayServerStatus;
import net.labymod.labyconnect.packets.PacketPlayServerStatusUpdate;
import net.labymod.labyconnect.packets.PacketPlayTyping;
import net.labymod.labyconnect.packets.PacketPong;
import net.labymod.labyconnect.packets.PacketServerMessage;
import net.labymod.labyconnect.packets.PacketUpdateCosmetics;
import net.labymod.labyconnect.packets.PacketUserBadge;

public abstract class PacketHandler
extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object packet) throws Exception {
        this.handlePacket((Packet)packet);
    }

    private void handlePacket(Packet packet) {
        packet.handle(this);
    }

    public abstract void handle(PacketLoginData var1);

    public abstract void handle(PacketHelloPing var1);

    public abstract void handle(PacketHelloPong var1);

    public abstract void handle(PacketPlayPlayerOnline var1);

    public abstract void handle(PacketLoginComplete var1);

    public abstract void handle(PacketChatVisibilityChange var1);

    public abstract void handle(PacketKick var1);

    public abstract void handle(PacketDisconnect var1);

    public abstract void handle(PacketPlayRequestAddFriend var1);

    public abstract void handle(PacketLoginFriend var1);

    public abstract void handle(PacketLoginRequest var1);

    public abstract void handle(PacketBanned var1);

    public abstract void handle(PacketPing var1);

    public abstract void handle(PacketPong var1);

    public abstract void handle(PacketServerMessage var1);

    public abstract void handle(PacketMessage var1);

    public abstract void handle(PacketPlayTyping var1);

    public abstract void handle(PacketPlayRequestAddFriendResponse var1);

    public abstract void handle(PacketPlayRequestRemove var1);

    public abstract void handle(PacketPlayDenyFriendRequest var1);

    public abstract void handle(PacketPlayFriendRemove var1);

    public abstract void handle(PacketLoginOptions var1);

    public abstract void handle(PacketPlayServerStatus var1);

    public abstract void handle(PacketPlayServerStatusUpdate var1);

    public abstract void handle(PacketPlayFriendStatus var1);

    public abstract void handle(PacketPlayFriendPlayingOn var1);

    public abstract void handle(PacketPlayChangeOptions var1);

    public abstract void handle(PacketLoginTime var1);

    public abstract void handle(PacketLoginVersion var1);

    public abstract void handle(PacketEncryptionRequest var1);

    public abstract void handle(PacketEncryptionResponse var1);

    public abstract void handle(PacketMojangStatus var1);

    public abstract void handle(PacketUpdateCosmetics var1);

    public abstract void handle(PacketAddonMessage var1);

    public abstract void handle(PacketUserBadge var1);
}

