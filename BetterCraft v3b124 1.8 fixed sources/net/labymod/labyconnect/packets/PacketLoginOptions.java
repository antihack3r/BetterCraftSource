/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.TimeZone;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.UserStatus;

public class PacketLoginOptions
extends Packet {
    private boolean showServer;
    private UserStatus status;
    private TimeZone timeZone;

    public PacketLoginOptions(boolean showServer, UserStatus status, TimeZone timeZone) {
        this.showServer = showServer;
        this.status = status;
        this.timeZone = timeZone;
    }

    public PacketLoginOptions() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.showServer = buf.readBoolean();
        this.status = buf.readUserStatus();
        this.timeZone = TimeZone.getTimeZone(buf.readString());
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeBoolean(this.showServer);
        buf.writeUserStatus(this.status);
        buf.writeString(this.timeZone.getID());
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public Options getOptions() {
        return new Options(this.showServer, this.status, this.timeZone);
    }

    public static class Options {
        private final boolean showServer;
        private final UserStatus onlineStatus;
        private final TimeZone timeZone;

        public Options(boolean showServer, UserStatus onlineStatus, TimeZone timeZone) {
            this.showServer = showServer;
            this.timeZone = timeZone;
            this.onlineStatus = onlineStatus;
        }

        public boolean isShowServer() {
            return this.showServer;
        }

        public UserStatus getOnlineStatus() {
            return this.onlineStatus;
        }

        public TimeZone getTimeZone() {
            return this.timeZone;
        }
    }
}

