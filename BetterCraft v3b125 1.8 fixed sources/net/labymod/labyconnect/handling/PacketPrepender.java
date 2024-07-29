/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.handling;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketPrepender
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> objects) {
        buffer.markReaderIndex();
        byte[] a2 = new byte[3];
        int i2 = 0;
        while (i2 < a2.length) {
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return;
            }
            a2[i2] = buffer.readByte();
            if (a2[i2] >= 0) {
                PacketBuf buf = LabyModCore.getMinecraft().createPacketBuf(Unpooled.wrappedBuffer(a2));
                try {
                    int varInt = buf.readVarIntFromBuffer();
                    if (buffer.readableBytes() < varInt) {
                        buffer.resetReaderIndex();
                        return;
                    }
                    objects.add(buffer.readBytes(varInt));
                }
                finally {
                    buf.release();
                }
                return;
            }
            ++i2;
        }
        throw new RuntimeException("length wider than 21-bit");
    }
}

