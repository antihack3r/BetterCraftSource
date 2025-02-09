/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketEncryptionRequest
extends Packet {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public PacketEncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public PacketEncryptionRequest() {
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public String getServerId() {
        return this.serverId;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public void read(PacketBuf buf) {
        this.serverId = buf.readString();
        this.publicKey = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.serverId);
        buf.writeByteArray(this.publicKey);
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}

