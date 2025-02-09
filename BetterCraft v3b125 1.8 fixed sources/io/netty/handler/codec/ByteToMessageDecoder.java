/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder
extends ChannelInboundHandlerAdapter {
    ByteBuf cumulation;
    private boolean singleDecode;
    private boolean decodeWasNull;
    private boolean first;

    protected ByteToMessageDecoder() {
        if (this.isSharable()) {
            throw new IllegalStateException("@Sharable annotation is not allowed");
        }
    }

    public void setSingleDecode(boolean singleDecode) {
        this.singleDecode = singleDecode;
    }

    public boolean isSingleDecode() {
        return this.singleDecode;
    }

    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }

    protected ByteBuf internalBuffer() {
        if (this.cumulation != null) {
            return this.cumulation;
        }
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = this.internalBuffer();
        int readable = buf.readableBytes();
        if (buf.isReadable()) {
            ByteBuf bytes = buf.readBytes(readable);
            buf.release();
            ctx.fireChannelRead(bytes);
        } else {
            buf.release();
        }
        this.cumulation = null;
        ctx.fireChannelReadComplete();
        this.handlerRemoved0(ctx);
    }

    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int size;
        RecyclableArrayList out;
        block15: {
            if (!(msg instanceof ByteBuf)) {
                ctx.fireChannelRead(msg);
                return;
            }
            out = RecyclableArrayList.newInstance();
            try {
                ByteBuf data = (ByteBuf)msg;
                boolean bl2 = this.first = this.cumulation == null;
                if (this.first) {
                    this.cumulation = data;
                } else {
                    if (this.cumulation.writerIndex() > this.cumulation.maxCapacity() - data.readableBytes() || this.cumulation.refCnt() > 1) {
                        this.expandCumulation(ctx, data.readableBytes());
                    }
                    this.cumulation.writeBytes(data);
                    data.release();
                }
                this.callDecode(ctx, this.cumulation, out);
                if (this.cumulation == null) break block15;
            }
            catch (DecoderException e2) {
                try {
                    throw e2;
                    catch (Throwable t2) {
                        throw new DecoderException(t2);
                    }
                }
                catch (Throwable throwable) {
                    int size2;
                    if (this.cumulation != null && !this.cumulation.isReadable()) {
                        this.cumulation.release();
                        this.cumulation = null;
                    }
                    this.decodeWasNull = (size2 = out.size()) == 0;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            out.recycle();
                            throw throwable;
                        }
                        ctx.fireChannelRead(out.get(i2));
                        ++i2;
                    }
                }
            }
            if (!this.cumulation.isReadable()) {
                this.cumulation.release();
                this.cumulation = null;
            }
        }
        this.decodeWasNull = (size = out.size()) == 0;
        int i3 = 0;
        while (true) {
            if (i3 >= size) {
                out.recycle();
                return;
            }
            ctx.fireChannelRead(out.get(i3));
            ++i3;
        }
    }

    private void expandCumulation(ChannelHandlerContext ctx, int readable) {
        ByteBuf oldCumulation = this.cumulation;
        this.cumulation = ctx.alloc().buffer(oldCumulation.readableBytes() + readable);
        this.cumulation.writeBytes(oldCumulation);
        oldCumulation.release();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
            this.cumulation.discardSomeReadBytes();
        }
        if (this.decodeWasNull) {
            this.decodeWasNull = false;
            if (!ctx.channel().config().isAutoRead()) {
                ctx.read();
            }
        }
        ctx.fireChannelReadComplete();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RecyclableArrayList out = RecyclableArrayList.newInstance();
        try {
            if (this.cumulation != null) {
                this.callDecode(ctx, this.cumulation, out);
                this.decodeLast(ctx, this.cumulation, out);
            } else {
                this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
            }
        }
        catch (DecoderException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw new DecoderException(e3);
        }
        finally {
            try {
                if (this.cumulation != null) {
                    this.cumulation.release();
                    this.cumulation = null;
                }
                int size = out.size();
                for (int i2 = 0; i2 < size; ++i2) {
                    ctx.fireChannelRead(out.get(i2));
                }
                if (size > 0) {
                    ctx.fireChannelReadComplete();
                }
                ctx.fireChannelInactive();
            }
            finally {
                out.recycle();
            }
        }
    }

    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) {
        try {
            while (in2.isReadable()) {
                int outSize = out.size();
                int oldInputLength = in2.readableBytes();
                this.decode(ctx, in2, out);
                if (!ctx.isRemoved()) {
                    if (outSize == out.size()) {
                        if (oldInputLength != in2.readableBytes()) continue;
                    } else {
                        if (oldInputLength == in2.readableBytes()) {
                            throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                        }
                        if (!this.isSingleDecode()) continue;
                    }
                }
                break;
            }
        }
        catch (DecoderException e2) {
            throw e2;
        }
        catch (Throwable cause) {
            throw new DecoderException(cause);
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        this.decode(ctx, in2, out);
    }
}

