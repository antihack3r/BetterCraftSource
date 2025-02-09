/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.TimeZone;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.packets.PacketLoginOptions;
import net.labymod.labyconnect.user.UserStatus;

public class PacketPlayChangeOptions
extends Packet {
    private PacketLoginOptions.Options options;

    public PacketPlayChangeOptions(PacketLoginOptions.Options options) {
        this.options = options;
    }

    public PacketPlayChangeOptions(boolean showServer, UserStatus status, TimeZone timeZone) {
        this.options = new PacketLoginOptions.Options(showServer, status, timeZone);
    }

    public PacketPlayChangeOptions() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.options = new PacketLoginOptions.Options(buf.readBoolean(), buf.readUserStatus(), TimeZone.getTimeZone(buf.readString()));
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeBoolean(this.getOptions().isShowServer());
        buf.writeUserStatus(this.getOptions().getOnlineStatus());
        buf.writeString(this.getOptions().getTimeZone().getID());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public PacketLoginOptions.Options getOptions() {
        return this.options;
    }
}

