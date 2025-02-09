/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.CodedInputStream
 */
package io.netty.handler.codec.protobuf;

import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder
extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        in2.markReaderIndex();
        byte[] buf = new byte[5];
        for (int i2 = 0; i2 < buf.length; ++i2) {
            if (!in2.isReadable()) {
                in2.resetReaderIndex();
                return;
            }
            buf[i2] = in2.readByte();
            if (buf[i2] < 0) continue;
            int length = CodedInputStream.newInstance((byte[])buf, (int)0, (int)(i2 + 1)).readRawVarint32();
            if (length < 0) {
                throw new CorruptedFrameException("negative length: " + length);
            }
            if (in2.readableBytes() < length) {
                in2.resetReaderIndex();
                return;
            }
            out.add(in2.readBytes(length));
            return;
        }
        throw new CorruptedFrameException("length wider than 32-bit");
    }
}

