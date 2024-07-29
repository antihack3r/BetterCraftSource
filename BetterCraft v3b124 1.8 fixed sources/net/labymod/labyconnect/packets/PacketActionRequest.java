/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.UUID;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketActionRequest
extends Packet {
    private UUID uuid;

    public PacketActionRequest() {
    }

    public PacketActionRequest(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void read(PacketBuf buf) {
        this.uuid = UUID.fromString(buf.readString());
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.uuid.toString());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
    }

    public UUID getUuid() {
        return this.uuid;
    }
}

