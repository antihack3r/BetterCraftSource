/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.CryptManager;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketEncryptionResponse
extends Packet {
    private byte[] sharedSecret;
    private byte[] verifyToken;

    public PacketEncryptionResponse(SecretKey key, PublicKey publicKey, byte[] hash) {
        this.sharedSecret = CryptManager.encryptData(publicKey, key.getEncoded());
        this.verifyToken = CryptManager.encryptData(publicKey, hash);
    }

    public PacketEncryptionResponse() {
    }

    public byte[] getSharedSecret() {
        return this.sharedSecret;
    }

    public byte[] getVerifyToken() {
        return this.verifyToken;
    }

    @Override
    public void read(PacketBuf buf) {
        this.sharedSecret = buf.readByteArray();
        this.verifyToken = buf.readByteArray();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeByteArray(this.sharedSecret);
        buf.writeByteArray(this.verifyToken);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}

