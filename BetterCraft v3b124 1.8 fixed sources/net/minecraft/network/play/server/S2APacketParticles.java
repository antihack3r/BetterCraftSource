/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumParticleTypes;

public class S2APacketParticles
implements Packet<INetHandlerPlayClient> {
    private EnumParticleTypes particleType;
    private float xCoord;
    private float yCoord;
    private float zCoord;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float particleSpeed;
    private int particleCount;
    private boolean longDistance;
    private int[] particleArguments;

    public S2APacketParticles() {
    }

    public S2APacketParticles(EnumParticleTypes particleTypeIn, boolean longDistanceIn, float x2, float y2, float z2, float xOffsetIn, float yOffset, float zOffset, float particleSpeedIn, int particleCountIn, int ... particleArgumentsIn) {
        this.particleType = particleTypeIn;
        this.longDistance = longDistanceIn;
        this.xCoord = x2;
        this.yCoord = y2;
        this.zCoord = z2;
        this.xOffset = xOffsetIn;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.particleSpeed = particleSpeedIn;
        this.particleCount = particleCountIn;
        this.particleArguments = particleArgumentsIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.particleType = EnumParticleTypes.getParticleFromId(buf.readInt());
        if (this.particleType == null) {
            this.particleType = EnumParticleTypes.BARRIER;
        }
        this.longDistance = buf.readBoolean();
        this.xCoord = buf.readFloat();
        this.yCoord = buf.readFloat();
        this.zCoord = buf.readFloat();
        this.xOffset = buf.readFloat();
        this.yOffset = buf.readFloat();
        this.zOffset = buf.readFloat();
        this.particleSpeed = buf.readFloat();
        this.particleCount = buf.readInt();
        int i2 = this.particleType.getArgumentCount();
        this.particleArguments = new int[i2];
        int j2 = 0;
        while (j2 < i2) {
            this.particleArguments[j2] = buf.readVarIntFromBuffer();
            ++j2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(this.particleType.getParticleID());
        buf.writeBoolean(this.longDistance);
        buf.writeFloat(this.xCoord);
        buf.writeFloat(this.yCoord);
        buf.writeFloat(this.zCoord);
        buf.writeFloat(this.xOffset);
        buf.writeFloat(this.yOffset);
        buf.writeFloat(this.zOffset);
        buf.writeFloat(this.particleSpeed);
        buf.writeInt(this.particleCount);
        int i2 = this.particleType.getArgumentCount();
        int j2 = 0;
        while (j2 < i2) {
            buf.writeVarIntToBuffer(this.particleArguments[j2]);
            ++j2;
        }
    }

    public EnumParticleTypes getParticleType() {
        return this.particleType;
    }

    public boolean isLongDistance() {
        return this.longDistance;
    }

    public double getXCoordinate() {
        return this.xCoord;
    }

    public double getYCoordinate() {
        return this.yCoord;
    }

    public double getZCoordinate() {
        return this.zCoord;
    }

    public float getXOffset() {
        return this.xOffset;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public float getZOffset() {
        return this.zOffset;
    }

    public float getParticleSpeed() {
        return this.particleSpeed;
    }

    public int getParticleCount() {
        return Math.min(this.particleCount, 120);
    }

    public int[] getParticleArgs() {
        return this.particleArguments;
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleParticles(this);
    }
}

