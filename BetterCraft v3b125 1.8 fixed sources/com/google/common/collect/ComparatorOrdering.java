/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(serializable=true)
final class ComparatorOrdering<T>
extends Ordering<T>
implements Serializable {
    final Comparator<T> comparator;
    private static final long serialVersionUID = 0L;

    ComparatorOrdering(Comparator<T> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }

    @Override
    public int compare(T a2, T b2) {
        return this.comparator.compare(a2, b2);
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComparatorOrdering) {
            ComparatorOrdering that = (ComparatorOrdering)object;
            return this.comparator.equals(that.comparator);
        }
        return false;
    }

    public int hashCode() {
        return this.comparator.hashCode();
    }

    public String toString() {
        return this.comparator.toString();
    }
}

