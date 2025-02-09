// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import java.util.Arrays;
import java.util.Map;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class Long2ObjectOpenCustomHashMap<V> extends AbstractLong2ObjectMap<V> implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected LongHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Long2ObjectMap.FastEntrySet<V> entries;
    protected transient LongSet keys;
    protected transient ObjectCollection<V> values;
    
    public Long2ObjectOpenCustomHashMap(final int expected, final float f, final LongHash.Strategy strategy) {
        this.strategy = strategy;
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new long[this.n + 1];
        this.value = (V[])new Object[this.n + 1];
    }
    
    public Long2ObjectOpenCustomHashMap(final int expected, final LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }
    
    public Long2ObjectOpenCustomHashMap(final LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }
    
    public Long2ObjectOpenCustomHashMap(final Map<? extends Long, ? extends V> m, final float f, final LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }
    
    public Long2ObjectOpenCustomHashMap(final Map<? extends Long, ? extends V> m, final LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Long2ObjectOpenCustomHashMap(final Long2ObjectMap<V> m, final float f, final LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll((Map<? extends Long, ? extends V>)m);
    }
    
    public Long2ObjectOpenCustomHashMap(final Long2ObjectMap<V> m, final LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Long2ObjectOpenCustomHashMap(final long[] k, final V[] v, final float f, final LongHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Long2ObjectOpenCustomHashMap(final long[] k, final V[] v, final LongHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }
    
    public LongHash.Strategy strategy() {
        return this.strategy;
    }
    
    private int realSize() {
        return this.containsNullKey ? (this.size - 1) : this.size;
    }
    
    private void ensureCapacity(final int capacity) {
        final int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }
    
    private void tryCapacity(final long capacity) {
        final int needed = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil(capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }
    
    private V removeEntry(final int pos) {
        final V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    private V removeNullEntry() {
        this.containsNullKey = false;
        final V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends Long, ? extends V> m) {
        if (this.f <= 0.5) {
            this.ensureCapacity(m.size());
        }
        else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }
    
    private int insert(final long k, final V v) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final long[] key = this.key;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return pos;
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                    if (this.strategy.equals(curr, k)) {
                        return pos;
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return -1;
    }
    
    @Override
    public V put(final long k, final V v) {
        final int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    @Deprecated
    @Override
    public V put(final Long ok, final V ov) {
        final V v = ov;
        final int pos = this.insert(ok, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    protected final void shiftKeys(int pos) {
        final long[] key = this.key;
        int last = 0;
    Label_0006:
        while (true) {
            pos = ((last = pos) + 1 & this.mask);
            long curr;
            while ((curr = key[pos]) != 0L) {
                final int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                Label_0105: {
                    if (last <= pos) {
                        if (last >= slot) {
                            break Label_0105;
                        }
                        if (slot > pos) {
                            break Label_0105;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break Label_0105;
                    }
                    pos = (pos + 1 & this.mask);
                    continue;
                }
                key[last] = curr;
                this.value[last] = this.value[pos];
                continue Label_0006;
            }
            break;
        }
        key[last] = 0L;
        this.value[last] = null;
    }
    
    @Override
    public V remove(final long k) {
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        else {
            final long[] key = this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
                return this.defRetValue;
            }
            if (this.strategy.equals(k, curr)) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                if (this.strategy.equals(k, curr)) {
                    return this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
    }
    
    @Deprecated
    @Override
    public V remove(final Object ok) {
        final long k = (long)ok;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        else {
            final long[] key = this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
                return this.defRetValue;
            }
            if (this.strategy.equals(curr, k)) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
    }
    
    @Deprecated
    public V get(final Long ok) {
        if (ok == null) {
            return null;
        }
        final long k = ok;
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        final long[] key = this.key;
        int pos;
        long curr;
        if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (this.strategy.equals(k, curr)) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public V get(final long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        final long[] key = this.key;
        int pos;
        long curr;
        if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (this.strategy.equals(k, curr)) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public boolean containsKey(final long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey;
        }
        final long[] key = this.key;
        int pos;
        long curr;
        if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (this.strategy.equals(k, curr)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object v) {
        final V[] value = this.value;
        final long[] key = this.key;
        Label_0048: {
            if (this.containsNullKey) {
                if (value[this.n] == null) {
                    if (v != null) {
                        break Label_0048;
                    }
                }
                else if (!value[this.n].equals(v)) {
                    break Label_0048;
                }
                return true;
            }
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] != 0L) {
                if (value[i] == null) {
                    if (v != null) {
                        continue;
                    }
                }
                else if (!value[i].equals(v)) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, 0L);
        Arrays.fill(this.value, null);
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Deprecated
    public void growthFactor(final int growthFactor) {
    }
    
    @Deprecated
    public int growthFactor() {
        return 16;
    }
    
    @Override
    public Long2ObjectMap.FastEntrySet<V> long2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }
    
    @Override
    public LongSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }
    
    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>() {
                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Long2ObjectOpenCustomHashMap.this.size;
                }
                
                @Override
                public boolean contains(final Object v) {
                    return Long2ObjectOpenCustomHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Long2ObjectOpenCustomHashMap.this.clear();
                }
            };
        }
        return this.values;
    }
    
    @Deprecated
    public boolean rehash() {
        return true;
    }
    
    public boolean trim() {
        final int l = HashCommon.arraySize(this.size, this.f);
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (final OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }
    
    public boolean trim(final int n) {
        final int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
        if (l >= n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (final OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }
    
    protected void rehash(final int newN) {
        final long[] key = this.key;
        final V[] value = this.value;
        final int mask = newN - 1;
        final long[] newKey = new long[newN + 1];
        final V[] newValue = (V[])new Object[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0L) {}
            int pos;
            if (newKey[pos = (HashCommon.mix(this.strategy.hashCode(key[i])) & mask)] != 0L) {
                while (newKey[pos = (pos + 1 & mask)] != 0L) {}
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }
    
    public Long2ObjectOpenCustomHashMap<V> clone() {
        Long2ObjectOpenCustomHashMap<V> c;
        try {
            c = (Long2ObjectOpenCustomHashMap)super.clone();
        }
        catch (final CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = this.key.clone();
        c.value = this.value.clone();
        c.strategy = this.strategy;
        return c;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0L) {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            if (this != this.value[i]) {
                t ^= ((this.value[i] == null) ? 0 : this.value[i].hashCode());
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += ((this.value[this.n] == null) ? 0 : this.value[this.n].hashCode());
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final long[] key = this.key;
        final V[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeObject(value[e]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final long[] key2 = new long[this.n + 1];
        this.key = key2;
        final long[] key = key2;
        final Object[] value2 = new Object[this.n + 1];
        this.value = (V[])value2;
        final V[] value = (V[])value2;
        int i = this.size;
        while (i-- != 0) {
            final long k = s.readLong();
            final V v = (V)s.readObject();
            int pos;
            if (this.strategy.equals(k, 0L)) {
                pos = this.n;
                this.containsNullKey = true;
            }
            else {
                for (pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask); key[pos] != 0L; pos = (pos + 1 & this.mask)) {}
            }
            key[pos] = k;
            value[pos] = v;
        }
    }
    
    private void checkTable() {
    }
    
    final class MapEntry implements Long2ObjectMap.Entry<V>, Map.Entry<Long, V>
    {
        int index;
        
        MapEntry(final int index) {
            this.index = index;
        }
        
        MapEntry() {
        }
        
        @Deprecated
        @Override
        public Long getKey() {
            return Long2ObjectOpenCustomHashMap.this.key[this.index];
        }
        
        @Override
        public long getLongKey() {
            return Long2ObjectOpenCustomHashMap.this.key[this.index];
        }
        
        @Override
        public V getValue() {
            return Long2ObjectOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public V setValue(final V v) {
            final V oldValue = Long2ObjectOpenCustomHashMap.this.value[this.index];
            Long2ObjectOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<Long, V> e = (Map.Entry<Long, V>)o;
            return Long2ObjectOpenCustomHashMap.this.strategy.equals(Long2ObjectOpenCustomHashMap.this.key[this.index], e.getKey()) && ((Long2ObjectOpenCustomHashMap.this.value[this.index] != null) ? Long2ObjectOpenCustomHashMap.this.value[this.index].equals(e.getValue()) : (e.getValue() == null));
        }
        
        @Override
        public int hashCode() {
            return Long2ObjectOpenCustomHashMap.this.strategy.hashCode(Long2ObjectOpenCustomHashMap.this.key[this.index]) ^ ((Long2ObjectOpenCustomHashMap.this.value[this.index] == null) ? 0 : Long2ObjectOpenCustomHashMap.this.value[this.index].hashCode());
        }
        
        @Override
        public String toString() {
            return Long2ObjectOpenCustomHashMap.this.key[this.index] + "=>" + Long2ObjectOpenCustomHashMap.this.value[this.index];
        }
    }
    
    private class MapIterator
    {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        LongArrayList wrapped;
        
        private MapIterator() {
            this.pos = Long2ObjectOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Long2ObjectOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Long2ObjectOpenCustomHashMap.this.containsNullKey;
        }
        
        public boolean hasNext() {
            return this.c != 0;
        }
        
        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                return this.last = Long2ObjectOpenCustomHashMap.this.n;
            }
            final long[] key = Long2ObjectOpenCustomHashMap.this.key;
            while (--this.pos >= 0) {
                if (key[this.pos] != 0L) {
                    return this.last = this.pos;
                }
            }
            this.last = Integer.MIN_VALUE;
            long k;
            int p;
            for (k = this.wrapped.getLong(-this.pos - 1), p = (HashCommon.mix(Long2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ObjectOpenCustomHashMap.this.mask); !Long2ObjectOpenCustomHashMap.this.strategy.equals(k, key[p]); p = (p + 1 & Long2ObjectOpenCustomHashMap.this.mask)) {}
            return p;
        }
        
        private final void shiftKeys(int pos) {
            final long[] key = Long2ObjectOpenCustomHashMap.this.key;
            int last = 0;
        Label_0009:
            while (true) {
                pos = ((last = pos) + 1 & Long2ObjectOpenCustomHashMap.this.mask);
                long curr;
                while ((curr = key[pos]) != 0L) {
                    final int slot = HashCommon.mix(Long2ObjectOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2ObjectOpenCustomHashMap.this.mask;
                    Label_0123: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0123;
                            }
                            if (slot > pos) {
                                break Label_0123;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0123;
                        }
                        pos = (pos + 1 & Long2ObjectOpenCustomHashMap.this.mask);
                        continue;
                    }
                    if (pos < last) {
                        if (this.wrapped == null) {
                            this.wrapped = new LongArrayList(2);
                        }
                        this.wrapped.add(key[pos]);
                    }
                    key[last] = curr;
                    Long2ObjectOpenCustomHashMap.this.value[last] = Long2ObjectOpenCustomHashMap.this.value[pos];
                    continue Label_0009;
                }
                break;
            }
            key[last] = 0L;
            Long2ObjectOpenCustomHashMap.this.value[last] = null;
        }
        
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2ObjectOpenCustomHashMap.this.n) {
                Long2ObjectOpenCustomHashMap.this.containsNullKey = false;
                Long2ObjectOpenCustomHashMap.this.value[Long2ObjectOpenCustomHashMap.this.n] = null;
            }
            else {
                if (this.pos < 0) {
                    Long2ObjectOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                    this.last = -1;
                    return;
                }
                this.shiftKeys(this.last);
            }
            final Long2ObjectOpenCustomHashMap this$0 = Long2ObjectOpenCustomHashMap.this;
            --this$0.size;
            this.last = -1;
        }
        
        public int skip(final int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
    }
    
    private class EntryIterator extends MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>>
    {
        private MapEntry entry;
        
        @Override
        public Long2ObjectMap.Entry<V> next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>>
    {
        private final MapEntry entry;
        
        private FastEntryIterator() {
            this.entry = new MapEntry();
        }
        
        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }
    
    private final class MapEntrySet extends AbstractObjectSet<Long2ObjectMap.Entry<V>> implements Long2ObjectMap.FastEntrySet<V>
    {
        @Override
        public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            final long k = (long)e.getKey();
            final V v = (V)e.getValue();
            if (Long2ObjectOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                return Long2ObjectOpenCustomHashMap.this.containsNullKey && ((Long2ObjectOpenCustomHashMap.this.value[Long2ObjectOpenCustomHashMap.this.n] != null) ? Long2ObjectOpenCustomHashMap.this.value[Long2ObjectOpenCustomHashMap.this.n].equals(v) : (v == null));
            }
            final long[] key = Long2ObjectOpenCustomHashMap.this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(Long2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ObjectOpenCustomHashMap.this.mask)]) == 0L) {
                return false;
            }
            if (Long2ObjectOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return (Long2ObjectOpenCustomHashMap.this.value[pos] == null) ? (v == null) : Long2ObjectOpenCustomHashMap.this.value[pos].equals(v);
            }
            while ((curr = key[pos = (pos + 1 & Long2ObjectOpenCustomHashMap.this.mask)]) != 0L) {
                if (Long2ObjectOpenCustomHashMap.this.strategy.equals(k, curr)) {
                    return (Long2ObjectOpenCustomHashMap.this.value[pos] == null) ? (v == null) : Long2ObjectOpenCustomHashMap.this.value[pos].equals(v);
                }
            }
            return false;
        }
        
        @Override
        public boolean rem(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            final long k = (long)e.getKey();
            final V v = (V)e.getValue();
            if (Long2ObjectOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                if (Long2ObjectOpenCustomHashMap.this.containsNullKey) {
                    if (Long2ObjectOpenCustomHashMap.this.value[Long2ObjectOpenCustomHashMap.this.n] == null) {
                        if (v != null) {
                            return false;
                        }
                    }
                    else if (!Long2ObjectOpenCustomHashMap.this.value[Long2ObjectOpenCustomHashMap.this.n].equals(v)) {
                        return false;
                    }
                    Long2ObjectOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            final long[] key = Long2ObjectOpenCustomHashMap.this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(Long2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ObjectOpenCustomHashMap.this.mask)]) == 0L) {
                return false;
            }
            if (Long2ObjectOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Long2ObjectOpenCustomHashMap.this.value[pos] == null) {
                    if (v != null) {
                        return false;
                    }
                }
                else if (!Long2ObjectOpenCustomHashMap.this.value[pos].equals(v)) {
                    return false;
                }
                Long2ObjectOpenCustomHashMap.this.removeEntry(pos);
                return true;
            }
            while ((curr = key[pos = (pos + 1 & Long2ObjectOpenCustomHashMap.this.mask)]) != 0L) {
                if (Long2ObjectOpenCustomHashMap.this.strategy.equals(curr, k)) {
                    if (Long2ObjectOpenCustomHashMap.this.value[pos] == null) {
                        if (v != null) {
                            continue;
                        }
                    }
                    else if (!Long2ObjectOpenCustomHashMap.this.value[pos].equals(v)) {
                        continue;
                    }
                    Long2ObjectOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int size() {
            return Long2ObjectOpenCustomHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Long2ObjectOpenCustomHashMap.this.clear();
        }
    }
    
    private final class KeyIterator extends MapIterator implements LongIterator
    {
        public KeyIterator() {
        }
        
        @Override
        public long nextLong() {
            return Long2ObjectOpenCustomHashMap.this.key[this.nextEntry()];
        }
        
        @Override
        public Long next() {
            return Long2ObjectOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }
    
    private final class KeySet extends AbstractLongSet
    {
        @Override
        public LongIterator iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return Long2ObjectOpenCustomHashMap.this.size;
        }
        
        @Override
        public boolean contains(final long k) {
            return Long2ObjectOpenCustomHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean rem(final long k) {
            final int oldSize = Long2ObjectOpenCustomHashMap.this.size;
            Long2ObjectOpenCustomHashMap.this.remove(k);
            return Long2ObjectOpenCustomHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Long2ObjectOpenCustomHashMap.this.clear();
        }
    }
    
    private final class ValueIterator extends MapIterator implements ObjectIterator<V>
    {
        public ValueIterator() {
        }
        
        @Override
        public V next() {
            return Long2ObjectOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }
}
