/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.UUID;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketUserBadge
extends Packet {
    private UUID[] uuids;
    private byte[] ranks;

    public PacketUserBadge(UUID[] uuids) {
        this.uuids = uuids;
    }

    @Override
    public void read(PacketBuf buf) {
        int size = buf.readVarIntFromBuffer();
        this.uuids = new UUID[size];
        int i2 = 0;
        while (i2 < size) {
            this.uuids[i2] = new UUID(buf.readLong(), buf.readLong());
            ++i2;
        }
        byte[] bytes = new byte[size];
        buf.readBytes(bytes);
        this.ranks = bytes;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeVarIntToBuffer(this.uuids.length);
        int i2 = 0;
        while (i2 < this.uuids.length) {
            UUID uuid = this.uuids[i2];
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
            ++i2;
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public UUID[] getUuids() {
        return this.uuids;
    }

    public byte[] getRanks() {
        return this.ranks;
    }

    public PacketUserBadge() {
    }
}

