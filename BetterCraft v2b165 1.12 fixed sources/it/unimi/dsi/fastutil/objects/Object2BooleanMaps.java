// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import java.util.Collection;
import java.util.Set;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import java.util.Map;
import java.io.Serializable;

public class Object2BooleanMaps
{
    public static final EmptyMap EMPTY_MAP;
    
    private Object2BooleanMaps() {
    }
    
    public static <K> Object2BooleanMap<K> emptyMap() {
        return Object2BooleanMaps.EMPTY_MAP;
    }
    
    public static <K> Object2BooleanMap<K> singleton(final K key, final boolean value) {
        return new Singleton<K>(key, value);
    }
    
    public static <K> Object2BooleanMap<K> singleton(final K key, final Boolean value) {
        return new Singleton<K>(key, value);
    }
    
    public static <K> Object2BooleanMap<K> synchronize(final Object2BooleanMap<K> m) {
        return new SynchronizedMap<K>(m);
    }
    
    public static <K> Object2BooleanMap<K> synchronize(final Object2BooleanMap<K> m, final Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }
    
    public static <K> Object2BooleanMap<K> unmodifiable(final Object2BooleanMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }
    
    static {
        EMPTY_MAP = new EmptyMap();
    }
    
    public static class EmptyMap<K> extends Object2BooleanFunctions.EmptyFunction<K> implements Object2BooleanMap<K>, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyMap() {
        }
        
        @Override
        public boolean containsValue(final boolean v) {
            return false;
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry<K>> object2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }
        
        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }
        
        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return false;
        }
        
        private Object readResolve() {
            return Object2BooleanMaps.EMPTY_MAP;
        }
        
        @Override
        public Object clone() {
            return Object2BooleanMaps.EMPTY_MAP;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return (ObjectSet<Map.Entry<K, Boolean>>)this.object2BooleanEntrySet();
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
    
    public static class Singleton<K> extends Object2BooleanFunctions.Singleton<K> implements Object2BooleanMap<K>, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;
        
        protected Singleton(final K key, final boolean value) {
            super(key, value);
        }
        
        @Override
        public boolean containsValue(final boolean v) {
            return this.value == v;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return (boolean)ov == this.value;
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = (ObjectSet<Entry<K>>)ObjectSets.singleton(new SingletonEntry());
            }
            return this.entries;
        }
        
        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
            }
            return this.keys;
        }
        
        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = BooleanSets.singleton(this.value);
            }
            return this.values;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return (ObjectSet<Map.Entry<K, Boolean>>)this.object2BooleanEntrySet();
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ (this.value ? 1231 : 1237);
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
        
        protected class SingletonEntry implements Object2BooleanMap.Entry<K>, Map.Entry<K, Boolean>
        {
            @Override
            public K getKey() {
                return (K)Singleton.this.key;
            }
            
            @Deprecated
            @Override
            public Boolean getValue() {
                return Singleton.this.value;
            }
            
            @Override
            public boolean getBooleanValue() {
                return Singleton.this.value;
            }
            
            @Override
            public boolean setValue(final boolean value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Boolean setValue(final Boolean value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean equals(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                    return false;
                }
                if (Singleton.this.key == null) {
                    if (e.getKey() != null) {
                        return false;
                    }
                }
                else if (!Singleton.this.key.equals(e.getKey())) {
                    return false;
                }
                if (Singleton.this.value == (boolean)e.getValue()) {
                    return true;
                }
                return false;
            }
            
            @Override
            public int hashCode() {
                return ((Singleton.this.key == null) ? 0 : Singleton.this.key.hashCode()) ^ (Singleton.this.value ? 1231 : 1237);
            }
            
            @Override
            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }
    
    public static class SynchronizedMap<K> extends Object2BooleanFunctions.SynchronizedFunction<K> implements Object2BooleanMap<K>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanMap<K> map;
        protected transient ObjectSet<Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;
        
        protected SynchronizedMap(final Object2BooleanMap<K> m, final Object sync) {
            super(m, sync);
            this.map = m;
        }
        
        protected SynchronizedMap(final Object2BooleanMap<K> m) {
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
        public boolean containsKey(final Object k) {
            synchronized (this.sync) {
                return this.map.containsKey(k);
            }
        }
        
        @Override
        public boolean containsValue(final boolean v) {
            synchronized (this.sync) {
                return this.map.containsValue(v);
            }
        }
        
        @Override
        public boolean defaultReturnValue() {
            synchronized (this.sync) {
                return this.map.defaultReturnValue();
            }
        }
        
        @Override
        public void defaultReturnValue(final boolean defRetValue) {
            synchronized (this.sync) {
                this.map.defaultReturnValue(defRetValue);
            }
        }
        
        @Override
        public boolean put(final K k, final boolean v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends Boolean> m) {
            synchronized (this.sync) {
                this.map.putAll((Map<?, ?>)m);
            }
        }
        
        @Override
        public ObjectSet<Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.object2BooleanEntrySet(), this.sync);
            }
            return this.entries;
        }
        
        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }
            return this.keys;
        }
        
        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.synchronize(this.map.values(), this.sync);
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
        public Boolean put(final K k, final Boolean v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Deprecated
        @Override
        public boolean containsValue(final Object ov) {
            synchronized (this.sync) {
                return this.map.containsValue(ov);
            }
        }
        
        @Deprecated
        @Override
        public boolean removeBoolean(final Object k) {
            synchronized (this.sync) {
                return this.map.removeBoolean(k);
            }
        }
        
        @Deprecated
        @Override
        public boolean getBoolean(final Object k) {
            synchronized (this.sync) {
                return this.map.getBoolean(k);
            }
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (this.sync) {
                return this.map.isEmpty();
            }
        }
        
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
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
    
    public static class UnmodifiableMap<K> extends Object2BooleanFunctions.UnmodifiableFunction<K> implements Object2BooleanMap<K>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanMap<K> map;
        protected transient ObjectSet<Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;
        
        protected UnmodifiableMap(final Object2BooleanMap<K> m) {
            super(m);
            this.map = m;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean containsKey(final Object k) {
            return this.map.containsKey(k);
        }
        
        @Override
        public boolean containsValue(final boolean v) {
            return this.map.containsValue(v);
        }
        
        @Override
        public boolean defaultReturnValue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void defaultReturnValue(final boolean defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean put(final K k, final boolean v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2BooleanEntrySet());
            }
            return this.entries;
        }
        
        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }
        
        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.unmodifiable(this.map.values());
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
        
        @Override
        public boolean containsValue(final Object ov) {
            return this.map.containsValue(ov);
        }
        
        @Override
        public boolean removeBoolean(final Object k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean getBoolean(final Object k) {
            return this.map.getBoolean(k);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }
}
