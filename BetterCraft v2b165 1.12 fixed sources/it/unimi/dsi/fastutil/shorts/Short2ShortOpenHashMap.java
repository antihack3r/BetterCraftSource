// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.shorts;

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
import java.util.Arrays;
import java.util.Map;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class Short2ShortOpenHashMap extends AbstractShort2ShortMap implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient short[] key;
    protected transient short[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Short2ShortMap.FastEntrySet entries;
    protected transient ShortSet keys;
    protected transient ShortCollection values;
    
    public Short2ShortOpenHashMap(final int expected, final float f) {
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
        this.key = new short[this.n + 1];
        this.value = new short[this.n + 1];
    }
    
    public Short2ShortOpenHashMap(final int expected) {
        this(expected, 0.75f);
    }
    
    public Short2ShortOpenHashMap() {
        this(16, 0.75f);
    }
    
    public Short2ShortOpenHashMap(final Map<? extends Short, ? extends Short> m, final float f) {
        this(m.size(), f);
        this.putAll(m);
    }
    
    public Short2ShortOpenHashMap(final Map<? extends Short, ? extends Short> m) {
        this(m, 0.75f);
    }
    
    public Short2ShortOpenHashMap(final Short2ShortMap m, final float f) {
        this(m.size(), f);
        this.putAll(m);
    }
    
    public Short2ShortOpenHashMap(final Short2ShortMap m) {
        this(m, 0.75f);
    }
    
    public Short2ShortOpenHashMap(final short[] k, final short[] v, final float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Short2ShortOpenHashMap(final short[] k, final short[] v) {
        this(k, v, 0.75f);
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
    
    private short removeEntry(final int pos) {
        final short oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    private short removeNullEntry() {
        this.containsNullKey = false;
        final short oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends Short, ? extends Short> m) {
        if (this.f <= 0.5) {
            this.ensureCapacity(m.size());
        }
        else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }
    
    private int insert(final short k, final short v) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final short[] key = this.key;
            short curr;
            if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) != 0) {
                if (curr == k) {
                    return pos;
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                    if (curr == k) {
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
    public short put(final short k, final short v) {
        final int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final short oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    @Deprecated
    @Override
    public Short put(final Short ok, final Short ov) {
        final short v = ov;
        final int pos = this.insert(ok, v);
        if (pos < 0) {
            return null;
        }
        final short oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    private short addToValue(final int pos, final short incr) {
        final short oldValue = this.value[pos];
        this.value[pos] = (short)(oldValue + incr);
        return oldValue;
    }
    
    public short addTo(final short k, final short incr) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        }
        else {
            final short[] key = this.key;
            short curr;
            if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) != 0) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                    if (curr == k) {
                        return this.addToValue(pos, incr);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = (short)(this.defRetValue + incr);
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }
    
    protected final void shiftKeys(int pos) {
        final short[] key = this.key;
        int last = 0;
    Label_0006:
        while (true) {
            pos = ((last = pos) + 1 & this.mask);
            short curr;
            while ((curr = key[pos]) != 0) {
                final int slot = HashCommon.mix(curr) & this.mask;
                Label_0087: {
                    if (last <= pos) {
                        if (last >= slot) {
                            break Label_0087;
                        }
                        if (slot > pos) {
                            break Label_0087;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break Label_0087;
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
        key[last] = 0;
    }
    
    @Override
    public short remove(final short k) {
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        else {
            final short[] key = this.key;
            int pos;
            short curr;
            if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) == 0) {
                return this.defRetValue;
            }
            if (k == curr) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                if (k == curr) {
                    return this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
    }
    
    @Deprecated
    @Override
    public Short remove(final Object ok) {
        final short k = (short)ok;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        else {
            final short[] key = this.key;
            int pos;
            short curr;
            if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) == 0) {
                return null;
            }
            if (curr == k) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                if (curr == k) {
                    return this.removeEntry(pos);
                }
            }
            return null;
        }
    }
    
    @Deprecated
    public Short get(final Short ok) {
        if (ok == null) {
            return null;
        }
        final short k = ok;
        if (k == 0) {
            return this.containsNullKey ? Short.valueOf(this.value[this.n]) : null;
        }
        final short[] key = this.key;
        int pos;
        short curr;
        if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) == 0) {
            return null;
        }
        if (k == curr) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (k == curr) {
                return this.value[pos];
            }
        }
        return null;
    }
    
    @Override
    public short get(final short k) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        final short[] key = this.key;
        int pos;
        short curr;
        if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (k == curr) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public boolean containsKey(final short k) {
        if (k == 0) {
            return this.containsNullKey;
        }
        final short[] key = this.key;
        int pos;
        short curr;
        if ((curr = key[pos = (HashCommon.mix(k) & this.mask)]) == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (k == curr) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final short v) {
        final short[] value = this.value;
        final short[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] != 0 && value[i] == v) {
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
        Arrays.fill(this.key, (short)0);
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
    public Short2ShortMap.FastEntrySet short2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }
    
    @Override
    public ShortSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }
    
    @Override
    public ShortCollection values() {
        if (this.values == null) {
            this.values = new AbstractShortCollection() {
                @Override
                public ShortIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Short2ShortOpenHashMap.this.size;
                }
                
                @Override
                public boolean contains(final short v) {
                    return Short2ShortOpenHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Short2ShortOpenHashMap.this.clear();
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
        final short[] key = this.key;
        final short[] value = this.value;
        final int mask = newN - 1;
        final short[] newKey = new short[newN + 1];
        final short[] newValue = new short[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {}
            int pos;
            if (newKey[pos = (HashCommon.mix(key[i]) & mask)] != 0) {
                while (newKey[pos = (pos + 1 & mask)] != 0) {}
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
    
    public Short2ShortOpenHashMap clone() {
        Short2ShortOpenHashMap c;
        try {
            c = (Short2ShortOpenHashMap)super.clone();
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
        return c;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            t = this.key[i];
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
        final short[] key = this.key;
        final short[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeShort(key[e]);
            s.writeShort(value[e]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final short[] key2 = new short[this.n + 1];
        this.key = key2;
        final short[] key = key2;
        final short[] value2 = new short[this.n + 1];
        this.value = value2;
        final short[] value = value2;
        int i = this.size;
        while (i-- != 0) {
            final short k = s.readShort();
            final short v = s.readShort();
            int pos;
            if (k == 0) {
                pos = this.n;
                this.containsNullKey = true;
            }
            else {
                for (pos = (HashCommon.mix(k) & this.mask); key[pos] != 0; pos = (pos + 1 & this.mask)) {}
            }
            key[pos] = k;
            value[pos] = v;
        }
    }
    
    private void checkTable() {
    }
    
    final class MapEntry implements Short2ShortMap.Entry, Map.Entry<Short, Short>
    {
        int index;
        
        MapEntry(final int index) {
            this.index = index;
        }
        
        MapEntry() {
        }
        
        @Deprecated
        @Override
        public Short getKey() {
            return Short2ShortOpenHashMap.this.key[this.index];
        }
        
        @Override
        public short getShortKey() {
            return Short2ShortOpenHashMap.this.key[this.index];
        }
        
        @Deprecated
        @Override
        public Short getValue() {
            return Short2ShortOpenHashMap.this.value[this.index];
        }
        
        @Override
        public short getShortValue() {
            return Short2ShortOpenHashMap.this.value[this.index];
        }
        
        @Override
        public short setValue(final short v) {
            final short oldValue = Short2ShortOpenHashMap.this.value[this.index];
            Short2ShortOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public Short setValue(final Short v) {
            return this.setValue((short)v);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<Short, Short> e = (Map.Entry<Short, Short>)o;
            return Short2ShortOpenHashMap.this.key[this.index] == e.getKey() && Short2ShortOpenHashMap.this.value[this.index] == e.getValue();
        }
        
        @Override
        public int hashCode() {
            return Short2ShortOpenHashMap.this.key[this.index] ^ Short2ShortOpenHashMap.this.value[this.index];
        }
        
        @Override
        public String toString() {
            return Short2ShortOpenHashMap.this.key[this.index] + "=>" + Short2ShortOpenHashMap.this.value[this.index];
        }
    }
    
    private class MapIterator
    {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ShortArrayList wrapped;
        
        private MapIterator() {
            this.pos = Short2ShortOpenHashMap.this.n;
            this.last = -1;
            this.c = Short2ShortOpenHashMap.this.size;
            this.mustReturnNullKey = Short2ShortOpenHashMap.this.containsNullKey;
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
                return this.last = Short2ShortOpenHashMap.this.n;
            }
            final short[] key = Short2ShortOpenHashMap.this.key;
            while (--this.pos >= 0) {
                if (key[this.pos] != 0) {
                    return this.last = this.pos;
                }
            }
            this.last = Integer.MIN_VALUE;
            short k;
            int p;
            for (k = this.wrapped.getShort(-this.pos - 1), p = (HashCommon.mix(k) & Short2ShortOpenHashMap.this.mask); k != key[p]; p = (p + 1 & Short2ShortOpenHashMap.this.mask)) {}
            return p;
        }
        
        private final void shiftKeys(int pos) {
            final short[] key = Short2ShortOpenHashMap.this.key;
            int last = 0;
        Label_0009:
            while (true) {
                pos = ((last = pos) + 1 & Short2ShortOpenHashMap.this.mask);
                short curr;
                while ((curr = key[pos]) != 0) {
                    final int slot = HashCommon.mix(curr) & Short2ShortOpenHashMap.this.mask;
                    Label_0099: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0099;
                            }
                            if (slot > pos) {
                                break Label_0099;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0099;
                        }
                        pos = (pos + 1 & Short2ShortOpenHashMap.this.mask);
                        continue;
                    }
                    if (pos < last) {
                        if (this.wrapped == null) {
                            this.wrapped = new ShortArrayList(2);
                        }
                        this.wrapped.add(key[pos]);
                    }
                    key[last] = curr;
                    Short2ShortOpenHashMap.this.value[last] = Short2ShortOpenHashMap.this.value[pos];
                    continue Label_0009;
                }
                break;
            }
            key[last] = 0;
        }
        
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Short2ShortOpenHashMap.this.n) {
                Short2ShortOpenHashMap.this.containsNullKey = false;
            }
            else {
                if (this.pos < 0) {
                    Short2ShortOpenHashMap.this.remove(this.wrapped.getShort(-this.pos - 1));
                    this.last = -1;
                    return;
                }
                this.shiftKeys(this.last);
            }
            final Short2ShortOpenHashMap this$0 = Short2ShortOpenHashMap.this;
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
    
    private class EntryIterator extends MapIterator implements ObjectIterator<Short2ShortMap.Entry>
    {
        private MapEntry entry;
        
        @Override
        public Short2ShortMap.Entry next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectIterator<Short2ShortMap.Entry>
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
    
    private final class MapEntrySet extends AbstractObjectSet<Short2ShortMap.Entry> implements Short2ShortMap.FastEntrySet
    {
        @Override
        public ObjectIterator<Short2ShortMap.Entry> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public ObjectIterator<Short2ShortMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            final short k = (short)e.getKey();
            final short v = (short)e.getValue();
            if (k == 0) {
                return Short2ShortOpenHashMap.this.containsNullKey && Short2ShortOpenHashMap.this.value[Short2ShortOpenHashMap.this.n] == v;
            }
            final short[] key = Short2ShortOpenHashMap.this.key;
            int pos;
            short curr;
            if ((curr = key[pos = (HashCommon.mix(k) & Short2ShortOpenHashMap.this.mask)]) == 0) {
                return false;
            }
            if (k == curr) {
                return Short2ShortOpenHashMap.this.value[pos] == v;
            }
            while ((curr = key[pos = (pos + 1 & Short2ShortOpenHashMap.this.mask)]) != 0) {
                if (k == curr) {
                    return Short2ShortOpenHashMap.this.value[pos] == v;
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            final short k = (short)e.getKey();
            final short v = (short)e.getValue();
            if (k == 0) {
                if (Short2ShortOpenHashMap.this.containsNullKey && Short2ShortOpenHashMap.this.value[Short2ShortOpenHashMap.this.n] == v) {
                    Short2ShortOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            else {
                final short[] key = Short2ShortOpenHashMap.this.key;
                int pos;
                short curr;
                if ((curr = key[pos = (HashCommon.mix(k) & Short2ShortOpenHashMap.this.mask)]) == 0) {
                    return false;
                }
                if (curr != k) {
                    while ((curr = key[pos = (pos + 1 & Short2ShortOpenHashMap.this.mask)]) != 0) {
                        if (curr == k && Short2ShortOpenHashMap.this.value[pos] == v) {
                            Short2ShortOpenHashMap.this.removeEntry(pos);
                            return true;
                        }
                    }
                    return false;
                }
                if (Short2ShortOpenHashMap.this.value[pos] == v) {
                    Short2ShortOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
        }
        
        @Override
        public int size() {
            return Short2ShortOpenHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Short2ShortOpenHashMap.this.clear();
        }
    }
    
    private final class KeyIterator extends MapIterator implements ShortIterator
    {
        public KeyIterator() {
        }
        
        @Override
        public short nextShort() {
            return Short2ShortOpenHashMap.this.key[this.nextEntry()];
        }
        
        @Override
        public Short next() {
            return Short2ShortOpenHashMap.this.key[this.nextEntry()];
        }
    }
    
    private final class KeySet extends AbstractShortSet
    {
        @Override
        public ShortIterator iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return Short2ShortOpenHashMap.this.size;
        }
        
        @Override
        public boolean contains(final short k) {
            return Short2ShortOpenHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean rem(final short k) {
            final int oldSize = Short2ShortOpenHashMap.this.size;
            Short2ShortOpenHashMap.this.remove(k);
            return Short2ShortOpenHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Short2ShortOpenHashMap.this.clear();
        }
    }
    
    private final class ValueIterator extends MapIterator implements ShortIterator
    {
        public ValueIterator() {
        }
        
        @Override
        public short nextShort() {
            return Short2ShortOpenHashMap.this.value[this.nextEntry()];
        }
        
        @Deprecated
        @Override
        public Short next() {
            return Short2ShortOpenHashMap.this.value[this.nextEntry()];
        }
    }
}
