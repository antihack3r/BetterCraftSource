/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatRequest;

public class PacketPlayDenyFriendRequest
extends Packet {
    private ChatRequest denied;

    public PacketPlayDenyFriendRequest(ChatRequest denied) {
        this.denied = denied;
    }

    public PacketPlayDenyFriendRequest() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.denied = (ChatRequest)buf.readChatUser();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.denied);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public ChatRequest getDenied() {
        return this.denied;
    }
}

