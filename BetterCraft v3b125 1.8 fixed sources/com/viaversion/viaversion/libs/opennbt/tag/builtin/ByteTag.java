/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag
extends NumberTag {
    public static final int ID = 1;
    private byte value;

    public ByteTag() {
        this(0);
    }

    public ByteTag(byte value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public Byte getValue() {
        return this.value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in2, TagLimiter tagLimiter, int nestingLevel) throws IOException {
        tagLimiter.countByte();
        this.value = in2.readByte();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.value);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        ByteTag byteTag = (ByteTag)o2;
        return this.value == byteTag.value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public final ByteTag clone() {
        return new ByteTag(this.value);
    }

    @Override
    public byte asByte() {
        return this.value;
    }

    @Override
    public short asShort() {
        return this.value;
    }

    @Override
    public int asInt() {
        return this.value;
    }

    @Override
    public long asLong() {
        return this.value;
    }

    @Override
    public float asFloat() {
        return this.value;
    }

    @Override
    public double asDouble() {
        return this.value;
    }

    @Override
    public int getTagId() {
        return 1;
    }
}

