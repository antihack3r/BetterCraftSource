/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketPlayPlayerOnline
extends Packet {
    private ChatUser newOnlinePlayer;

    public PacketPlayPlayerOnline(ChatUser newOnlinePlayer) {
        this.newOnlinePlayer = newOnlinePlayer;
    }

    public PacketPlayPlayerOnline() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.newOnlinePlayer = buf.readChatUser();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.newOnlinePlayer);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public ChatUser getPlayer() {
        return this.newOnlinePlayer;
    }
}

