// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleArraySet;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import java.util.Map;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import java.io.Serializable;

public class Object2DoubleArrayMap<K> extends AbstractObject2DoubleMap<K> implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;
    private transient Object[] key;
    private transient double[] value;
    private int size;
    
    public Object2DoubleArrayMap(final Object[] key, final double[] value) {
        this.key = key;
        this.value = value;
        this.size = key.length;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
    }
    
    public Object2DoubleArrayMap() {
        this.key = ObjectArrays.EMPTY_ARRAY;
        this.value = DoubleArrays.EMPTY_ARRAY;
    }
    
    public Object2DoubleArrayMap(final int capacity) {
        this.key = new Object[capacity];
        this.value = new double[capacity];
    }
    
    public Object2DoubleArrayMap(final Object2DoubleMap<K> m) {
        this(m.size());
        this.putAll((Map<? extends K, ? extends Double>)m);
    }
    
    public Object2DoubleArrayMap(final Map<? extends K, ? extends Double> m) {
        this(m.size());
        this.putAll(m);
    }
    
    public Object2DoubleArrayMap(final Object[] key, final double[] value, final int size) {
        this.key = key;
        this.value = value;
        this.size = size;
        if (key.length != value.length) {
            throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
        }
        if (size > key.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
        }
    }
    
    @Override
    public Object2DoubleMap.FastEntrySet<K> object2DoubleEntrySet() {
        return new EntrySet();
    }
    
    private int findKey(final Object k) {
        final Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] == null) {
                if (k != null) {
                    continue;
                }
            }
            else if (!key[i].equals(k)) {
                continue;
            }
            return i;
        }
        return -1;
    }
    
    @Override
    public double getDouble(final Object k) {
        final Object[] key = this.key;
        int i = this.size;
        while (i-- != 0) {
            if (key[i] == null) {
                if (k != null) {
                    continue;
                }
            }
            else if (!key[i].equals(k)) {
                continue;
            }
            return this.value[i];
        }
        return this.defRetValue;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public void clear() {
        int i = this.size;
        while (i-- != 0) {
            this.key[i] = null;
        }
        this.size = 0;
    }
    
    @Override
    public boolean containsKey(final Object k) {
        return this.findKey(k) != -1;
    }
    
    @Override
    public boolean containsValue(final double v) {
        int i = this.size;
        while (i-- != 0) {
            if (this.value[i] == v) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public double put(final K k, final double v) {
        final int oldKey = this.findKey(k);
        if (oldKey != -1) {
            final double oldValue = this.value[oldKey];
            this.value[oldKey] = v;
            return oldValue;
        }
        if (this.size == this.key.length) {
            final Object[] newKey = new Object[(this.size == 0) ? 2 : (this.size * 2)];
            final double[] newValue = new double[(this.size == 0) ? 2 : (this.size * 2)];
            int i = this.size;
            while (i-- != 0) {
                newKey[i] = this.key[i];
                newValue[i] = this.value[i];
            }
            this.key = newKey;
            this.value = newValue;
        }
        this.key[this.size] = k;
        this.value[this.size] = v;
        ++this.size;
        return this.defRetValue;
    }
    
    @Override
    public double removeDouble(final Object k) {
        final int oldPos = this.findKey(k);
        if (oldPos == -1) {
            return this.defRetValue;
        }
        final double oldValue = this.value[oldPos];
        final int tail = this.size - oldPos - 1;
        System.arraycopy(this.key, oldPos + 1, this.key, oldPos, tail);
        System.arraycopy(this.value, oldPos + 1, this.value, oldPos, tail);
        --this.size;
        this.key[this.size] = null;
        return oldValue;
    }
    
    @Override
    public ObjectSet<K> keySet() {
        return new ObjectArraySet<K>(this.key, this.size);
    }
    
    @Override
    public DoubleCollection values() {
        return DoubleCollections.unmodifiable(new DoubleArraySet(this.value, this.size));
    }
    
    public Object2DoubleArrayMap<K> clone() {
        Object2DoubleArrayMap<K> c;
        try {
            c = (Object2DoubleArrayMap)super.clone();
        }
        catch (final CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = this.key.clone();
        c.value = this.value.clone();
        return c;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeObject(this.key[i]);
            s.writeDouble(this.value[i]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.key = new Object[this.size];
        this.value = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.key[i] = s.readObject();
            this.value[i] = s.readDouble();
        }
    }
    
    private final class EntrySet extends AbstractObjectSet<Object2DoubleMap.Entry<K>> implements Object2DoubleMap.FastEntrySet<K>
    {
        @Override
        public ObjectIterator<Object2DoubleMap.Entry<K>> iterator() {
            return new AbstractObjectIterator<Object2DoubleMap.Entry<K>>() {
                int curr = -1;
                int next = 0;
                
                @Override
                public boolean hasNext() {
                    return this.next < Object2DoubleArrayMap.this.size;
                }
                
                @Override
                public Object2DoubleMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    final Object[] access$100 = Object2DoubleArrayMap.this.key;
                    final int next = this.next;
                    this.curr = next;
                    return new BasicEntry<K>(access$100[next], Object2DoubleArrayMap.this.value[this.next++]);
                }
                
                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    final int tail = Object2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2DoubleArrayMap.this.key, this.next + 1, Object2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2DoubleArrayMap.this.value, this.next + 1, Object2DoubleArrayMap.this.value, this.next, tail);
                    Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
                }
            };
        }
        
        @Override
        public ObjectIterator<Object2DoubleMap.Entry<K>> fastIterator() {
            return new AbstractObjectIterator<Object2DoubleMap.Entry<K>>() {
                int next = 0;
                int curr = -1;
                final BasicEntry<K> entry = new BasicEntry<K>(null, 0.0);
                
                @Override
                public boolean hasNext() {
                    return this.next < Object2DoubleArrayMap.this.size;
                }
                
                @Override
                public Object2DoubleMap.Entry<K> next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    final BasicEntry<K> entry = this.entry;
                    final Object[] access$100 = Object2DoubleArrayMap.this.key;
                    final int next = this.next;
                    this.curr = next;
                    entry.key = (K)access$100[next];
                    this.entry.value = Object2DoubleArrayMap.this.value[this.next++];
                    return this.entry;
                }
                
                @Override
                public void remove() {
                    if (this.curr == -1) {
                        throw new IllegalStateException();
                    }
                    this.curr = -1;
                    final int tail = Object2DoubleArrayMap.this.size-- - this.next--;
                    System.arraycopy(Object2DoubleArrayMap.this.key, this.next + 1, Object2DoubleArrayMap.this.key, this.next, tail);
                    System.arraycopy(Object2DoubleArrayMap.this.value, this.next + 1, Object2DoubleArrayMap.this.value, this.next, tail);
                    Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
                }
            };
        }
        
        @Override
        public int size() {
            return Object2DoubleArrayMap.this.size;
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            final K k = (K)e.getKey();
            return Object2DoubleArrayMap.this.containsKey(k) && Object2DoubleArrayMap.this.getDouble(k) == (double)e.getValue();
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            final K k = (K)e.getKey();
            final double v = (double)e.getValue();
            final int oldPos = Object2DoubleArrayMap.this.findKey(k);
            if (oldPos == -1 || v != Object2DoubleArrayMap.this.value[oldPos]) {
                return false;
            }
            final int tail = Object2DoubleArrayMap.this.size - oldPos - 1;
            System.arraycopy(Object2DoubleArrayMap.this.key, oldPos + 1, Object2DoubleArrayMap.this.key, oldPos, tail);
            System.arraycopy(Object2DoubleArrayMap.this.value, oldPos + 1, Object2DoubleArrayMap.this.value, oldPos, tail);
            Object2DoubleArrayMap.this.size--;
            Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
            return true;
        }
    }
}
