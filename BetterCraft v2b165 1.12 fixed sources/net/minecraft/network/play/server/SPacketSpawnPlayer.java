// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import net.minecraft.network.datasync.EntityDataManager;
import java.util.UUID;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.Packet;

public class SPacketSpawnPlayer implements Packet<INetHandlerPlayClient>
{
    public int entityId;
    public UUID uniqueId;
    public double x;
    public double y;
    public double z;
    public byte yaw;
    public byte pitch;
    private EntityDataManager watcher;
    public List<EntityDataManager.DataEntry<?>> dataManagerEntries;
    
    public SPacketSpawnPlayer() {
    }
    
    public SPacketSpawnPlayer(final EntityPlayer player) {
        this.entityId = player.getEntityId();
        this.uniqueId = player.getGameProfile().getId();
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
        this.yaw = (byte)(player.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(player.rotationPitch * 256.0f / 360.0f);
        this.watcher = player.getDataManager();
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.entityId = buf.readVarIntFromBuffer();
        this.uniqueId = buf.readUuid();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.dataManagerEntries = EntityDataManager.readEntries(buf);
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeUuid(this.uniqueId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        this.watcher.writeEntries(buf);
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleSpawnPlayer(this);
    }
    
    @Nullable
    public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
        return this.dataManagerEntries;
    }
    
    public int getEntityID() {
        return this.entityId;
    }
    
    public UUID getUniqueId() {
        return this.uniqueId;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public byte getYaw() {
        return this.yaw;
    }
    
    public byte getPitch() {
        return this.pitch;
    }
}
