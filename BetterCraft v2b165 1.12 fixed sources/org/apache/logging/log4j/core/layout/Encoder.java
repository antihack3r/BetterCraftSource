// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.layout;

public interface Encoder<T>
{
    void encode(final T p0, final ByteBufferDestination p1);
}
