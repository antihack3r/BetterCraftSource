/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.stream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream
implements ChunkedInput<ByteBuf> {
    private final ReadableByteChannel in;
    private final int chunkSize;
    private long offset;
    private final ByteBuffer byteBuffer;

    public ChunkedNioStream(ReadableByteChannel in2) {
        this(in2, 8192);
    }

    public ChunkedNioStream(ReadableByteChannel in2, int chunkSize) {
        if (in2 == null) {
            throw new NullPointerException("in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        this.in = in2;
        this.offset = 0L;
        this.chunkSize = chunkSize;
        this.byteBuffer = ByteBuffer.allocate(chunkSize);
    }

    public long transferredBytes() {
        return this.offset;
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return false;
        }
        if (this.in.isOpen()) {
            int b2 = this.in.read(this.byteBuffer);
            if (b2 < 0) {
                return true;
            }
            this.offset += (long)b2;
            return false;
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        this.in.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        int localReadBytes;
        if (this.isEndOfInput()) {
            return null;
        }
        int readBytes = this.byteBuffer.position();
        while ((localReadBytes = this.in.read(this.byteBuffer)) >= 0) {
            this.offset += (long)localReadBytes;
            if ((readBytes += localReadBytes) != this.chunkSize) continue;
            break;
        }
        this.byteBuffer.flip();
        boolean release = true;
        ByteBuf buffer = ctx.alloc().buffer(this.byteBuffer.remaining());
        try {
            buffer.writeBytes(this.byteBuffer);
            this.byteBuffer.clear();
            release = false;
            ByteBuf byteBuf = buffer;
            return byteBuf;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }
}

