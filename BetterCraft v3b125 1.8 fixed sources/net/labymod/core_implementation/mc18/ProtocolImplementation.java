/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import io.netty.buffer.Unpooled;
import net.labymod.api.protocol.chunk.ChunkCachingProtocol;
import net.labymod.api.protocol.chunk.Extracted;
import net.labymod.api.protocol.shadow.ShadowProtocol;
import net.labymod.core.LabyModCore;
import net.labymod.core.ProtocolAdapter;
import net.labymod.main.LabyMod;
import net.labymod.utils.GZIPCompression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.util.MovementInput;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.Chunk;

public class ProtocolImplementation
implements ProtocolAdapter {
    @Override
    public void onReceiveChunkPacket(Object packetBuffer, Object packet) {
        ChunkCachingProtocol protocol = LabyMod.getInstance().getChunkCachingProtocol();
        if (packet instanceof S21PacketChunkData) {
            S21PacketChunkData chunkDataPacket = (S21PacketChunkData)packet;
            protocol.onReceive18ChunkData(chunkDataPacket.getChunkX(), chunkDataPacket.getChunkZ(), chunkDataPacket.getExtractedSize(), chunkDataPacket.getExtractedDataBytes());
        }
        if (packet instanceof S26PacketMapChunkBulk) {
            S26PacketMapChunkBulk chunkDataPacket2 = (S26PacketMapChunkBulk)packet;
            int i2 = chunkDataPacket2.getChunkCount();
            int k2 = 0;
            while (k2 < i2) {
                protocol.onReceive18ChunkData(chunkDataPacket2.getChunkX(k2), chunkDataPacket2.getChunkZ(k2), chunkDataPacket2.getChunkSize(k2), chunkDataPacket2.getChunkBytes(k2));
                ++k2;
            }
        }
    }

    @Override
    public void loadChunk(ChunkCachingProtocol protocol, Extracted extracted, int chunkX, int chunkZ, boolean flag) {
        byte[] decompressedData = GZIPCompression.decompress(extracted.data);
        WorldClient clientWorldController = Minecraft.getMinecraft().theWorld;
        if (flag) {
            if (extracted.dataSize == 0) {
                clientWorldController.doPreChunk(chunkX, chunkZ, false);
                return;
            }
            clientWorldController.doPreChunk(chunkX, chunkZ, true);
        }
        clientWorldController.invalidateBlockReceiveRegion(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
        Chunk chunk = clientWorldController.getChunkFromChunkCoords(chunkX, chunkZ);
        chunk.fillChunk(decompressedData, extracted.dataSize, flag);
        clientWorldController.markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
        if (!flag || !(clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
        }
    }

    @Override
    public void loadChunkBulk(ChunkCachingProtocol protocol, Extracted[] extractedArray, int[] chunkX, int[] chunkZ) {
        WorldClient clientWorldController = Minecraft.getMinecraft().theWorld;
        int i2 = 0;
        while (i2 < extractedArray.length) {
            Extracted extracted = extractedArray[i2];
            byte[] decompressedData = GZIPCompression.decompress(extracted.data);
            int x2 = chunkX[i2];
            int z2 = chunkZ[i2];
            clientWorldController.doPreChunk(x2, z2, true);
            clientWorldController.invalidateBlockReceiveRegion(x2 << 4, 0, z2 << 4, (x2 << 4) + 15, 256, (z2 << 4) + 15);
            Chunk chunk = clientWorldController.getChunkFromChunkCoords(x2, z2);
            chunk.fillChunk(decompressedData, extracted.dataSize, true);
            clientWorldController.markBlockRangeForRenderUpdate(x2 << 4, 0, z2 << 4, (x2 << 4) + 15, 256, (z2 << 4) + 15);
            if (!(clientWorldController.provider instanceof WorldProviderSurface)) {
                chunk.resetRelightChecks();
            }
            ++i2;
        }
    }

    @Override
    public boolean handleOutgoingPacket(Object msg, ShadowProtocol shadowProtocol) {
        if (msg instanceof C03PacketPlayer.C04PacketPlayerPosition) {
            C03PacketPlayer.C04PacketPlayerPosition packet = (C03PacketPlayer.C04PacketPlayerPosition)msg;
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeByte(0);
            packetBuffer.writeDouble(packet.getPositionX());
            packetBuffer.writeDouble(packet.getPositionY());
            packetBuffer.writeDouble(packet.getPositionZ());
            packetBuffer.writeBoolean(packet.isOnGround());
            this.addInputs(packetBuffer, shadowProtocol);
            LabyMod.getInstance().getLabyModAPI().sendPluginMessage("SHADOW", packetBuffer);
            return true;
        }
        if (msg instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook packet2 = (C03PacketPlayer.C05PacketPlayerLook)msg;
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeByte(1);
            packetBuffer.writeFloat(packet2.getYaw());
            packetBuffer.writeFloat(packet2.getPitch());
            packetBuffer.writeBoolean(packet2.isOnGround());
            this.addInputs(packetBuffer, shadowProtocol);
            LabyMod.getInstance().getLabyModAPI().sendPluginMessage("SHADOW", packetBuffer);
            return true;
        }
        if (msg instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook packet3 = (C03PacketPlayer.C06PacketPlayerPosLook)msg;
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeByte(2);
            packetBuffer.writeDouble(packet3.getPositionX());
            packetBuffer.writeDouble(packet3.getPositionY());
            packetBuffer.writeDouble(packet3.getPositionZ());
            packetBuffer.writeFloat(packet3.getYaw());
            packetBuffer.writeFloat(packet3.getPitch());
            packetBuffer.writeBoolean(packet3.isOnGround());
            this.addInputs(packetBuffer, shadowProtocol);
            LabyMod.getInstance().getLabyModAPI().sendPluginMessage("SHADOW", packetBuffer);
            return true;
        }
        if (msg instanceof C03PacketPlayer) {
            C03PacketPlayer packet4 = (C03PacketPlayer)msg;
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeByte(3);
            packetBuffer.writeBoolean(packet4.isOnGround());
            this.addInputs(packetBuffer, shadowProtocol);
            LabyMod.getInstance().getLabyModAPI().sendPluginMessage("SHADOW", packetBuffer);
        }
        return false;
    }

    private void addInputs(PacketBuffer packetBuffer, ShadowProtocol shadowProtocol) {
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        MovementInput movementInput = player.movementInput;
        packetBuffer.writeLong(System.currentTimeMillis());
        packetBuffer.writeDouble(movementInput.moveForward);
        packetBuffer.writeDouble(movementInput.moveStrafe);
        packetBuffer.writeBoolean(movementInput.jump);
        packetBuffer.writeBoolean(movementInput.sneak);
        packetBuffer.writeDouble(player.posX);
        packetBuffer.writeDouble(player.posY);
        packetBuffer.writeDouble(player.posZ);
        packetBuffer.writeFloat(player.rotationYaw);
        packetBuffer.writeFloat(player.rotationPitch);
        packetBuffer.writeBoolean(player.isSprinting());
        packetBuffer.writeInt(shadowProtocol.getPacketCounter().get());
    }
}

