// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ThreadContextMap
{
    void clear();
    
    boolean containsKey(final String p0);
    
    String get(final String p0);
    
    Map<String, String> getCopy();
    
    Map<String, String> getImmutableMapOrNull();
    
    boolean isEmpty();
    
    void put(final String p0, final String p1);
    
    void remove(final String p0);
}
