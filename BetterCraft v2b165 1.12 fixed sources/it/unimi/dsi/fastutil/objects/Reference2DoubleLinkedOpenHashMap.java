// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.SortedSet;
import java.util.SortedMap;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import java.util.Comparator;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Map;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class Reference2DoubleLinkedOpenHashMap<K> extends AbstractReference2DoubleSortedMap<K> implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int first;
    protected transient int last;
    protected transient long[] link;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    protected transient Reference2DoubleSortedMap.FastSortedEntrySet<K> entries;
    protected transient ReferenceSortedSet<K> keys;
    protected transient DoubleCollection values;
    
    public Reference2DoubleLinkedOpenHashMap(final int expected, final float f) {
        this.first = -1;
        this.last = -1;
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
        this.key = (K[])new Object[this.n + 1];
        this.value = new double[this.n + 1];
        this.link = new long[this.n + 1];
    }
    
    public Reference2DoubleLinkedOpenHashMap(final int expected) {
        this(expected, 0.75f);
    }
    
    public Reference2DoubleLinkedOpenHashMap() {
        this(16, 0.75f);
    }
    
    public Reference2DoubleLinkedOpenHashMap(final Map<? extends K, ? extends Double> m, final float f) {
        this(m.size(), f);
        this.putAll(m);
    }
    
    public Reference2DoubleLinkedOpenHashMap(final Map<? extends K, ? extends Double> m) {
        this(m, 0.75f);
    }
    
    public Reference2DoubleLinkedOpenHashMap(final Reference2DoubleMap<K> m, final float f) {
        this(m.size(), f);
        this.putAll((Map<? extends K, ? extends Double>)m);
    }
    
    public Reference2DoubleLinkedOpenHashMap(final Reference2DoubleMap<K> m) {
        this(m, 0.75f);
    }
    
    public Reference2DoubleLinkedOpenHashMap(final K[] k, final double[] v, final float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Reference2DoubleLinkedOpenHashMap(final K[] k, final double[] v) {
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
    
    private double removeEntry(final int pos) {
        final double oldValue = this.value[pos];
        --this.size;
        this.fixPointers(pos);
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    private double removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        final double oldValue = this.value[this.n];
        --this.size;
        this.fixPointers(this.n);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends Double> m) {
        if (this.f <= 0.5) {
            this.ensureCapacity(m.size());
        }
        else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }
    
    private int insert(final K k, final double v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return this.n;
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final K[] key = this.key;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) != null) {
                if (curr == k) {
                    return pos;
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                    if (curr == k) {
                        return pos;
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return -1;
    }
    
    @Override
    public double put(final K k, final double v) {
        final int pos = this.insert(k, v);
        if (pos < 0) {
            return this.defRetValue;
        }
        final double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    @Deprecated
    @Override
    public Double put(final K ok, final Double ov) {
        final double v = ov;
        final int pos = this.insert(ok, v);
        if (pos < 0) {
            return null;
        }
        final double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    private double addToValue(final int pos, final double incr) {
        final double oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }
    
    public double addTo(final K k, final double incr) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        }
        else {
            final K[] key = this.key;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) != null) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                    if (curr == k) {
                        return this.addToValue(pos, incr);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }
    
    protected final void shiftKeys(int pos) {
        final K[] key = this.key;
        int last = 0;
    Label_0006:
        while (true) {
            pos = ((last = pos) + 1 & this.mask);
            K curr;
            while ((curr = key[pos]) != null) {
                final int slot = HashCommon.mix(System.identityHashCode(curr)) & this.mask;
                Label_0091: {
                    if (last <= pos) {
                        if (last >= slot) {
                            break Label_0091;
                        }
                        if (slot > pos) {
                            break Label_0091;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break Label_0091;
                    }
                    pos = (pos + 1 & this.mask);
                    continue;
                }
                key[last] = curr;
                this.value[last] = this.value[pos];
                this.fixPointers(pos, last);
                continue Label_0006;
            }
            break;
        }
        key[last] = null;
    }
    
    @Override
    public double removeDouble(final Object k) {
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        else {
            final K[] key = this.key;
            int pos;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
                return this.defRetValue;
            }
            if (k == curr) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                if (k == curr) {
                    return this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
    }
    
    @Deprecated
    @Override
    public Double remove(final Object ok) {
        final K k = (K)ok;
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return null;
        }
        else {
            final K[] key = this.key;
            int pos;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
                return null;
            }
            if (curr == k) {
                return this.removeEntry(pos);
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                if (curr == k) {
                    return this.removeEntry(pos);
                }
            }
            return null;
        }
    }
    
    private double setValue(final int pos, final double v) {
        final double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }
    
    public double removeFirstDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        final int pos = this.first;
        this.first = (int)this.link[pos];
        if (0 <= this.first) {
            final long[] link = this.link;
            final int first = this.first;
            link[first] |= 0xFFFFFFFF00000000L;
        }
        --this.size;
        final double v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
        }
        else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }
    
    public double removeLastDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        final int pos = this.last;
        this.last = (int)(this.link[pos] >>> 32);
        if (0 <= this.last) {
            final long[] link = this.link;
            final int last = this.last;
            link[last] |= 0xFFFFFFFFL;
        }
        --this.size;
        final double v = this.value[pos];
        if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
        }
        else {
            this.shiftKeys(pos);
        }
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return v;
    }
    
    private void moveIndexToFirst(final int i) {
        if (this.size == 1 || this.first == i) {
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            final long[] link = this.link;
            final int last = this.last;
            link[last] |= 0xFFFFFFFFL;
        }
        else {
            final long linki = this.link[i];
            final int prev = (int)(linki >>> 32);
            final int next = (int)linki;
            final long[] link2 = this.link;
            final int n = prev;
            link2[n] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            final long[] link3 = this.link;
            final int n2 = next;
            link3[n2] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
        }
        final long[] link4 = this.link;
        final int first = this.first;
        link4[first] ^= ((this.link[this.first] ^ ((long)i & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
        this.link[i] = (0xFFFFFFFF00000000L | ((long)this.first & 0xFFFFFFFFL));
        this.first = i;
    }
    
    private void moveIndexToLast(final int i) {
        if (this.size == 1 || this.last == i) {
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            final long[] link = this.link;
            final int first = this.first;
            link[first] |= 0xFFFFFFFF00000000L;
        }
        else {
            final long linki = this.link[i];
            final int prev = (int)(linki >>> 32);
            final int next = (int)linki;
            final long[] link2 = this.link;
            final int n = prev;
            link2[n] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            final long[] link3 = this.link;
            final int n2 = next;
            link3[n2] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
        }
        final long[] link4 = this.link;
        final int last = this.last;
        link4[last] ^= ((this.link[this.last] ^ ((long)i & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        this.link[i] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
        this.last = i;
    }
    
    public double getAndMoveToFirst(final K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        else {
            final K[] key = this.key;
            int pos;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
                return this.defRetValue;
            }
            if (k == curr) {
                this.moveIndexToFirst(pos);
                return this.value[pos];
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                if (k == curr) {
                    this.moveIndexToFirst(pos);
                    return this.value[pos];
                }
            }
            return this.defRetValue;
        }
    }
    
    public double getAndMoveToLast(final K k) {
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.value[this.n];
            }
            return this.defRetValue;
        }
        else {
            final K[] key = this.key;
            int pos;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
                return this.defRetValue;
            }
            if (k == curr) {
                this.moveIndexToLast(pos);
                return this.value[pos];
            }
            while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                if (k == curr) {
                    this.moveIndexToLast(pos);
                    return this.value[pos];
                }
            }
            return this.defRetValue;
        }
    }
    
    public double putAndMoveToFirst(final K k, final double v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToFirst(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final K[] key = this.key;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) != null) {
                if (curr == k) {
                    this.moveIndexToFirst(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                    if (curr == k) {
                        this.moveIndexToFirst(pos);
                        return this.setValue(pos, v);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int first = this.first;
            link[first] ^= ((this.link[this.first] ^ ((long)pos & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            this.link[pos] = (0xFFFFFFFF00000000L | ((long)this.first & 0xFFFFFFFFL));
            this.first = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }
    
    public double putAndMoveToLast(final K k, final double v) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                this.moveIndexToLast(this.n);
                return this.setValue(this.n, v);
            }
            this.containsNullKey = true;
            pos = this.n;
        }
        else {
            final K[] key = this.key;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) != null) {
                if (curr == k) {
                    this.moveIndexToLast(pos);
                    return this.setValue(pos, v);
                }
                while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
                    if (curr == k) {
                        this.moveIndexToLast(pos);
                        return this.setValue(pos, v);
                    }
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size == 0) {
            final int n = pos;
            this.last = n;
            this.first = n;
            this.link[pos] = -1L;
        }
        else {
            final long[] link = this.link;
            final int last = this.last;
            link[last] ^= ((this.link[this.last] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[pos] = (((long)this.last & 0xFFFFFFFFL) << 32 | 0xFFFFFFFFL);
            this.last = pos;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size, this.f));
        }
        return this.defRetValue;
    }
    
    @Override
    public double getDouble(final Object k) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        final K[] key = this.key;
        int pos;
        K curr;
        if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
            if (k == curr) {
                return this.value[pos];
            }
        }
        return this.defRetValue;
    }
    
    @Override
    public boolean containsKey(final Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        final K[] key = this.key;
        int pos;
        K curr;
        if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask)]) == null) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        while ((curr = key[pos = (pos + 1 & this.mask)]) != null) {
            if (k == curr) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final double v) {
        final double[] value = this.value;
        final K[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] != null && value[i] == v) {
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
        Arrays.fill(this.key, null);
        final int n = -1;
        this.last = n;
        this.first = n;
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
    
    protected void fixPointers(final int i) {
        if (this.size == 0) {
            final int n = -1;
            this.last = n;
            this.first = n;
            return;
        }
        if (this.first == i) {
            this.first = (int)this.link[i];
            if (0 <= this.first) {
                final long[] link = this.link;
                final int first = this.first;
                link[first] |= 0xFFFFFFFF00000000L;
            }
            return;
        }
        if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            if (0 <= this.last) {
                final long[] link2 = this.link;
                final int last = this.last;
                link2[last] |= 0xFFFFFFFFL;
            }
            return;
        }
        final long linki = this.link[i];
        final int prev = (int)(linki >>> 32);
        final int next = (int)linki;
        final long[] link3 = this.link;
        final int n2 = prev;
        link3[n2] ^= ((this.link[prev] ^ (linki & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        final long[] link4 = this.link;
        final int n3 = next;
        link4[n3] ^= ((this.link[next] ^ (linki & 0xFFFFFFFF00000000L)) & 0xFFFFFFFF00000000L);
    }
    
    protected void fixPointers(final int s, final int d) {
        if (this.size == 1) {
            this.last = d;
            this.first = d;
            this.link[d] = -1L;
            return;
        }
        if (this.first == s) {
            this.first = d;
            final long[] link = this.link;
            final int n = (int)this.link[s];
            link[n] ^= ((this.link[(int)this.link[s]] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            this.link[d] = this.link[s];
            return;
        }
        if (this.last == s) {
            this.last = d;
            final long[] link2 = this.link;
            final int n2 = (int)(this.link[s] >>> 32);
            link2[n2] ^= ((this.link[(int)(this.link[s] >>> 32)] ^ ((long)d & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            this.link[d] = this.link[s];
            return;
        }
        final long links = this.link[s];
        final int prev = (int)(links >>> 32);
        final int next = (int)links;
        final long[] link3 = this.link;
        final int n3 = prev;
        link3[n3] ^= ((this.link[prev] ^ ((long)d & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
        final long[] link4 = this.link;
        final int n4 = next;
        link4[n4] ^= ((this.link[next] ^ ((long)d & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
        this.link[d] = links;
    }
    
    @Override
    public K firstKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.first];
    }
    
    @Override
    public K lastKey() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.key[this.last];
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return null;
    }
    
    @Override
    public Reference2DoubleSortedMap<K> tailMap(final K from) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reference2DoubleSortedMap<K> headMap(final K to) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reference2DoubleSortedMap<K> subMap(final K from, final K to) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reference2DoubleSortedMap.FastSortedEntrySet<K> reference2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }
    
    @Override
    public ReferenceSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }
    
    @Override
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection() {
                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public int size() {
                    return Reference2DoubleLinkedOpenHashMap.this.size;
                }
                
                @Override
                public boolean contains(final double v) {
                    return Reference2DoubleLinkedOpenHashMap.this.containsValue(v);
                }
                
                @Override
                public void clear() {
                    Reference2DoubleLinkedOpenHashMap.this.clear();
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
        final K[] key = this.key;
        final double[] value = this.value;
        final int mask = newN - 1;
        final K[] newKey = (K[])new Object[newN + 1];
        final double[] newValue = new double[newN + 1];
        int i = this.first;
        int prev = -1;
        int newPrev = -1;
        final long[] link = this.link;
        final long[] newLink = new long[newN + 1];
        this.first = -1;
        int j = this.size;
        while (j-- != 0) {
            int pos;
            if (key[i] == null) {
                pos = newN;
            }
            else {
                for (pos = (HashCommon.mix(System.identityHashCode(key[i])) & mask); newKey[pos] != null; pos = (pos + 1 & mask)) {}
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
            if (prev != -1) {
                final long[] array = newLink;
                final int n = newPrev;
                array[n] ^= ((newLink[newPrev] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
                final long[] array2 = newLink;
                final int n2 = pos;
                array2[n2] ^= ((newLink[pos] ^ ((long)newPrev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
                newPrev = pos;
            }
            else {
                final int first = pos;
                this.first = first;
                newPrev = first;
                newLink[pos] = -1L;
            }
            final int t = i;
            i = (int)link[i];
            prev = t;
        }
        this.link = newLink;
        if ((this.last = newPrev) != -1) {
            final long[] array3 = newLink;
            final int n3 = newPrev;
            array3[n3] |= 0xFFFFFFFFL;
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }
    
    public Reference2DoubleLinkedOpenHashMap<K> clone() {
        Reference2DoubleLinkedOpenHashMap<K> c;
        try {
            c = (Reference2DoubleLinkedOpenHashMap)super.clone();
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
        c.link = this.link.clone();
        return c;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = System.identityHashCode(this.key[i]);
            }
            t ^= HashCommon.double2int(this.value[i]);
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final K[] key = this.key;
        final double[] value = this.value;
        final MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            final int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeDouble(value[e]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final Object[] key2 = new Object[this.n + 1];
        this.key = (K[])key2;
        final K[] key = (K[])key2;
        final double[] value2 = new double[this.n + 1];
        this.value = value2;
        final double[] value = value2;
        final long[] link2 = new long[this.n + 1];
        this.link = link2;
        final long[] link = link2;
        int prev = -1;
        final int n = -1;
        this.last = n;
        this.first = n;
        int i = this.size;
        while (i-- != 0) {
            final K k = (K)s.readObject();
            final double v = s.readDouble();
            int pos;
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            }
            else {
                for (pos = (HashCommon.mix(System.identityHashCode(k)) & this.mask); key[pos] != null; pos = (pos + 1 & this.mask)) {}
            }
            key[pos] = k;
            value[pos] = v;
            if (this.first != -1) {
                final long[] array = link;
                final int n2 = prev;
                array[n2] ^= ((link[prev] ^ ((long)pos & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
                final long[] array2 = link;
                final int n3 = pos;
                array2[n3] ^= ((link[pos] ^ ((long)prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
                prev = pos;
            }
            else {
                final int first = pos;
                this.first = first;
                prev = first;
                final long[] array3 = link;
                final int n4 = pos;
                array3[n4] |= 0xFFFFFFFF00000000L;
            }
        }
        if ((this.last = prev) != -1) {
            final long[] array4 = link;
            final int n5 = prev;
            array4[n5] |= 0xFFFFFFFFL;
        }
    }
    
    private void checkTable() {
    }
    
    final class MapEntry implements Reference2DoubleMap.Entry<K>, Map.Entry<K, Double>
    {
        int index;
        
        MapEntry(final int index) {
            this.index = index;
        }
        
        MapEntry() {
        }
        
        @Override
        public K getKey() {
            return Reference2DoubleLinkedOpenHashMap.this.key[this.index];
        }
        
        @Deprecated
        @Override
        public Double getValue() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.index];
        }
        
        @Override
        public double getDoubleValue() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.index];
        }
        
        @Override
        public double setValue(final double v) {
            final double oldValue = Reference2DoubleLinkedOpenHashMap.this.value[this.index];
            Reference2DoubleLinkedOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }
        
        @Override
        public Double setValue(final Double v) {
            return this.setValue((double)v);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, Double> e = (Map.Entry<K, Double>)o;
            return Reference2DoubleLinkedOpenHashMap.this.key[this.index] == e.getKey() && Reference2DoubleLinkedOpenHashMap.this.value[this.index] == e.getValue();
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2DoubleLinkedOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Reference2DoubleLinkedOpenHashMap.this.value[this.index]);
        }
        
        @Override
        public String toString() {
            return Reference2DoubleLinkedOpenHashMap.this.key[this.index] + "=>" + Reference2DoubleLinkedOpenHashMap.this.value[this.index];
        }
    }
    
    private class MapIterator
    {
        int prev;
        int next;
        int curr;
        int index;
        
        private MapIterator() {
            this.prev = -1;
            this.next = -1;
            this.curr = -1;
            this.index = -1;
            this.next = Reference2DoubleLinkedOpenHashMap.this.first;
            this.index = 0;
        }
        
        private MapIterator(final K from) {
            this.prev = -1;
            this.next = -1;
            this.curr = -1;
            this.index = -1;
            if (from == null) {
                if (Reference2DoubleLinkedOpenHashMap.this.containsNullKey) {
                    this.next = (int)Reference2DoubleLinkedOpenHashMap.this.link[Reference2DoubleLinkedOpenHashMap.this.n];
                    this.prev = Reference2DoubleLinkedOpenHashMap.this.n;
                    return;
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
            else {
                if (Reference2DoubleLinkedOpenHashMap.this.key[Reference2DoubleLinkedOpenHashMap.this.last] == from) {
                    this.prev = Reference2DoubleLinkedOpenHashMap.this.last;
                    this.index = Reference2DoubleLinkedOpenHashMap.this.size;
                    return;
                }
                for (int pos = HashCommon.mix(System.identityHashCode(from)) & Reference2DoubleLinkedOpenHashMap.this.mask; Reference2DoubleLinkedOpenHashMap.this.key[pos] != null; pos = (pos + 1 & Reference2DoubleLinkedOpenHashMap.this.mask)) {
                    if (Reference2DoubleLinkedOpenHashMap.this.key[pos] == from) {
                        this.next = (int)Reference2DoubleLinkedOpenHashMap.this.link[pos];
                        this.prev = pos;
                        return;
                    }
                }
                throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
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
                this.index = Reference2DoubleLinkedOpenHashMap.this.size;
                return;
            }
            int pos = Reference2DoubleLinkedOpenHashMap.this.first;
            this.index = 1;
            while (pos != this.prev) {
                pos = (int)Reference2DoubleLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Reference2DoubleLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Reference2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
                --this.index;
            }
            return this.curr;
        }
        
        public void remove() {
            this.ensureIndexKnown();
            if (this.curr == -1) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
                this.prev = (int)(Reference2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            }
            else {
                this.next = (int)Reference2DoubleLinkedOpenHashMap.this.link[this.curr];
            }
            final Reference2DoubleLinkedOpenHashMap this$0 = Reference2DoubleLinkedOpenHashMap.this;
            --this$0.size;
            if (this.prev == -1) {
                Reference2DoubleLinkedOpenHashMap.this.first = this.next;
            }
            else {
                final long[] link = Reference2DoubleLinkedOpenHashMap.this.link;
                final int prev = this.prev;
                link[prev] ^= ((Reference2DoubleLinkedOpenHashMap.this.link[this.prev] ^ ((long)this.next & 0xFFFFFFFFL)) & 0xFFFFFFFFL);
            }
            if (this.next == -1) {
                Reference2DoubleLinkedOpenHashMap.this.last = this.prev;
            }
            else {
                final long[] link2 = Reference2DoubleLinkedOpenHashMap.this.link;
                final int next = this.next;
                link2[next] ^= ((Reference2DoubleLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 0xFFFFFFFFL) << 32) & 0xFFFFFFFF00000000L);
            }
            int pos = this.curr;
            this.curr = -1;
            if (pos == Reference2DoubleLinkedOpenHashMap.this.n) {
                Reference2DoubleLinkedOpenHashMap.this.containsNullKey = false;
                Reference2DoubleLinkedOpenHashMap.this.key[Reference2DoubleLinkedOpenHashMap.this.n] = null;
                return;
            }
            final K[] key = Reference2DoubleLinkedOpenHashMap.this.key;
            int last = 0;
        Label_0280:
            while (true) {
                pos = ((last = pos) + 1 & Reference2DoubleLinkedOpenHashMap.this.mask);
                K curr;
                while ((curr = key[pos]) != null) {
                    final int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2DoubleLinkedOpenHashMap.this.mask;
                    Label_0374: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0374;
                            }
                            if (slot > pos) {
                                break Label_0374;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0374;
                        }
                        pos = (pos + 1 & Reference2DoubleLinkedOpenHashMap.this.mask);
                        continue;
                    }
                    key[last] = curr;
                    Reference2DoubleLinkedOpenHashMap.this.value[last] = Reference2DoubleLinkedOpenHashMap.this.value[pos];
                    if (this.next == pos) {
                        this.next = last;
                    }
                    if (this.prev == pos) {
                        this.prev = last;
                    }
                    Reference2DoubleLinkedOpenHashMap.this.fixPointers(pos, last);
                    continue Label_0280;
                }
                break;
            }
            key[last] = null;
        }
        
        public int skip(final int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
        
        public int back(final int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }
    }
    
    private class EntryIterator extends MapIterator implements ObjectListIterator<Reference2DoubleMap.Entry<K>>
    {
        private MapEntry entry;
        
        public EntryIterator() {
        }
        
        public EntryIterator(final K from) {
            super((Object)from);
        }
        
        @Override
        public MapEntry next() {
            return this.entry = new MapEntry(this.nextEntry());
        }
        
        @Override
        public MapEntry previous() {
            return this.entry = new MapEntry(this.previousEntry());
        }
        
        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
        
        @Override
        public void set(final Reference2DoubleMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Reference2DoubleMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private class FastEntryIterator extends MapIterator implements ObjectListIterator<Reference2DoubleMap.Entry<K>>
    {
        final MapEntry entry;
        
        public FastEntryIterator() {
            this.entry = new MapEntry();
        }
        
        public FastEntryIterator(final K from) {
            super((Object)from);
            this.entry = new MapEntry();
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
        
        @Override
        public void set(final Reference2DoubleMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Reference2DoubleMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class MapEntrySet extends AbstractObjectSortedSet<Reference2DoubleMap.Entry<K>> implements Reference2DoubleSortedMap.FastSortedEntrySet<K>
    {
        @Override
        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public Comparator<? super Reference2DoubleMap.Entry<K>> comparator() {
            return null;
        }
        
        @Override
        public ObjectSortedSet<Reference2DoubleMap.Entry<K>> subSet(final Reference2DoubleMap.Entry<K> fromElement, final Reference2DoubleMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSortedSet<Reference2DoubleMap.Entry<K>> headSet(final Reference2DoubleMap.Entry<K> toElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectSortedSet<Reference2DoubleMap.Entry<K>> tailSet(final Reference2DoubleMap.Entry<K> fromElement) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Reference2DoubleMap.Entry<K> first() {
            if (Reference2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Reference2DoubleLinkedOpenHashMap.this.first);
        }
        
        @Override
        public Reference2DoubleMap.Entry<K> last() {
            if (Reference2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return new MapEntry(Reference2DoubleLinkedOpenHashMap.this.last);
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
            final double v = (double)e.getValue();
            if (k == null) {
                return Reference2DoubleLinkedOpenHashMap.this.containsNullKey && Reference2DoubleLinkedOpenHashMap.this.value[Reference2DoubleLinkedOpenHashMap.this.n] == v;
            }
            final K[] key = Reference2DoubleLinkedOpenHashMap.this.key;
            int pos;
            K curr;
            if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & Reference2DoubleLinkedOpenHashMap.this.mask)]) == null) {
                return false;
            }
            if (k == curr) {
                return Reference2DoubleLinkedOpenHashMap.this.value[pos] == v;
            }
            while ((curr = key[pos = (pos + 1 & Reference2DoubleLinkedOpenHashMap.this.mask)]) != null) {
                if (k == curr) {
                    return Reference2DoubleLinkedOpenHashMap.this.value[pos] == v;
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            final K k = (K)e.getKey();
            final double v = (double)e.getValue();
            if (k == null) {
                if (Reference2DoubleLinkedOpenHashMap.this.containsNullKey && Reference2DoubleLinkedOpenHashMap.this.value[Reference2DoubleLinkedOpenHashMap.this.n] == v) {
                    Reference2DoubleLinkedOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            else {
                final K[] key = Reference2DoubleLinkedOpenHashMap.this.key;
                int pos;
                K curr;
                if ((curr = key[pos = (HashCommon.mix(System.identityHashCode(k)) & Reference2DoubleLinkedOpenHashMap.this.mask)]) == null) {
                    return false;
                }
                if (curr != k) {
                    while ((curr = key[pos = (pos + 1 & Reference2DoubleLinkedOpenHashMap.this.mask)]) != null) {
                        if (curr == k && Reference2DoubleLinkedOpenHashMap.this.value[pos] == v) {
                            Reference2DoubleLinkedOpenHashMap.this.removeEntry(pos);
                            return true;
                        }
                    }
                    return false;
                }
                if (Reference2DoubleLinkedOpenHashMap.this.value[pos] == v) {
                    Reference2DoubleLinkedOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
        }
        
        @Override
        public int size() {
            return Reference2DoubleLinkedOpenHashMap.this.size;
        }
        
        @Override
        public void clear() {
            Reference2DoubleLinkedOpenHashMap.this.clear();
        }
        
        @Override
        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> iterator(final Reference2DoubleMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }
        
        @Override
        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }
        
        @Override
        public ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator(final Reference2DoubleMap.Entry<K> from) {
            return new FastEntryIterator(from.getKey());
        }
    }
    
    private final class KeyIterator extends MapIterator implements ObjectListIterator<K>
    {
        public KeyIterator(final K k) {
            super((Object)k);
        }
        
        @Override
        public K previous() {
            return Reference2DoubleLinkedOpenHashMap.this.key[this.previousEntry()];
        }
        
        @Override
        public void set(final K k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final K k) {
            throw new UnsupportedOperationException();
        }
        
        public KeyIterator() {
        }
        
        @Override
        public K next() {
            return Reference2DoubleLinkedOpenHashMap.this.key[this.nextEntry()];
        }
    }
    
    private final class KeySet extends AbstractReferenceSortedSet<K>
    {
        @Override
        public ObjectListIterator<K> iterator(final K from) {
            return new KeyIterator(from);
        }
        
        @Override
        public ObjectListIterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return Reference2DoubleLinkedOpenHashMap.this.size;
        }
        
        @Override
        public boolean contains(final Object k) {
            return Reference2DoubleLinkedOpenHashMap.this.containsKey(k);
        }
        
        @Override
        public boolean rem(final Object k) {
            final int oldSize = Reference2DoubleLinkedOpenHashMap.this.size;
            Reference2DoubleLinkedOpenHashMap.this.remove(k);
            return Reference2DoubleLinkedOpenHashMap.this.size != oldSize;
        }
        
        @Override
        public void clear() {
            Reference2DoubleLinkedOpenHashMap.this.clear();
        }
        
        @Override
        public K first() {
            if (Reference2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Reference2DoubleLinkedOpenHashMap.this.key[Reference2DoubleLinkedOpenHashMap.this.first];
        }
        
        @Override
        public K last() {
            if (Reference2DoubleLinkedOpenHashMap.this.size == 0) {
                throw new NoSuchElementException();
            }
            return Reference2DoubleLinkedOpenHashMap.this.key[Reference2DoubleLinkedOpenHashMap.this.last];
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return null;
        }
        
        @Override
        public final ReferenceSortedSet<K> tailSet(final K from) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final ReferenceSortedSet<K> headSet(final K to) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final ReferenceSortedSet<K> subSet(final K from, final K to) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class ValueIterator extends MapIterator implements DoubleListIterator
    {
        @Override
        public double previousDouble() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
        }
        
        @Override
        public Double previous() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
        }
        
        @Override
        public void set(final Double ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Double ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void set(final double v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final double v) {
            throw new UnsupportedOperationException();
        }
        
        public ValueIterator() {
        }
        
        @Override
        public double nextDouble() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
        }
        
        @Deprecated
        @Override
        public Double next() {
            return Reference2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
        }
    }
}
