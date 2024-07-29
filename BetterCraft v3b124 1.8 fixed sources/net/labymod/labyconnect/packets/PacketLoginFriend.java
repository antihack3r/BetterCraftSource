/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.ArrayList;
import java.util.List;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketLoginFriend
extends Packet {
    private List<ChatUser> friends;

    public PacketLoginFriend(List<ChatUser> friends) {
        this.friends = friends;
    }

    public PacketLoginFriend() {
    }

    @Override
    public void read(PacketBuf buf) {
        ArrayList<ChatUser> players = new ArrayList<ChatUser>();
        int a2 = buf.readInt();
        int i2 = 0;
        while (i2 < a2) {
            players.add(buf.readChatUser());
            ++i2;
        }
        this.friends = new ArrayList<ChatUser>();
        this.friends.addAll(players);
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeInt(this.getFriends().size());
        int i2 = 0;
        while (i2 < this.getFriends().size()) {
            ChatUser p2 = this.getFriends().get(i2);
            buf.writeChatUser(p2);
            ++i2;
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public List<ChatUser> getFriends() {
        return this.friends;
    }
}

