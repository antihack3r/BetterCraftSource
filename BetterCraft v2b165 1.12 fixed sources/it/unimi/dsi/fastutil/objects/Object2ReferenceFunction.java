// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;

public interface Object2ReferenceFunction<K, V> extends Function<K, V>
{
    void defaultReturnValue(final V p0);
    
    V defaultReturnValue();
}
