// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.http.websocketx;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TextWebSocketFrame extends WebSocketFrame
{
    public TextWebSocketFrame() {
        super(Unpooled.buffer(0));
    }
    
    public TextWebSocketFrame(final String text) {
        super(fromText(text));
    }
    
    public TextWebSocketFrame(final ByteBuf binaryData) {
        super(binaryData);
    }
    
    public TextWebSocketFrame(final boolean finalFragment, final int rsv, final String text) {
        super(finalFragment, rsv, fromText(text));
    }
    
    private static ByteBuf fromText(final String text) {
        if (text == null || text.isEmpty()) {
            return Unpooled.EMPTY_BUFFER;
        }
        return Unpooled.copiedBuffer(text, CharsetUtil.UTF_8);
    }
    
    public TextWebSocketFrame(final boolean finalFragment, final int rsv, final ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }
    
    public String text() {
        return this.content().toString(CharsetUtil.UTF_8);
    }
    
    @Override
    public TextWebSocketFrame copy() {
        return (TextWebSocketFrame)super.copy();
    }
    
    @Override
    public TextWebSocketFrame duplicate() {
        return (TextWebSocketFrame)super.duplicate();
    }
    
    @Override
    public TextWebSocketFrame retainedDuplicate() {
        return (TextWebSocketFrame)super.retainedDuplicate();
    }
    
    @Override
    public TextWebSocketFrame replace(final ByteBuf content) {
        return new TextWebSocketFrame(this.isFinalFragment(), this.rsv(), content);
    }
    
    @Override
    public TextWebSocketFrame retain() {
        super.retain();
        return this;
    }
    
    @Override
    public TextWebSocketFrame retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public TextWebSocketFrame touch() {
        super.touch();
        return this;
    }
    
    @Override
    public TextWebSocketFrame touch(final Object hint) {
        super.touch(hint);
        return this;
    }
}
