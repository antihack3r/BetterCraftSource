// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.util;

public interface ReferenceCounted
{
    int refCnt();
    
    ReferenceCounted retain();
    
    ReferenceCounted retain(final int p0);
    
    boolean release();
    
    boolean release(final int p0);
}
