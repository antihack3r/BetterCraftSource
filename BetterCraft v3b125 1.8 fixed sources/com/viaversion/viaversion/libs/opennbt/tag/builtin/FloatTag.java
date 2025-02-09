/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag
extends NumberTag {
    public static final int ID = 5;
    private float value;

    public FloatTag() {
        this(0.0f);
    }

    public FloatTag(float value) {
        this.value = value;
    }

    @Override
    @Deprecated
    public Float getValue() {
        return Float.valueOf(this.value);
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in2, TagLimiter tagLimiter, int nestingLevel) throws IOException {
        tagLimiter.countFloat();
        this.value = in2.readFloat();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeFloat(this.value);
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        FloatTag floatTag = (FloatTag)o2;
        return this.value == floatTag.value;
    }

    public int hashCode() {
        return Float.hashCode(this.value);
    }

    @Override
    public final FloatTag clone() {
        return new FloatTag(this.value);
    }

    @Override
    public byte asByte() {
        return (byte)this.value;
    }

    @Override
    public short asShort() {
        return (short)this.value;
    }

    @Override
    public int asInt() {
        return (int)this.value;
    }

    @Override
    public long asLong() {
        return (long)this.value;
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
        return 5;
    }
}

