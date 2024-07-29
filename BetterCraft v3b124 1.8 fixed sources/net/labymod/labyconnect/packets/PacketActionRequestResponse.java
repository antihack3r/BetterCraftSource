/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.main.LabyMod;
import net.labymod.user.User;

public class PacketActionRequestResponse
extends Packet {
    private UUID uuid;
    private short actionId;
    private byte[] data;

    public PacketActionRequestResponse() {
    }

    public PacketActionRequestResponse(UUID uuid, short actionId, byte[] data) {
        this.uuid = uuid;
        this.actionId = actionId;
        this.data = data;
    }

    @Override
    public void read(PacketBuf buf) {
        this.uuid = UUID.fromString(buf.readString());
        this.actionId = buf.readShort();
        int length = buf.readVarIntFromBuffer();
        if (length > 1024) {
            throw new RuntimeException("data array too big");
        }
        this.data = new byte[length];
        buf.readBytes(this.data);
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeString(this.uuid.toString());
        buf.writeShort(this.actionId);
        if (this.data == null) {
            buf.writeVarIntToBuffer(0);
        } else {
            buf.writeVarIntToBuffer(this.data.length);
            buf.writeBytes(this.data);
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        switch (this.actionId) {
            case 1: {
                if (LabyModCore.getMinecraft().getPlayer() != null && LabyModCore.getMinecraft().getPlayer().getUniqueID().equals(this.uuid)) break;
                LabyMod.getInstance().getEmoteRegistry().handleEmote(this.uuid, this.data);
                break;
            }
            case 2: {
                LabyMod.getInstance().getUserManager().updateUsersJson(this.uuid, new String(this.data, StandardCharsets.UTF_8), null);
                break;
            }
            case 3: {
                if (LabyModCore.getMinecraft().getPlayer() != null && LabyModCore.getMinecraft().getPlayer().getUniqueID().equals(this.uuid)) break;
                User user = LabyMod.getInstance().getUserManager().getUser(this.uuid);
                LabyMod.getInstance().getStickerRegistry().handleSticker(user, LabyMod.getInstance().getStickerRegistry().bytesToShort(this.data));
            }
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public short getActionId() {
        return this.actionId;
    }

    public byte[] getData() {
        return this.data;
    }
}

