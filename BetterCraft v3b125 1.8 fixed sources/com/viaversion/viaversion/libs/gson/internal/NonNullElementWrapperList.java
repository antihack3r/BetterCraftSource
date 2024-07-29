/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

public class NonNullElementWrapperList<E>
extends AbstractList<E>
implements RandomAccess {
    private final ArrayList<E> delegate;

    public NonNullElementWrapperList(ArrayList<E> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public E get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    private E nonNull(E element) {
        if (element == null) {
            throw new NullPointerException("Element must be non-null");
        }
        return element;
    }

    @Override
    public E set(int index, E element) {
        return this.delegate.set(index, this.nonNull(element));
    }

    @Override
    public void add(int index, E element) {
        this.delegate.add(index, this.nonNull(element));
    }

    @Override
    public E remove(int index) {
        return this.delegate.remove(index);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public boolean remove(Object o2) {
        return this.delegate.remove(o2);
    }

    @Override
    public boolean removeAll(Collection<?> c2) {
        return this.delegate.removeAll(c2);
    }

    @Override
    public boolean retainAll(Collection<?> c2) {
        return this.delegate.retainAll(c2);
    }

    @Override
    public boolean contains(Object o2) {
        return this.delegate.contains(o2);
    }

    @Override
    public int indexOf(Object o2) {
        return this.delegate.indexOf(o2);
    }

    @Override
    public int lastIndexOf(Object o2) {
        return this.delegate.lastIndexOf(o2);
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a2) {
        return this.delegate.toArray(a2);
    }

    @Override
    public boolean equals(Object o2) {
        return this.delegate.equals(o2);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
}

