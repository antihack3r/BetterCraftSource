// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.floats;

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

public class Float2FloatOpenCustomHashMap extends AbstractFloat2FloatMap implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient float[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected FloatHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Float2FloatMap.FastEntrySet entries;
    protected transient FloatSet keys;
    protected transient FloatCollection values;
    
    public Float2FloatOpenCustomHashMap(final int expected, final float f, final FloatHash.Strategy strategy) {
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
        this.key = new float[this.n + 1];
        this.value = new float[this.n + 1];
    }
    
    public Float2FloatOpenCustomHashMap(final int expected, final FloatHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }
    
    public Float2FloatOpenCustomHashMap(final FloatHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }
    
    public Float2FloatOpenCustomHashMap(final Map<? extends Float, ? extends Float> m, final float f, final FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }
    
    public Float2FloatOpenCustomHashMap(final Map<? extends Float, ? extends Float> m, final FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Float2FloatOpenCustomHashMap(final Float2FloatMap m, final float f, final FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }
    
    public Float2FloatOpenCustomHashMap(final Float2FloatMap m, final FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }
    
    public Float2FloatOpenCustomHashMap(final float[] k, final float[] v, final float f, final FloatHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Float2FloatOpenCustomHashMap(final float[] k, final float[] v, final FloatHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }
    
    public FloatHash.Strategy strategy() {
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
    
    private float removeEntry(final int pos) {
        final float oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    private float removeNullEntry() {
        this.containsNullKey = false;
        final float oldValue = this.value[this.n];
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends Float, ? extends Float> m) {
        if (this.f <= 0.5) {
            this.ensureCapacity(m.size());
        }
        else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }
    
    private int insert(final float k, final float v) {
        int pos;
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final float[] key = this.key;
            float curr;
            if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) != 0) {
                if (this.strategy.equals(curr, k)) {
                    return pos;
                }
                while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
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
    public float put(final float k, final float v) {
        final int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    @Deprecated
    @Override
    public Float put(final Float ok, final Float ov) {
        final float v = ov;
        final int pos = this.insert(ok, v);
        if (pos < 0) {
            return null;
        }
        final float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    private float addToValue(final int pos, final float incr) {
        final float oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }
    
    public float addTo(final float k, final float incr) {
        int pos;
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        }
        else {
            final float[] key = this.key;
            float curr;
            if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) != 0) {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                    if (this.strategy.equals(curr, k)) {
                        return this.addToValue(pos, incr);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }
    
    protected final void shiftKeys(int pos) {
        final float[] key = this.key;
        int last = 0;
    Label_0006:
        while (true) {
            pos = ((last = pos) + 1 & this.mask);
            float curr;
            while (Float.floatToIntBits(curr = key[pos]) != 0) {
                final int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
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
                    pos = (pos + 1 & this.mask);
                    continue;
                }
                key[last] = curr;
                this.value[last] = this.value[pos];
                continue Label_0006;
            }
            break;
        }
        key[last] = 0.0f;
    }
    
    @Override
    public float remove(final float k) {
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        else {
            final float[] key = this.key;
            int pos;
            float curr;
            if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0) {
                return this.defRetValue;
            }
            if (this.strategy.equals(k, curr)) {
                return this.removeEntry(pos);
            }
            while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                if (this.strategy.equals(k, curr)) {
                    return this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
    }
    
    @Deprecated
    @Override
    public Float remove(final Object ok) {
        final float k = (float)ok;
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        else {
            final float[] key = this.key;
            int pos;
            float curr;
            if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0) {
                return null;
            }
            if (this.strategy.equals(curr, k)) {
                return this.removeEntry(pos);
            }
            while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
                if (this.strategy.equals(curr, k)) {
                    return this.removeEntry(pos);
                }
            }
            return null;
        }
    }
    
    @Deprecated
    public Float get(final Float ok) {
        if (ok == null) {
            return null;
        }
        final float k = ok;
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey ? Float.valueOf(this.value[this.n]) : null;
        }
        final float[] key = this.key;
        int pos;
        float curr;
        if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0) {
            return null;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (this.strategy.equals(k, curr)) {
                return this.value[pos];
            }
        }
        return null;
    }
    
    @Override
    public float get(final float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        final float[] key = this.key;
        int pos;
        float curr;
        if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (this.strategy.equals(k, curr)) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public boolean containsKey(final float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey;
        }
        final float[] key = this.key;
        int pos;
        float curr;
        if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask)]) == 0) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        while (Float.floatToIntBits(curr = key[pos = (pos + 1 & this.mask)]) != 0) {
            if (this.strategy.equals(k, curr)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final float v) {
        final float[] value = this.value;
        final float[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != 0 && value[i] == v) {
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
        Arrays.fill(this.key, 0.0f);
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
    public Float2FloatMap.FastEntrySet float2FloatEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }
    
    @Override
    public FloatSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }
    
    @Override
    public FloatCollection values() {
        if (this.values == null) {
            this.values = new AbstractFloatCollection() {
                @Override
                public FloatIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Float2FloatOpenCustomHashMap.this.size;
                }
                
                @Override
                public boolean contains(final float v) {
                    return Float2FloatOpenCustomHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Float2FloatOpenCustomHashMap.this.clear();
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
        final float[] key = this.key;
        final float[] value = this.value;
        final int mask = newN - 1;
        final float[] newKey = new float[newN + 1];
        final float[] newValue = new float[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Float.floatToIntBits(key[--i]) == 0) {}
            int pos;
            if (Float.floatToIntBits(newKey[pos = (HashCommon.mix(this.strategy.hashCode(key[i])) & mask)]) != 0) {
                while (Float.floatToIntBits(newKey[pos = (pos + 1 & mask)]) != 0) {}
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
    
    public Float2FloatOpenCustomHashMap clone() {
        Float2FloatOpenCustomHashMap c;
        try {
            c = (Float2FloatOpenCustomHashMap)super.clone();
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
            while (Float.floatToIntBits(this.key[i]) == 0) {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            t ^= HashCommon.float2int(this.value[i]);
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.float2int(this.value[this.n]);
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final float[] key = this.key;
        final float[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeFloat(key[e]);
            s.writeFloat(value[e]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final float[] key2 = new float[this.n + 1];
        this.key = key2;
        final float[] key = key2;
        final float[] value2 = new float[this.n + 1];
        this.value = value2;
        final float[] value = value2;
        int i = this.size;
        while (i-- != 0) {
            final float k = s.readFloat();
            final float v = s.readFloat();
            int pos;
            if (this.strategy.equals(k, 0.0f)) {
                pos = this.n;
                this.containsNullKey = true;
            }
            else {
                for (pos = (HashCommon.mix(this.strategy.hashCode(k)) & this.mask); Float.floatToIntBits(key[pos]) != 0; pos = (pos + 1 & this.mask)) {}
            }
            key[pos] = k;
            value[pos] = v;
        }
    }
    
    private void checkTable() {
    }
    
    final class MapEntry implements Float2FloatMap.Entry, Map.Entry<Float, Float>
    {
        int index;
        
        MapEntry(final int index) {
            this.index = index;
        }
        
        MapEntry() {
        }
        
        @Deprecated
        @Override
        public Float getKey() {
            return Float2FloatOpenCustomHashMap.this.key[this.index];
        }
        
        @Override
        public float getFloatKey() {
            return Float2FloatOpenCustomHashMap.this.key[this.index];
        }
        
        @Deprecated
        @Override
        public Float getValue() {
            return Float2FloatOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public float getFloatValue() {
            return Float2FloatOpenCustomHashMap.this.value[this.index];
        }
        
        @Override
        public float setValue(final float v) {
            final float oldValue = Float2FloatOpenCustomHashMap.this.value[this.index];
            Float2FloatOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public Float setValue(final Float v) {
            return this.setValue((float)v);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<Float, Float> e = (Map.Entry<Float, Float>)o;
            return Float2FloatOpenCustomHashMap.this.strategy.equals(Float2FloatOpenCustomHashMap.this.key[this.index], e.getKey()) && Float2FloatOpenCustomHashMap.this.value[this.index] == e.getValue();
        }
        
        @Override
        public int hashCode() {
            return Float2FloatOpenCustomHashMap.this.strategy.hashCode(Float2FloatOpenCustomHashMap.this.key[this.index]) ^ HashCommon.float2int(Float2FloatOpenCustomHashMap.this.value[this.index]);
        }
        
        @Override
        public String toString() {
            return Float2FloatOpenCustomHashMap.this.key[this.index] + "=>" + Float2FloatOpenCustomHashMap.this.value[this.index];
        }
    }
    
    private class MapIterator
    {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        FloatArrayList wrapped;
        
        private MapIterator() {
            this.pos = Float2FloatOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Float2FloatOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Float2FloatOpenCustomHashMap.this.containsNullKey;
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
                return this.last = Float2FloatOpenCustomHashMap.this.n;
            }
            final float[] key = Float2FloatOpenCustomHashMap.this.key;
            while (--this.pos >= 0) {
                if (Float.floatToIntBits(key[this.pos]) != 0) {
                    return this.last = this.pos;
                }
            }
            this.last = Integer.MIN_VALUE;
            float k;
            int p;
            for (k = this.wrapped.getFloat(-this.pos - 1), p = (HashCommon.mix(Float2FloatOpenCustomHashMap.this.strategy.hashCode(k)) & Float2FloatOpenCustomHashMap.this.mask); !Float2FloatOpenCustomHashMap.this.strategy.equals(k, key[p]); p = (p + 1 & Float2FloatOpenCustomHashMap.this.mask)) {}
            return p;
        }
        
        private final void shiftKeys(int pos) {
            final float[] key = Float2FloatOpenCustomHashMap.this.key;
            int last = 0;
        Label_0009:
            while (true) {
                pos = ((last = pos) + 1 & Float2FloatOpenCustomHashMap.this.mask);
                float curr;
                while (Float.floatToIntBits(curr = key[pos]) != 0) {
                    final int slot = HashCommon.mix(Float2FloatOpenCustomHashMap.this.strategy.hashCode(curr)) & Float2FloatOpenCustomHashMap.this.mask;
                    Label_0114: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0114;
                            }
                            if (slot > pos) {
                                break Label_0114;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0114;
                        }
                        pos = (pos + 1 & Float2FloatOpenCustomHashMap.this.mask);
                        continue;
                    }
                    if (pos < last) {
                        if (this.wrapped == null) {
                            this.wrapped = new FloatArrayList(2);
                        }
                        this.wrapped.add(key[pos]);
                    }
                    key[last] = curr;
                    Float2FloatOpenCustomHashMap.this.value[last] = Float2FloatOpenCustomHashMap.this.value[pos];
                    continue Label_0009;
                }
                break;
            }
            key[last] = 0.0f;
        }
        
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Float2FloatOpenCustomHashMap.this.n) {
                Float2FloatOpenCustomHashMap.this.containsNullKey = false;
            }
            else {
                if (this.pos < 0) {
                    Float2FloatOpenCustomHashMap.this.remove(this.wrapped.getFloat(-this.pos - 1));
                    this.last = -1;
                    return;
                }
                this.shiftKeys(this.last);
            }
            final Float2FloatOpenCustomHashMap this$0 = Float2FloatOpenCustomHashMap.this;
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
    
    private class EntryIterator extends MapIterator implements ObjectIterator<Float2FloatMap.Entry>
    {
        private MapEntry entry;
        
        @Override
        public Float2FloatMap.Entry next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectIterator<Float2FloatMap.Entry>
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
    
    private final class MapEntrySet extends AbstractObjectSet<Float2FloatMap.Entry> implements Float2FloatMap.FastEntrySet
    {
        @Override
        public ObjectIterator<Float2FloatMap.Entry> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public ObjectIterator<Float2FloatMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            final float k = (float)e.getKey();
            final float v = (float)e.getValue();
            if (Float2FloatOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                return Float2FloatOpenCustomHashMap.this.containsNullKey && Float2FloatOpenCustomHashMap.this.value[Float2FloatOpenCustomHashMap.this.n] == v;
            }
            final float[] key = Float2FloatOpenCustomHashMap.this.key;
            int pos;
            float curr;
            if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(Float2FloatOpenCustomHashMap.this.strategy.hashCode(k)) & Float2FloatOpenCustomHashMap.this.mask)]) == 0) {
                return false;
            }
            if (Float2FloatOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Float2FloatOpenCustomHashMap.this.value[pos] == v;
            }
            while (Float.floatToIntBits(curr = key[pos = (pos + 1 & Float2FloatOpenCustomHashMap.this.mask)]) != 0) {
                if (Float2FloatOpenCustomHashMap.this.strategy.equals(k, curr)) {
                    return Float2FloatOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            final float k = (float)e.getKey();
            final float v = (float)e.getValue();
            if (Float2FloatOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                if (Float2FloatOpenCustomHashMap.this.containsNullKey && Float2FloatOpenCustomHashMap.this.value[Float2FloatOpenCustomHashMap.this.n] == v) {
                    Float2FloatOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            else {
                final float[] key = Float2FloatOpenCustomHashMap.this.key;
                int pos;
                float curr;
                if (Float.floatToIntBits(curr = key[pos = (HashCommon.mix(Float2FloatOpenCustomHashMap.this.strategy.hashCode(k)) & Float2FloatOpenCustomHashMap.this.mask)]) == 0) {
                    return false;
                }
                if (!Float2FloatOpenCustomHashMap.this.strategy.equals(curr, k)) {
                    while (Float.floatToIntBits(curr = key[pos = (pos + 1 & Float2FloatOpenCustomHashMap.this.mask)]) != 0) {
                        if (Float2FloatOpenCustomHashMap.this.strategy.equals(curr, k) && Float2FloatOpenCustomHashMap.this.value[pos] == v) {
                            Float2FloatOpenCustomHashMap.this.removeEntry(pos);
                            return true;
                        }
                    }
                    return false;
                }
                if (Float2FloatOpenCustomHashMap.this.value[pos] == v) {
                    Float2FloatOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
        }
        
        @Override
        public int size() {
            return Float2FloatOpenCustomHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Float2FloatOpenCustomHashMap.this.clear();
        }
    }
    
    private final class KeyIterator extends MapIterator implements FloatIterator
    {
        public KeyIterator() {
        }
        
        @Override
        public float nextFloat() {
            return Float2FloatOpenCustomHashMap.this.key[this.nextEntry()];
        }
        
        @Override
        public Float next() {
            return Float2FloatOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }
    
    private final class KeySet extends AbstractFloatSet
    {
        @Override
        public FloatIterator iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return Float2FloatOpenCustomHashMap.this.size;
        }
        
        @Override
        public boolean contains(final float k) {
            return Float2FloatOpenCustomHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean rem(final float k) {
            final int oldSize = Float2FloatOpenCustomHashMap.this.size;
            Float2FloatOpenCustomHashMap.this.remove(k);
            return Float2FloatOpenCustomHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Float2FloatOpenCustomHashMap.this.clear();
        }
    }
    
    private final class ValueIterator extends MapIterator implements FloatIterator
    {
        public ValueIterator() {
        }
        
        @Override
        public float nextFloat() {
            return Float2FloatOpenCustomHashMap.this.value[this.nextEntry()];
        }
        
        @Deprecated
        @Override
        public Float next() {
            return Float2FloatOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }
}