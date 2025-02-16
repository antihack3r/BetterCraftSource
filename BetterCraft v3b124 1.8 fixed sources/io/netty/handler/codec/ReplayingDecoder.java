/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoderBuffer;
import io.netty.util.Signal;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ReplayingDecoder<S>
extends ByteToMessageDecoder {
    static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class.getName() + ".REPLAY");
    private final ReplayingDecoderBuffer replayable = new ReplayingDecoderBuffer();
    private S state;
    private int checkpoint = -1;

    protected ReplayingDecoder() {
        this(null);
    }

    protected ReplayingDecoder(S initialState) {
        this.state = initialState;
    }

    protected void checkpoint() {
        this.checkpoint = this.internalBuffer().readerIndex();
    }

    protected void checkpoint(S state) {
        this.checkpoint();
        this.state(state);
    }

    protected S state() {
        return this.state;
    }

    protected S state(S newState) {
        S oldState = this.state;
        this.state = newState;
        return oldState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RecyclableArrayList out = RecyclableArrayList.newInstance();
        try {
            this.replayable.terminate();
            this.callDecode(ctx, this.internalBuffer(), out);
            this.decodeLast(ctx, this.replayable, out);
        }
        catch (Signal replay) {
            replay.expect(REPLAY);
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) {
        this.replayable.setCumulation(in2);
        try {
            while (in2.isReadable()) {
                int oldReaderIndex = this.checkpoint = in2.readerIndex();
                int outSize = out.size();
                S oldState = this.state;
                int oldInputLength = in2.readableBytes();
                try {
                    this.decode(ctx, this.replayable, out);
                    if (ctx.isRemoved()) {
                        return;
                    }
                    if (outSize == out.size()) {
                        if (oldInputLength != in2.readableBytes() || oldState != this.state) continue;
                        throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() must consume the inbound " + "data or change its state if it did not decode anything.");
                    }
                }
                catch (Signal replay) {
                    replay.expect(REPLAY);
                    if (ctx.isRemoved()) {
                        return;
                    }
                    int checkpoint = this.checkpoint;
                    if (checkpoint < 0) return;
                    in2.readerIndex(checkpoint);
                    return;
                }
                if (oldReaderIndex == in2.readerIndex() && oldState == this.state) {
                    throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() method must consume the inbound data " + "or change its state if it decoded something.");
                }
                if (this.isSingleDecode()) return;
            }
            return;
        }
        catch (DecoderException e2) {
            throw e2;
        }
        catch (Throwable cause) {
            throw new DecoderException(cause);
        }
    }
}

