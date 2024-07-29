/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketDisconnect
extends Packet {
    private String reason;

    public PacketDisconnect() {
    }

    public PacketDisconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void read(PacketBuf buf) {
        this.reason = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        if (this.getReason() == null) {
            buf.writeString("Client Error");
            return;
        }
        buf.writeString(this.getReason());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getReason() {
        return this.reason;
    }
}

