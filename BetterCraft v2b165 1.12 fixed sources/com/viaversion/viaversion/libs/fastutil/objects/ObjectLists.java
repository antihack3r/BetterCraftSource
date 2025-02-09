// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Objects;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.Collection;
import java.io.Serializable;
import java.util.RandomAccess;
import java.util.Random;

public final class ObjectLists
{
    public static final EmptyList EMPTY_LIST;
    
    private ObjectLists() {
    }
    
    public static <K> ObjectList<K> shuffle(final ObjectList<K> l, final Random random) {
        int i = l.size();
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final K t = l.get(i);
            l.set(i, l.get(p));
            l.set(p, t);
        }
        return l;
    }
    
    public static <K> ObjectList<K> emptyList() {
        return ObjectLists.EMPTY_LIST;
    }
    
    public static <K> ObjectList<K> singleton(final K element) {
        return new Singleton<K>(element);
    }
    
    public static <K> ObjectList<K> synchronize(final ObjectList<K> l) {
        return (ObjectList<K>)((l instanceof RandomAccess) ? new ObjectLists.SynchronizedRandomAccessList((ObjectList)l) : new ObjectLists.SynchronizedList((ObjectList)l));
    }
    
    public static <K> ObjectList<K> synchronize(final ObjectList<K> l, final Object sync) {
        return (ObjectList<K>)((l instanceof RandomAccess) ? new ObjectLists.SynchronizedRandomAccessList((ObjectList)l, sync) : new ObjectLists.SynchronizedList((ObjectList)l, sync));
    }
    
    public static <K> ObjectList<K> unmodifiable(final ObjectList<K> l) {
        return (ObjectList<K>)((l instanceof RandomAccess) ? new ObjectLists.UnmodifiableRandomAccessList((ObjectList)l) : new ObjectLists.UnmodifiableList((ObjectList)l));
    }
    
    static {
        EMPTY_LIST = new EmptyList();
    }
    
    public static class EmptyList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectList<K>, RandomAccess, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptyList() {
        }
        
        @Override
        public K get(final int i) {
            throw new IndexOutOfBoundsException();
        }
        
        @Override
        public boolean remove(final Object k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K remove(final int i) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void add(final int index, final K k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K set(final int index, final K k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int indexOf(final Object k) {
            return -1;
        }
        
        @Override
        public int lastIndexOf(final Object k) {
            return -1;
        }
        
        @Override
        public boolean addAll(final int i, final Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void sort(final Comparator<? super K> comparator) {
        }
        
        @Override
        public void unstableSort(final Comparator<? super K> comparator) {
        }
        
        @Override
        public ObjectListIterator<K> listIterator() {
            return ObjectIterators.EMPTY_ITERATOR;
        }
        
        @Override
        public ObjectListIterator<K> iterator() {
            return ObjectIterators.EMPTY_ITERATOR;
        }
        
        @Override
        public ObjectListIterator<K> listIterator(final int i) {
            if (i == 0) {
                return ObjectIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }
        
        @Override
        public ObjectList<K> subList(final int from, final int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }
        
        @Override
        public void getElements(final int from, final Object[] a, final int offset, final int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }
        
        @Override
        public void removeElements(final int from, final int to) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void addElements(final int index, final K[] a, final int offset, final int length) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void addElements(final int index, final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final int index, final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final int index, final K[] a, final int offset, final int length) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void size(final int s) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int compareTo(final List<? extends K> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }
        
        public Object clone() {
            return ObjectLists.EMPTY_LIST;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof List && ((List)o).isEmpty();
        }
        
        @Override
        public String toString() {
            return "[]";
        }
        
        private Object readResolve() {
            return ObjectLists.EMPTY_LIST;
        }
    }
    
    public static class Singleton<K> extends AbstractObjectList<K> implements RandomAccess, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        private final K element;
        
        protected Singleton(final K element) {
            this.element = element;
        }
        
        @Override
        public K get(final int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }
        
        @Override
        public boolean remove(final Object k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K remove(final int i) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean contains(final Object k) {
            return Objects.equals(k, this.element);
        }
        
        @Override
        public Object[] toArray() {
            final Object[] a = { this.element };
            return a;
        }
        
        @Override
        public ObjectListIterator<K> listIterator() {
            return ObjectIterators.singleton(this.element);
        }
        
        @Override
        public ObjectListIterator<K> iterator() {
            return this.listIterator();
        }
        
        @Override
        public ObjectListIterator<K> listIterator(final int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            final ObjectListIterator<K> l = this.listIterator();
            if (i == 1) {
                l.next();
            }
            return l;
        }
        
        @Override
        public ObjectList<K> subList(final int from, final int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return ObjectLists.EMPTY_LIST;
            }
            return this;
        }
        
        @Override
        public boolean addAll(final int i, final Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean addAll(final Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void sort(final Comparator<? super K> comparator) {
        }
        
        @Override
        public void unstableSort(final Comparator<? super K> comparator) {
        }
        
        @Override
        public void removeElements(final int from, final int to) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void addElements(final int index, final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void addElements(final int index, final K[] a, final int offset, final int length) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final int index, final K[] a) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setElements(final int index, final K[] a, final int offset, final int length) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int size() {
            return 1;
        }
        
        @Override
        public void size(final int size) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        public Object clone() {
            return this;
        }
    }
}
