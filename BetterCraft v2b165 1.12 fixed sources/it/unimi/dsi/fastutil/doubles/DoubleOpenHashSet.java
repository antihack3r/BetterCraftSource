// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.doubles;

import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Hash;
import java.io.Serializable;

public class DoubleOpenHashSet extends AbstractDoubleSet implements Serializable, Cloneable, Hash
{
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient int mask;
    protected transient boolean containsNull;
    protected transient int n;
    protected transient int maxFill;
    protected int size;
    protected final float f;
    
    public DoubleOpenHashSet(final int expected, final float f) {
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
        this.key = new double[this.n + 1];
    }
    
    public DoubleOpenHashSet(final int expected) {
        this(expected, 0.75f);
    }
    
    public DoubleOpenHashSet() {
        this(16, 0.75f);
    }
    
    public DoubleOpenHashSet(final Collection<? extends Double> c, final float f) {
        this(c.size(), f);
        this.addAll(c);
    }
    
    public DoubleOpenHashSet(final Collection<? extends Double> c) {
        this(c, 0.75f);
    }
    
    public DoubleOpenHashSet(final DoubleCollection c, final float f) {
        this(c.size(), f);
        this.addAll(c);
    }
    
    public DoubleOpenHashSet(final DoubleCollection c) {
        this(c, 0.75f);
    }
    
    public DoubleOpenHashSet(final DoubleIterator i, final float f) {
        this(16, f);
        while (i.hasNext()) {
            this.add(i.nextDouble());
        }
    }
    
    public DoubleOpenHashSet(final DoubleIterator i) {
        this(i, 0.75f);
    }
    
    public DoubleOpenHashSet(final Iterator<?> i, final float f) {
        this(DoubleIterators.asDoubleIterator(i), f);
    }
    
    public DoubleOpenHashSet(final Iterator<?> i) {
        this(DoubleIterators.asDoubleIterator(i));
    }
    
    public DoubleOpenHashSet(final double[] a, final int offset, final int length, final float f) {
        this((length < 0) ? 0 : length, f);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }
    
    public DoubleOpenHashSet(final double[] a, final int offset, final int length) {
        this(a, offset, length, 0.75f);
    }
    
    public DoubleOpenHashSet(final double[] a, final float f) {
        this(a, 0, a.length, f);
    }
    
    public DoubleOpenHashSet(final double[] a) {
        this(a, 0.75f);
    }
    
    private int realSize() {
        return this.containsNull ? (this.size - 1) : this.size;
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
    
    @Override
    public boolean addAll(final DoubleCollection c) {
        if (this.f <= 0.5) {
            this.ensureCapacity(c.size());
        }
        else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }
    
    @Override
    public boolean addAll(final Collection<? extends Double> c) {
        if (this.f <= 0.5) {
            this.ensureCapacity(c.size());
        }
        else {
            this.tryCapacity(this.size() + c.size());
        }
        return super.addAll(c);
    }
    
    @Override
    public boolean add(final double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        }
        else {
            final double[] key = this.key;
            int pos;
            double curr;
            if (Double.doubleToLongBits(curr = key[pos = ((int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask)]) != 0L) {
                if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                    return false;
                }
                while (Double.doubleToLongBits(curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
                    if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                        return false;
                    }
                }
            }
            key[pos] = k;
        }
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return true;
    }
    
    protected final void shiftKeys(int pos) {
        final double[] key = this.key;
        int last = 0;
    Label_0006:
        while (true) {
            pos = ((last = pos) + 1 & this.mask);
            double curr;
            while (Double.doubleToLongBits(curr = key[pos]) != 0L) {
                final int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
                Label_0096: {
                    if (last <= pos) {
                        if (last >= slot) {
                            break Label_0096;
                        }
                        if (slot > pos) {
                            break Label_0096;
                        }
                    }
                    else if (last >= slot && slot > pos) {
                        break Label_0096;
                    }
                    pos = (pos + 1 & this.mask);
                    continue;
                }
                key[last] = curr;
                continue Label_0006;
            }
            break;
        }
        key[last] = 0.0;
    }
    
    private boolean removeEntry(final int pos) {
        --this.size;
        this.shiftKeys(pos);
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }
    
