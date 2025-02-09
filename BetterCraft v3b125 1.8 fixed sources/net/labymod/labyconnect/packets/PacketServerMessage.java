/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketServerMessage
extends Packet {
    private String message;

    public PacketServerMessage(String message) {
        this.message = message;
    }

    public PacketServerMessage() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.message = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.message);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getMessage() {
        return this.message;
    }
}

