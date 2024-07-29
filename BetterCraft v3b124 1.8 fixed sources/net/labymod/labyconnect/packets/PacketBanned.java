/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketBanned
extends Packet {
    private String reason;
    private long until;

    public PacketBanned(String reason, long until) {
        this.reason = reason;
        this.until = until;
    }

    public PacketBanned() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.reason = buf.readString();
        this.until = buf.readLong();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.reason);
        buf.writeLong(this.until);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getReason() {
        return this.reason;
    }

    public long getUntil() {
        return this.until;
    }
}

