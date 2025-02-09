/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag
extends Tag {
    public static final int ID = 11;
    private static final int[] EMPTY_ARRAY = new int[0];
    private int[] value;

    public IntArrayTag() {
        this(EMPTY_ARRAY);
    }

    public IntArrayTag(int[] value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }

    public int[] getValue() {
        return this.value;
    }

    public void setValue(int[] value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }

    public int getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, int value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in2, TagLimiter tagLimiter, int nestingLevel) throws IOException {
        tagLimiter.countInt();
        this.value = new int[in2.readInt()];
        tagLimiter.countBytes(4 * this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in2.readInt();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int i2 : this.value) {
            out.writeInt(i2);
        }
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        IntArrayTag that = (IntArrayTag)o2;
        return Arrays.equals(this.value, that.value);
    }

    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public final IntArrayTag clone() {
        return new IntArrayTag((int[])this.value.clone());
    }

    @Override
    public int getTagId() {
        return 11;
    }
}

