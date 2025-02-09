// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.Map;

public interface Reference2LongMap<K> extends Reference2LongFunction<K>, Map<K, Long>
{
    ObjectSet<Map.Entry<K, Long>> entrySet();
    
    ObjectSet<Entry<K>> reference2LongEntrySet();
    
    ReferenceSet<K> keySet();
    
    LongCollection values();
    
    boolean containsValue(final long p0);
    
    public interface Entry<K> extends Map.Entry<K, Long>
    {
        @Deprecated
        Long getValue();
        
        long setValue(final long p0);
        
        long getLongValue();
    }
    
    public interface FastEntrySet<K> extends ObjectSet<Entry<K>>
    {
        ObjectIterator<Entry<K>> fastIterator();
    }
}
