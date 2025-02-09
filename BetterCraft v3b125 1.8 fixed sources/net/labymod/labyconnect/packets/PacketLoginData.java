/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.UUID;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketLoginData
extends Packet {
    private UUID id;
    private String name;
    private String motd;

    public PacketLoginData() {
    }

    public PacketLoginData(UUID id2, String name, String motd) {
        this.id = id2;
        this.name = name;
        this.motd = motd;
    }

    @Override
    public void read(PacketBuf buf) {
        this.id = UUID.fromString(buf.readString());
        this.name = buf.readString();
        this.motd = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        if (this.id == null) {
            buf.writeString(UUID.randomUUID().toString());
        } else {
            buf.writeString(this.id.toString());
        }
        buf.writeString(this.name);
        buf.writeString(this.motd);
    }

    public int getId() {
        return 0;
    }

    public UUID getUUID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getMotd() {
        return this.motd;
    }
}

