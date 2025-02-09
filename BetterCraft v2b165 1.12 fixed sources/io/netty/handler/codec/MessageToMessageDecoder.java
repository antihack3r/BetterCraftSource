// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec;

import io.netty.util.ReferenceCountUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter
{
    private final TypeParameterMatcher matcher;
    
    protected MessageToMessageDecoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
    }
    
    protected MessageToMessageDecoder(final Class<? extends I> inboundMessageType) {
        this.matcher = TypeParameterMatcher.get(inboundMessageType);
    }
    
    public boolean acceptInboundMessage(final Object msg) throws Exception {
        return this.matcher.match(msg);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final CodecOutputList out = CodecOutputList.newInstance();
        try {
            if (this.acceptInboundMessage(msg)) {
                final I cast = (I)msg;
                try {
                    this.decode(ctx, cast, out);
                }
                finally {
                    ReferenceCountUtil.release(cast);
                }
            }
            else {
                out.add(msg);
            }
        }
        catch (final DecoderException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new DecoderException(e2);
        }
        finally {
            for (int size = out.size(), i = 0; i < size; ++i) {
                ctx.fireChannelRead(out.getUnsafe(i));
            }
            out.recycle();
        }
    }
    
    protected abstract void decode(final ChannelHandlerContext p0, final I p1, final List<Object> p2) throws Exception;
}
