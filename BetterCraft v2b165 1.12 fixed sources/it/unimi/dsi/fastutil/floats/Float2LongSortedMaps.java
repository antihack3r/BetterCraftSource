// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.floats;

import java.util.SortedMap;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.NoSuchElementException;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.Serializable;
import java.util.Map;
import java.util.Comparator;

public class Float2LongSortedMaps
{
    public static final EmptySortedMap EMPTY_MAP;
    
    private Float2LongSortedMaps() {
    }
    
    public static Comparator<? super Map.Entry<Float, ?>> entryComparator(final FloatComparator comparator) {
        return new Comparator<Map.Entry<Float, ?>>() {
            @Override
            public int compare(final Map.Entry<Float, ?> x, final Map.Entry<Float, ?> y) {
                return comparator.compare(x.getKey(), y.getKey());
            }
        };
    }
    
    public static Float2LongSortedMap singleton(final Float key, final Long value) {
        return new Singleton(key, value);
    }
    
    public static Float2LongSortedMap singleton(final Float key, final Long value, final FloatComparator comparator) {
        return new Singleton(key, value, comparator);
    }
    
    public static Float2LongSortedMap singleton(final float key, final long value) {
        return new Singleton(key, value);
    }
    
    public static Float2LongSortedMap singleton(final float key, final long value, final FloatComparator comparator) {
        return new Singleton(key, value, comparator);
    }
    
    public static Float2LongSortedMap synchronize(final Float2LongSortedMap m) {
        return new SynchronizedSortedMap(m);
    }
    
    public static Float2LongSortedMap synchronize(final Float2LongSortedMap m, final Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }
    
    public static Float2LongSortedMap unmodifiable(final Float2LongSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }
    
    static {
        EMPTY_MAP = new EmptySortedMap();
    }
    
    public static class EmptySortedMap extends Float2LongMaps.EmptyMap implements Float2LongSortedMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptySortedMap() {
        }
        
        @Override
        public FloatComparator comparator() {
            return null;
        }
        
        @Override
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }
        
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }
        
        @Override
        public FloatSortedSet keySet() {
            return FloatSortedSets.EMPTY_SET;
        }
        
        @Override
        public Float2LongSortedMap subMap(final float from, final float to) {
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public Float2LongSortedMap headMap(final float to) {
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public Float2LongSortedMap tailMap(final float from) {
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public float firstFloatKey() {
            throw new NoSuchElementException();
        }
        
        @Override
        public float lastFloatKey() {
            throw new NoSuchElementException();
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap headMap(final Float oto) {
            return this.headMap((float)oto);
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(final Float ofrom) {
            return this.tailMap((float)ofrom);
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap subMap(final Float ofrom, final Float oto) {
            return this.subMap((float)ofrom, (float)oto);
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
    }
    
    public static class Singleton extends Float2LongMaps.Singleton implements Float2LongSortedMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatComparator comparator;
        
        protected Singleton(final float key, final long value, final FloatComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }
        
        protected Singleton(final float key, final long value) {
            this(key, value, null);
        }
        
        final int compare(final float k1, final float k2) {
            return (this.comparator == null) ? Float.compare(k1, k2) : this.comparator.compare(k1, k2);
        }
        
        @Override
        public FloatComparator comparator() {
            return this.comparator;
        }
        
        @Override
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = (ObjectSet<Float2LongMap.Entry>)ObjectSortedSets.singleton(new SingletonEntry(), Float2LongSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }
        
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return (ObjectSortedSet<Map.Entry<Float, Long>>)this.float2LongEntrySet();
        }
        
        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.singleton(this.key, this.comparator);
            }
            return (FloatSortedSet)this.keys;
        }
        
        @Override
        public Float2LongSortedMap subMap(final float from, final float to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public Float2LongSortedMap headMap(final float to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public Float2LongSortedMap tailMap(final float from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
        }
        
        @Override
        public float firstFloatKey() {
            return this.key;
        }
        
        @Override
        public float lastFloatKey() {
            return this.key;
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap headMap(final Float oto) {
            return this.headMap((float)oto);
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(final Float ofrom) {
            return this.tailMap((float)ofrom);
        }
        
        @Deprecated
        @Override
        public Float2LongSortedMap subMap(final Float ofrom, final Float oto) {
            return this.subMap((float)ofrom, (float)oto);
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
    }
    
    public static class SynchronizedSortedMap extends Float2LongMaps.SynchronizedMap implements Float2LongSortedMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongSortedMap sortedMap;
        
        protected SynchronizedSortedMap(final Float2LongSortedMap m, final Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }
        
        protected SynchronizedSortedMap(final Float2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }
        
        @Override
        public FloatComparator comparator() {
            synchronized (this.sync) {
                return this.sortedMap.comparator();
            }
        }
        
        @Override
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2LongEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }
        
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return (ObjectSortedSet<Map.Entry<Float, Long>>)this.float2LongEntrySet();
        }
        
        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (FloatSortedSet)this.keys;
        }
        
        @Override
        public Float2LongSortedMap subMap(final float from, final float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }
        
        @Override
        public Float2LongSortedMap headMap(final float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }
        
        @Override
        public Float2LongSortedMap tailMap(final float from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
        
        @Override
        public float firstFloatKey() {
            synchronized (this.sync) {
                return this.sortedMap.firstFloatKey();
            }
        }
        
        @Override
        public float lastFloatKey() {
            synchronized (this.sync) {
                return this.sortedMap.lastFloatKey();
            }
        }
        
        @Override
        public Float firstKey() {
            synchronized (this.sync) {
                return ((SortedMap<Float, V>)this.sortedMap).firstKey();
            }
        }
        
        @Override
        public Float lastKey() {
            synchronized (this.sync) {
                return ((SortedMap<Float, V>)this.sortedMap).lastKey();
            }
        }
        
        @Override
        public Float2LongSortedMap subMap(final Float from, final Float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }
        
        @Override
        public Float2LongSortedMap headMap(final Float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }
        
        @Override
        public Float2LongSortedMap tailMap(final Float from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }
    
    public static class UnmodifiableSortedMap extends Float2LongMaps.UnmodifiableMap implements Float2LongSortedMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongSortedMap sortedMap;
        
        protected UnmodifiableSortedMap(final Float2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }
        
        @Override
        public FloatComparator comparator() {
            return this.sortedMap.comparator();
        }
        
        @Override
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2LongEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }
        
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return (ObjectSortedSet<Map.Entry<Float, Long>>)this.float2LongEntrySet();
        }
        
        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (FloatSortedSet)this.keys;
        }
        
        @Override
        public Float2LongSortedMap subMap(final float from, final float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }
        
        @Override
        public Float2LongSortedMap headMap(final float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }
        
        @Override
        public Float2LongSortedMap tailMap(final float from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
        
        @Override
        public float firstFloatKey() {
            return this.sortedMap.firstFloatKey();
        }
        
        @Override
        public float lastFloatKey() {
            return this.sortedMap.lastFloatKey();
        }
        
        @Override
        public Float firstKey() {
            return ((SortedMap<Float, V>)this.sortedMap).firstKey();
        }
        
        @Override
        public Float lastKey() {
            return ((SortedMap<Float, V>)this.sortedMap).lastKey();
        }
        
        @Override
        public Float2LongSortedMap subMap(final Float from, final Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }
        
        @Override
        public Float2LongSortedMap headMap(final Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }
        
        @Override
        public Float2LongSortedMap tailMap(final Float from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }
}
