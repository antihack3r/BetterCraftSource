// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.http2;

public interface Http2HeadersFrame extends Http2StreamFrame
{
    Http2Headers headers();
    
    boolean isEndStream();
    
    int padding();
}
