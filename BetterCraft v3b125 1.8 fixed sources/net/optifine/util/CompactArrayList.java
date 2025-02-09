/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.ArrayList;

public class CompactArrayList {
    private ArrayList list = null;
    private int initialCapacity = 0;
    private float loadFactor = 1.0f;
    private int countValid = 0;

    public CompactArrayList() {
        this(10, 0.75f);
    }

    public CompactArrayList(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public CompactArrayList(int initialCapacity, float loadFactor) {
        this.list = new ArrayList(initialCapacity);
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
    }

    public void add(int index, Object element) {
        if (element != null) {
            ++this.countValid;
        }
        this.list.add(index, element);
    }

    public boolean add(Object element) {
        if (element != null) {
            ++this.countValid;
        }
        return this.list.add(element);
    }

    public Object set(int index, Object element) {
        Object object = this.list.set(index, element);
        if (element != object) {
            if (object == null) {
                ++this.countValid;
            }
            if (element == null) {
                --this.countValid;
            }
        }
        return object;
    }

    public Object remove(int index) {
        Object object = this.list.remove(index);
        if (object != null) {
            --this.countValid;
        }
        return object;
    }

    public void clear() {
        this.list.clear();
        this.countValid = 0;
    }

    public void compact() {
        float f2;
        if (this.countValid <= 0 && this.list.size() <= 0) {
            this.clear();
        } else if (this.list.size() > this.initialCapacity && (f2 = (float)this.countValid * 1.0f / (float)this.list.size()) <= this.loadFactor) {
            int i2 = 0;
            int j2 = 0;
            while (j2 < this.list.size()) {
                Object object = this.list.get(j2);
                if (object != null) {
                    if (j2 != i2) {
                        this.list.set(i2, object);
                    }
                    ++i2;
                }
                ++j2;
            }
            int k2 = this.list.size() - 1;
            while (k2 >= i2) {
                this.list.remove(k2);
                --k2;
            }
        }
    }

    public boolean contains(Object elem) {
        return this.list.contains(elem);
    }

    public Object get(int index) {
        return this.list.get(index);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public int getCountValid() {
        return this.countValid;
    }
}

