/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag
extends NumberTag {
    public static final int ID = 2;
    private short value;

    public ShortTag() {
        this(0);
    }

    public ShortTag(short value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public Short getValue() {
        return this.value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in2, TagLimiter tagLimiter, int nestingLevel) throws IOException {
        tagLimiter.countShort();
        this.value = in2.readShort();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeShort(this.value);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        ShortTag shortTag = (ShortTag)o2;
        return this.value == shortTag.value;
    }

    public int hashCode() {
        return this.value;
    }

    @Override
    public final ShortTag clone() {
        return new ShortTag(this.value);
    }

    @Override
    public byte asByte() {
        return (byte)this.value;
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
        return 2;
    }
}

