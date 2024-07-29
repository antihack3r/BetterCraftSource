/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import com.viaversion.viaversion.libs.opennbt.stringified.SNBT;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag
implements Cloneable {
    public abstract Object getValue();

    public <T> T value() {
        return (T)this.getValue();
    }

    public final void read(DataInput in2) throws IOException {
        this.read(in2, TagLimiter.noop(), 0);
    }

    public final void read(DataInput in2, TagLimiter tagLimiter) throws IOException {
        this.read(in2, tagLimiter, 0);
    }

    public abstract void read(DataInput var1, TagLimiter var2, int var3) throws IOException;

    public abstract void write(DataOutput var1) throws IOException;

    public abstract int getTagId();

    public abstract Tag clone();

    public String toString() {
        return SNBT.serialize(this);
    }
}

