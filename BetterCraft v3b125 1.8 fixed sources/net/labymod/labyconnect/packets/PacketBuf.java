/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.labymod.labyconnect.user.ChatRequest;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.ServerInfo;
import net.labymod.labyconnect.user.UserStatus;

public abstract class PacketBuf
extends ByteBuf {
    protected ByteBuf buf;

    public PacketBuf(ByteBuf buf) {
        this.buf = buf;
    }

    public ChatUser readChatUser() {
        String username = this.readString();
        UUID uuid = this.readUUID();
        String statusMessage = this.readString();
        UserStatus status = this.readUserStatus();
        boolean request = this.readBoolean();
        String timeZone = this.readString();
        int contactsAmound = this.readInt();
        long lastOnline = this.readLong();
        long firstJoined = this.readLong();
        ServerInfo serverInfo = this.readServerInfo();
        if (request) {
            return new ChatRequest(new GameProfile(uuid, username));
        }
        return new ChatUser(new GameProfile(uuid, username), status, statusMessage, serverInfo, 0, System.currentTimeMillis(), 0L, timeZone, lastOnline, firstJoined, contactsAmound, false);
    }

    public void writeChatUser(ChatUser player) {
        this.writeString(player.getGameProfile().getName());
        this.writeUUID(player.getGameProfile().getId());
        this.writeString(player.getStatusMessage());
        this.writeUserStatus(player.getStatus());
        this.writeBoolean(player.isFriendRequest());
        this.writeString(player.getTimeZone());
        this.writeInt(player.getContactAmount());
        this.writeLong(player.getLastOnline());
        this.writeLong(player.getFirstJoined());
        this.writeServerInfo(player.getCurrentServerInfo());
    }

    public ServerInfo readServerInfo() {
        String serverIp = this.readString();
        int serverPort = this.readInt();
        if (this.readBoolean()) {
            return new ServerInfo(serverIp, serverPort, this.readString());
        }
        return new ServerInfo(serverIp, serverPort);
    }

    public PacketBuf writeServerInfo(ServerInfo info) {
        if (info == null) {
            info = new ServerInfo("", 0);
        }
        this.writeString(info.getServerIp() == null ? "" : info.getServerIp());
        this.writeInt(info.getServerPort());
        if (info.getSpecifiedServerName() != null) {
            this.writeBoolean(true);
            this.writeString(info.getSpecifiedServerName());
        } else {
            this.writeBoolean(false);
        }
        return this;
    }

    public PacketBuf writeUserStatus(UserStatus status) {
        this.writeByte(status.getId());
        return this;
    }

    public UserStatus readUserStatus() {
        return UserStatus.getById(this.readByte());
    }

    public void writeByteArray(byte[] data) {
        this.writeInt(data.length);
        this.writeBytes(data);
    }

    public byte[] readByteArray() {
        byte[] b2 = new byte[this.readInt()];
        int i2 = 0;
        while (i2 < b2.length) {
            b2[i2] = this.readByte();
            ++i2;
        }
        return b2;
    }

    public void writeEnum(Enum<?> enume) {
        this.writeInt(enume.ordinal());
    }

    public void writeUUID(UUID uuid) {
        this.writeString(uuid.toString());
    }

    public UUID readUUID() {
        return UUID.fromString(this.readString());
    }

    public void writeString(String string) {
        this.writeInt(string.getBytes(Charsets.UTF_8).length);
        this.writeBytes(string.getBytes(Charsets.UTF_8));
    }

    public String readString() {
        byte[] a2 = new byte[this.readInt()];
        int i2 = 0;
        while (i2 < a2.length) {
            a2[i2] = this.readByte();
            ++i2;
        }
        return new String(a2, Charsets.UTF_8);
    }

    public static int getVarIntSize(int input) {
        int var1 = 1;
        while (var1 < 5) {
            if ((input & -1 << var1 * 7) == 0) {
                return var1;
            }
            ++var1;
        }
        return 5;
    }

    public int readVarIntFromBuffer() {
        byte var3;
        int var1 = 0;
        int var2 = 0;
        do {
            var3 = this.readByte();
            var1 |= (var3 & 0x7F) << var2++ * 7;
            if (var2 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((var3 & 0x80) == 128);
        return var1;
    }

    public void writeVarIntToBuffer(int input) {
        while ((input & 0xFFFFFF80) != 0) {
            this.writeByte(input & 0x7F | 0x80);
            input >>>= 7;
        }
        this.writeByte(input);
    }
}

