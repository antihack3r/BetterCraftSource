// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.base.Predicates;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.IdentityHashMap;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T>
{
    private final IdentityHashMap<T, Integer> identityMap;
    private final List<T> objectList;
    
    public ObjectIntIdentityMap() {
        this(512);
    }
    
    public ObjectIntIdentityMap(final int expectedSize) {
        this.objectList = (List<T>)Lists.newArrayListWithExpectedSize(expectedSize);
        this.identityMap = new IdentityHashMap<T, Integer>(expectedSize);
    }
    
    public void put(final T key, final int value) {
        this.identityMap.put(key, value);
        while (this.objectList.size() <= value) {
            this.objectList.add(null);
        }
        this.objectList.set(value, key);
    }
    
    public int get(final T key) {
        final Integer integer = this.identityMap.get(key);
        return (integer == null) ? -1 : integer;
    }
    
    @Nullable
    public final T getByValue(final int value) {
        return (value >= 0 && value < this.objectList.size()) ? this.objectList.get(value) : null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
    }
    
    public int size() {
        return this.identityMap.size();
    }
}
