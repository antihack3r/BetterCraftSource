/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketPlayFriendRemove
extends Packet {
    private ChatUser toRemove;

    public PacketPlayFriendRemove(ChatUser toRemove) {
        this.toRemove = toRemove;
    }

    public PacketPlayFriendRemove() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.toRemove = buf.readChatUser();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.toRemove);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public ChatUser getToRemove() {
        return this.toRemove;
    }
}

