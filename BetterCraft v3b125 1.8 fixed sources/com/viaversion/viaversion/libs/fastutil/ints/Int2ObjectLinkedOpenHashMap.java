/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.Hash;
import com.viaversion.viaversion.libs.fastutil.HashCommon;
import com.viaversion.viaversion.libs.fastutil.Size64;
import com.viaversion.viaversion.libs.fastutil.ints.AbstractInt2ObjectSortedMap;
import com.viaversion.viaversion.libs.fastutil.ints.AbstractIntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectSortedMap;
import com.viaversion.viaversion.libs.fastutil.ints.IntComparator;
import com.viaversion.viaversion.libs.fastutil.ints.IntListIterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntObjectPair;
import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterator;
import com.viaversion.viaversion.libs.fastutil.ints.IntSpliterators;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObjectSortedSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectCollection;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectListIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class Int2ObjectLinkedOpenHashMap<V>
extends AbstractInt2ObjectSortedMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient int[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first = -1;
    protected transient int last = -1;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Int2ObjectSortedMap.FastSortedEntrySet<V> entries;
    protected transient IntSortedSet keys;
    protected transient ObjectCollection<V> values;

    public Int2ObjectLinkedOpenHashMap(int expected, float f2) {
        if (f2 <= 0.0f || f2 >= 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f2;
        this.minN = this.n = HashCommon.arraySize(expected, f2);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f2);
        this.key = new int[this.n + 1];
        this.value = new Object[this.n + 1];
        this.link = new long[this.n + 1];
    }

    public Int2ObjectLinkedOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Int2ObjectLinkedOpenHashMap() {
        this(16, 0.75f);
    }

    public Int2ObjectLinkedOpenHashMap(Map<? extends Integer, ? extends V> m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Int2ObjectLinkedOpenHashMap(Map<? extends Integer, ? extends V> m2) {
        this(m2, 0.75f);
    }

    public Int2ObjectLinkedOpenHashMap(Int2ObjectMap<V> m2, float f2) {
        this(m2.size(), f2);
        this.putAll(m2);
    }

    public Int2ObjectLinkedOpenHashMap(Int2ObjectMap<V> m2) {
        this(m2, 0.75f);
    }

    public Int2ObjectLinkedOpenHashMap(int[] k2, V[] v2, float f2) {
        this(k2.length, f2);
        if (k2.length != v2.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k2.length + " and " + v2.length + ")");
        }
        for (int i2 = 0; i2 < k2.length; ++i2) {
            this.put(k2[i2], v2[i2]);
        }
    }

    public Int2ObjectLinkedOpenHashMap(int[] k2, V[] v2) {
        this(k2, v2, 0.75f);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    public void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        this.fixPointers(this.n);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m2) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m2.size());
        } else {
            this.tryCapacity(this.size() + m2.size());
        }
        super.putAll(m2);
    }

    private int find(int k2) {
        if (k2 == 0) {
            return this.containsNullKey ? this.n : -(this.n + 1);
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return -(pos + 1);
        }
        if (k2 == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return -(pos + 1);
        } while (k2 != curr);
        return pos;
    }

    private void insert(int pos, int k2, V v2) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.last;
            this.link[n2] = this.link[n2] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public V put(int k2, V v2) {
        int pos = this.find(k2);
        if (pos < 0) {
            this.insert(-pos - 1, k2, v2);
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        int[] key = this.key;
        while (true) {
            int curr;
            int last = pos;
            pos = last + 1 & this.mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    this.value[last] = null;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            }
            key[last] = curr;
            this.value[last] = this.value[pos];
            this.fixPointers(pos, last);
        }
    }

    @Override
    public V remove(int k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k2 == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k2 != curr);
        return this.removeEntry(pos);
    }

    private V setValue(int pos, V v2) {
        V oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    public V removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.first;
        if (this.size == 1) {
            this.last = -1;
            this.first = -1;
        } else {
            this.first = (int)this.link[pos];
            if (0 <= this.first) {
                int n2 = this.first;
                this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
            }
        }
        --this.size;
        V v2 = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v2;
    }

    public V removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int pos = this.last;
        if (this.size == 1) {
            this.last = -1;
            this.first = -1;
        } else {
            this.last = (int)(this.link[pos] >>> 32);
            if (0 <= this.last) {
                int n2 = this.last;
                this.link[n2] = this.link[n2] | 0xFFFFFFFFL;
            }
        }
        --this.size;
        V v2 = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
        } else {
            this.shiftKeys(pos);
        }
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v2;
    }

    private void moveIndexToFirst(int i2) {
        if (this.size == 1 || this.first == i2) {
            return;
        }
        if (this.last == i2) {
            int n2 = this.last = (int)(this.link[i2] >>> 32);
            this.link[n2] = this.link[n2] | 0xFFFFFFFFL;
        } else {
            long linki = this.link[i2];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n3 = prev;
            this.link[n3] = this.link[n3] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n4 = next;
            this.link[n4] = this.link[n4] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n5 = this.first;
        this.link[n5] = this.link[n5] ^ (this.link[this.first] ^ ((long)i2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[i2] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
        this.first = i2;
    }

    private void moveIndexToLast(int i2) {
        if (this.size == 1 || this.last == i2) {
            return;
        }
        if (this.first == i2) {
            int n2 = this.first = (int)this.link[i2];
            this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
        } else {
            long linki = this.link[i2];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            int n3 = prev;
            this.link[n3] = this.link[n3] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            int n4 = next;
            this.link[n4] = this.link[n4] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
        }
        int n5 = this.last;
        this.link[n5] = this.link[n5] ^ (this.link[this.last] ^ (long)i2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        this.link[i2] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
        this.last = i2;
    }

    public V getAndMoveToFirst(int k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k2 == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToFirst(pos);
        return this.value[pos];
    }

    public V getAndMoveToLast(int k2) {
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return (V)this.defRetValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k2 == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k2 != curr);
        this.moveIndexToLast(pos);
        return this.value[pos];
    }

    public V putAndMoveToFirst(int k2, V v2) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            int[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            int curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v2);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.first;
            this.link[n2] = this.link[n2] ^ (this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[pos] = 0xFFFFFFFF00000000L | (long)this.first & 0xFFFFFFFFL;
            this.first = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return (V)this.defRetValue;
    }

    public V putAndMoveToLast(int k2, V v2) {
        int pos;
        if (k2 == 0) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v2);
            }
            this.containsNullKey = true;
            pos = this.n;
        } else {
            int[] key = this.key;
            pos = HashCommon.mix(k2) & this.mask;
            int curr = key[pos];
            if (curr != 0) {
                if (curr == k2) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v2);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k2) continue;
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v2);
                }
            }
        }
        this.key[pos] = k2;
        this.value[pos] = v2;
        if (this.size == 0) {
            this.first = this.last = pos;
            this.link[pos] = -1L;
        } else {
            int n2 = this.last;
            this.link[n2] = this.link[n2] ^ (this.link[this.last] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[pos] = ((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL;
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return (V)this.defRetValue;
    }

    @Override
    public V get(int k2) {
        if (k2 == 0) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return (V)this.defRetValue;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(int k2) {
        if (k2 == 0) {
            return this.containsNullKey;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k2 == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k2 != curr);
        return true;
    }

    @Override
    public boolean containsValue(Object v2) {
        V[] value = this.value;
        int[] key = this.key;
        if (this.containsNullKey && Objects.equals(value[this.n], v2)) {
            return true;
        }
        int i2 = this.n;
        while (i2-- != 0) {
            if (key[i2] == 0 || !Objects.equals(value[i2], v2)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V getOrDefault(int k2, V defaultValue) {
        if (k2 == 0) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return defaultValue;
        }
        if (k2 == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (k2 != curr);
        return this.value[pos];
    }

    @Override
    public V putIfAbsent(int k2, V v2) {
        int pos = this.find(k2);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(-pos - 1, k2, v2);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(int k2, Object v2) {
        if (k2 == 0) {
            if (this.containsNullKey && Objects.equals(v2, this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        int[] key = this.key;
        int pos = HashCommon.mix(k2) & this.mask;
        int curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k2 == curr && Objects.equals(v2, this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k2 != curr || !Objects.equals(v2, this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(int k2, V oldValue, V v2) {
        int pos = this.find(k2);
        if (pos < 0 || !Objects.equals(oldValue, this.value[pos])) {
            return false;
        }
        this.value[pos] = v2;
        return true;
    }

    @Override
    public V replace(int k2, V v2) {
        int pos = this.find(k2);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v2;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(int k2, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k2);
        if (pos >= 0) {
            return this.value[pos];
        }
        V newValue = mappingFunction.apply(k2);
        this.insert(-pos - 1, k2, newValue);
        return newValue;
    }

    @Override
    public V computeIfAbsent(int key, Int2ObjectFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(key);
        if (pos >= 0) {
            return this.value[pos];
        }
        if (!mappingFunction.containsKey(key)) {
            return (V)this.defRetValue;
        }
        V newValue = mappingFunction.get(key);
        this.insert(-pos - 1, key, newValue);
        return newValue;
    }

    @Override
    public V computeIfPresent(int k2, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k2);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        if (this.value[pos] == null) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply(k2, this.value[pos]);
        if (newValue == null) {
            if (k2 == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public V compute(int k2, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k2);
        V newValue = remappingFunction.apply(k2, pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k2 == 0) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return (V)this.defRetValue;
        }
        V newVal = newValue;
        if (pos < 0) {
            this.insert(-pos - 1, k2, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public V merge(int k2, V v2, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(v2);
        int pos = this.find(k2);
        if (pos < 0 || this.value[pos] == null) {
            if (pos < 0) {
                this.insert(-pos - 1, k2, v2);
            } else {
                this.value[pos] = v2;
            }
            return v2;
        }
        V newValue = remappingFunction.apply(this.value[pos], v2);
        if (newValue == null) {
            if (k2 == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, 0);
        Arrays.fill(this.value, null);
        this.last = -1;
        this.first = -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    protected void fixPointers(int i2) {
        if (this.size == 0) {
            this.last = -1;
            this.first = -1;
            return;
        }
        if (this.first == i2) {
            this.first = (int)this.link[i2];
            if (0 <= this.first) {
                int n2 = this.first;
                this.link[n2] = this.link[n2] | 0xFFFFFFFF00000000L;
            }
            return;
        }
        if (this.last == i2) {
            this.last = (int)(this.link[i2] >>> 32);
            if (0 <= this.last) {
                int n3 = this.last;
                this.link[n3] = this.link[n3] | 0xFFFFFFFFL;
            }
            return;
        }
        long linki = this.link[i2];
        int prev = (int)(linki >>> 32);
        int next = (int)linki;
        int n4 = prev;
        this.link[n4] = this.link[n4] ^ (this.link[prev] ^ linki & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n5 = next;
        this.link[n5] = this.link[n5] ^ (this.link[next] ^ linki & 0xFFFFFFFF00000000L) & 0xFFFFFFFF00000000L;
    }

    protected void fixPointers(int s2, int d2) {
        if (this.size == 1) {
            this.first = this.last = d2;
            this.link[d2] = -1L;
            return;
        }
        if (this.first == s2) {
            this.first = d2;
            int n2 = (int)this.link[s2];
            this.link[n2] = this.link[n2] ^ (this.link[(int)this.link[s2]] ^ ((long)d2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            this.link[d2] = this.link[s2];
            return;
        }
        if (this.last == s2) {
            this.last = d2;
            int n3 = (int)(this.link[s2] >>> 32);
            this.link[n3] = this.link[n3] ^ (this.link[(int)(this.link[s2] >>> 32)] ^ (long)d2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            this.link[d2] = this.link[s2];
            return;
        }
        long links = this.link[s2];
        int prev = (int)(links >>> 32);
        int next = (int)links;
        int n4 = prev;
        this.link[n4] = this.link[n4] ^ (this.link[prev] ^ (long)d2 & 0xFFFFFFFFL) & 0xFFFFFFFFL;
        int n5 = next;
        this.link[n5] = this.link[n5] ^ (this.link[next] ^ ((long)d2 & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
        this.link[d2] = links;
    }

    @Override
    public int firstIntKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }

    @Override
    public int lastIntKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }

    @Override
    public Int2ObjectSortedMap<V> tailMap(int from) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> headMap(int to2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Int2ObjectSortedMap<V> subMap(int from, int to2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IntComparator comparator() {
        return null;
    }

    @Override
    public Int2ObjectSortedMap.FastSortedEntrySet<V> int2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public IntSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){
                private static final int SPLITERATOR_CHARACTERISTICS = 80;

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public ObjectSpliterator<V> spliterator() {
                    return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2ObjectLinkedOpenHashMap.this), 80);
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    int i2 = Int2ObjectLinkedOpenHashMap.this.size;
                    int next = Int2ObjectLinkedOpenHashMap.this.first;
                    while (i2-- != 0) {
                        int curr = next;
                        next = (int)Int2ObjectLinkedOpenHashMap.this.link[curr];
                        consumer.accept(Int2ObjectLinkedOpenHashMap.this.value[curr]);
                    }
                }

                @Override
                public int size() {
                    return Int2ObjectLinkedOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v2) {
                    return Int2ObjectLinkedOpenHashMap.this.containsValue(v2);
                }

                @Override
                public void clear() {
                    Int2ObjectLinkedOpenHashMap.this.clear();
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        return this.trim(this.size);
    }

    public boolean trim(int n2) {
        int l2 = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n2 / this.f));
        if (l2 >= this.n || this.size > HashCommon.maxFill(l2, this.f)) {
            return true;
        }
        try {
            this.rehash(l2);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        int[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        int[] newKey = new int[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i2 = this.first;
        int prev = -1;
        int newPrev = -1;
        long[] link = this.link;
        long[] newLink = new long[newN + 1];
        this.first = -1;
        int j2 = this.size;
        while (j2-- != 0) {
            int pos;
            if (key[i2] == 0) {
                pos = newN;
            } else {
                pos = HashCommon.mix(key[i2]) & mask;
                while (newKey[pos] != 0) {
                    pos = pos + 1 & mask;
                }
            }
            newKey[pos] = key[i2];
            newValue[pos] = value[i2];
            if (prev != -1) {
                int n2 = newPrev;
                newLink[n2] = newLink[n2] ^ (newLink[newPrev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n3 = pos;
                newLink[n3] = newLink[n3] ^ (newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                newPrev = pos;
            } else {
                newPrev = this.first = pos;
                newLink[pos] = -1L;
            }
            int t2 = i2;
            i2 = (int)link[i2];
            prev = t2;
        }
        this.link = newLink;
        this.last = newPrev;
        if (newPrev != -1) {
            int n4 = newPrev;
            newLink[n4] = newLink[n4] | 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Int2ObjectLinkedOpenHashMap<V> clone() {
        Int2ObjectLinkedOpenHashMap c2;
        try {
            c2 = (Int2ObjectLinkedOpenHashMap)super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c2.keys = null;
        c2.values = null;
        c2.entries = null;
        c2.containsNullKey = this.containsNullKey;
        c2.key = (int[])this.key.clone();
        c2.value = (Object[])this.value.clone();
        c2.link = (long[])this.link.clone();
        return c2;
    }

    @Override
    public int hashCode() {
        int h2 = 0;
        int j2 = this.realSize();
        int i2 = 0;
        int t2 = 0;
        while (j2-- != 0) {
            while (this.key[i2] == 0) {
                ++i2;
            }
            t2 = this.key[i2];
            if (this != this.value[i2]) {
                t2 ^= this.value[i2] == null ? 0 : this.value[i2].hashCode();
            }
            h2 += t2;
            ++i2;
        }
        if (this.containsNullKey) {
            h2 += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
        }
        return h2;
    }

    private void writeObject(ObjectOutputStream s2) throws IOException {
        int[] key = this.key;
        V[] value = this.value;
        EntryIterator i2 = new EntryIterator();
        s2.defaultWriteObject();
        int j2 = this.size;
        while (j2-- != 0) {
            int e2 = i2.nextEntry();
            s2.writeInt(key[e2]);
            s2.writeObject(value[e2]);
        }
    }

    private void readObject(ObjectInputStream s2) throws IOException, ClassNotFoundException {
        s2.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new int[this.n + 1];
        int[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        this.link = new long[this.n + 1];
        long[] link = this.link;
        int prev = -1;
        this.last = -1;
        this.first = -1;
        int i2 = this.size;
        while (i2-- != 0) {
            int pos;
            int k2 = s2.readInt();
            Object v2 = s2.readObject();
            if (k2 == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k2) & this.mask;
                while (key[pos] != 0) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k2;
            value[pos] = v2;
            if (this.first != -1) {
                int n2 = prev;
                link[n2] = link[n2] ^ (link[prev] ^ (long)pos & 0xFFFFFFFFL) & 0xFFFFFFFFL;
                int n3 = pos;
                link[n3] = link[n3] ^ (link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
                prev = pos;
                continue;
            }
            prev = this.first = pos;
            int n4 = pos;
            link[n4] = link[n4] | 0xFFFFFFFF00000000L;
        }
        this.last = prev;
        if (prev != -1) {
            int n5 = prev;
            link[n5] = link[n5] | 0xFFFFFFFFL;
        }
    }

    private void checkTable() {
    }

    private final class MapEntrySet
    extends AbstractObjectSortedSet<Int2ObjectMap.Entry<V>>
    implements Int2ObjectSortedMap.FastSortedEntrySet<V> {
        private static final int SPLITERATOR_CHARACTERISTICS = 81;

        private MapEntrySet() {
        }

        @Override
        public ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectSpliterator<Int2ObjectMap.Entry<V>> spliterator() {
            return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2ObjectLinkedOpenHashMap.this), 81);
        }

        @Override
        public Comparator<? super Int2ObjectMap.Entry<V>> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Int2ObjectMap.Entry<V>> subSet(Int2ObjectMap.Entry<V> fromElement, Int2ObjectMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Int2ObjectMap.Entry<V>> headSet(Int2ObjectMap.Entry<V> toElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSortedSet<Int2ObjectMap.Entry<V>> tailSet(Int2ObjectMap.Entry<V> fromElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Int2ObjectMap.Entry<V> first() {
            if (Int2ObjectLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Int2ObjectLinkedOpenHashMap.this.first);
        }

        @Override
        public Int2ObjectMap.Entry<V> last() {
            if (Int2ObjectLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Int2ObjectLinkedOpenHashMap.this.last);
        }

        @Override
        public boolean contains(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Integer)) {
                return false;
            }
            int k2 = (Integer)e2.getKey();
            Object v2 = e2.getValue();
            if (k2 == 0) {
                return Int2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n], v2);
            }
            int[] key = Int2ObjectLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Int2ObjectLinkedOpenHashMap.this.mask;
            int curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k2 == curr) {
                return Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v2);
            }
            do {
                if ((curr = key[pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k2 != curr);
            return Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v2);
        }

        @Override
        public boolean remove(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            if (e2.getKey() == null || !(e2.getKey() instanceof Integer)) {
                return false;
            }
            int k2 = (Integer)e2.getKey();
            Object v2 = e2.getValue();
            if (k2 == 0) {
                if (Int2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n], v2)) {
                    Int2ObjectLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            int[] key = Int2ObjectLinkedOpenHashMap.this.key;
            int pos = HashCommon.mix(k2) & Int2ObjectLinkedOpenHashMap.this.mask;
            int curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k2) {
                if (Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v2)) {
                    Int2ObjectLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k2 || !Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[pos], v2));
            Int2ObjectLinkedOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Int2ObjectLinkedOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Int2ObjectLinkedOpenHashMap.this.clear();
        }

        @Override
        public ObjectListIterator<Int2ObjectMap.Entry<V>> iterator(Int2ObjectMap.Entry<V> from) {
            return new EntryIterator(from.getIntKey());
        }

        @Override
        public ObjectListIterator<Int2ObjectMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public ObjectListIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap.Entry<V> from) {
            return new FastEntryIterator(from.getIntKey());
        }

        @Override
        public void forEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
            int i2 = Int2ObjectLinkedOpenHashMap.this.size;
            int next = Int2ObjectLinkedOpenHashMap.this.first;
            while (i2-- != 0) {
                int curr = next;
                next = (int)Int2ObjectLinkedOpenHashMap.this.link[curr];
                consumer.accept(new MapEntry(curr));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
            MapEntry entry = new MapEntry();
            int i2 = Int2ObjectLinkedOpenHashMap.this.size;
            int next = Int2ObjectLinkedOpenHashMap.this.first;
            while (i2-- != 0) {
                entry.index = next;
                next = (int)Int2ObjectLinkedOpenHashMap.this.link[next];
                consumer.accept(entry);
            }
        }
    }

    private final class KeySet
    extends AbstractIntSortedSet {
        private static final int SPLITERATOR_CHARACTERISTICS = 337;

        private KeySet() {
        }

        @Override
        public IntListIterator iterator(int from) {
            return new KeyIterator(from);
        }

        @Override
        public IntListIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public IntSpliterator spliterator() {
            return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2ObjectLinkedOpenHashMap.this), 337);
        }

        @Override
        public void forEach(IntConsumer consumer) {
            int i2 = Int2ObjectLinkedOpenHashMap.this.size;
            int next = Int2ObjectLinkedOpenHashMap.this.first;
            while (i2-- != 0) {
                int curr = next;
                next = (int)Int2ObjectLinkedOpenHashMap.this.link[curr];
                consumer.accept(Int2ObjectLinkedOpenHashMap.this.key[curr]);
            }
        }

        @Override
        public int size() {
            return Int2ObjectLinkedOpenHashMap.this.size;
        }

        @Override
        public boolean contains(int k2) {
            return Int2ObjectLinkedOpenHashMap.this.containsKey(k2);
        }

        @Override
        public boolean remove(int k2) {
            int oldSize = Int2ObjectLinkedOpenHashMap.this.size;
            Int2ObjectLinkedOpenHashMap.this.remove(k2);
            return Int2ObjectLinkedOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Int2ObjectLinkedOpenHashMap.this.clear();
        }

        @Override
        public int firstInt() {
            if (Int2ObjectLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Int2ObjectLinkedOpenHashMap.this.key[Int2ObjectLinkedOpenHashMap.this.first];
        }

        @Override
        public int lastInt() {
            if (Int2ObjectLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Int2ObjectLinkedOpenHashMap.this.key[Int2ObjectLinkedOpenHashMap.this.last];
        }

        @Override
        public IntComparator comparator() {
            return null;
        }

        @Override
        public IntSortedSet tailSet(int from) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntSortedSet headSet(int to2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntSortedSet subSet(int from, int to2) {
            throw new UnsupportedOperationException();
        }
    }

    private final class EntryIterator
    extends MapIterator<Consumer<? super Int2ObjectMap.Entry<V>>>
    implements ObjectListIterator<Int2ObjectMap.Entry<V>> {
        private MapEntry entry;

        public EntryIterator() {
        }

        public EntryIterator(int from) {
            super(from);
        }

        @Override
        final void acceptOnIndex(Consumer<? super Int2ObjectMap.Entry<V>> action, int index) {
            action.accept(new MapEntry(index));
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry = new MapEntry(this.previousEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private final class ValueIterator
    extends MapIterator<Consumer<? super V>>
    implements ObjectListIterator<V> {
        @Override
        public V previous() {
            return Int2ObjectLinkedOpenHashMap.this.value[this.previousEntry()];
        }

        @Override
        final void acceptOnIndex(Consumer<? super V> action, int index) {
            action.accept(Int2ObjectLinkedOpenHashMap.this.value[index]);
        }

        @Override
        public V next() {
            return Int2ObjectLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeyIterator
    extends MapIterator<IntConsumer>
    implements IntListIterator {
        public KeyIterator(int k2) {
            super(k2);
        }

        @Override
        public int previousInt() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.previousEntry()];
        }

        public KeyIterator() {
        }

        @Override
        final void acceptOnIndex(IntConsumer action, int index) {
            action.accept(Int2ObjectLinkedOpenHashMap.this.key[index]);
        }

        @Override
        public int nextInt() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class FastEntryIterator
    extends MapIterator<Consumer<? super Int2ObjectMap.Entry<V>>>
    implements ObjectListIterator<Int2ObjectMap.Entry<V>> {
        final MapEntry entry;

        public FastEntryIterator() {
            this.entry = new MapEntry();
        }

        public FastEntryIterator(int from) {
            super(from);
            this.entry = new MapEntry();
        }

        @Override
        final void acceptOnIndex(Consumer<? super Int2ObjectMap.Entry<V>> action, int index) {
            this.entry.index = index;
            action.accept(this.entry);
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }

        @Override
        public MapEntry previous() {
            this.entry.index = this.previousEntry();
            return this.entry;
        }
    }

    private abstract class MapIterator<ConsumerType> {
        int prev = -1;
        int next = -1;
        int curr = -1;
        int index = -1;

        abstract void acceptOnIndex(ConsumerType var1, int var2);

        protected MapIterator() {
            this.next = Int2ObjectLinkedOpenHashMap.this.first;
            this.index = 0;
        }

        private MapIterator(int from) {
            if (from == 0) {
                if (Int2ObjectLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[Int2ObjectLinkedOpenHashMap.this.n];
                    this.prev = Int2ObjectLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            if (Int2ObjectLinkedOpenHashMap.this.key[Int2ObjectLinkedOpenHashMap.this.last] == from) {
                this.prev = Int2ObjectLinkedOpenHashMap.this.last;
                this.index = Int2ObjectLinkedOpenHashMap.this.size;
                return;
            }
            int pos = HashCommon.mix(from) & Int2ObjectLinkedOpenHashMap.this.mask;
            while (Int2ObjectLinkedOpenHashMap.this.key[pos] != 0) {
                if (Int2ObjectLinkedOpenHashMap.this.key[pos] == from) {
                    this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[pos];
                    this.prev = pos;
                    return;
                }
                pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
            }
            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
        }

        public boolean hasNext() {
            return this.next != -1;
        }

        public boolean hasPrevious() {
            return this.prev != -1;
        }

        private final void ensureIndexKnown() {
            if (this.index >= 0) {
                return;
            }
            if (this.prev == -1) {
                this.index = 0;
                return;
            }
            if (this.next == -1) {
                this.index = Int2ObjectLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Int2ObjectLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Int2ObjectLinkedOpenHashMap.this.link[pos];
                ++this.index;
            }
        }

        public int nextIndex() {
            this.ensureIndexKnown();
            return this.index;
        }

        public int previousIndex() {
            this.ensureIndexKnown();
            return this.index - 1;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next;
            this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
                ++this.index;
            }
            return this.curr;
        }

        public int previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev;
            this.prev = (int)(Int2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }

        public void forEachRemaining(ConsumerType action) {
            while (this.hasNext()) {
                this.curr = this.next;
                this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[this.curr];
                this.prev = this.curr;
                if (this.index >= 0) {
                    ++this.index;
                }
                this.acceptOnIndex(action, this.curr);
            }
        }

        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Int2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
                this.next = (int)Int2ObjectLinkedOpenHashMap.this.link[this.curr];
            }
            --Int2ObjectLinkedOpenHashMap.this.size;
            if (this.prev == -1) {
                Int2ObjectLinkedOpenHashMap.this.first = this.next;
            } else {
                int n2 = this.prev;
                Int2ObjectLinkedOpenHashMap.this.link[n2] = Int2ObjectLinkedOpenHashMap.this.link[n2] ^ (Int2ObjectLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 0xFFFFFFFFL) & 0xFFFFFFFFL;
            }
            if (this.next == -1) {
                Int2ObjectLinkedOpenHashMap.this.last = this.prev;
            } else {
                int n3 = this.next;
                Int2ObjectLinkedOpenHashMap.this.link[n3] = Int2ObjectLinkedOpenHashMap.this.link[n3] ^ (Int2ObjectLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L;
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos != Int2ObjectLinkedOpenHashMap.this.n) {
                int[] key = Int2ObjectLinkedOpenHashMap.this.key;
                while (true) {
                    int curr;
                    int last = pos;
                    pos = last + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
                    while (true) {
                        if ((curr = key[pos]) == 0) {
                            key[last] = 0;
                            Int2ObjectLinkedOpenHashMap.this.value[last] = null;
                            return;
                        }
                        int slot = HashCommon.mix(curr) & Int2ObjectLinkedOpenHashMap.this.mask;
                        if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                        pos = pos + 1 & Int2ObjectLinkedOpenHashMap.this.mask;
                    }
                    key[last] = curr;
                    Int2ObjectLinkedOpenHashMap.this.value[last] = Int2ObjectLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Int2ObjectLinkedOpenHashMap.this.fixPointers(pos, last);
                }
            }
            Int2ObjectLinkedOpenHashMap.this.containsNullKey = false;
            Int2ObjectLinkedOpenHashMap.this.value[Int2ObjectLinkedOpenHashMap.this.n] = null;
        }

        public int skip(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n2 - i2 - 1;
        }

        public int back(int n2) {
            int i2 = n2;
            while (i2-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n2 - i2 - 1;
        }

        public void set(Int2ObjectMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }

        public void add(Int2ObjectMap.Entry<V> ok2) {
            throw new UnsupportedOperationException();
        }
    }

    final class MapEntry
    implements Int2ObjectMap.Entry<V>,
    Map.Entry<Integer, V>,
    IntObjectPair<V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public int getIntKey() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public int leftInt() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Int2ObjectLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public V right() {
            return Int2ObjectLinkedOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v2) {
            Object oldValue = Int2ObjectLinkedOpenHashMap.this.value[this.index];
            Int2ObjectLinkedOpenHashMap.this.value[this.index] = v2;
            return oldValue;
        }

        public IntObjectPair<V> right(V v2) {
            Int2ObjectLinkedOpenHashMap.this.value[this.index] = v2;
            return this;
        }

        @Override
        @Deprecated
        public Integer getKey() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean equals(Object o2) {
            if (!(o2 instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e2 = (Map.Entry)o2;
            return Int2ObjectLinkedOpenHashMap.this.key[this.index] == (Integer)e2.getKey() && Objects.equals(Int2ObjectLinkedOpenHashMap.this.value[this.index], e2.getValue());
        }

        @Override
        public int hashCode() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.index] ^ (Int2ObjectLinkedOpenHashMap.this.value[this.index] == null ? 0 : Int2ObjectLinkedOpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return Int2ObjectLinkedOpenHashMap.this.key[this.index] + "=>" + Int2ObjectLinkedOpenHashMap.this.value[this.index];
        }
    }
}

