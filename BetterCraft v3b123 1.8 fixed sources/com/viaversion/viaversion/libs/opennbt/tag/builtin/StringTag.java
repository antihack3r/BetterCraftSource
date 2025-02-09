// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataOutput;
import java.io.IOException;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;

public class StringTag extends Tag
{
    public static final int ID = 8;
    private String value;
    
    public StringTag() {
        this("");
    }
    
    public StringTag(final String value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public void read(final DataInput in, final TagLimiter tagLimiter, final int nestingLevel) throws IOException {
        this.value = in.readUTF();
        tagLimiter.countBytes(2 * this.value.length());
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final StringTag stringTag = (StringTag)o;
        return this.value.equals(stringTag.value);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public final StringTag clone() {
        return new StringTag(this.value);
    }
    
    @Override
    public int getTagId() {
        return 8;
    }
}
