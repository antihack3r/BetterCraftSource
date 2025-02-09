/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSets$SynchronizedSortedSet
 *  com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSets$UnmodifiableSortedSet
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectIterators;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSets;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSet;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSortedSets;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterator;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectSpliterators;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public final class ObjectSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ObjectSortedSets() {
    }

    public static <K> ObjectSet<K> emptySet() {
        return EMPTY_SET;
    }

    public static <K> ObjectSortedSet<K> singleton(K element) {
        return new Singleton<K>(element);
    }

    public static <K> ObjectSortedSet<K> singleton(K element, Comparator<? super K> comparator) {
        return new Singleton<K>(element, comparator);
    }

    public static <K> ObjectSortedSet<K> synchronize(ObjectSortedSet<K> s2) {
        return new SynchronizedSortedSet(s2);
    }

    public static <K> ObjectSortedSet<K> synchronize(ObjectSortedSet<K> s2, Object sync) {
        return new SynchronizedSortedSet(s2, sync);
    }

    public static <K> ObjectSortedSet<K> unmodifiable(ObjectSortedSet<K> s2) {
        return new UnmodifiableSortedSet(s2);
    }

    public static class EmptySet<K>
    extends ObjectSets.EmptySet<K>
    implements ObjectSortedSet<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return ObjectIterators.EMPTY_ITERATOR;
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to2) {
            return EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> headSet(K from) {
            return EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> tailSet(K to2) {
            return EMPTY_SET;
        }

        @Override
        public K first() {
            throw new NoSuchElementException();
        }

        @Override
        public K last() {
            throw new NoSuchElementException();
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public Object clone() {
            return EMPTY_SET;
        }

        private Object readResolve() {
            return EMPTY_SET;
        }
    }

    public static class Singleton<K>
    extends ObjectSets.Singleton<K>
    implements ObjectSortedSet<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final Comparator<? super K> comparator;

        protected Singleton(K element, Comparator<? super K> comparator) {
            super(element);
            this.comparator = comparator;
        }

        Singleton(K element) {
            this(element, null);
        }

        final int compare(K k1, K k2) {
            return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            ObjectBidirectionalIterator i2 = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i2.next();
            }
            return i2;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSpliterator<K> spliterator() {
            return ObjectSpliterators.singleton(this.element, this.comparator);
        }

        @Override
        public ObjectSortedSet<K> subSet(K from, K to2) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to2) < 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> headSet(K to2) {
            if (this.compare(this.element, to2) < 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> tailSet(K from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return EMPTY_SET;
        }

        @Override
        public K first() {
            return (K)this.element;
        }

        @Override
        public K last() {
            return (K)this.element;
        }
    }
}

