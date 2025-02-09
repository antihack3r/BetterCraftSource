// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.floats;

import java.util.Comparator;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.SortedMap;
import java.util.Collection;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

public abstract class AbstractFloat2FloatSortedMap extends AbstractFloat2FloatMap implements Float2FloatSortedMap
{
    private static final long serialVersionUID = -1773560792952436569L;
    
    protected AbstractFloat2FloatSortedMap() {
    }
    
    @Deprecated
    @Override
    public Float2FloatSortedMap headMap(final Float to) {
        return this.headMap((float)to);
    }
    
    @Deprecated
    @Override
    public Float2FloatSortedMap tailMap(final Float from) {
        return this.tailMap((float)from);
    }
    
    @Deprecated
    @Override
    public Float2FloatSortedMap subMap(final Float from, final Float to) {
        return this.subMap((float)from, (float)to);
    }
    
    @Deprecated
    @Override
    public Float firstKey() {
        return this.firstFloatKey();
    }
    
    @Deprecated
    @Override
    public Float lastKey() {
        return this.lastFloatKey();
    }
    
    @Override
    public FloatSortedSet keySet() {
        return new KeySet();
    }
    
    @Override
    public FloatCollection values() {
        return new ValuesCollection();
    }
    
    @Override
    public ObjectSortedSet<Map.Entry<Float, Float>> entrySet() {
        return (ObjectSortedSet<Map.Entry<Float, Float>>)this.float2FloatEntrySet();
    }
    
    protected class KeySet extends AbstractFloatSortedSet
    {
        @Override
        public boolean contains(final float k) {
            return AbstractFloat2FloatSortedMap.this.containsKey(k);
        }
        
        @Override
        public int size() {
            return AbstractFloat2FloatSortedMap.this.size();
        }
        
        @Override
        public void clear() {
            AbstractFloat2FloatSortedMap.this.clear();
        }
        
        @Override
        public FloatComparator comparator() {
            return AbstractFloat2FloatSortedMap.this.comparator();
        }
        
        @Override
        public float firstFloat() {
            return AbstractFloat2FloatSortedMap.this.firstFloatKey();
        }
        
        @Override
        public float lastFloat() {
            return AbstractFloat2FloatSortedMap.this.lastFloatKey();
        }
        
        @Override
        public FloatSortedSet headSet(final float to) {
            return AbstractFloat2FloatSortedMap.this.headMap(to).keySet();
        }
        
        @Override
        public FloatSortedSet tailSet(final float from) {
            return AbstractFloat2FloatSortedMap.this.tailMap(from).keySet();
        }
        
        @Override
        public FloatSortedSet subSet(final float from, final float to) {
            return AbstractFloat2FloatSortedMap.this.subMap(from, to).keySet();
        }
        
        @Override
        public FloatBidirectionalIterator iterator(final float from) {
            return new KeySetIterator((ObjectBidirectionalIterator<Map.Entry<Float, Float>>)AbstractFloat2FloatSortedMap.this.entrySet().iterator(new BasicEntry(from, 0.0f)));
        }
        
        @Override
        public FloatBidirectionalIterator iterator() {
            return new KeySetIterator(AbstractFloat2FloatSortedMap.this.entrySet().iterator());
        }
    }
    
    protected static class KeySetIterator extends AbstractFloatBidirectionalIterator
    {
        protected final ObjectBidirectionalIterator<Map.Entry<Float, Float>> i;
        
        public KeySetIterator(final ObjectBidirectionalIterator<Map.Entry<Float, Float>> i) {
            this.i = i;
        }
        
        @Override
        public float nextFloat() {
            return (float)this.i.next().getKey();
        }
        
        @Override
        public float previousFloat() {
            return (float)this.i.previous().getKey();
        }
        
        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }
    }
    
    protected class ValuesCollection extends AbstractFloatCollection
    {
        @Override
        public FloatIterator iterator() {
            return new ValuesIterator(AbstractFloat2FloatSortedMap.this.entrySet().iterator());
        }
        
        @Override
        public boolean contains(final float k) {
            return AbstractFloat2FloatSortedMap.this.containsValue(k);
        }
        
        @Override
        public int size() {
            return AbstractFloat2FloatSortedMap.this.size();
        }
        
        @Override
        public void clear() {
            AbstractFloat2FloatSortedMap.this.clear();
        }
    }
    
    protected static class ValuesIterator extends AbstractFloatIterator
    {
        protected final ObjectBidirectionalIterator<Map.Entry<Float, Float>> i;
        
        public ValuesIterator(final ObjectBidirectionalIterator<Map.Entry<Float, Float>> i) {
            this.i = i;
        }
        
        @Override
        public float nextFloat() {
            return (float)this.i.next().getValue();
        }
        
        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }
    }
}
