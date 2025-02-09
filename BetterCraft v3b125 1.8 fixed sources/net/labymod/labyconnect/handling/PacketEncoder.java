/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.handling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.Packet;
import net.labymod.labyconnect.packets.PacketBuf;
import net.labymod.labyconnect.packets.Protocol;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;

public class PacketEncoder
extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        PacketBuf packetBuffer = LabyModCore.getMinecraft().createPacketBuf(byteBuf);
        int id2 = Protocol.getProtocol().getPacketId(packet);
        if (id2 != 62 && id2 != 63 || LabyMod.getInstance().getLabyConnect().getClientConnection().getCustomIp() != null) {
            Debug.log(Debug.EnumDebugMode.LABYMOD_CHAT, "[OUT] " + id2 + " " + packet.getClass().getSimpleName());
        }
        packetBuffer.writeVarIntToBuffer(Protocol.getProtocol().getPacketId(packet));
        packet.write(packetBuffer);
    }
}

