// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import java.util.Arrays;
import java.util.Map;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class Long2ByteOpenCustomHashMap extends AbstractLong2ByteMap implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient byte[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected LongHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Long2ByteMap.FastEntrySet entries;
    protected transient LongSet keys;
    protected transient ByteCollection values;
    
    public Long2ByteOpenCustomHashMap(final int expected, final float f, final LongHash.Strategy strategy) {
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
        this.value = new byte[this.n + 1];
    }
    
    public Long2ByteOpenCustomHashMap(final int expected, final LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }
    
    public Long2ByteOpenCustomHashMap(final LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }
    
    public Long2ByteOpenCustomHashMap(final Map<? extends Long, ? extends Byte> m, final float f, final LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }
    
    public Long2ByteOpenCustomHashMap(final Map<? extends Long, ? extends Byte> m, final LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Long2ByteOpenCustomHashMap(final Long2ByteMap m, final float f, final LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }
    
    public Long2ByteOpenCustomHashMap(final Long2ByteMap m, final LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Long2ByteOpenCustomHashMap(final long[] k, final byte[] v, final float f, final LongHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Long2ByteOpenCustomHashMap(final long[] k, final byte[] v, final LongHash.Strategy strategy) {
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
    
    private byte removeEntry(final int pos) {
        final byte oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    private byte removeNullEntry() {
        this.containsNullKey = false;
        final byte oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends Long, ? extends Byte> m) {
        if (this.f <= 0.5) {
            this.ensureCapacity(m.size());
        }
        else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }
    
    private int insert(final long k, final byte v) {
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
    public byte put(final long k, final byte v) {
        final int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final byte oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    @Deprecated
    @Override
    public Byte put(final Long ok, final Byte ov) {
        final byte v = ov;
        final int pos = this.insert(ok, v);
        if (pos < 0) {
            return null;
        }
        final byte oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    private byte addToValue(final int pos, final byte incr) {
        final byte oldValue = this.value[pos];
        this.value[pos] = (byte)(oldValue + incr);
        return oldValue;
    }
    
    public byte addTo(final long k, final byte incr) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        }
        else {
            final long[] key = this.key;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                    if (this.strategy.equals(curr, k)) {
                        return this.addToValue(pos, incr);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = (byte)(this.defRetValue + incr);
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
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
                Label_0098: {
                    if (last <= pos) {
                        if (last >= slot) {
                            break Label_0098;
                        }
                        if (slot > pos) {
                            break Label_0098;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break Label_0098;
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
    }
    
    @Override
    public byte remove(final long k) {
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
    public Byte remove(final Object ok) {
        final long k = (long)ok;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        else {
            final long[] key = this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
                return null;
            }
            if (this.strategy.equals(curr, k)) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return this.removeEntry(pos);
                }
            }
            return null;
        }
    }
    
    @Deprecated
    public Byte get(final Long ok) {
        if (ok == null) {
            return null;
        }
        final long k = ok;
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? Byte.valueOf(this.value[this.n]) : null;
        }
        final long[] key = this.key;
        int pos;
        long curr;
        if ((curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0L) {
            return null;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (this.strategy.equals(k, curr)) {
                return this.value[pos];
            }
        }
        return null;
    }
    
    @Override
    public byte get(final long k) {
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
    public boolean containsValue(final byte v) {
        final byte[] value = this.value;
        final long[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] != 0L && value[i] == v) {
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
    public Long2ByteMap.FastEntrySet long2ByteEntrySet() {
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
    public ByteCollection values() {
        if (this.values == null) {
            this.values = new AbstractByteCollection() {
                @Override
                public ByteIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Long2ByteOpenCustomHashMap.this.size;
                }
                
                @Override
                public boolean contains(final byte v) {
                    return Long2ByteOpenCustomHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Long2ByteOpenCustomHashMap.this.clear();
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
        final byte[] value = this.value;
        final int mask = newN - 1;
        final long[] newKey = new long[newN + 1];
        final byte[] newValue = new byte[newN + 1];
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
    
    public Long2ByteOpenCustomHashMap clone() {
        Long2ByteOpenCustomHashMap c;
        try {
            c = (Long2ByteOpenCustomHashMap)super.clone();
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
            t ^= this.value[i];
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final long[] key = this.key;
        final byte[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeByte(value[e]);
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
        final byte[] value2 = new byte[this.n + 1];
        this.value = value2;
        final byte[] value = value2;
        int i = this.size;
        while (i-- != 0) {
            final long k = s.readLong();
            final byte v = s.readByte();
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
    
    final class MapEntry implements Long2ByteMap.Entry, Map.Entry<Long, Byte>
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
            return Long2ByteOpenCustomHashMap.this.key[this.index];
        }
        
        @Override
        public long getLongKey() {
            return Long2ByteOpenCustomHashMap.this.key[this.index];
        }
        
        @Deprecated
        @Override
        public Byte getValue() {
            return Long2ByteOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public byte getByteValue() {
            return Long2ByteOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public byte setValue(final byte v) {
            final byte oldValue = Long2ByteOpenCustomHashMap.this.value[this.index];
            Long2ByteOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public Byte setValue(final Byte v) {
            return this.setValue((byte)v);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<Long, Byte> e = (Map.Entry<Long, Byte>)o;
            return Long2ByteOpenCustomHashMap.this.strategy.equals(Long2ByteOpenCustomHashMap.this.key[this.index], e.getKey()) && Long2ByteOpenCustomHashMap.this.value[this.index] == e.getValue();
        }
        
        @Override
        public int hashCode() {
            return Long2ByteOpenCustomHashMap.this.strategy.hashCode(Long2ByteOpenCustomHashMap.this.key[this.index]) ^ Long2ByteOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public String toString() {
            return Long2ByteOpenCustomHashMap.this.key[this.index] + "=>" + Long2ByteOpenCustomHashMap.this.value[this.index];
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
            this.pos = Long2ByteOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Long2ByteOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Long2ByteOpenCustomHashMap.this.containsNullKey;
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
                return this.last = Long2ByteOpenCustomHashMap.this.n;
            }
            final long[] key = Long2ByteOpenCustomHashMap.this.key;
            while (--this.pos >= 0) {
                if (key[this.pos] != 0L) {
                    return this.last = this.pos;
                }
            }
            this.last = Integer.MIN_VALUE;
            long k;
            int p;
            for (k = this.wrapped.getLong(-this.pos - 1), p = (HashCommon.mix(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask); !Long2ByteOpenCustomHashMap.this.strategy.equals(k, key[p]); p = (p + 1 & Long2ByteOpenCustomHashMap.this.mask)) {}
            return p;
        }
        
        private final void shiftKeys(int pos) {
            final long[] key = Long2ByteOpenCustomHashMap.this.key;
            int last = 0;
        Label_0009:
            while (true) {
                pos = ((last = pos) + 1 & Long2ByteOpenCustomHashMap.this.mask);
                long curr;
                while ((curr = key[pos]) != 0L) {
                    final int slot = HashCommon.mix(Long2ByteOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2ByteOpenCustomHashMap.this.mask;
                    Label_0113: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0113;
                            }
                            if (slot > pos) {
                                break Label_0113;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0113;
                        }
                        pos = (pos + 1 & Long2ByteOpenCustomHashMap.this.mask);
                        continue;
                    }
                    if (pos < last) {
                        if (this.wrapped == null) {
                            this.wrapped = new LongArrayList(2);
                        }
                        this.wrapped.add(key[pos]);
                    }
                    key[last] = curr;
                    Long2ByteOpenCustomHashMap.this.value[last] = Long2ByteOpenCustomHashMap.this.value[pos];
                    continue Label_0009;
                }
                break;
            }
            key[last] = 0L;
        }
        
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2ByteOpenCustomHashMap.this.n) {
                Long2ByteOpenCustomHashMap.this.containsNullKey = false;
            }
            else {
                if (this.pos < 0) {
                    Long2ByteOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                    this.last = -1;
                    return;
                }
                this.shiftKeys(this.last);
            }
            final Long2ByteOpenCustomHashMap this$0 = Long2ByteOpenCustomHashMap.this;
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
    
    private class EntryIterator extends MapIterator implements ObjectIterator<Long2ByteMap.Entry>
    {
        private MapEntry entry;
        
        @Override
        public Long2ByteMap.Entry next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectIterator<Long2ByteMap.Entry>
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
    
    private final class MapEntrySet extends AbstractObjectSet<Long2ByteMap.Entry> implements Long2ByteMap.FastEntrySet
    {
        @Override
        public ObjectIterator<Long2ByteMap.Entry> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public ObjectIterator<Long2ByteMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            final long k = (long)e.getKey();
            final byte v = (byte)e.getValue();
            if (Long2ByteOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                return Long2ByteOpenCustomHashMap.this.containsNullKey && Long2ByteOpenCustomHashMap.this.value[Long2ByteOpenCustomHashMap.this.n] == v;
            }
            final long[] key = Long2ByteOpenCustomHashMap.this.key;
            int pos;
            long curr;
            if ((curr = key[pos = (HashCommon.mix(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask)]) == 0L) {
                return false;
            }
            if (Long2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Long2ByteOpenCustomHashMap.this.value[pos] == v;
            }
            while ((curr = key[pos = (pos + 1 & Long2ByteOpenCustomHashMap.this.mask)]) != 0L) {
                if (Long2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                    return Long2ByteOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            final long k = (long)e.getKey();
            final byte v = (byte)e.getValue();
            if (Long2ByteOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                if (Long2ByteOpenCustomHashMap.this.containsNullKey && Long2ByteOpenCustomHashMap.this.value[Long2ByteOpenCustomHashMap.this.n] == v) {
                    Long2ByteOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            else {
                final long[] key = Long2ByteOpenCustomHashMap.this.key;
                int pos;
                long curr;
                if ((curr = key[pos = (HashCommon.mix(Long2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ByteOpenCustomHashMap.this.mask)]) == 0L) {
                    return false;
                }
                if (!Long2ByteOpenCustomHashMap.this.strategy.equals(curr, k)) {
                    while ((curr = key[pos = (pos + 1 & Long2ByteOpenCustomHashMap.this.mask)]) != 0L) {
                        if (Long2ByteOpenCustomHashMap.this.strategy.equals(curr, k) && Long2ByteOpenCustomHashMap.this.value[pos] == v) {
                            Long2ByteOpenCustomHashMap.this.removeEntry(pos);
                            return true;
                        }
                    }
                    return false;
                }
                if (Long2ByteOpenCustomHashMap.this.value[pos] == v) {
                    Long2ByteOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
        }
        
        @Override
        public int size() {
            return Long2ByteOpenCustomHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Long2ByteOpenCustomHashMap.this.clear();
        }
    }
    
    private final class KeyIterator extends MapIterator implements LongIterator
    {
        public KeyIterator() {
        }
        
        @Override
        public long nextLong() {
            return Long2ByteOpenCustomHashMap.this.key[this.nextEntry()];
        }
        
        @Override
        public Long next() {
            return Long2ByteOpenCustomHashMap.this.key[this.nextEntry()];
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
            return Long2ByteOpenCustomHashMap.this.size;
        }
        
        @Override
        public boolean contains(final long k) {
            return Long2ByteOpenCustomHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean rem(final long k) {
            final int oldSize = Long2ByteOpenCustomHashMap.this.size;
            Long2ByteOpenCustomHashMap.this.remove(k);
            return Long2ByteOpenCustomHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Long2ByteOpenCustomHashMap.this.clear();
        }
    }
    
    private final class ValueIterator extends MapIterator implements ByteIterator
    {
        public ValueIterator() {
        }
        
        @Override
        public byte nextByte() {
            return Long2ByteOpenCustomHashMap.this.value[this.nextEntry()];
        }
        
        @Deprecated
        @Override
        public Byte next() {
            return Long2ByteOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }
}
