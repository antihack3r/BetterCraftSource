// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.http2;

public interface Http2WindowUpdateFrame extends Http2StreamFrame
{
    int windowSizeIncrement();
}
