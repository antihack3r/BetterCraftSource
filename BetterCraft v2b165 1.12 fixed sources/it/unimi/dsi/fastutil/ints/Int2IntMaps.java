// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.ints;

import java.util.Collection;
import java.util.Set;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.io.Serializable;

public class Int2IntMaps
{
    public static final EmptyMap EMPTY_MAP;
    
    private Int2IntMaps() {
    }
    
    public static Int2IntMap singleton(final int key, final int value) {
        return new Singleton(key, value);
    }
    
    public static Int2IntMap singleton(final Integer key, final Integer value) {
        return new Singleton(key, value);
    }
    
    public static Int2IntMap synchronize(final Int2IntMap m) {
        return new SynchronizedMap(m);
    }
    
    public static Int2IntMap synchronize(final Int2IntMap m, final Object sync) {
        return new SynchronizedMap(m, sync);
    }
    
    public static Int2IntMap unmodifiable(final Int2IntMap m) {
        return new UnmodifiableMap(m);
    }
    
    static {
        EMPTY_MAP = new EmptyMap();
    }
    
    public static class EmptyMap extends Int2IntFunctions.EmptyFunction implements Int2IntMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyMap() {
        }
        
        @Override
        public boolean containsValue(final int v) {
            return false;
        }
        
        @Override
        public void putAll(final Map<? extends Integer, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> int2IntEntrySet() {
            return ObjectSets.EMPTY_SET;
        }
        
        @Override
        public IntSet keySet() {
            return IntSets.EMPTY_SET;
        }
        
        @Override
        public IntCollection values() {
            return IntSets.EMPTY_SET;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return false;
        }
        
        private Object readResolve() {
            return Int2IntMaps.EMPTY_MAP;
        }
        
        @Override
        public Object clone() {
            return Int2IntMaps.EMPTY_MAP;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
            return (ObjectSet<Map.Entry<Integer, Integer>>)this.int2IntEntrySet();
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Map && ((Map)o).isEmpty();
        }
        
        @Override
        public String toString() {
            return "{}";
        }
    }
    
    public static class Singleton extends Int2IntFunctions.Singleton implements Int2IntMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Entry> entries;
        protected transient IntSet keys;
        protected transient IntCollection values;
        
        protected Singleton(final int key, final int value) {
            super(key, value);
        }
        
        @Override
        public boolean containsValue(final int v) {
            return this.value == v;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return (int)ov == this.value;
        }
        
