/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class S33PacketUpdateSign
implements Packet<INetHandlerPlayClient> {
    private World world;
    private BlockPos blockPos;
    private IChatComponent[] lines;

    public S33PacketUpdateSign() {
    }

    public S33PacketUpdateSign(World worldIn, BlockPos blockPosIn, IChatComponent[] linesIn) {
        this.world = worldIn;
        this.blockPos = blockPosIn;
        this.lines = new IChatComponent[]{linesIn[0], linesIn[1], linesIn[2], linesIn[3]};
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.blockPos = buf.readBlockPos();
        this.lines = new IChatComponent[4];
        int i2 = 0;
        while (i2 < 4) {
            this.lines[i2] = buf.readChatComponent();
            ++i2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeBlockPos(this.blockPos);
        int i2 = 0;
        while (i2 < 4) {
            buf.writeChatComponent(this.lines[i2]);
            ++i2;
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateSign(this);
    }

    public BlockPos getPos() {
        return this.blockPos;
    }

    public IChatComponent[] getLines() {
        return this.lines;
    }
}

