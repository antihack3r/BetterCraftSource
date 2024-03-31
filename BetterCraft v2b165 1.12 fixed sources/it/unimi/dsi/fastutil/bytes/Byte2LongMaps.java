// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.HashCommon;
import java.util.Collection;
import java.util.Set;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.io.Serializable;

public class Byte2LongMaps
{
    public static final EmptyMap EMPTY_MAP;
    
    private Byte2LongMaps() {
    }
    
    public static Byte2LongMap singleton(final byte key, final long value) {
        return new Singleton(key, value);
    }
    
    public static Byte2LongMap singleton(final Byte key, final Long value) {
        return new Singleton(key, value);
    }
    
    public static Byte2LongMap synchronize(final Byte2LongMap m) {
        return new SynchronizedMap(m);
    }
    
    public static Byte2LongMap synchronize(final Byte2LongMap m, final Object sync) {
        return new SynchronizedMap(m, sync);
    }
    
    public static Byte2LongMap unmodifiable(final Byte2LongMap m) {
        return new UnmodifiableMap(m);
    }
    
    static {
        EMPTY_MAP = new EmptyMap();
    }
    
    public static class EmptyMap extends Byte2LongFunctions.EmptyFunction implements Byte2LongMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyMap() {
        }
        
        @Override
        public boolean containsValue(final long v) {
            return false;
        }
        
        @Override
        public void putAll(final Map<? extends Byte, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> byte2LongEntrySet() {
            return ObjectSets.EMPTY_SET;
        }
        
        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }
        
        @Override
        public LongCollection values() {
            return LongSets.EMPTY_SET;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return false;
        }
        
        private Object readResolve() {
            return Byte2LongMaps.EMPTY_MAP;
        }
        
        @Override
        public Object clone() {
            return Byte2LongMaps.EMPTY_MAP;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public ObjectSet<Map.Entry<Byte, Long>> entrySet() {
            return (ObjectSet<Map.Entry<Byte, Long>>)this.byte2LongEntrySet();
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
    
    public static class Singleton extends Byte2LongFunctions.Singleton implements Byte2LongMap, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Entry> entries;
        protected transient ByteSet keys;
        protected transient LongCollection values;
        
        protected Singleton(final byte key, final long value) {
            super(key, value);
        }
        
        @Override
        public boolean containsValue(final long v) {
            return this.value == v;
        }
        
        @Override
        public boolean containsValue(final Object ov) {
            return (long)ov == this.value;
        }
        
        @Override
        public void putAll(final Map<? extends Byte, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> byte2LongEntrySet() {
            if (this.entries == null) {
                this.entries = (ObjectSet<Entry>)ObjectSets.singleton(new SingletonEntry());
            }
            return this.entries;
        }
        
        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
            }
            return this.keys;
        }
        
        @Override
        public LongCollection values() {
            if (this.values == null) {
                this.values = LongSets.singleton(this.value);
            }
            return this.values;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public ObjectSet<Map.Entry<Byte, Long>> entrySet() {
            return (ObjectSet<Map.Entry<Byte, Long>>)this.byte2LongEntrySet();
        }
        
        @Override
        public int hashCode() {
            return this.key ^ HashCommon.long2int(this.value);
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
        
        protected class SingletonEntry implements Byte2LongMap.Entry, Map.Entry<Byte, Long>
        {
            @Deprecated
            @Override
            public Byte getKey() {
                return Singleton.this.key;
            }
            
            @Deprecated
            @Override
            public Long getValue() {
                return Singleton.this.value;
            }
            
            @Override
            public byte getByteKey() {
                return Singleton.this.key;
            }
            
            @Override
            public long getLongValue() {
                return Singleton.this.value;
            }
            
            @Override
            public long setValue(final long value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Long setValue(final Long value) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean equals(final Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                return e.getKey() != null && e.getKey() instanceof Byte && e.getValue() != null && e.getValue() instanceof Long && Singleton.this.key == (byte)e.getKey() && Singleton.this.value == (long)e.getValue();
            }
            
            @Override
            public int hashCode() {
                return Singleton.this.key ^ HashCommon.long2int(Singleton.this.value);
            }
            
            @Override
            public String toString() {
                return Singleton.this.key + "->" + Singleton.this.value;
            }
        }
    }
    
    public static class SynchronizedMap extends Byte2LongFunctions.SynchronizedFunction implements Byte2LongMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2LongMap map;
        protected transient ObjectSet<Entry> entries;
        protected transient ByteSet keys;
        protected transient LongCollection values;
        
        protected SynchronizedMap(final Byte2LongMap m, final Object sync) {
            super(m, sync);
            this.map = m;
        }
        
        protected SynchronizedMap(final Byte2LongMap m) {
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
        public boolean containsKey(final byte k) {
            synchronized (this.sync) {
                return this.map.containsKey(k);
            }
        }
        
        @Override
        public boolean containsValue(final long v) {
            synchronized (this.sync) {
                return this.map.containsValue(v);
            }
        }
        
        @Override
        public long defaultReturnValue() {
            synchronized (this.sync) {
                return this.map.defaultReturnValue();
            }
        }
        
        @Override
        public void defaultReturnValue(final long defRetValue) {
            synchronized (this.sync) {
                this.map.defaultReturnValue(defRetValue);
            }
        }
        
        @Override
        public long put(final byte k, final long v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Override
        public void putAll(final Map<? extends Byte, ? extends Long> m) {
            synchronized (this.sync) {
                this.map.putAll(m);
            }
        }
        
        @Override
        public ObjectSet<Entry> byte2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.synchronize(this.map.byte2LongEntrySet(), this.sync);
            }
            return this.entries;
        }
        
        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
            }
            return this.keys;
        }
        
        @Override
        public LongCollection values() {
            if (this.values == null) {
                return LongCollections.synchronize(this.map.values(), this.sync);
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
        public Long put(final Byte k, final Long v) {
            synchronized (this.sync) {
                return this.map.put(k, v);
            }
        }
        
        @Deprecated
        @Override
        public long remove(final byte k) {
            synchronized (this.sync) {
                return this.map.remove(k);
            }
        }
        
        @Deprecated
        @Override
        public long get(final byte k) {
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
        public ObjectSet<Map.Entry<Byte, Long>> entrySet() {
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
    
    public static class UnmodifiableMap extends Byte2LongFunctions.UnmodifiableFunction implements Byte2LongMap, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2LongMap map;
        protected transient ObjectSet<Entry> entries;
        protected transient ByteSet keys;
        protected transient LongCollection values;
        
        protected UnmodifiableMap(final Byte2LongMap m) {
            super(m);
            this.map = m;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean containsKey(final byte k) {
            return this.map.containsKey(k);
        }
        
        @Override
        public boolean containsValue(final long v) {
            return this.map.containsValue(v);
        }
        
        @Override
        public long defaultReturnValue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void defaultReturnValue(final long defRetValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long put(final byte k, final long v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Map<? extends Byte, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSet<Entry> byte2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2LongEntrySet());
            }
            return this.entries;
        }
        
        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }
        
        @Override
        public LongCollection values() {
            if (this.values == null) {
                return LongCollections.unmodifiable(this.map.values());
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
        public Long put(final Byte k, final Long v) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public long remove(final byte k) {
            throw new UnsupportedOperationException();
        }
        
        @Deprecated
        @Override
        public long get(final byte k) {
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
        public ObjectSet<Map.Entry<Byte, Long>> entrySet() {
            return ObjectSets.unmodifiable(this.map.entrySet());
        }
    }
}