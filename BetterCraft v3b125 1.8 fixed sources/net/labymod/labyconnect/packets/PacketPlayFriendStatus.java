/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.ServerInfo;

public class PacketPlayFriendStatus
extends Packet {
    private ChatUser player;
    private ServerInfo playerInfo;

    public PacketPlayFriendStatus(ChatUser player, ServerInfo playerInfo) {
        this.player = player;
        this.playerInfo = playerInfo;
    }

    public PacketPlayFriendStatus() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.player = buf.readChatUser();
        this.playerInfo = buf.readServerInfo();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.player);
        buf.writeServerInfo(this.playerInfo);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public ChatUser getPlayer() {
        return this.player;
    }

    public ServerInfo getPlayerInfo() {
        return this.playerInfo;
    }
}

