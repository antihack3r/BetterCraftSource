// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.io.Serializable;

public final class ObjectSets
{
    public static final EmptySet EMPTY_SET;
    
    private ObjectSets() {
    }
    
    public static <K> ObjectSet<K> emptySet() {
        return ObjectSets.EMPTY_SET;
    }
    
    public static <K> ObjectSet<K> singleton(final K element) {
        return new Singleton<K>(element);
    }
    
    public static <K> ObjectSet<K> synchronize(final ObjectSet<K> s) {
        return (ObjectSet<K>)new ObjectSets.SynchronizedSet((ObjectSet)s);
    }
    
    public static <K> ObjectSet<K> synchronize(final ObjectSet<K> s, final Object sync) {
        return (ObjectSet<K>)new ObjectSets.SynchronizedSet((ObjectSet)s, sync);
    }
    
    public static <K> ObjectSet<K> unmodifiable(final ObjectSet<K> s) {
        return (ObjectSet<K>)new ObjectSets.UnmodifiableSet((ObjectSet)s);
    }
    
    static {
        EMPTY_SET = new EmptySet();
    }
    
    public static class EmptySet<K> extends ObjectCollections.EmptyCollection<K> implements ObjectSet<K>, Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        protected EmptySet() {
        }
        
        @Override
        public boolean remove(final Object ok) {
            throw new UnsupportedOperationException();
        }
        
        public Object clone() {
            return ObjectSets.EMPTY_SET;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }
        
        private Object readResolve() {
            return ObjectSets.EMPTY_SET;
        }
    }
    
    public static class Singleton<K> extends AbstractObjectSet<K> implements Serializable, Cloneable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K element;
        
        protected Singleton(final K element) {
            this.element = element;
        }
        
        @Override
        public boolean contains(final Object k) {
            return Objects.equals(k, this.element);
        }
        
        @Override
        public boolean remove(final Object k) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ObjectListIterator<K> iterator() {
            return ObjectIterators.singleton(this.element);
        }
        
        @Override
        public int size() {
            return 1;
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
        
        public Object clone() {
            return this;
        }
    }
}
