// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.channel;

import java.util.List;
import io.netty.util.concurrent.EventExecutorGroup;
import java.util.Map;

public interface ChannelPipeline extends ChannelInboundInvoker, ChannelOutboundInvoker, Iterable<Map.Entry<String, ChannelHandler>>
{
    ChannelPipeline addFirst(final String p0, final ChannelHandler p1);
    
    ChannelPipeline addFirst(final EventExecutorGroup p0, final String p1, final ChannelHandler p2);
    
    ChannelPipeline addLast(final String p0, final ChannelHandler p1);
    
    ChannelPipeline addLast(final EventExecutorGroup p0, final String p1, final ChannelHandler p2);
    
    ChannelPipeline addBefore(final String p0, final String p1, final ChannelHandler p2);
    
    ChannelPipeline addBefore(final EventExecutorGroup p0, final String p1, final String p2, final ChannelHandler p3);
    
    ChannelPipeline addAfter(final String p0, final String p1, final ChannelHandler p2);
    
    ChannelPipeline addAfter(final EventExecutorGroup p0, final String p1, final String p2, final ChannelHandler p3);
    
    ChannelPipeline addFirst(final ChannelHandler... p0);
    
    ChannelPipeline addFirst(final EventExecutorGroup p0, final ChannelHandler... p1);
    
    ChannelPipeline addLast(final ChannelHandler... p0);
    
    ChannelPipeline addLast(final EventExecutorGroup p0, final ChannelHandler... p1);
    
    ChannelPipeline remove(final ChannelHandler p0);
    
    ChannelHandler remove(final String p0);
    
     <T extends ChannelHandler> T remove(final Class<T> p0);
    
    ChannelHandler removeFirst();
    
    ChannelHandler removeLast();
    
    ChannelPipeline replace(final ChannelHandler p0, final String p1, final ChannelHandler p2);
    
    ChannelHandler replace(final String p0, final String p1, final ChannelHandler p2);
    
     <T extends ChannelHandler> T replace(final Class<T> p0, final String p1, final ChannelHandler p2);
    
    ChannelHandler first();
    
    ChannelHandlerContext firstContext();
    
    ChannelHandler last();
    
    ChannelHandlerContext lastContext();
    
    ChannelHandler get(final String p0);
    
     <T extends ChannelHandler> T get(final Class<T> p0);
    
    ChannelHandlerContext context(final ChannelHandler p0);
    
    ChannelHandlerContext context(final String p0);
    
    ChannelHandlerContext context(final Class<? extends ChannelHandler> p0);
    
    Channel channel();
    
    List<String> names();
    
    Map<String, ChannelHandler> toMap();
    
    ChannelPipeline fireChannelRegistered();
    
    ChannelPipeline fireChannelUnregistered();
    
    ChannelPipeline fireChannelActive();
    
    ChannelPipeline fireChannelInactive();
    
    ChannelPipeline fireExceptionCaught(final Throwable p0);
    
    ChannelPipeline fireUserEventTriggered(final Object p0);
    
    ChannelPipeline fireChannelRead(final Object p0);
    
    ChannelPipeline fireChannelReadComplete();
    
    ChannelPipeline fireChannelWritabilityChanged();
    
    ChannelPipeline flush();
}
