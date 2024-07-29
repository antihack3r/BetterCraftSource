/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketKick
extends Packet {
    private String cause;

    public PacketKick(String cause) {
        this.cause = cause;
    }

    public PacketKick() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.cause = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.getReason());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getReason() {
        return this.cause;
    }
}