    private boolean removeNullEntry() {
        this.containsNull = false;
        this.key[this.n] = 0.0;
        --this.size;
        if (this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return true;
    }
    
    @Override
    public boolean rem(final double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNull && this.removeNullEntry();
        }
        final double[] key = this.key;
        int pos;
        double curr;
        if (Double.doubleToLongBits(curr = key[pos = ((int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask)]) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
        }
        while (Double.doubleToLongBits(curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                return this.removeEntry(pos);
            }
        }
        return false;
    }
    
    @Override
    public boolean contains(final double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNull;
        }
        final double[] key = this.key;
        int pos;
        double curr;
        if (Double.doubleToLongBits(curr = key[pos = ((int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask)]) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
        }
        while (Double.doubleToLongBits(curr = key[pos = (pos + 1 & this.mask)]) != 0L) {
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
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
        this.containsNull = false;
        Arrays.fill(this.key, 0.0);
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
    public DoubleIterator iterator() {
        return new SetIterator();
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
        final double[] key = this.key;
        final int mask = newN - 1;
        final double[] newKey = new double[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Double.doubleToLongBits(key[--i]) == 0L) {}
            int pos;
            if (Double.doubleToLongBits(newKey[pos = ((int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask)]) != 0L) {
                while (Double.doubleToLongBits(newKey[pos = (pos + 1 & mask)]) != 0L) {}
            }
            newKey[pos] = key[i];
        }
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
    }
    
    public DoubleOpenHashSet clone() {
        DoubleOpenHashSet c;
        try {
            c = (DoubleOpenHashSet)super.clone();
        }
        catch (final CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.key = this.key.clone();
        c.containsNull = this.containsNull;
        return c;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        while (j-- != 0) {
            while (Double.doubleToLongBits(this.key[i]) == 0L) {
                ++i;
            }
            h += HashCommon.double2int(this.key[i]);
            ++i;
        }
        return h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final DoubleIterator i = this.iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            s.writeDouble(i.nextDouble());
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        final double[] key2 = new double[this.n + 1];
        this.key = key2;
        final double[] key = key2;
        int i = this.size;
        while (i-- != 0) {
            final double k = s.readDouble();
            int pos;
            if (Double.doubleToLongBits(k) == 0L) {
                pos = this.n;
                this.containsNull = true;
            }
            else if (Double.doubleToLongBits(key[pos = ((int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask)]) != 0L) {
                while (Double.doubleToLongBits(key[pos = (pos + 1 & this.mask)]) != 0L) {}
            }
            key[pos] = k;
        }
    }
    
    private void checkTable() {
    }
    
    private class SetIterator extends AbstractDoubleIterator
    {
        int pos;
        int last;
        int c;
        boolean mustReturnNull;
        DoubleArrayList wrapped;
        
        private SetIterator() {
            this.pos = DoubleOpenHashSet.this.n;
            this.last = -1;
            this.c = DoubleOpenHashSet.this.size;
            this.mustReturnNull = DoubleOpenHashSet.this.containsNull;
        }
        
        @Override
        public boolean hasNext() {
            return this.c != 0;
        }
        
        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = DoubleOpenHashSet.this.n;
                return DoubleOpenHashSet.this.key[DoubleOpenHashSet.this.n];
            }
            final double[] key = DoubleOpenHashSet.this.key;
            while (--this.pos >= 0) {
                if (Double.doubleToLongBits(key[this.pos]) != 0L) {
                    final double[] array = key;
                    final int pos = this.pos;
                    this.last = pos;
                    return array[pos];
                }
            }
            this.last = Integer.MIN_VALUE;
            return this.wrapped.getDouble(-this.pos - 1);
        }
        
        private final void shiftKeys(int pos) {
            final double[] key = DoubleOpenHashSet.this.key;
            int last = 0;
        Label_0009:
            while (true) {
                pos = ((last = pos) + 1 & DoubleOpenHashSet.this.mask);
                double curr;
                while (Double.doubleToLongBits(curr = key[pos]) != 0L) {
                    final int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & DoubleOpenHashSet.this.mask;
                    Label_0108: {
                        if (last <= pos) {
                            if (last >= slot) {
                                break Label_0108;
                            }
                            if (slot > pos) {
                                break Label_0108;
                            }
                        }
                        else if (last >= slot && slot > pos) {
                            break Label_0108;
                        }
                        pos = (pos + 1 & DoubleOpenHashSet.this.mask);
                        continue;
                    }
                    if (pos < last) {
                        if (this.wrapped == null) {
                            this.wrapped = new DoubleArrayList(2);
                        }
                        this.wrapped.add(key[pos]);
                    }
                    key[last] = curr;
                    continue Label_0009;
                }
                break;
            }
            key[last] = 0.0;
        }
        
        @Override
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == DoubleOpenHashSet.this.n) {
                DoubleOpenHashSet.this.containsNull = false;
                DoubleOpenHashSet.this.key[DoubleOpenHashSet.this.n] = 0.0;
            }
            else {
                if (this.pos < 0) {
                    DoubleOpenHashSet.this.rem(this.wrapped.getDouble(-this.pos - 1));
                    this.last = -1;
                    return;
                }
                this.shiftKeys(this.last);
            }
            final DoubleOpenHashSet this$0 = DoubleOpenHashSet.this;
            --this$0.size;
            this.last = -1;
        }
    }
}
