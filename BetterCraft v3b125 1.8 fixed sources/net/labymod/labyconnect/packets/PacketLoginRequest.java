/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import java.util.ArrayList;
import java.util.List;
import net.labymod.labyconnect.handling.PacketHandler;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.user.ChatRequest;

public class PacketLoginRequest
extends Packet {
    private List<ChatRequest> requesters;

    public PacketLoginRequest(List<ChatRequest> requesters) {
        this.requesters = requesters;
    }

    public PacketLoginRequest() {
    }

    public List<ChatRequest> getRequests() {
        return this.requesters;
    }

    @Override
    public void read(PacketBuf buf) {
        this.requesters = new ArrayList<ChatRequest>();
        int a2 = buf.readInt();
        int i2 = 0;
        while (i2 < a2) {
            this.requesters.add((ChatRequest)buf.readChatUser());
            ++i2;
        }
    }

    @Override
    public void write(PacketBuf buf) {
        buf.writeInt(this.getRequests().size());
        int i2 = 0;
        while (i2 < this.getRequests().size()) {
            buf.writeChatUser(this.getRequests().get(i2));
            ++i2;
        }
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
}

