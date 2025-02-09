/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.util.ReferenceCountUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SpdyHttpResponseStreamIdHandler
extends MessageToMessageCodec<Object, HttpMessage> {
    private static final Integer NO_ID = -1;
    private final Queue<Integer> ids = new LinkedList<Integer>();

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return msg instanceof HttpMessage || msg instanceof SpdyRstStreamFrame;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpMessage msg, List<Object> out) throws Exception {
        Integer id2 = this.ids.poll();
        if (id2 != null && id2.intValue() != NO_ID.intValue() && !msg.headers().contains("X-SPDY-Stream-ID")) {
            SpdyHttpHeaders.setStreamId(msg, id2);
        }
        out.add(ReferenceCountUtil.retain(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg instanceof HttpMessage) {
            boolean contains = ((HttpMessage)msg).headers().contains("X-SPDY-Stream-ID");
            if (!contains) {
                this.ids.add(NO_ID);
            } else {
                this.ids.add(SpdyHttpHeaders.getStreamId((HttpMessage)msg));
            }
        } else if (msg instanceof SpdyRstStreamFrame) {
            this.ids.remove(((SpdyRstStreamFrame)msg).streamId());
        }
        out.add(ReferenceCountUtil.retain(msg));
    }
}

