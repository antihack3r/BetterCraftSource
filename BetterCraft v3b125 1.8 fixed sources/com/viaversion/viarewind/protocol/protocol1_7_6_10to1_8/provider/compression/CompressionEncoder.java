/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viarewind.protocol.protocol1_7_6_10to1_8.provider.compression;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class CompressionEncoder
extends MessageToByteEncoder<ByteBuf> {
    private final Deflater deflater = new Deflater();
    private final int threshold;

    public CompressionEncoder(int threshold) {
        this.threshold = threshold;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in2, ByteBuf out) throws Exception {
        int frameLength = in2.readableBytes();
        if (frameLength < this.threshold) {
            out.writeByte(0);
            out.writeBytes(in2);
            return;
        }
        Type.VAR_INT.writePrimitive(out, frameLength);
        ByteBuf temp = in2;
        if (!in2.hasArray()) {
            temp = ByteBufAllocator.DEFAULT.heapBuffer().writeBytes(in2);
        } else {
            in2.retain();
        }
        ByteBuf output = ByteBufAllocator.DEFAULT.heapBuffer();
        try {
            this.deflater.setInput(temp.array(), temp.arrayOffset() + temp.readerIndex(), temp.readableBytes());
            this.deflater.finish();
            while (!this.deflater.finished()) {
                output.ensureWritable(4096);
                output.writerIndex(output.writerIndex() + this.deflater.deflate(output.array(), output.arrayOffset() + output.writerIndex(), output.writableBytes()));
            }
            out.writeBytes(output);
        }
        finally {
            output.release();
            temp.release();
            this.deflater.reset();
        }
    }
}

