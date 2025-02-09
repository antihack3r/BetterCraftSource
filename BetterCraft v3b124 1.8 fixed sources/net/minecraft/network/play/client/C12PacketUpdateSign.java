/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class C12PacketUpdateSign
implements Packet<INetHandlerPlayServer> {
    private BlockPos pos;
    private IChatComponent[] lines;

    public C12PacketUpdateSign() {
    }

    public C12PacketUpdateSign(BlockPos pos, IChatComponent[] lines) {
        this.pos = pos;
        this.lines = new IChatComponent[]{lines[0], lines[1], lines[2], lines[3]};
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.lines = new IChatComponent[4];
        int i2 = 0;
        while (i2 < 4) {
            IChatComponent ichatcomponent;
            String s2 = buf.readStringFromBuffer(384);
            this.lines[i2] = ichatcomponent = IChatComponent.Serializer.jsonToComponent(s2);
            ++i2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.pos);
        int i2 = 0;
        while (i2 < 4) {
            IChatComponent ichatcomponent = this.lines[i2];
            String s2 = IChatComponent.Serializer.componentToJson(ichatcomponent);
            buf.writeString(s2);
            ++i2;
        }
    }

    @Override
    public void processPacket(INetHandlerPlayServer handler) {
        handler.processUpdateSign(this);
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public IChatComponent[] getLines() {
        return this.lines;
    }
}

