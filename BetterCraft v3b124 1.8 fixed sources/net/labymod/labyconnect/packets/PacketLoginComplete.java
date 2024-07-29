/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketLoginComplete
extends Packet {
    private String dashboardPin;

    public PacketLoginComplete(String string) {
        this.dashboardPin = string;
    }

    public PacketLoginComplete() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.dashboardPin = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.dashboardPin);
    }

    public int getId() {
        return 2;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getDashboardPin() {
        return this.dashboardPin;
    }
}

