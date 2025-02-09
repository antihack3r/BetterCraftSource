// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map;
import java.util.Comparator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import java.io.Serializable;

public class Object2FloatAVLTreeMap<K> extends AbstractObject2FloatSortedMap<K> implements Serializable, Cloneable
{
    protected transient Entry<K> tree;
    protected int count;
    protected transient Entry<K> firstEntry;
    protected transient Entry<K> lastEntry;
    protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient FloatCollection values;
    protected transient boolean modified;
    protected Comparator<? super K> storedComparator;
    protected transient Comparator<? super K> actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    
    public Object2FloatAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }
    
    private void setActualComparator() {
        this.actualComparator = this.storedComparator;
    }
    
    public Object2FloatAVLTreeMap(final Comparator<? super K> c) {
        this();
        this.storedComparator = c;
        this.setActualComparator();
    }
    
    public Object2FloatAVLTreeMap(final Map<? extends K, ? extends Float> m) {
        this();
        this.putAll(m);
    }
    
    public Object2FloatAVLTreeMap(final SortedMap<K, Float> m) {
        this(m.comparator());
        this.putAll((Map<? extends K, ? extends Float>)m);
    }
    
    public Object2FloatAVLTreeMap(final Object2FloatMap<? extends K> m) {
        this();
        this.putAll(m);
    }
    
    public Object2FloatAVLTreeMap(final Object2FloatSortedMap<K> m) {
        this(m.comparator());
        this.putAll((Map<? extends K, ? extends Float>)m);
    }
    
    public Object2FloatAVLTreeMap(final K[] k, final float[] v, final Comparator<? super K> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Object2FloatAVLTreeMap(final K[] k, final float[] v) {
        this(k, v, null);
    }
    
    final int compare(final K k1, final K k2) {
        return (this.actualComparator == null) ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare((Object)k1, (Object)k2);
    }
    
    final Entry<K> findKey(final K k) {
        Entry<K> e;
        int cmp;
        for (e = this.tree; e != null && (cmp = this.compare(k, e.key)) != 0; e = ((cmp < 0) ? e.left() : e.right())) {}
        return e;
    }
    
    final Entry<K> locateKey(final K k) {
        Entry<K> e = this.tree;
        Entry<K> last = this.tree;
        int cmp;
        for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = ((cmp < 0) ? e.left() : e.right())) {
            last = e;
        }
        return (cmp == 0) ? e : last;
    }
    
    private void allocatePaths() {
        this.dirPath = new boolean[48];
    }
    
    public float addTo(final K k, final float incr) {
        final Entry<K> e = this.add(k);
        final float oldValue = e.value;
        final Entry<K> entry = e;
        entry.value += incr;
        return oldValue;
    }
    
    @Override
    public float put(final K k, final float v) {
        final Entry<K> e = this.add(k);
        final float oldValue = e.value;
        e.value = v;
        return oldValue;
    }
    
    private Entry<K> add(final K k) {
        this.modified = false;
        Entry<K> e = null;
        if (this.tree != null) {
            Entry<K> p = this.tree;
            Entry<K> q = null;
            Entry<K> y = this.tree;
            Entry<K> z = null;
            Entry<K> w = null;
            int i = 0;
            int cmp;
            while ((cmp = this.compare(k, p.key)) != 0) {
                if (p.balance() != 0) {
                    i = 0;
                    z = q;
                    y = p;
                }
                final boolean[] dirPath = this.dirPath;
                final int n = i++;
                final boolean b = cmp > 0;
                dirPath[n] = b;
                if (b) {
                    if (!p.succ()) {
                        q = p;
                        p = p.right;
                        continue;
                    }
                    ++this.count;
                    e = new Entry<K>(k, this.defRetValue);
                    this.modified = true;
                    if (p.right == null) {
                        this.lastEntry = e;
                    }
                    e.left = p;
                    e.right = p.right;
                    p.right(e);
                }
                else {
                    if (!p.pred()) {
                        q = p;
                        p = p.left;
                        continue;
                    }
                    ++this.count;
                    e = new Entry<K>(k, this.defRetValue);
                    this.modified = true;
                    if (p.left == null) {
                        this.firstEntry = e;
                    }
                    e.right = p;
                    e.left = p.left;
                    p.left(e);
                }
                for (p = y, i = 0; p != e; p = (this.dirPath[i++] ? p.right : p.left)) {
                    if (this.dirPath[i]) {
                        p.incBalance();
                    }
                    else {
                        p.decBalance();
                    }
                }
                if (y.balance() == -2) {
                    final Entry<K> x = y.left;
                    if (x.balance() == -1) {
                        w = x;
                        if (x.succ()) {
                            x.succ(false);
                            y.pred(x);
                        }
                        else {
                            y.left = x.right;
                        }
                        x.right = y;
                        x.balance(0);
                        y.balance(0);
                    }
                    else {
                        w = x.right;
                        x.right = w.left;
                        w.left = x;
                        y.left = w.right;
                        w.right = y;
                        if (w.balance() == -1) {
                            x.balance(0);
                            y.balance(1);
                        }
                        else if (w.balance() == 0) {
                            x.balance(0);
                            y.balance(0);
                        }
                        else {
                            x.balance(-1);
                            y.balance(0);
                        }
                        w.balance(0);
                        if (w.pred()) {
                            x.succ(w);
                            w.pred(false);
                        }
                        if (w.succ()) {
                            y.pred(w);
                            w.succ(false);
                        }
                    }
                }
                else {
                    if (y.balance() != 2) {
                        return e;
                    }
                    final Entry<K> x = y.right;
                    if (x.balance() == 1) {
                        w = x;
                        if (x.pred()) {
                            x.pred(false);
                            y.succ(x);
                        }
                        else {
                            y.right = x.left;
                        }
                        x.left = y;
                        x.balance(0);
                        y.balance(0);
                    }
                    else {
                        w = x.left;
                        x.left = w.right;
                        w.right = x;
                        y.right = w.left;
                        w.left = y;
                        if (w.balance() == 1) {
                            x.balance(0);
                            y.balance(-1);
                        }
                        else if (w.balance() == 0) {
                            x.balance(0);
                            y.balance(0);
                        }
                        else {
                            x.balance(1);
                            y.balance(0);
                        }
                        w.balance(0);
                        if (w.pred()) {
                            y.succ(w);
                            w.pred(false);
                        }
                        if (w.succ()) {
                            x.pred(w);
                            w.succ(false);
                        }
                    }
                }
                if (z == null) {
                    this.tree = w;
                    return e;
                }
                if (z.left == y) {
                    z.left = w;
                    return e;
                }
                z.right = w;
                return e;
            }
            return p;
        }
        ++this.count;
        final Entry<K> tree = new Entry<K>(k, this.defRetValue);
        this.firstEntry = tree;
        this.lastEntry = tree;
        this.tree = tree;
        e = tree;
        this.modified = true;
        return e;
    }
    
    private Entry<K> parent(final Entry<K> e) {
        if (e == this.tree) {
            return null;
        }
        Entry<K> y = e;
        Entry<K> x = e;
        while (!y.succ()) {
            if (x.pred()) {
                Entry<K> p = x.left;
                if (p == null || p.right != e) {
                    while (!y.succ()) {
                        y = y.right;
                    }
                    p = y.right;
                }
                return p;
            }
            x = x.left;
            y = y.right;
        }
        Entry<K> p = y.right;
        if (p == null || p.left != e) {
            while (!x.pred()) {
                x = x.left;
            }
            p = x.left;
        }
        return p;
    }
    
    @Override
    public float removeFloat(final Object k) {
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry<K> p = this.tree;
        Entry<K> q = null;
        boolean dir = false;
        final K kk = (K)k;
        int cmp;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            if (dir = (cmp > 0)) {
                q = p;
                if ((p = p.right()) == null) {
                    return this.defRetValue;
                }
                continue;
            }
            else {
                q = p;
                if ((p = p.left()) == null) {
                    return this.defRetValue;
                }
                continue;
            }
        }
        if (p.left == null) {
            this.firstEntry = p.next();
        }
        if (p.right == null) {
            this.lastEntry = p.prev();
        }
        if (p.succ()) {
            if (p.pred()) {
                if (q != null) {
                    if (dir) {
                        q.succ(p.right);
                    }
                    else {
                        q.pred(p.left);
                    }
                }
                else {
                    this.tree = (dir ? p.right : p.left);
                }
            }
            else {
                p.prev().right = p.right;
                if (q != null) {
                    if (dir) {
                        q.right = p.left;
                    }
                    else {
                        q.left = p.left;
                    }
                }
                else {
                    this.tree = p.left;
                }
            }
        }
        else {
            Entry<K> r = p.right;
            if (r.pred()) {
                r.left = p.left;
                r.pred(p.pred());
                if (!r.pred()) {
                    r.prev().right = r;
                }
                if (q != null) {
                    if (dir) {
                        q.right = r;
                    }
                    else {
                        q.left = r;
                    }
                }
                else {
                    this.tree = r;
                }
                r.balance(p.balance());
                q = r;
                dir = true;
            }
            else {
                Entry<K> s;
                while (true) {
                    s = r.left;
                    if (s.pred()) {
                        break;
                    }
                    r = s;
                }
                if (s.succ()) {
                    r.pred(s);
                }
                else {
                    r.left = s.right;
                }
                s.left = p.left;
                if (!p.pred()) {
                    (p.prev().right = s).pred(false);
                }
                s.right = p.right;
                s.succ(false);
                if (q != null) {
                    if (dir) {
                        q.right = s;
                    }
                    else {
                        q.left = s;
                    }
                }
                else {
                    this.tree = s;
                }
                s.balance(p.balance());
                q = r;
                dir = false;
            }
        }
        while (q != null) {
            final Entry<K> y = q;
            q = this.parent(y);
            if (!dir) {
                dir = (q != null && q.left != y);
                y.incBalance();
                if (y.balance() == 1) {
                    break;
                }
                if (y.balance() != 2) {
                    continue;
                }
                final Entry<K> x = y.right;
                if (x.balance() == -1) {
                    final Entry<K> w = x.left;
                    x.left = w.right;
                    w.right = x;
                    y.right = w.left;
                    w.left = y;
                    if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                    }
                    else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    }
                    else {
                        x.balance(1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                    }
                    if (q != null) {
                        if (dir) {
                            q.right = w;
                        }
                        else {
                            q.left = w;
                        }
                    }
                    else {
                        this.tree = w;
                    }
                }
                else {
                    if (q != null) {
                        if (dir) {
                            q.right = x;
                        }
                        else {
                            q.left = x;
                        }
                    }
                    else {
                        this.tree = x;
                    }
                    if (x.balance() == 0) {
                        y.right = x.left;
                        x.left = y;
                        x.balance(-1);
                        y.balance(1);
                        break;
                    }
                    if (x.pred()) {
                        y.succ(true);
                        x.pred(false);
                    }
                    else {
                        y.right = x.left;
                    }
                    (x.left = y).balance(0);
                    x.balance(0);
                }
            }
            else {
                dir = (q != null && q.left != y);
                y.decBalance();
                if (y.balance() == -1) {
                    break;
                }
                if (y.balance() != -2) {
                    continue;
                }
                final Entry<K> x = y.left;
                if (x.balance() == 1) {
                    final Entry<K> w = x.right;
                    x.right = w.left;
                    w.left = x;
                    y.left = w.right;
                    w.right = y;
                    if (w.balance() == -1) {
                        x.balance(0);
                        y.balance(1);
                    }
                    else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    }
                    else {
                        x.balance(-1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        x.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        y.pred(w);
                        w.succ(false);
                    }
                    if (q != null) {
                        if (dir) {
                            q.right = w;
                        }
                        else {
                            q.left = w;
                        }
                    }
                    else {
                        this.tree = w;
                    }
                }
                else {
                    if (q != null) {
                        if (dir) {
                            q.right = x;
                        }
                        else {
                            q.left = x;
                        }
                    }
                    else {
                        this.tree = x;
                    }
                    if (x.balance() == 0) {
                        y.left = x.right;
                        x.right = y;
                        x.balance(1);
                        y.balance(-1);
                        break;
                    }
                    if (x.succ()) {
                        y.pred(true);
                        x.succ(false);
                    }
                    else {
                        y.left = x.right;
                    }
                    (x.right = y).balance(0);
                    x.balance(0);
                }
            }
        }
        this.modified = true;
        --this.count;
        return p.value;
    }
    
    @Deprecated
    @Override
    public Float put(final K ok, final Float ov) {
        final float oldValue = this.put(ok, (float)ov);
        return this.modified ? null : Float.valueOf(oldValue);
    }
    
    @Deprecated
    @Override
    public Float remove(final Object ok) {
        final float oldValue = this.removeFloat(ok);
        return this.modified ? Float.valueOf(oldValue) : null;
    }
    
    @Override
    public boolean containsValue(final float v) {
        final ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            final float ev = i.nextFloat();
            if (ev == v) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.entries = null;
        this.values = null;
        this.keys = null;
        final Entry<K> entry = null;
        this.lastEntry = entry;
        this.firstEntry = entry;
    }
    
    @Override
    public boolean containsKey(final Object k) {
        return this.findKey(k) != null;
    }
    
    @Override
    public int size() {
        return this.count;
    }
    
    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    @Override
    public float getFloat(final Object k) {
        final Entry<K> e = (Entry<K>)this.findKey(k);
        return (e == null) ? this.defRetValue : e.value;
    }
    
    @Deprecated
    @Override
    public Float get(final Object ok) {
        final Entry<K> e = (Entry<K>)this.findKey(ok);
        return (e == null) ? null : e.getValue();
    }
    
    @Override
    public K firstKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }
    
    @Override
    public K lastKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }
    
    @Override
    public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>() {
                final Comparator<? super Object2FloatMap.Entry<K>> comparator = new Comparator<Object2FloatMap.Entry<K>>() {
                    @Override
                    public int compare(final Object2FloatMap.Entry<K> x, final Object2FloatMap.Entry<K> y) {
                        return Object2FloatAVLTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());
                    }
                };
                
                @Override
                public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
                    return this.comparator;
                }
                
                @Override
                public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
                    return new EntryIterator();
                }
                
                @Override
                public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(final Object2FloatMap.Entry<K> from) {
                    return new EntryIterator(from.getKey());
                }
                
                @Override
                public boolean contains(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                        return false;
                    }
                    final Entry<K> f = (Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
                    return e.equals(f);
                }
                
                @Override
                public boolean remove(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                        return false;
                    }
                    final Entry<K> f = (Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
                    if (f != null) {
                        Object2FloatAVLTreeMap.this.removeFloat(f.key);
                    }
                    return f != null;
                }
                
                @Override
                public int size() {
                    return Object2FloatAVLTreeMap.this.count;
                }
                
                @Override
                public void clear() {
                    Object2FloatAVLTreeMap.this.clear();
                }
                
                @Override
                public Object2FloatMap.Entry<K> first() {
                    return Object2FloatAVLTreeMap.this.firstEntry;
                }
                
                @Override
                public Object2FloatMap.Entry<K> last() {
                    return Object2FloatAVLTreeMap.this.lastEntry;
                }
                
                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(final Object2FloatMap.Entry<K> from, final Object2FloatMap.Entry<K> to) {
                    return Object2FloatAVLTreeMap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
                }
                
                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(final Object2FloatMap.Entry<K> to) {
                    return Object2FloatAVLTreeMap.this.headMap(to.getKey()).object2FloatEntrySet();
                }
                
                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(final Object2FloatMap.Entry<K> from) {
                    return Object2FloatAVLTreeMap.this.tailMap(from.getKey()).object2FloatEntrySet();
                }
            };
        }
        return this.entries;
    }
    
    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = (ObjectSortedSet<K>)new KeySet();
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
                public boolean contains(final float k) {
                    return Object2FloatAVLTreeMap.this.containsValue(k);
                }
                
                @Override
                public int size() {
                    return Object2FloatAVLTreeMap.this.count;
                }
                
                @Override
                public void clear() {
                    Object2FloatAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.actualComparator;
    }
    
    @Override
    public Object2FloatSortedMap<K> headMap(final K to) {
        return new Submap(null, true, to, false);
    }
    
    @Override
    public Object2FloatSortedMap<K> tailMap(final K from) {
        return new Submap(from, false, null, true);
    }
    
    @Override
    public Object2FloatSortedMap<K> subMap(final K from, final K to) {
        return new Submap(from, false, to, false);
    }
    
    public Object2FloatAVLTreeMap<K> clone() {
        Object2FloatAVLTreeMap<K> c;
        try {
            c = (Object2FloatAVLTreeMap)super.clone();
        }
        catch (final CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            final Entry<K> rp = new Entry<K>();
            final Entry<K> rq = new Entry<K>();
            Entry<K> p = rp;
            rp.left(this.tree);
            Entry<K> q = rq;
            rq.pred(null);
        Block_4:
            while (true) {
                if (!p.pred()) {
                    final Entry<K> e = p.left.clone();
                    e.pred(q.left);
                    e.succ(q);
                    q.left(e);
                    p = p.left;
                    q = q.left;
                }
                else {
                    while (p.succ()) {
                        p = p.right;
                        if (p == null) {
                            break Block_4;
                        }
                        q = q.right;
                    }
                    p = p.right;
                    q = q.right;
                }
                if (!p.succ()) {
                    final Entry<K> e = p.right.clone();
                    e.succ(q.right);
                    e.pred(q);
                    q.right(e);
                }
            }
            q.right = null;
            c.tree = rq.left;
            c.firstEntry = c.tree;
            while (c.firstEntry.left != null) {
                c.firstEntry = c.firstEntry.left;
            }
            c.lastEntry = c.tree;
            while (c.lastEntry.right != null) {
                c.lastEntry = c.lastEntry.right;
            }
            return c;
        }
        return c;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        int n = this.count;
        final EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        while (n-- != 0) {
            final Entry<K> e = i.nextEntry();
            s.writeObject(e.key);
            s.writeFloat(e.value);
        }
    }
    
    private Entry<K> readTree(final ObjectInputStream s, final int n, final Entry<K> pred, final Entry<K> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            final Entry<K> top = new Entry<K>((K)s.readObject(), s.readFloat());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            final Entry<K> top = new Entry<K>((K)s.readObject(), s.readFloat());
            top.right(new Entry<K>((K)s.readObject(), s.readFloat()));
            top.right.pred(top);
            top.balance(1);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        final int rightN = n / 2;
        final int leftN = n - rightN - 1;
        final Entry<K> top2 = new Entry<K>();
        top2.left(this.readTree(s, leftN, pred, top2));
        top2.key = (K)s.readObject();
        top2.value = s.readFloat();
        top2.right(this.readTree(s, rightN, top2, succ));
        if (n == (n & -n)) {
            top2.balance(1);
        }
        return top2;
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setActualComparator();
        this.allocatePaths();
        if (this.count != 0) {
            this.tree = this.readTree(s, this.count, null, null);
            Entry<K> e;
            for (e = this.tree; e.left() != null; e = e.left()) {}
            this.firstEntry = e;
            for (e = this.tree; e.right() != null; e = e.right()) {}
            this.lastEntry = e;
        }
    }
    
    private static <K> int checkTree(final Entry<K> e) {
        return 0;
    }
    
    private static final class Entry<K> implements Cloneable, Object2FloatMap.Entry<K>
    {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        private static final int BALANCE_MASK = 255;
        K key;
        float value;
        Entry<K> left;
        Entry<K> right;
        int info;
        
        Entry() {
        }
        
        Entry(final K k, final float v) {
            this.key = k;
            this.value = v;
            this.info = -1073741824;
        }
        
        Entry<K> left() {
            return ((this.info & 0x40000000) != 0x0) ? null : this.left;
        }
        
        Entry<K> right() {
            return ((this.info & Integer.MIN_VALUE) != 0x0) ? null : this.right;
        }
        
        boolean pred() {
            return (this.info & 0x40000000) != 0x0;
        }
        
        boolean succ() {
            return (this.info & Integer.MIN_VALUE) != 0x0;
        }
        
        void pred(final boolean pred) {
            if (pred) {
                this.info |= 0x40000000;
            }
            else {
                this.info &= 0xBFFFFFFF;
            }
        }
        
        void succ(final boolean succ) {
            if (succ) {
                this.info |= Integer.MIN_VALUE;
            }
            else {
                this.info &= Integer.MAX_VALUE;
            }
        }
        
        void pred(final Entry<K> pred) {
            this.info |= 0x40000000;
            this.left = pred;
        }
        
        void succ(final Entry<K> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }
        
        void left(final Entry<K> left) {
            this.info &= 0xBFFFFFFF;
            this.left = left;
        }
        
        void right(final Entry<K> right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }
        
        int balance() {
            return (byte)this.info;
        }
        
        void balance(final int level) {
            this.info &= 0xFFFFFF00;
            this.info |= (level & 0xFF);
        }
        
        void incBalance() {
            this.info = ((this.info & 0xFFFFFF00) | ((byte)this.info + 1 & 0xFF));
        }
        
        protected void decBalance() {
            this.info = ((this.info & 0xFFFFFF00) | ((byte)this.info - 1 & 0xFF));
        }
        
        Entry<K> next() {
            Entry<K> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0x0) {
                while ((next.info & 0x40000000) == 0x0) {
                    next = next.left;
                }
            }
            return next;
        }
        
        Entry<K> prev() {
            Entry<K> prev = this.left;
            if ((this.info & 0x40000000) == 0x0) {
                while ((prev.info & Integer.MIN_VALUE) == 0x0) {
                    prev = prev.right;
                }
            }
            return prev;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Deprecated
        @Override
        public Float getValue() {
            return this.value;
        }
        
        @Override
        public float getFloatValue() {
            return this.value;
        }
        
        @Override
        public float setValue(final float value) {
            final float oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public Float setValue(final Float value) {
            return this.setValue((float)value);
        }
        
        public Entry<K> clone() {
            Entry<K> c;
            try {
                c = (Entry)super.clone();
            }
            catch (final CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c.key = this.key;
            c.value = this.value;
            c.info = this.info;
            return c;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, Float> e = (Map.Entry<K, Float>)o;
            if (this.key == null) {
                if (e.getKey() != null) {
                    return false;
                }
            }
            else if (!this.key.equals(e.getKey())) {
                return false;
            }
            if (this.value == e.getValue()) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ HashCommon.float2int(this.value);
        }
        
        @Override
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }
    
    private class TreeIterator
    {
        Entry<K> prev;
        Entry<K> next;
        Entry<K> curr;
        int index;
        
        TreeIterator() {
            this.index = 0;
            this.next = Object2FloatAVLTreeMap.this.firstEntry;
        }
        
        TreeIterator(final K k) {
            this.index = 0;
            final Entry<K> locateKey = Object2FloatAVLTreeMap.this.locateKey(k);
            this.next = locateKey;
            if (locateKey != null) {
                if (Object2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                }
                else {
                    this.prev = this.next.prev();
                }
            }
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        public boolean hasPrevious() {
            return this.prev != null;
        }
        
        void updateNext() {
            this.next = this.next.next();
        }
        
        Entry<K> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final Entry<K> next = this.next;
            this.prev = next;
            this.curr = next;
            ++this.index;
            this.updateNext();
            return this.curr;
        }
        
        void updatePrevious() {
            this.prev = this.prev.prev();
        }
        
        Entry<K> previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            final Entry<K> prev = this.prev;
            this.next = prev;
            this.curr = prev;
            --this.index;
            this.updatePrevious();
            return this.curr;
        }
        
        public int nextIndex() {
            return this.index;
        }
        
        public int previousIndex() {
            return this.index - 1;
        }
        
        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
            }
            final Entry<K> curr = this.curr;
            this.prev = curr;
            this.next = curr;
            this.updatePrevious();
            this.updateNext();
            Object2FloatAVLTreeMap.this.removeFloat(this.curr.key);
            this.curr = null;
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
    
    private class EntryIterator extends TreeIterator implements ObjectListIterator<Object2FloatMap.Entry<K>>
    {
        EntryIterator() {
        }
        
        EntryIterator(final K k) {
            super(k);
        }
        
        @Override
        public Object2FloatMap.Entry<K> next() {
            return this.nextEntry();
        }
        
        @Override
        public Object2FloatMap.Entry<K> previous() {
            return this.previousEntry();
        }
        
        @Override
        public void set(final Object2FloatMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Object2FloatMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class KeyIterator extends TreeIterator implements ObjectListIterator<K>
    {
        public KeyIterator() {
        }
        
        public KeyIterator(final K k) {
            super(k);
        }
        
        @Override
        public K next() {
            return this.nextEntry().key;
        }
        
        @Override
        public K previous() {
            return this.previousEntry().key;
        }
        
        @Override
        public void set(final K k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final K k) {
            throw new UnsupportedOperationException();
        }
    }
    
    private class KeySet extends AbstractObject2FloatSortedMap.KeySet
    {
        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public ObjectBidirectionalIterator<K> iterator(final K from) {
            return new KeyIterator(from);
        }
    }
    
    private final class ValueIterator extends TreeIterator implements FloatListIterator
    {
        @Override
        public float nextFloat() {
            return this.nextEntry().value;
        }
        
        @Override
        public float previousFloat() {
            return this.previousEntry().value;
        }
        
        @Override
        public void set(final float v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final float v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Float next() {
            return this.nextEntry().value;
        }
        
        @Override
        public Float previous() {
            return this.previousEntry().value;
        }
        
        @Override
        public void set(final Float ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Float ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class Submap extends AbstractObject2FloatSortedMap<K> implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        K from;
        K to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
        protected transient ObjectSortedSet<K> keys;
        protected transient FloatCollection values;
        final /* synthetic */ Object2FloatAVLTreeMap this$0;
        
        public Submap(final K from, final boolean bottom, final K to, final boolean top) {
            if (!bottom && !top && Object2FloatAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2FloatAVLTreeMap.this.defRetValue;
        }
        
        @Override
        public void clear() {
            final SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }
        
        final boolean in(final K k) {
            return (this.bottom || Object2FloatAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Object2FloatAVLTreeMap.this.compare(k, this.to) < 0);
        }
        
        @Override
        public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>() {
                    @Override
                    public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
                        return new SubmapEntryIterator();
                    }
                    
                    @Override
                    public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(final Object2FloatMap.Entry<K> from) {
                        return new SubmapEntryIterator(from.getKey());
                    }
                    
                    @Override
                    public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
                        return Object2FloatAVLTreeMap.this.entrySet().comparator();
                    }
                    
                    @Override
                    public boolean contains(final Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                            return false;
                        }
                        final Object2FloatAVLTreeMap.Entry<K> f = (Object2FloatAVLTreeMap.Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }
                    
                    @Override
                    public boolean remove(final Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                            return false;
                        }
                        final Object2FloatAVLTreeMap.Entry<K> f = (Object2FloatAVLTreeMap.Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.removeFloat(f.key);
                        }
                        return f != null;
                    }
                    
                    @Override
                    public int size() {
                        int c = 0;
                        final Iterator<?> i = this.iterator();
                        while (i.hasNext()) {
                            ++c;
                            i.next();
                        }
                        return c;
                    }
                    
                    @Override
                    public boolean isEmpty() {
                        return !new SubmapIterator().hasNext();
                    }
                    
                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }
                    
                    @Override
                    public Object2FloatMap.Entry<K> first() {
                        return Submap.this.firstEntry();
                    }
                    
                    @Override
                    public Object2FloatMap.Entry<K> last() {
                        return Submap.this.lastEntry();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(final Object2FloatMap.Entry<K> from, final Object2FloatMap.Entry<K> to) {
                        return Submap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(final Object2FloatMap.Entry<K> to) {
                        return Submap.this.headMap(to.getKey()).object2FloatEntrySet();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(final Object2FloatMap.Entry<K> from) {
                        return Submap.this.tailMap(from.getKey()).object2FloatEntrySet();
                    }
                };
            }
            return this.entries;
        }
        
        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = (ObjectSortedSet<K>)new KeySet();
            }
            return this.keys;
        }
        
        @Override
        public FloatCollection values() {
            if (this.values == null) {
                this.values = new AbstractFloatCollection() {
                    @Override
                    public FloatIterator iterator() {
                        return new SubmapValueIterator();
                    }
                    
                    @Override
                    public boolean contains(final float k) {
                        return Submap.this.containsValue(k);
                    }
                    
                    @Override
                    public int size() {
                        return Submap.this.size();
                    }
                    
                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }
                };
            }
            return this.values;
        }
        
        @Override
        public boolean containsKey(final Object k) {
            return this.in(k) && Object2FloatAVLTreeMap.this.containsKey(k);
        }
        
        @Override
        public boolean containsValue(final float v) {
            final SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                final float ev = i.nextEntry().value;
                if (ev == v) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public float getFloat(final Object k) {
            final K kk = (K)k;
            final Object2FloatAVLTreeMap.Entry<K> e;
            return (this.in(kk) && (e = Object2FloatAVLTreeMap.this.findKey(kk)) != null) ? e.value : this.defRetValue;
        }
        
        @Deprecated
        @Override
        public Float get(final Object ok) {
            final K kk = (K)ok;
            final Object2FloatAVLTreeMap.Entry<K> e;
            return (this.in(kk) && (e = Object2FloatAVLTreeMap.this.findKey(kk)) != null) ? e.getValue() : null;
        }
        
        @Override
        public float put(final K k, final float v) {
            Object2FloatAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            final float oldValue = Object2FloatAVLTreeMap.this.put(k, v);
            return Object2FloatAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }
        
        @Deprecated
        @Override
        public Float put(final K ok, final Float ov) {
            final float oldValue = this.put(ok, (float)ov);
            return Object2FloatAVLTreeMap.this.modified ? null : Float.valueOf(oldValue);
        }
        
        @Override
        public float removeFloat(final Object k) {
            Object2FloatAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            final float oldValue = Object2FloatAVLTreeMap.this.removeFloat(k);
            return Object2FloatAVLTreeMap.this.modified ? oldValue : this.defRetValue;
        }
        
        @Deprecated
        @Override
        public Float remove(final Object ok) {
            final float oldValue = this.removeFloat(ok);
            return Object2FloatAVLTreeMap.this.modified ? Float.valueOf(oldValue) : null;
        }
        
        @Override
        public int size() {
            final SubmapIterator i = new SubmapIterator();
            int n = 0;
            while (i.hasNext()) {
                ++n;
                i.nextEntry();
            }
            return n;
        }
        
        @Override
        public boolean isEmpty() {
            return !new SubmapIterator().hasNext();
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return Object2FloatAVLTreeMap.this.actualComparator;
        }
        
        @Override
        public Object2FloatSortedMap<K> headMap(final K to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return (Object2FloatAVLTreeMap.this.compare(to, this.to) < 0) ? new Submap(this.from, this.bottom, to, false) : this;
        }
        
        @Override
        public Object2FloatSortedMap<K> tailMap(final K from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return (Object2FloatAVLTreeMap.this.compare(from, this.from) > 0) ? new Submap(from, false, this.to, this.top) : this;
        }
        
        @Override
        public Object2FloatSortedMap<K> subMap(K from, K to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                to = ((Object2FloatAVLTreeMap.this.compare(to, this.to) < 0) ? to : this.to);
            }
            if (!this.bottom) {
                from = ((Object2FloatAVLTreeMap.this.compare(from, this.from) > 0) ? from : this.from);
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }
        
        public Object2FloatAVLTreeMap.Entry<K> firstEntry() {
            if (Object2FloatAVLTreeMap.this.tree == null) {
                return null;
            }
            Object2FloatAVLTreeMap.Entry<K> e;
            if (this.bottom) {
                e = Object2FloatAVLTreeMap.this.firstEntry;
            }
            else {
                e = Object2FloatAVLTreeMap.this.locateKey(this.from);
                if (Object2FloatAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || (!this.top && Object2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0)) {
                return null;
            }
            return e;
        }
        
        public Object2FloatAVLTreeMap.Entry<K> lastEntry() {
            if (Object2FloatAVLTreeMap.this.tree == null) {
                return null;
            }
            Object2FloatAVLTreeMap.Entry<K> e;
            if (this.top) {
                e = Object2FloatAVLTreeMap.this.lastEntry;
            }
            else {
                e = Object2FloatAVLTreeMap.this.locateKey(this.to);
                if (Object2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || (!this.bottom && Object2FloatAVLTreeMap.this.compare(e.key, this.from) < 0)) {
                return null;
            }
            return e;
        }
        
        @Override
        public K firstKey() {
            final Object2FloatAVLTreeMap.Entry<K> e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        @Override
        public K lastKey() {
            final Object2FloatAVLTreeMap.Entry<K> e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        private class KeySet extends AbstractObject2FloatSortedMap.KeySet
        {
            @Override
            public ObjectBidirectionalIterator<K> iterator() {
                return new SubmapKeyIterator();
            }
            
            @Override
            public ObjectBidirectionalIterator<K> iterator(final K from) {
                return new SubmapKeyIterator(from);
            }
        }
        
        private class SubmapIterator extends TreeIterator
        {
            SubmapIterator() {
                Submap.this.this$0.super();
                this.next = Submap.this.firstEntry();
            }
            
            SubmapIterator(final Submap submap, final K k) {
                this(submap);
                if (this.next != null) {
                    if (!submap.bottom && submap.this$0.compare(k, this.next.key) < 0) {
                        this.prev = null;
                    }
                    else {
                        if (!submap.top) {
                            final Object2FloatAVLTreeMap this$0 = submap.this$0;
                            final Object2FloatAVLTreeMap.Entry<K> lastEntry = submap.lastEntry();
                            this.prev = lastEntry;
                            if (this$0.compare(k, lastEntry.key) >= 0) {
                                this.next = null;
                                return;
                            }
                        }
                        this.next = submap.this$0.locateKey(k);
                        if (submap.this$0.compare(this.next.key, k) <= 0) {
                            this.prev = this.next;
                            this.next = this.next.next();
                        }
                        else {
                            this.prev = this.next.prev();
                        }
                    }
                }
            }
            
            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Object2FloatAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }
            
            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Object2FloatAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }
        
        private class SubmapEntryIterator extends SubmapIterator implements ObjectListIterator<Object2FloatMap.Entry<K>>
        {
            SubmapEntryIterator() {
            }
            
            SubmapEntryIterator(final K k) {
                super(k);
            }
            
            @Override
            public Object2FloatMap.Entry<K> next() {
                return this.nextEntry();
            }
            
            @Override
            public Object2FloatMap.Entry<K> previous() {
                return this.previousEntry();
            }
            
            @Override
            public void set(final Object2FloatMap.Entry<K> ok) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Object2FloatMap.Entry<K> ok) {
                throw new UnsupportedOperationException();
            }
        }
        
        private final class SubmapKeyIterator extends SubmapIterator implements ObjectListIterator<K>
        {
            public SubmapKeyIterator() {
            }
            
            public SubmapKeyIterator(final K from) {
                super(from);
            }
            
            @Override
            public K next() {
                return this.nextEntry().key;
            }
            
            @Override
            public K previous() {
                return this.previousEntry().key;
            }
            
            @Override
            public void set(final K k) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final K k) {
                throw new UnsupportedOperationException();
            }
        }
        
        private final class SubmapValueIterator extends SubmapIterator implements FloatListIterator
        {
            @Override
            public float nextFloat() {
                return this.nextEntry().value;
            }
            
            @Override
            public float previousFloat() {
                return this.previousEntry().value;
            }
            
            @Override
            public void set(final float v) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final float v) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Float next() {
                return this.nextEntry().value;
            }
            
            @Override
            public Float previous() {
                return this.previousEntry().value;
            }
            
            @Override
            public void set(final Float ok) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Float ok) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
