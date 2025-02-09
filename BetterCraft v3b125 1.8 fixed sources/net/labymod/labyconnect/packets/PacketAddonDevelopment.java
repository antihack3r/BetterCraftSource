/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.UUID;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.main.LabyMod;
import net.labymod.utils.GZIPCompression;

public class PacketAddonDevelopment
extends Packet {
    private UUID sender;
    private UUID[] receivers;
    private String key;
    private byte[] data;

    public PacketAddonDevelopment(UUID sender, String key, byte[] data) {
        this.sender = sender;
        this.key = key;
        this.data = GZIPCompression.compress(data);
        this.receivers = new UUID[0];
    }

    public PacketAddonDevelopment(UUID sender, UUID[] receivers, String key, byte[] data) {
        this.sender = sender;
        this.receivers = receivers;
        this.key = key;
        this.data = GZIPCompression.compress(data);
    }

    @Override
    public void read(PacketBuf buf) {
        this.sender = new UUID(buf.readLong(), buf.readLong());
        short receiverCnt = buf.readShort();
        this.receivers = new UUID[receiverCnt];
        int i2 = 0;
        while (i2 < this.receivers.length) {
            this.receivers[i2] = new UUID(buf.readLong(), buf.readLong());
            ++i2;
        }
        this.key = buf.readString();
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        this.data = data;
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeLong(this.sender.getMostSignificantBits());
        buf.writeLong(this.sender.getLeastSignificantBits());
        buf.writeShort(this.receivers.length);
        UUID[] uUIDArray = this.receivers;
        int n2 = this.receivers.length;
        int n3 = 0;
        while (n3 < n2) {
            UUID receiver = uUIDArray[n3];
            buf.writeLong(receiver.getMostSignificantBits());
            buf.writeLong(receiver.getLeastSignificantBits());
            ++n3;
        }
        buf.writeString(this.key);
        buf.writeInt(this.data.length);
        buf.writeBytes(this.data);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        LabyMod.getInstance().getEventManager().callAddonDevelopmentPacket(this);
    }

    public byte[] getData() {
        return GZIPCompression.decompress(this.data);
    }

    public UUID getSender() {
        return this.sender;
    }

    public UUID[] getReceivers() {
        return this.receivers;
    }

    public String getKey() {
        return this.key;
    }

    public PacketAddonDevelopment() {
    }
}

