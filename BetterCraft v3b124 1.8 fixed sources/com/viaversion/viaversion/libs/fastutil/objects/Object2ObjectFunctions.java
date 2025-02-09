/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions$SynchronizedFunction
 *  com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions$UnmodifiableFunction
 */
package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.Function;
import com.viaversion.viaversion.libs.fastutil.objects.AbstractObject2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunction;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectFunctions;
import java.io.Serializable;
import java.util.Objects;

public final class Object2ObjectFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2ObjectFunctions() {
    }

    public static <K, V> Object2ObjectFunction<K, V> singleton(K key, V value) {
        return new Singleton<K, V>(key, value);
    }

    public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f2) {
        return new SynchronizedFunction(f2);
    }

    public static <K, V> Object2ObjectFunction<K, V> synchronize(Object2ObjectFunction<K, V> f2, Object sync) {
        return new SynchronizedFunction(f2, sync);
    }

    public static <K, V> Object2ObjectFunction<K, V> unmodifiable(Object2ObjectFunction<? extends K, ? extends V> f2) {
        return new UnmodifiableFunction(f2);
    }

    public static class Singleton<K, V>
    extends AbstractObject2ObjectFunction<K, V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final V value;

        protected Singleton(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k2) {
            return Objects.equals(this.key, k2);
        }

        @Override
        public V get(Object k2) {
            return (V)(Objects.equals(this.key, k2) ? this.value : this.defRetValue);
        }

        @Override
        public V getOrDefault(Object k2, V defaultValue) {
            return Objects.equals(this.key, k2) ? this.value : defaultValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyFunction<K, V>
    extends AbstractObject2ObjectFunction<K, V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public V get(Object k2) {
            return null;
        }

        @Override
        public V getOrDefault(Object k2, V defaultValue) {
            return defaultValue;
        }

        @Override
        public boolean containsKey(Object k2) {
            return false;
        }

        @Override
        public V defaultReturnValue() {
            return null;
        }

        @Override
        public void defaultReturnValue(V defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        public Object clone() {
            return EMPTY_FUNCTION;
        }

        public int hashCode() {
            return 0;
        }

        public boolean equals(Object o2) {
            if (!(o2 instanceof Function)) {
                return false;
            }
            return ((Function)o2).size() == 0;
        }

        public String toString() {
            return "{}";
        }

        private Object readResolve() {
            return EMPTY_FUNCTION;
        }
    }
}

