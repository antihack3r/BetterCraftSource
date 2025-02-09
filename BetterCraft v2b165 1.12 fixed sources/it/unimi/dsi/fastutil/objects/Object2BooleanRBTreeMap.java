// 
// Decompiled by Procyon v0.6.0
// 

package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map;
import java.util.Comparator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import java.io.Serializable;

public class Object2BooleanRBTreeMap<K> extends AbstractObject2BooleanSortedMap<K> implements Serializable, Cloneable
{
    protected transient Entry<K> tree;
    protected int count;
    protected transient Entry<K> firstEntry;
    protected transient Entry<K> lastEntry;
    protected transient ObjectSortedSet<Object2BooleanMap.Entry<K>> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient BooleanCollection values;
    protected transient boolean modified;
    protected Comparator<? super K> storedComparator;
    protected transient Comparator<? super K> actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private static final boolean ASSERTS = false;
    private transient boolean[] dirPath;
    private transient Entry<K>[] nodePath;
    
    public Object2BooleanRBTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }
    
    private void setActualComparator() {
        this.actualComparator = this.storedComparator;
    }
    
    public Object2BooleanRBTreeMap(final Comparator<? super K> c) {
        this();
        this.storedComparator = c;
        this.setActualComparator();
    }
    
    public Object2BooleanRBTreeMap(final Map<? extends K, ? extends Boolean> m) {
        this();
        this.putAll(m);
    }
    
    public Object2BooleanRBTreeMap(final SortedMap<K, Boolean> m) {
        this(m.comparator());
        this.putAll((Map<? extends K, ? extends Boolean>)m);
    }
    
    public Object2BooleanRBTreeMap(final Object2BooleanMap<? extends K> m) {
        this();
        this.putAll(m);
    }
    
    public Object2BooleanRBTreeMap(final Object2BooleanSortedMap<K> m) {
        this(m.comparator());
        this.putAll((Map<? extends K, ? extends Boolean>)m);
    }
    
    public Object2BooleanRBTreeMap(final K[] k, final boolean[] v, final Comparator<? super K> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }
    
    public Object2BooleanRBTreeMap(final K[] k, final boolean[] v) {
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
        this.dirPath = new boolean[64];
        this.nodePath = new Entry[64];
    }
    
    @Override
    public boolean put(final K k, final boolean v) {
        final Entry<K> e = this.add(k);
        final boolean oldValue = e.value;
        e.value = v;
        return oldValue;
    }
    
    private Entry<K> add(final K k) {
        this.modified = false;
        int maxDepth = 0;
        Entry<K> e = null;
        Label_0908: {
            if (this.tree != null) {
                Entry<K> p = this.tree;
                int i = 0;
                int cmp;
                while ((cmp = this.compare(k, p.key)) != 0) {
                    this.nodePath[i] = p;
                    final boolean[] dirPath = this.dirPath;
                    final int n = i++;
                    final boolean b = cmp > 0;
                    dirPath[n] = b;
                    if (b) {
                        if (!p.succ()) {
                            p = p.right;
                            continue;
                        }
                        ++this.count;
                        e = new Entry<K>(k, this.defRetValue);
                        if (p.right == null) {
                            this.lastEntry = e;
                        }
                        e.left = p;
                        e.right = p.right;
                        p.right(e);
                    }
                    else {
                        if (!p.pred()) {
                            p = p.left;
                            continue;
                        }
                        ++this.count;
                        e = new Entry<K>(k, this.defRetValue);
                        if (p.left == null) {
                            this.firstEntry = e;
                        }
                        e.right = p;
                        e.left = p.left;
                        p.left(e);
                    }
                    this.modified = true;
                    maxDepth = i--;
                    while (i > 0 && !this.nodePath[i].black()) {
                        if (!this.dirPath[i - 1]) {
                            Entry<K> y = this.nodePath[i - 1].right;
                            if (!this.nodePath[i - 1].succ() && !y.black()) {
                                this.nodePath[i].black(true);
                                y.black(true);
                                this.nodePath[i - 1].black(false);
                                i -= 2;
                            }
                            else {
                                if (!this.dirPath[i]) {
                                    y = this.nodePath[i];
                                }
                                else {
                                    final Entry<K> x = this.nodePath[i];
                                    y = x.right;
                                    x.right = y.left;
                                    y.left = x;
                                    this.nodePath[i - 1].left = y;
                                    if (y.pred()) {
                                        y.pred(false);
                                        x.succ(y);
                                    }
                                }
                                final Entry<K> x = this.nodePath[i - 1];
                                x.black(false);
                                y.black(true);
                                x.left = y.right;
                                y.right = x;
                                if (i < 2) {
                                    this.tree = y;
                                }
                                else if (this.dirPath[i - 2]) {
                                    this.nodePath[i - 2].right = y;
                                }
                                else {
                                    this.nodePath[i - 2].left = y;
                                }
                                if (y.succ()) {
                                    y.succ(false);
                                    x.pred(y);
                                    break;
                                }
                                break;
                            }
                        }
                        else {
                            Entry<K> y = this.nodePath[i - 1].left;
                            if (!this.nodePath[i - 1].pred() && !y.black()) {
                                this.nodePath[i].black(true);
                                y.black(true);
                                this.nodePath[i - 1].black(false);
                                i -= 2;
                            }
                            else {
                                if (this.dirPath[i]) {
                                    y = this.nodePath[i];
                                }
                                else {
                                    final Entry<K> x = this.nodePath[i];
                                    y = x.left;
                                    x.left = y.right;
                                    y.right = x;
                                    this.nodePath[i - 1].right = y;
                                    if (y.succ()) {
                                        y.succ(false);
                                        x.pred(y);
                                    }
                                }
                                final Entry<K> x = this.nodePath[i - 1];
                                x.black(false);
                                y.black(true);
                                x.right = y.left;
                                y.left = x;
                                if (i < 2) {
                                    this.tree = y;
                                }
                                else if (this.dirPath[i - 2]) {
                                    this.nodePath[i - 2].right = y;
                                }
                                else {
                                    this.nodePath[i - 2].left = y;
                                }
                                if (y.pred()) {
                                    y.pred(false);
                                    x.succ(y);
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    break Label_0908;
                }
                while (i-- != 0) {
                    this.nodePath[i] = null;
                }
                return p;
            }
            ++this.count;
            final Entry<K> tree = new Entry<K>(k, this.defRetValue);
            this.firstEntry = tree;
            this.lastEntry = tree;
            this.tree = tree;
            e = tree;
        }
        this.tree.black(true);
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return e;
    }
    
    @Override
    public boolean removeBoolean(final Object k) {
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry<K> p = this.tree;
        int i = 0;
        final K kk = (K)k;
        int cmp;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            this.dirPath[i] = (cmp > 0);
            this.nodePath[i] = p;
            if (this.dirPath[i++]) {
                if ((p = p.right()) == null) {
                    while (i-- != 0) {
                        this.nodePath[i] = null;
                    }
                    return this.defRetValue;
                }
                continue;
            }
            else {
                if ((p = p.left()) == null) {
                    while (i-- != 0) {
                        this.nodePath[i] = null;
                    }
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
                if (i == 0) {
                    this.tree = p.left;
                }
                else if (this.dirPath[i - 1]) {
                    this.nodePath[i - 1].succ(p.right);
                }
                else {
                    this.nodePath[i - 1].pred(p.left);
                }
            }
            else {
                p.prev().right = p.right;
                if (i == 0) {
                    this.tree = p.left;
                }
                else if (this.dirPath[i - 1]) {
                    this.nodePath[i - 1].right = p.left;
                }
                else {
                    this.nodePath[i - 1].left = p.left;
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
                if (i == 0) {
                    this.tree = r;
                }
                else if (this.dirPath[i - 1]) {
                    this.nodePath[i - 1].right = r;
                }
                else {
                    this.nodePath[i - 1].left = r;
                }
                final boolean color = r.black();
                r.black(p.black());
                p.black(color);
                this.dirPath[i] = true;
                this.nodePath[i++] = r;
            }
            else {
                final int j = i++;
                Entry<K> s;
                while (true) {
                    this.dirPath[i] = false;
                    this.nodePath[i++] = r;
                    s = r.left;
                    if (s.pred()) {
                        break;
                    }
                    r = s;
                }
                this.dirPath[j] = true;
                this.nodePath[j] = s;
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
                s.right(p.right);
                final boolean color = s.black();
                s.black(p.black());
                p.black(color);
                if (j == 0) {
                    this.tree = s;
                }
                else if (this.dirPath[j - 1]) {
                    this.nodePath[j - 1].right = s;
                }
                else {
                    this.nodePath[j - 1].left = s;
                }
            }
        }
        int maxDepth = i;
        if (p.black()) {
            while (i > 0) {
                if ((this.dirPath[i - 1] && !this.nodePath[i - 1].succ()) || (!this.dirPath[i - 1] && !this.nodePath[i - 1].pred())) {
                    final Entry<K> x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                    if (!x.black()) {
                        x.black(true);
                        break;
                    }
                }
                if (!this.dirPath[i - 1]) {
                    Entry<K> w = this.nodePath[i - 1].right;
                    if (!w.black()) {
                        w.black(true);
                        this.nodePath[i - 1].black(false);
                        this.nodePath[i - 1].right = w.left;
                        w.left = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        }
                        else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        }
                        else {
                            this.nodePath[i - 2].left = w;
                        }
                        this.nodePath[i] = this.nodePath[i - 1];
                        this.dirPath[i] = false;
                        this.nodePath[i - 1] = w;
                        if (maxDepth == i++) {
                            ++maxDepth;
                        }
                        w = this.nodePath[i - 1].right;
                    }
                    if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                        w.black(false);
                    }
                    else {
                        if (w.succ() || w.right.black()) {
                            final Entry<K> y = w.left;
                            y.black(true);
                            w.black(false);
                            w.left = y.right;
                            y.right = w;
                            final Entry<K> entry = this.nodePath[i - 1];
                            final Entry<K> right = y;
                            entry.right = right;
                            w = right;
                            if (w.succ()) {
                                w.succ(false);
                                w.right.pred(w);
                            }
                        }
                        w.black(this.nodePath[i - 1].black());
                        this.nodePath[i - 1].black(true);
                        w.right.black(true);
                        this.nodePath[i - 1].right = w.left;
                        w.left = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        }
                        else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        }
                        else {
                            this.nodePath[i - 2].left = w;
                        }
                        if (w.pred()) {
                            w.pred(false);
                            this.nodePath[i - 1].succ(w);
                            break;
                        }
                        break;
                    }
                }
                else {
                    Entry<K> w = this.nodePath[i - 1].left;
                    if (!w.black()) {
                        w.black(true);
                        this.nodePath[i - 1].black(false);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        }
                        else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        }
                        else {
                            this.nodePath[i - 2].left = w;
                        }
                        this.nodePath[i] = this.nodePath[i - 1];
                        this.dirPath[i] = true;
                        this.nodePath[i - 1] = w;
                        if (maxDepth == i++) {
                            ++maxDepth;
                        }
                        w = this.nodePath[i - 1].left;
                    }
                    if ((w.pred() || w.left.black()) && (w.succ() || w.right.black())) {
                        w.black(false);
                    }
                    else {
                        if (w.pred() || w.left.black()) {
                            final Entry<K> y = w.right;
                            y.black(true);
                            w.black(false);
                            w.right = y.left;
                            y.left = w;
                            final Entry<K> entry2 = this.nodePath[i - 1];
                            final Entry<K> left = y;
                            entry2.left = left;
                            w = left;
                            if (w.pred()) {
                                w.pred(false);
                                w.left.succ(w);
                            }
                        }
                        w.black(this.nodePath[i - 1].black());
                        this.nodePath[i - 1].black(true);
                        w.left.black(true);
                        this.nodePath[i - 1].left = w.right;
                        w.right = this.nodePath[i - 1];
                        if (i < 2) {
                            this.tree = w;
                        }
                        else if (this.dirPath[i - 2]) {
                            this.nodePath[i - 2].right = w;
                        }
                        else {
                            this.nodePath[i - 2].left = w;
                        }
                        if (w.succ()) {
                            w.succ(false);
                            this.nodePath[i - 1].pred(w);
                            break;
                        }
                        break;
                    }
                }
                --i;
            }
            if (this.tree != null) {
                this.tree.black(true);
            }
        }
        this.modified = true;
        --this.count;
        while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
        }
        return p.value;
    }
    
    @Deprecated
    @Override
    public Boolean put(final K ok, final Boolean ov) {
        final boolean oldValue = this.put(ok, (boolean)ov);
        return this.modified ? null : Boolean.valueOf(oldValue);
    }
    
    @Deprecated
    @Override
    public Boolean remove(final Object ok) {
        final boolean oldValue = this.removeBoolean(ok);
        return this.modified ? Boolean.valueOf(oldValue) : null;
    }
    
    @Override
    public boolean containsValue(final boolean v) {
        final ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            final boolean ev = i.nextBoolean();
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
    public boolean getBoolean(final Object k) {
        final Entry<K> e = (Entry<K>)this.findKey(k);
        return (e == null) ? this.defRetValue : e.value;
    }
    
    @Deprecated
    @Override
    public Boolean get(final Object ok) {
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
    public ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2BooleanMap.Entry<K>>() {
                final Comparator<? super Object2BooleanMap.Entry<K>> comparator = new Comparator<Object2BooleanMap.Entry<K>>() {
                    @Override
                    public int compare(final Object2BooleanMap.Entry<K> x, final Object2BooleanMap.Entry<K> y) {
                        return Object2BooleanRBTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());
                    }
                };
                
                @Override
                public Comparator<? super Object2BooleanMap.Entry<K>> comparator() {
                    return this.comparator;
                }
                
                @Override
                public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator() {
                    return new EntryIterator();
                }
                
                @Override
                public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator(final Object2BooleanMap.Entry<K> from) {
                    return new EntryIterator(from.getKey());
                }
                
                @Override
                public boolean contains(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                        return false;
                    }
                    final Entry<K> f = (Entry<K>)Object2BooleanRBTreeMap.this.findKey(e.getKey());
                    return e.equals(f);
                }
                
                @Override
                public boolean remove(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                        return false;
                    }
                    final Entry<K> f = (Entry<K>)Object2BooleanRBTreeMap.this.findKey(e.getKey());
                    if (f != null) {
                        Object2BooleanRBTreeMap.this.removeBoolean(f.key);
                    }
                    return f != null;
                }
                
                @Override
                public int size() {
                    return Object2BooleanRBTreeMap.this.count;
                }
                
                @Override
                public void clear() {
                    Object2BooleanRBTreeMap.this.clear();
                }
                
                @Override
                public Object2BooleanMap.Entry<K> first() {
                    return Object2BooleanRBTreeMap.this.firstEntry;
                }
                
                @Override
                public Object2BooleanMap.Entry<K> last() {
                    return Object2BooleanRBTreeMap.this.lastEntry;
                }
                
                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> subSet(final Object2BooleanMap.Entry<K> from, final Object2BooleanMap.Entry<K> to) {
                    return Object2BooleanRBTreeMap.this.subMap(from.getKey(), to.getKey()).object2BooleanEntrySet();
                }
                
                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> headSet(final Object2BooleanMap.Entry<K> to) {
                    return Object2BooleanRBTreeMap.this.headMap(to.getKey()).object2BooleanEntrySet();
                }
                
                @Override
                public ObjectSortedSet<Object2BooleanMap.Entry<K>> tailSet(final Object2BooleanMap.Entry<K> from) {
                    return Object2BooleanRBTreeMap.this.tailMap(from.getKey()).object2BooleanEntrySet();
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
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection() {
                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }
                
                @Override
                public boolean contains(final boolean k) {
                    return Object2BooleanRBTreeMap.this.containsValue(k);
                }
                
                @Override
                public int size() {
                    return Object2BooleanRBTreeMap.this.count;
                }
                
                @Override
                public void clear() {
                    Object2BooleanRBTreeMap.this.clear();
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
    public Object2BooleanSortedMap<K> headMap(final K to) {
        return new Submap(null, true, to, false);
    }
    
    @Override
    public Object2BooleanSortedMap<K> tailMap(final K from) {
        return new Submap(from, false, null, true);
    }
    
    @Override
    public Object2BooleanSortedMap<K> subMap(final K from, final K to) {
        return new Submap(from, false, to, false);
    }
    
    public Object2BooleanRBTreeMap<K> clone() {
        Object2BooleanRBTreeMap<K> c;
        try {
            c = (Object2BooleanRBTreeMap)super.clone();
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
            s.writeBoolean(e.value);
        }
    }
    
    private Entry<K> readTree(final ObjectInputStream s, final int n, final Entry<K> pred, final Entry<K> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            final Entry<K> top = new Entry<K>((K)s.readObject(), s.readBoolean());
            top.pred(pred);
            top.succ(succ);
            top.black(true);
            return top;
        }
        if (n == 2) {
            final Entry<K> top = new Entry<K>((K)s.readObject(), s.readBoolean());
            top.black(true);
            top.right(new Entry<K>((K)s.readObject(), s.readBoolean()));
            top.right.pred(top);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        final int rightN = n / 2;
        final int leftN = n - rightN - 1;
        final Entry<K> top2 = new Entry<K>();
        top2.left(this.readTree(s, leftN, pred, top2));
        top2.key = (K)s.readObject();
        top2.value = s.readBoolean();
        top2.black(true);
        top2.right(this.readTree(s, rightN, top2, succ));
        if (n + 2 == (n + 2 & -(n + 2))) {
            top2.right.black(false);
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
    
    private void checkNodePath() {
    }
    
    private static <K> int checkTree(final Entry<K> e, final int d, final int D) {
        return 0;
    }
    
    private static final class Entry<K> implements Cloneable, Object2BooleanMap.Entry<K>
    {
        private static final int BLACK_MASK = 1;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        K key;
        boolean value;
        Entry<K> left;
        Entry<K> right;
        int info;
        
        Entry() {
        }
        
        Entry(final K k, final boolean v) {
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
        
        boolean black() {
            return (this.info & 0x1) != 0x0;
        }
        
        void black(final boolean black) {
            if (black) {
                this.info |= 0x1;
            }
            else {
                this.info &= 0xFFFFFFFE;
            }
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
        public Boolean getValue() {
            return this.value;
        }
        
        @Override
        public boolean getBooleanValue() {
            return this.value;
        }
        
        @Override
        public boolean setValue(final boolean value) {
            final boolean oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public Boolean setValue(final Boolean value) {
            return this.setValue((boolean)value);
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
            final Map.Entry<K, Boolean> e = (Map.Entry<K, Boolean>)o;
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
            return this.key.hashCode() ^ (this.value ? 1231 : 1237);
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
            this.next = Object2BooleanRBTreeMap.this.firstEntry;
        }
        
        TreeIterator(final K k) {
            this.index = 0;
            final Entry<K> locateKey = Object2BooleanRBTreeMap.this.locateKey(k);
            this.next = locateKey;
            if (locateKey != null) {
                if (Object2BooleanRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            Object2BooleanRBTreeMap.this.removeBoolean(this.curr.key);
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
    
    private class EntryIterator extends TreeIterator implements ObjectListIterator<Object2BooleanMap.Entry<K>>
    {
        EntryIterator() {
        }
        
        EntryIterator(final K k) {
            super(k);
        }
        
        @Override
        public Object2BooleanMap.Entry<K> next() {
            return this.nextEntry();
        }
        
        @Override
        public Object2BooleanMap.Entry<K> previous() {
            return this.previousEntry();
        }
        
        @Override
        public void set(final Object2BooleanMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Object2BooleanMap.Entry<K> ok) {
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
    
    private class KeySet extends AbstractObject2BooleanSortedMap.KeySet
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
    
    private final class ValueIterator extends TreeIterator implements BooleanListIterator
    {
        @Override
        public boolean nextBoolean() {
            return this.nextEntry().value;
        }
        
        @Override
        public boolean previousBoolean() {
            return this.previousEntry().value;
        }
        
        @Override
        public void set(final boolean v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final boolean v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Boolean next() {
            return this.nextEntry().value;
        }
        
        @Override
        public Boolean previous() {
            return this.previousEntry().value;
        }
        
        @Override
        public void set(final Boolean ok) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final Boolean ok) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class Submap extends AbstractObject2BooleanSortedMap<K> implements Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        K from;
        K to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Object2BooleanMap.Entry<K>> entries;
        protected transient ObjectSortedSet<K> keys;
        protected transient BooleanCollection values;
        final /* synthetic */ Object2BooleanRBTreeMap this$0;
        
        public Submap(final K from, final boolean bottom, final K to, final boolean top) {
            if (!bottom && !top && Object2BooleanRBTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2BooleanRBTreeMap.this.defRetValue;
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
            return (this.bottom || Object2BooleanRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Object2BooleanRBTreeMap.this.compare(k, this.to) < 0);
        }
        
        @Override
        public ObjectSortedSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Object2BooleanMap.Entry<K>>() {
                    @Override
                    public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator() {
                        return new SubmapEntryIterator();
                    }
                    
                    @Override
                    public ObjectBidirectionalIterator<Object2BooleanMap.Entry<K>> iterator(final Object2BooleanMap.Entry<K> from) {
                        return new SubmapEntryIterator(from.getKey());
                    }
                    
                    @Override
                    public Comparator<? super Object2BooleanMap.Entry<K>> comparator() {
                        return Object2BooleanRBTreeMap.this.object2BooleanEntrySet().comparator();
                    }
                    
                    @Override
                    public boolean contains(final Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                            return false;
                        }
                        final Object2BooleanRBTreeMap.Entry<K> f = (Object2BooleanRBTreeMap.Entry<K>)Object2BooleanRBTreeMap.this.findKey(e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }
                    
                    @Override
                    public boolean remove(final Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                            return false;
                        }
                        final Object2BooleanRBTreeMap.Entry<K> f = (Object2BooleanRBTreeMap.Entry<K>)Object2BooleanRBTreeMap.this.findKey(e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.removeBoolean(f.key);
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
                    public Object2BooleanMap.Entry<K> first() {
                        return Submap.this.firstEntry();
                    }
                    
                    @Override
                    public Object2BooleanMap.Entry<K> last() {
                        return Submap.this.lastEntry();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> subSet(final Object2BooleanMap.Entry<K> from, final Object2BooleanMap.Entry<K> to) {
                        return Submap.this.subMap(from.getKey(), to.getKey()).object2BooleanEntrySet();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> headSet(final Object2BooleanMap.Entry<K> to) {
                        return Submap.this.headMap(to.getKey()).object2BooleanEntrySet();
                    }
                    
                    @Override
                    public ObjectSortedSet<Object2BooleanMap.Entry<K>> tailSet(final Object2BooleanMap.Entry<K> from) {
                        return Submap.this.tailMap(from.getKey()).object2BooleanEntrySet();
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
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = new AbstractBooleanCollection() {
                    @Override
                    public BooleanIterator iterator() {
                        return new SubmapValueIterator();
                    }
                    
                    @Override
                    public boolean contains(final boolean k) {
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
            return this.in(k) && Object2BooleanRBTreeMap.this.containsKey(k);
        }
        
        @Override
        public boolean containsValue(final boolean v) {
            final SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                final boolean ev = i.nextEntry().value;
                if (ev == v) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean getBoolean(final Object k) {
            final K kk = (K)k;
            final Object2BooleanRBTreeMap.Entry<K> e;
            return (this.in(kk) && (e = Object2BooleanRBTreeMap.this.findKey(kk)) != null) ? e.value : this.defRetValue;
        }
        
        @Deprecated
        @Override
        public Boolean get(final Object ok) {
            final K kk = (K)ok;
            final Object2BooleanRBTreeMap.Entry<K> e;
            return (this.in(kk) && (e = Object2BooleanRBTreeMap.this.findKey(kk)) != null) ? e.getValue() : null;
        }
        
        @Override
        public boolean put(final K k, final boolean v) {
            Object2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            final boolean oldValue = Object2BooleanRBTreeMap.this.put(k, v);
            return Object2BooleanRBTreeMap.this.modified ? this.defRetValue : oldValue;
        }
        
        @Deprecated
        @Override
        public Boolean put(final K ok, final Boolean ov) {
            final boolean oldValue = this.put(ok, (boolean)ov);
            return Object2BooleanRBTreeMap.this.modified ? null : Boolean.valueOf(oldValue);
        }
        
        @Override
        public boolean removeBoolean(final Object k) {
            Object2BooleanRBTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            final boolean oldValue = Object2BooleanRBTreeMap.this.removeBoolean(k);
            return Object2BooleanRBTreeMap.this.modified ? oldValue : this.defRetValue;
        }
        
        @Deprecated
        @Override
        public Boolean remove(final Object ok) {
            final boolean oldValue = this.removeBoolean(ok);
            return Object2BooleanRBTreeMap.this.modified ? Boolean.valueOf(oldValue) : null;
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
            return Object2BooleanRBTreeMap.this.actualComparator;
        }
        
        @Override
        public Object2BooleanSortedMap<K> headMap(final K to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return (Object2BooleanRBTreeMap.this.compare(to, this.to) < 0) ? new Submap(this.from, this.bottom, to, false) : this;
        }
        
        @Override
        public Object2BooleanSortedMap<K> tailMap(final K from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return (Object2BooleanRBTreeMap.this.compare(from, this.from) > 0) ? new Submap(from, false, this.to, this.top) : this;
        }
        
        @Override
        public Object2BooleanSortedMap<K> subMap(K from, K to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                to = ((Object2BooleanRBTreeMap.this.compare(to, this.to) < 0) ? to : this.to);
            }
            if (!this.bottom) {
                from = ((Object2BooleanRBTreeMap.this.compare(from, this.from) > 0) ? from : this.from);
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }
        
        public Object2BooleanRBTreeMap.Entry<K> firstEntry() {
            if (Object2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            Object2BooleanRBTreeMap.Entry<K> e;
            if (this.bottom) {
                e = Object2BooleanRBTreeMap.this.firstEntry;
            }
            else {
                e = Object2BooleanRBTreeMap.this.locateKey(this.from);
                if (Object2BooleanRBTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || (!this.top && Object2BooleanRBTreeMap.this.compare(e.key, this.to) >= 0)) {
                return null;
            }
            return e;
        }
        
        public Object2BooleanRBTreeMap.Entry<K> lastEntry() {
            if (Object2BooleanRBTreeMap.this.tree == null) {
                return null;
            }
            Object2BooleanRBTreeMap.Entry<K> e;
            if (this.top) {
                e = Object2BooleanRBTreeMap.this.lastEntry;
            }
            else {
                e = Object2BooleanRBTreeMap.this.locateKey(this.to);
                if (Object2BooleanRBTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || (!this.bottom && Object2BooleanRBTreeMap.this.compare(e.key, this.from) < 0)) {
                return null;
            }
            return e;
        }
        
        @Override
        public K firstKey() {
            final Object2BooleanRBTreeMap.Entry<K> e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        @Override
        public K lastKey() {
            final Object2BooleanRBTreeMap.Entry<K> e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        private class KeySet extends AbstractObject2BooleanSortedMap.KeySet
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
                            final Object2BooleanRBTreeMap this$0 = submap.this$0;
                            final Object2BooleanRBTreeMap.Entry<K> lastEntry = submap.lastEntry();
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
                if (!Submap.this.bottom && this.prev != null && Object2BooleanRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }
            
            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Object2BooleanRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }
        
        private class SubmapEntryIterator extends SubmapIterator implements ObjectListIterator<Object2BooleanMap.Entry<K>>
        {
            SubmapEntryIterator() {
            }
            
            SubmapEntryIterator(final K k) {
                super(k);
            }
            
            @Override
            public Object2BooleanMap.Entry<K> next() {
                return this.nextEntry();
            }
            
            @Override
            public Object2BooleanMap.Entry<K> previous() {
                return this.previousEntry();
            }
            
            @Override
            public void set(final Object2BooleanMap.Entry<K> ok) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Object2BooleanMap.Entry<K> ok) {
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
        
        private final class SubmapValueIterator extends SubmapIterator implements BooleanListIterator
        {
            @Override
            public boolean nextBoolean() {
                return this.nextEntry().value;
            }
            
            @Override
            public boolean previousBoolean() {
                return this.previousEntry().value;
            }
            
            @Override
            public void set(final boolean v) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final boolean v) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Boolean next() {
                return this.nextEntry().value;
            }
            
            @Override
            public Boolean previous() {
                return this.previousEntry().value;
            }
            
            @Override
            public void set(final Boolean ok) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Boolean ok) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
