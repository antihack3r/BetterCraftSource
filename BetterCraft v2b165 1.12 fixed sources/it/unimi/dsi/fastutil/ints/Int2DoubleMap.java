// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;

public interface Int2DoubleMap extends Int2DoubleFunction, Map<Integer, Double>
{
    ObjectSet<Map.Entry<Integer, Double>> entrySet();
    
    ObjectSet<Entry> int2DoubleEntrySet();
    
    IntSet keySet();
    
    DoubleCollection values();
    
    boolean containsValue(final double p0);
    
    public interface Entry extends Map.Entry<Integer, Double>
    {
        @Deprecated
        Integer getKey();
        
        int getIntKey();
        
        @Deprecated
        Double getValue();
        
        double setValue(final double p0);
        
        double getDoubleValue();
    }
    
    public interface FastEntrySet extends ObjectSet<Entry>
    {
        ObjectIterator<Entry> fastIterator();
    }
}
