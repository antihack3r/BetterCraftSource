/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;
import net.minecraft.network.PacketBuffer;

public class NettyCompressionEncoder
extends MessageToByteEncoder<ByteBuf> {
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater;
    private int treshold;

    public NettyCompressionEncoder(int treshold) {
        this.treshold = treshold;
        this.deflater = new Deflater();
    }

    @Override
    protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
        int i2 = p_encode_2_.readableBytes();
        PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
        if (i2 < this.treshold) {
            packetbuffer.writeVarIntToBuffer(0);
            packetbuffer.writeBytes(p_encode_2_);
        } else {
            byte[] abyte = new byte[i2];
            p_encode_2_.readBytes(abyte);
            packetbuffer.writeVarIntToBuffer(abyte.length);
            this.deflater.setInput(abyte, 0, i2);
            this.deflater.finish();
            while (!this.deflater.finished()) {
                int j2 = this.deflater.deflate(this.buffer);
                packetbuffer.writeBytes(this.buffer, 0, j2);
            }
            this.deflater.reset();
        }
    }

    public void setCompressionTreshold(int treshold) {
        this.treshold = treshold;
    }
}

