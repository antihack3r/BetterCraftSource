// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class Http2InboundFrameLogger implements Http2FrameReader
{
    private final Http2FrameReader reader;
    private final Http2FrameLogger logger;
    
    public Http2InboundFrameLogger(final Http2FrameReader reader, final Http2FrameLogger logger) {
        this.reader = ObjectUtil.checkNotNull(reader, "reader");
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
    }
    
    @Override
    public void readFrame(final ChannelHandlerContext ctx, final ByteBuf input, final Http2FrameListener listener) throws Http2Exception {
        this.reader.readFrame(ctx, input, new Http2FrameListener() {
            @Override
            public int onDataRead(final ChannelHandlerContext ctx, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logData(Http2FrameLogger.Direction.INBOUND, ctx, streamId, data, padding, endOfStream);
                return listener.onDataRead(ctx, streamId, data, padding, endOfStream);
            }
            
            @Override
            public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int padding, final boolean endStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, ctx, streamId, headers, padding, endStream);
                listener.onHeadersRead(ctx, streamId, headers, padding, endStream);
            }
            
            @Override
            public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
                listener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
            }
            
            @Override
            public void onPriorityRead(final ChannelHandlerContext ctx, final int streamId, final int streamDependency, final short weight, final boolean exclusive) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPriority(Http2FrameLogger.Direction.INBOUND, ctx, streamId, streamDependency, weight, exclusive);
                listener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
            }
            
            @Override
            public void onRstStreamRead(final ChannelHandlerContext ctx, final int streamId, final long errorCode) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logRstStream(Http2FrameLogger.Direction.INBOUND, ctx, streamId, errorCode);
                listener.onRstStreamRead(ctx, streamId, errorCode);
            }
            
            @Override
            public void onSettingsAckRead(final ChannelHandlerContext ctx) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logSettingsAck(Http2FrameLogger.Direction.INBOUND, ctx);
                listener.onSettingsAckRead(ctx);
            }
            
            @Override
            public void onSettingsRead(final ChannelHandlerContext ctx, final Http2Settings settings) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logSettings(Http2FrameLogger.Direction.INBOUND, ctx, settings);
                listener.onSettingsRead(ctx, settings);
            }
            
            @Override
            public void onPingRead(final ChannelHandlerContext ctx, final ByteBuf data) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPing(Http2FrameLogger.Direction.INBOUND, ctx, data);
                listener.onPingRead(ctx, data);
            }
            
            @Override
            public void onPingAckRead(final ChannelHandlerContext ctx, final ByteBuf data) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPingAck(Http2FrameLogger.Direction.INBOUND, ctx, data);
                listener.onPingAckRead(ctx, data);
            }
            
            @Override
            public void onPushPromiseRead(final ChannelHandlerContext ctx, final int streamId, final int promisedStreamId, final Http2Headers headers, final int padding) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPushPromise(Http2FrameLogger.Direction.INBOUND, ctx, streamId, promisedStreamId, headers, padding);
                listener.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
            }
            
            @Override
            public void onGoAwayRead(final ChannelHandlerContext ctx, final int lastStreamId, final long errorCode, final ByteBuf debugData) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logGoAway(Http2FrameLogger.Direction.INBOUND, ctx, lastStreamId, errorCode, debugData);
                listener.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
            }
            
            @Override
            public void onWindowUpdateRead(final ChannelHandlerContext ctx, final int streamId, final int windowSizeIncrement) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logWindowsUpdate(Http2FrameLogger.Direction.INBOUND, ctx, streamId, windowSizeIncrement);
                listener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
            }
            
            @Override
            public void onUnknownFrame(final ChannelHandlerContext ctx, final byte frameType, final int streamId, final Http2Flags flags, final ByteBuf payload) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logUnknownFrame(Http2FrameLogger.Direction.INBOUND, ctx, frameType, streamId, flags, payload);
                listener.onUnknownFrame(ctx, frameType, streamId, flags, payload);
            }
        });
    }
    
    @Override
    public void close() {
        this.reader.close();
    }
    
    @Override
    public Configuration configuration() {
        return this.reader.configuration();
    }
}
