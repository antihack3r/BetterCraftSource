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

public class ByteArrayTag
extends Tag {
    public static final int ID = 7;
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private byte[] value;

    public ByteArrayTag() {
        this(EMPTY_ARRAY);
    }

    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        if (value == null) {
            return;
        }
        this.value = value;
    }

    public byte getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, byte value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in2, TagLimiter tagLimiter, int nestingLevel) throws IOException {
        tagLimiter.countInt();
        this.value = new byte[in2.readInt()];
        tagLimiter.countBytes(this.value.length);
        in2.readFully(this.value);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        out.write(this.value);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        ByteArrayTag that = (ByteArrayTag)o2;
        return Arrays.equals(this.value, that.value);
    }

    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public final ByteArrayTag clone() {
        return new ByteArrayTag(this.value);
    }

    @Override
    public int getTagId() {
        return 7;
    }
}

