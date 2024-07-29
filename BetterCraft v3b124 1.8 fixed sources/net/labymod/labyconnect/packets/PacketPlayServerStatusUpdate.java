/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import com.google.common.base.Objects;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketPlayServerStatusUpdate
extends Packet {
    private String serverIp = "";
    private int port = 25565;
    private String gamemode = null;
    private boolean viaServerlist;

    public PacketPlayServerStatusUpdate(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = null;
    }

    public PacketPlayServerStatusUpdate() {
    }

    public PacketPlayServerStatusUpdate(String serverIp, int port, String gamemode, boolean viaServerlist) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = gamemode;
        this.viaServerlist = viaServerlist;
    }

    @Override
    public void read(PacketBuf buf) {
        this.serverIp = buf.readString();
        this.port = buf.readInt();
        this.viaServerlist = buf.readBoolean();
        if (buf.readBoolean()) {
            this.gamemode = buf.readString();
        }
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.serverIp);
        buf.writeInt(this.port);
        buf.writeBoolean(this.viaServerlist);
        if (this.gamemode != null && !this.gamemode.isEmpty()) {
            buf.writeBoolean(true);
            buf.writeString(this.gamemode);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public boolean equals(PacketPlayServerStatusUpdate packet) {
        return this.serverIp.equals(packet.serverIp) && this.port == packet.port && Objects.equal(this.gamemode, packet.gamemode);
    }
}

