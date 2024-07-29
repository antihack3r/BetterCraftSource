/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatUser;

public class PacketMessage
extends Packet {
    private ChatUser sender;
    private ChatUser to;
    private String message;
    private long sentTime;
    private long fileSize;
    private double audioTime;

    public PacketMessage(ChatUser sender, ChatUser to2, String message, long fileSize, double time, long sentTime) {
        this.sender = sender;
        this.to = to2;
        this.message = message;
        this.fileSize = fileSize;
        this.audioTime = time;
        this.sentTime = sentTime;
    }

    public PacketMessage() {
    }

    @Override
    public void read(PacketBuf buf) {
        this.sender = buf.readChatUser();
        this.to = buf.readChatUser();
        this.message = buf.readString();
        this.fileSize = buf.readLong();
        this.audioTime = buf.readDouble();
        this.sentTime = buf.readLong();
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeChatUser(this.sender);
        buf.writeChatUser(this.to);
        buf.writeString(this.message);
        buf.writeLong(this.fileSize);
        buf.writeDouble(this.audioTime);
        buf.writeLong(this.sentTime);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    public double getAudioTime() {
        return this.audioTime;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public String getMessage() {
        return this.message;
    }

    public ChatUser getSender() {
        return this.sender;
    }

    public ChatUser getTo() {
        return this.to;
    }

    public long getSentTime() {
        return this.sentTime;
    }
}

