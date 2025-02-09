/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities
implements Packet<INetHandlerPlayClient> {
    private int[] entityIDs;

    public S13PacketDestroyEntities() {
    }

    public S13PacketDestroyEntities(int ... entityIDsIn) {
        this.entityIDs = entityIDsIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityIDs = new int[buf.readVarIntFromBuffer()];
        int i2 = 0;
        while (i2 < this.entityIDs.length) {
            this.entityIDs[i2] = buf.readVarIntFromBuffer();
            ++i2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.entityIDs.length);
        int i2 = 0;
        while (i2 < this.entityIDs.length) {
            buf.writeVarIntToBuffer(this.entityIDs[i2]);
            ++i2;
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDestroyEntities(this);
    }

    public int[] getEntityIDs() {
        return this.entityIDs;
    }
}

