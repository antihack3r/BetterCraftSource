/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.beans.ConstructorProperties;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketActionPlay
extends Packet {
    private short requestId;
    private short actionType;
    private byte[] data;

    @Override
    public void read(PacketBuf buf) {
        this.requestId = buf.readShort();
        this.actionType = buf.readShort();
        int length = buf.readVarIntFromBuffer();
        if (length > 1024) {
            throw new RuntimeException("data array too big");
        }
        this.data = new byte[length];
        buf.readBytes(this.data);
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeShort(this.requestId);
        buf.writeShort(this.actionType);
        if (this.data == null) {
            buf.writeVarIntToBuffer(0);
        } else {
            buf.writeVarIntToBuffer(this.data.length);
            buf.writeBytes(this.data);
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
    }

    public short getRequestId() {
        return this.requestId;
    }

    public short getActionType() {
        return this.actionType;
    }

    public byte[] getData() {
        return this.data;
    }

    @ConstructorProperties(value={"requestId", "actionType", "data"})
    public PacketActionPlay(short requestId, short actionType, byte[] data) {
        this.requestId = requestId;
        this.actionType = actionType;
        this.data = data;
    }

    public PacketActionPlay() {
    }
}

