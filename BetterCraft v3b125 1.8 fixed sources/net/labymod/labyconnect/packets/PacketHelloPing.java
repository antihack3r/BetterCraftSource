/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketHelloPing
extends Packet {
    private long a;

    public PacketHelloPing() {
    }

    public PacketHelloPing(long a2) {
    }

    @Override
    public void read(PacketBuf buf) {
        this.a = buf.readLong();
        buf.readInt();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeLong(this.a);
        buf.writeInt(23);
    }

    public int getId() {
        return 0;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}

