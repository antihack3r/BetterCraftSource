// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.channel;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import io.netty.util.ReferenceCounted;

public interface FileRegion extends ReferenceCounted
{
    long position();
    
    @Deprecated
    long transfered();
    
    long transferred();
    
    long count();
    
    long transferTo(final WritableByteChannel p0, final long p1) throws IOException;
    
    FileRegion retain();
    
    FileRegion retain(final int p0);
    
    FileRegion touch();
    
    FileRegion touch(final Object p0);
}
