/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.handling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.packets.Protocol;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;

public class PacketDecoder
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
        PacketBuf packetBuffer = LabyModCore.getMinecraft().createPacketBuf(byteBuf);
        if (packetBuffer.readableBytes() < 1) {
            return;
        }
        int id2 = packetBuffer.readVarIntFromBuffer();
        Packet packet = Protocol.getProtocol().getPacket(id2);
        if (id2 != 62 && id2 != 63 || LabyMod.getInstance().getLabyConnect().getClientConnection().getCustomIp() != null) {
            Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "[IN] " + id2 + " " + packet.getClass().getSimpleName());
        }
        packet.read(packetBuffer);
        if (packetBuffer.readableBytes() > 0) {
            throw new IOException("Packet  (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetBuffer.readableBytes() + " bytes extra whilst reading packet " + packet);
        }
        objects.add(packet);
    }
}

