/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

public class IntArray {
    private int[] array = null;
    private int position = 0;
    private int limit = 0;

    public IntArray(int size) {
        this.array = new int[size];
    }

    public void put(int x2) {
        this.array[this.position] = x2;
        ++this.position;
        if (this.limit < this.position) {
            this.limit = this.position;
        }
    }

    public void put(int pos, int x2) {
        this.array[pos] = x2;
        if (this.limit < pos) {
            this.limit = pos;
        }
    }

    public void position(int pos) {
        this.position = pos;
    }

    public void put(int[] ints) {
        int i2 = ints.length;
        int j2 = 0;
        while (j2 < i2) {
            this.array[this.position] = ints[j2];
            ++this.position;
            ++j2;
        }
        if (this.limit < this.position) {
            this.limit = this.position;
        }
    }

    public int get(int pos) {
        return this.array[pos];
    }

    public int[] getArray() {
        return this.array;
    }

    public void clear() {
        this.position = 0;
        this.limit = 0;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getPosition() {
        return this.position;
    }
}