        @Override
        public void putAll(final Map<? extends Integer, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = (ObjectSet<Entry>)ObjectSets.singleton(new SingletonEntry());
            }
            return this.entries;
        }
        
        @Override
        public IntSet keySet() {
            if (this.keys == null) {
                this.keys = IntSets.singleton(this.key);
            }
            return this.keys;
        }
        
        @Override
        public IntCollection values() {
            if (this.values == null) {
                this.values = IntSets.singleton(this.value);
            }
            return this.values;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
            return (ObjectSet<Map.Entry<Integer, Integer>>)this.int2IntEntrySet();
        }
        
        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map)) {
                return false;
            }
            final Map<?, ?> m = (Map<?, ?>)o;
            return m.size() == 1 && this.entrySet().iterator().next().equals(m.entrySet().iterator().next());
        }
        
        @Override
        public String toString() {
            return "{" + this.key + "=>" + this.value + "}";
        }
        
        protected class SingletonEntry implements Int2IntMap.Entry, Map.Entry<Integer, Integer>
        {
            @Deprecated
            @Override
            public Integer getKey() {
                return Singleton.this.key;
            }
            
            @Deprecated
            @Override
            public Integer getValue() {
                return Singleton.this.value;
            }
            
            @Override
            public int getIntKey() {
                return Singleton.this.key;
            }
            
            @Override
            public int getIntValue() {
                return Singleton.this.value;
            }
            
            @Override
            public int setValue(final int value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Integer setValue(final Integer value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean equals(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                return e.getKey() != null && e.getKey() instanceof Integer && e.getValue() != null && e.getValue() instanceof Integer && Singleton.this.key == (int)e.getKey() && Singleton.this.value == (int)e.getValue();
            }
            
            @Override
            public int hashCode() {
                return Singleton.this.key ^ Singleton.this.value;
            }
            
            @Override
            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }
    
    public static class SynchronizedMap extends Int2IntFunctions.SynchronizedFunction implements Int2IntMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2IntMap map;
        protected transient ObjectSet<Entry> entries;
        protected transient IntSet keys;
        protected transient IntCollection values;
        
        protected SynchronizedMap(final Int2IntMap m, final Object sync) {
            super(m, sync);
            this.map = m;
        }
        
        protected SynchronizedMap(final Int2IntMap m) {
            super(m);
            this.map = m;
        }
        
        @Override
        public int size() {
            synchronized (this.sync) {
                return this.map.size();
            }
        }
        
        @Override
        public boolean containsKey(final int k) {
            synchronized (this.sync) {
                return this.map.containsKey(k);
            }
        }
        
        @Override
        public boolean containsValue(final int v) {
            synchronized (this.sync) {
                return this.map.containsValue(v);
            }
        }
        
        @Override
        public int defaultReturnValue() {
            synchronized (this.sync) {
                return this.map.defaultReturnValue();
            }
        }
        
        @Override
        public void defaultReturnValue(final int defRetValue) {
            synchronized (this.sync) {
                this.map.defaultReturnValue(defRetValue);
            }
        }
        
        @Override
        public int put(final int k, final int v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Override
        public void putAll(final Map<? extends Integer, ? extends Integer> m) {
            synchronized (this.sync) {
                this.map.putAll(m);
            }
        }
        
        @Override
        public ObjectSet<Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.int2IntEntrySet(), this.sync);
            }
            return this.entries;
        }
        
        @Override
        public IntSet keySet() {
            if (this.keys == null) {
                this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
            }
            return this.keys;
        }
        
        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.synchronize(this.map.values(), this.sync);
            }
            return this.values;
        }
        
        @Override
        public void clear() {
            synchronized (this.sync) {
                this.map.clear();
            }
        }
        
        @Override
        public String toString() {
            synchronized (this.sync) {
                return this.map.toString();
            }
        }
        
        @Deprecated
        @Override
        public Integer put(final Integer k, final Integer v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Deprecated
        @Override
        public int remove(final int k) {
            synchronized (this.sync) {
                return this.map.remove(k);
            }
        }
        
        @Deprecated
        @Override
        public int get(final int k) {
            synchronized (this.sync) {
                return this.map.get(k);
            }
        }
        
        @Override
        public boolean containsKey(final Object ok) {
            synchronized (this.sync) {
                return this.map.containsKey(ok);
            }
        }
        
        @Deprecated
        @Override
        public boolean containsValue(final Object ov) {
            synchronized (this.sync) {
                return this.map.containsValue(ov);
            }
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (this.sync) {
                return this.map.isEmpty();
            }
        }
        
        @Override
        public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
            synchronized (this.sync) {
                return this.map.entrySet();
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.sync) {
                return this.map.hashCode();
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            synchronized (this.sync) {
                return this.map.equals(o);
            }
        }
    }
    
    public static class UnmodifiableMap extends Int2IntFunctions.UnmodifiableFunction implements Int2IntMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2IntMap map;
        protected transient ObjectSet<Entry> entries;
        protected transient IntSet keys;
        protected transient IntCollection values;
        
        protected UnmodifiableMap(final Int2IntMap m) {
            super(m);
            this.map = m;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean containsKey(final int k) {
            return this.map.containsKey(k);
        }
        
        @Override
        public boolean containsValue(final int v) {
            return this.map.containsValue(v);
        }
        
        @Override
        public int defaultReturnValue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void defaultReturnValue(final int defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int put(final int k, final int v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Map<? extends Integer, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.int2IntEntrySet());
            }
            return this.entries;
        }
        
        @Override
        public IntSet keySet() {
            if (this.keys == null) {
                this.keys = IntSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }
        
        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.unmodifiable(this.map.values());
            }
            return this.values;
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.map.toString();
        }
        
        @Deprecated
        @Override
        public Integer put(final Integer k, final Integer v) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public int remove(final int k) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public int get(final int k) {
            return this.map.get(k);
        }
        
        @Override
        public boolean containsKey(final Object ok) {
            return this.map.containsKey(ok);
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return this.map.containsValue(ov);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }
}
