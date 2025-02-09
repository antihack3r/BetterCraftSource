/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketPlayRequestAddFriend
extends Packet {
    private String name;

    public PacketPlayRequestAddFriend(String name) {
        this.name = name;
    }

    public PacketPlayRequestAddFriend() {
    }

    @Override
    public void read(PacketBuf buf) {
        byte[] a2 = new byte[buf.readInt()];
        int i2 = 0;
        while (i2 < a2.length) {
            a2[i2] = buf.readByte();
            ++i2;
        }
        this.name = new String(a2);
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeInt(this.name.getBytes().length);
        buf.writeBytes(this.name.getBytes());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getName() {
        return this.name;
    }
}

