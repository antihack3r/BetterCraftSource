/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketPlayTyping
extends Packet {
    private ChatUser player;
    private ChatUser inChatWith;
    private boolean typing;

    public PacketPlayTyping(ChatUser player, ChatUser inChatWith, boolean typing) {
        this.player = player;
        this.inChatWith = inChatWith;
        this.typing = typing;
    }

    public PacketPlayTyping() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.player = buf.readChatUser();
        this.inChatWith = buf.readChatUser();
        this.typing = buf.readBoolean();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.player);
        buf.writeChatUser(this.inChatWith);
        buf.writeBoolean(this.typing);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public ChatUser getInChatWith() {
        return this.inChatWith;
    }

    public ChatUser getPlayer() {
        return this.player;
    }

    public boolean isTyping() {
        return this.typing;
    }
}

