/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.protocol.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.labymod.api.protocol.chunk.ChunkCachingProtocol;

public class CCPChannelHandler
extends ChannelInboundHandlerAdapter {
    private ChunkCachingProtocol ccp;

    public CCPChannelHandler(ChunkCachingProtocol ccp) {
        this.ccp = ccp;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf;
        if (!this.ccp.isCachingSupported()) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg instanceof ByteBuf && (buf = (ByteBuf)msg).readableBytes() >= 4) {
            int index = buf.readerIndex();
            int packetId = this.readVarInt(buf);
            if (packetId != 32) {
                buf.readerIndex(index);
                ctx.fireChannelRead(msg);
                return;
            }
            byte[] array = buf.nioBuffer().array();
            buf.readerIndex(index);
            this.ccp.onReceive112ChunkData(array);
            buf.readerIndex(index);
        }
        ctx.fireChannelRead(msg);
    }

    private int readVarInt(ByteBuf buf) {
        byte var3;
        int var1 = 0;
        int var2 = 0;
        do {
            var3 = buf.readByte();
            var1 |= (var3 & 0x7F) << var2++ * 7;
            if (var2 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((var3 & 0x80) == 128);
        return var1;
    }
}

