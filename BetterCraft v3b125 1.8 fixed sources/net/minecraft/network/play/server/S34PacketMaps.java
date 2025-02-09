/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps
implements Packet<INetHandlerPlayClient> {
    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public S34PacketMaps() {
    }

    public S34PacketMaps(int mapIdIn, byte scale, Collection<Vec4b> visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY) {
        this.mapId = mapIdIn;
        this.mapScale = scale;
        this.mapVisiblePlayersVec4b = visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
        this.mapMinX = minX;
        this.mapMinY = minY;
        this.mapMaxX = maxX;
        this.mapMaxY = maxY;
        this.mapDataBytes = new byte[maxX * maxY];
        int i2 = 0;
        while (i2 < maxX) {
            int j2 = 0;
            while (j2 < maxY) {
                this.mapDataBytes[i2 + j2 * maxX] = colors[minX + i2 + (minY + j2) * 128];
                ++j2;
            }
            ++i2;
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.mapId = buf.readVarIntFromBuffer();
        this.mapScale = buf.readByte();
        this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];
        int i2 = 0;
        while (i2 < this.mapVisiblePlayersVec4b.length) {
            short short1 = buf.readByte();
            this.mapVisiblePlayersVec4b[i2] = new Vec4b((byte)(short1 >> 4 & 0xF), buf.readByte(), buf.readByte(), (byte)(short1 & 0xF));
            ++i2;
        }
        this.mapMaxX = buf.readUnsignedByte();
        if (this.mapMaxX > 0) {
            this.mapMaxY = buf.readUnsignedByte();
            this.mapMinX = buf.readUnsignedByte();
            this.mapMinY = buf.readUnsignedByte();
            this.mapDataBytes = buf.readByteArray();
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.mapId);
        buf.writeByte(this.mapScale);
        buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);
        Vec4b[] vec4bArray = this.mapVisiblePlayersVec4b;
        int n2 = this.mapVisiblePlayersVec4b.length;
        int n3 = 0;
        while (n3 < n2) {
            Vec4b vec4b = vec4bArray[n3];
            buf.writeByte((vec4b.func_176110_a() & 0xF) << 4 | vec4b.func_176111_d() & 0xF);
            buf.writeByte(vec4b.func_176112_b());
            buf.writeByte(vec4b.func_176113_c());
            ++n3;
        }
        buf.writeByte(this.mapMaxX);
        if (this.mapMaxX > 0) {
            buf.writeByte(this.mapMaxY);
            buf.writeByte(this.mapMinX);
            buf.writeByte(this.mapMinY);
            buf.writeByteArray(this.mapDataBytes);
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleMaps(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void setMapdataTo(MapData mapdataIn) {
        mapdataIn.scale = this.mapScale;
        mapdataIn.mapDecorations.clear();
        int i2 = 0;
        while (i2 < this.mapVisiblePlayersVec4b.length) {
            Vec4b vec4b = this.mapVisiblePlayersVec4b[i2];
            mapdataIn.mapDecorations.put("icon-" + i2, vec4b);
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.mapMaxX) {
            int k2 = 0;
            while (k2 < this.mapMaxY) {
                mapdataIn.colors[this.mapMinX + j2 + (this.mapMinY + k2) * 128] = this.mapDataBytes[j2 + k2 * this.mapMaxX];
                ++k2;
            }
            ++j2;
        }
    }
}

