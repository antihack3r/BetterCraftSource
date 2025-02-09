// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public final class NoopAddressResolverGroup extends AddressResolverGroup<SocketAddress>
{
    public static final NoopAddressResolverGroup INSTANCE;
    
    private NoopAddressResolverGroup() {
    }
    
    @Override
    protected AddressResolver<SocketAddress> newResolver(final EventExecutor executor) throws Exception {
        return new NoopAddressResolver(executor);
    }
    
    static {
        INSTANCE = new NoopAddressResolverGroup();
    }
}
