/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketMojangStatus
extends Packet {
    @Override
    public void read(PacketBuf buf) {
        buf.readInt();
        buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}

