/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.handling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketSplitter
extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf buffer, ByteBuf byteBuf) {
        int var4 = buffer.readableBytes();
        int var5 = PacketBuf.getVarIntSize(var4);
        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
        }
        PacketBuf packetBuffer = LabyModCore.getMinecraft().createPacketBuf(byteBuf);
        packetBuffer.ensureWritable(var5 + var4);
        packetBuffer.writeVarIntToBuffer(var4);
        packetBuffer.writeBytes(buffer, buffer.readerIndex(), var4);
    }
}

