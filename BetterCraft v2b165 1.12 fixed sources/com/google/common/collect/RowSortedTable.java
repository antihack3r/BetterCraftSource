// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface RowSortedTable<R, C, V> extends Table<R, C, V>
{
    SortedSet<R> rowKeySet();
    
    SortedMap<R, Map<C, V>> rowMap();
}
