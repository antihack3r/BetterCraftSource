/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
final class CartesianList<E>
extends AbstractList<List<E>>
implements RandomAccess {
    private final transient ImmutableList<List<E>> axes;
    private final transient int[] axesSizeProduct;

    static <E> List<List<E>> create(List<? extends List<? extends E>> lists) {
        ImmutableList.Builder axesBuilder = new ImmutableList.Builder(lists.size());
        for (List<E> list : lists) {
            ImmutableList<E> copy = ImmutableList.copyOf(list);
            if (copy.isEmpty()) {
                return ImmutableList.of();
            }
            axesBuilder.add(copy);
        }
        return new CartesianList<E>(axesBuilder.build());
    }

    CartesianList(ImmutableList<List<E>> axes) {
        this.axes = axes;
        int[] axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        try {
            for (int i2 = axes.size() - 1; i2 >= 0; --i2) {
                axesSizeProduct[i2] = IntMath.checkedMultiply(axesSizeProduct[i2 + 1], ((List)axes.get(i2)).size());
            }
        }
        catch (ArithmeticException e2) {
            throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
        }
        this.axesSizeProduct = axesSizeProduct;
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return index / this.axesSizeProduct[axis + 1] % ((List)this.axes.get(axis)).size();
    }

    @Override
    public ImmutableList<E> get(final int index) {
        Preconditions.checkElementIndex(index, this.size());
        return new ImmutableList<E>(){

            @Override
            public int size() {
                return CartesianList.this.axes.size();
            }

            @Override
            public E get(int axis) {
                Preconditions.checkElementIndex(axis, this.size());
                int axisIndex = CartesianList.this.getAxisIndexForProductIndex(index, axis);
                return ((List)CartesianList.this.axes.get(axis)).get(axisIndex);
            }

            @Override
            boolean isPartialView() {
                return true;
            }
        };
    }

    @Override
    public int size() {
        return this.axesSizeProduct[0];
    }

    @Override
    public boolean contains(@Nullable Object o2) {
        if (!(o2 instanceof List)) {
            return false;
        }
        List list = (List)o2;
        if (list.size() != this.axes.size()) {
            return false;
        }
        ListIterator itr = list.listIterator();
        while (itr.hasNext()) {
            int index = itr.nextIndex();
            if (((List)this.axes.get(index)).contains(itr.next())) continue;
            return false;
        }
        return true;
    }
}

