/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3APacketTabComplete
implements Packet<INetHandlerPlayClient> {
    private String[] matches;

    public S3APacketTabComplete() {
    }

    public S3APacketTabComplete(String[] matchesIn) {
        this.matches = matchesIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.matches = new String[buf.readVarIntFromBuffer()];
        int i2 = 0;
        while (i2 < this.matches.length) {
            this.matches[i2] = buf.readStringFromBuffer(Short.MAX_VALUE);
            ++i2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.matches.length);
        String[] stringArray = this.matches;
        int n2 = this.matches.length;
        int n3 = 0;
        while (n3 < n2) {
            String s2 = stringArray[n3];
            buf.writeString(s2);
            ++n3;
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTabComplete(this);
    }

    public String[] func_149630_c() {
        return this.matches;
    }
}

