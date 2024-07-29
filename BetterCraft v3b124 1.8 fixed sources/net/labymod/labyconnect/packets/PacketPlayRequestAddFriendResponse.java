/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketPlayRequestAddFriendResponse
extends Packet {
    private String searched;
    private boolean requestSent;
    private String reason;

    public PacketPlayRequestAddFriendResponse(String searched, boolean sent) {
        this.searched = searched;
        this.requestSent = sent;
    }

    public PacketPlayRequestAddFriendResponse(String searched, boolean sent, String reason) {
        this.searched = searched;
        this.requestSent = sent;
        this.reason = reason;
    }

    public PacketPlayRequestAddFriendResponse() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.searched = buf.readString();
        this.requestSent = buf.readBoolean();
        if (!this.requestSent) {
            this.reason = buf.readString();
        }
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.searched);
        buf.writeBoolean(this.requestSent);
        if (!this.isRequestSent()) {
            buf.writeString(this.reason);
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public boolean isRequestSent() {
        return this.requestSent;
    }

    public String getReason() {
        return this.reason;
    }

    public String getSearched() {
        return this.searched;
    }
}

