/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import net.minecraft.network.PacketBuffer;

public class NettyCompressionDecoder
extends ByteToMessageDecoder {
    private final Inflater inflater;
    private int treshold;

    public NettyCompressionDecoder(int treshold) {
        this.treshold = treshold;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws DataFormatException, Exception {
        if (p_decode_2_.readableBytes() != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            int i2 = packetbuffer.readVarIntFromBuffer();
            if (i2 == 0) {
                p_decode_3_.add(packetbuffer.readBytes(packetbuffer.readableBytes()));
            } else {
                if (i2 < this.treshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i2 + " is below server threshold of " + this.treshold);
                }
                if (i2 > 0x200000) {
                    throw new DecoderException("Badly compressed packet - size of " + i2 + " is larger than protocol maximum of " + 0x200000);
                }
                byte[] abyte = new byte[packetbuffer.readableBytes()];
                packetbuffer.readBytes(abyte);
                this.inflater.setInput(abyte);
                byte[] abyte1 = new byte[i2];
                this.inflater.inflate(abyte1);
                p_decode_3_.add(Unpooled.wrappedBuffer(abyte1));
                this.inflater.reset();
            }
        }
    }

    public void setCompressionTreshold(int treshold) {
        this.treshold = treshold;
    }
}

