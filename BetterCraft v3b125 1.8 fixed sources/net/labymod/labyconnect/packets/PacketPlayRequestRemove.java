/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketPlayRequestRemove
extends Packet {
    private String playerName;

    public PacketPlayRequestRemove(String playerName) {
        this.playerName = playerName;
    }

    public PacketPlayRequestRemove() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.playerName = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.playerName);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getPlayerName() {
        return this.playerName;
    }
}

