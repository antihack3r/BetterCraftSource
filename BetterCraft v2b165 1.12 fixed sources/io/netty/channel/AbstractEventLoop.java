// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.channel;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.AbstractEventExecutor;

public abstract class AbstractEventLoop extends AbstractEventExecutor implements EventLoop
{
    protected AbstractEventLoop() {
    }
    
    protected AbstractEventLoop(final EventLoopGroup parent) {
        super(parent);
    }
    
    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }
    
    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }
}
