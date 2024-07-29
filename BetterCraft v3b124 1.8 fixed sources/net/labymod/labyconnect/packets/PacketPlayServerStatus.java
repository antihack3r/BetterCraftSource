/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import com.google.common.base.Objects;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ServerInfo;

public class PacketPlayServerStatus
extends Packet {
    private String serverIp = "";
    private int port = 25565;
    private String gamemode = null;

    public PacketPlayServerStatus(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = null;
    }

    public PacketPlayServerStatus() {
    }

    public PacketPlayServerStatus(String serverIp, int port, String gamemode) {
        this.serverIp = serverIp;
        this.port = port;
        this.gamemode = gamemode;
    }

    @Override
    public void read(PacketBuf buf) {
        this.serverIp = buf.readString();
        this.port = buf.readInt();
        if (buf.readBoolean()) {
            this.gamemode = buf.readString();
        }
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.serverIp);
        buf.writeInt(this.port);
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

    public String getServerIp() {
        return this.serverIp;
    }

    public int getPort() {
        return this.port;
    }

    public String getGamemode() {
        return this.gamemode;
    }

    public ServerInfo build() {
        if (this.gamemode == null) {
            return new ServerInfo(this.serverIp, this.port);
        }
        return new ServerInfo(this.serverIp, this.port, this.gamemode);
    }

    public boolean equals(PacketPlayServerStatus packet) {
        return this.serverIp.equals(packet.getServerIp()) && this.port == packet.getPort() && Objects.equal(this.gamemode, packet.getGamemode());
    }
}

