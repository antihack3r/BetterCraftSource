// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.stomp;

public interface StompHeadersSubframe extends StompSubframe
{
    StompCommand command();
    
    StompHeaders headers();
}
