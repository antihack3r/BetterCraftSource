// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.channel;

import java.util.Map;
import java.lang.annotation.Annotation;
import io.netty.util.internal.InternalThreadLocalMap;

public abstract class ChannelHandlerAdapter implements ChannelHandler
{
    boolean added;
    
    protected void ensureNotSharable() {
        if (this.isSharable()) {
            throw new IllegalStateException("ChannelHandler " + this.getClass().getName() + " is not allowed to be shared");
        }
    }
    
    public boolean isSharable() {
        final Class<?> clazz = this.getClass();
        final Map<Class<?>, Boolean> cache = InternalThreadLocalMap.get().handlerSharableCache();
        Boolean sharable = cache.get(clazz);
        if (sharable == null) {
            sharable = clazz.isAnnotationPresent(Sharable.class);
            cache.put(clazz, sharable);
        }
        return sharable;
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
