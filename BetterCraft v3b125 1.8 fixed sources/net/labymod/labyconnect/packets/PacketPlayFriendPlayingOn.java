/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketPlayFriendPlayingOn
extends Packet {
    private ChatUser player;
    private String gameModeName;

    public PacketPlayFriendPlayingOn(ChatUser player, String gameModeName) {
        this.player = player;
        this.gameModeName = gameModeName;
    }

    public PacketPlayFriendPlayingOn() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.player = buf.readChatUser();
        this.gameModeName = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.player);
        buf.writeString(this.gameModeName);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getGameModeName() {
        return this.gameModeName;
    }

    public ChatUser getPlayer() {
        return this.player;
    }
}

