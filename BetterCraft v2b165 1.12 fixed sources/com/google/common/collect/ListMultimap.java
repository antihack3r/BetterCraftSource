// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V>
{
    List<V> get(@Nullable final K p0);
    
    @CanIgnoreReturnValue
    List<V> removeAll(@Nullable final Object p0);
    
    @CanIgnoreReturnValue
    List<V> replaceValues(final K p0, final Iterable<? extends V> p1);
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable final Object p0);
}
