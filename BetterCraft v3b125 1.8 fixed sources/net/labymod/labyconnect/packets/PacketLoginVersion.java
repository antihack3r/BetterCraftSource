/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketLoginVersion
extends Packet {
    private int versionId;
    private String versionName;
    private String updateLink;

    public PacketLoginVersion(int internalVersion, String externalVersion) {
        this.versionId = internalVersion;
        this.versionName = externalVersion;
    }

    public PacketLoginVersion() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.versionId = buf.readInt();
        this.versionName = buf.readString();
        this.updateLink = buf.readString();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeInt(this.versionId);
        buf.writeString(this.versionName);
        buf.writeString("");
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public String getVersionName() {
        return this.versionName;
    }

    public int getVersionID() {
        return this.versionId;
    }

    public String getUpdateLink() {
        return this.updateLink;
    }
}

