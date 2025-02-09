// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.opennbt.tag.builtin;

import java.io.DataOutput;
import java.io.EOFException;
import com.viaversion.viaversion.libs.opennbt.tag.TagCreateException;
import java.io.IOException;
import com.viaversion.viaversion.libs.opennbt.tag.TagRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import java.io.DataInput;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompoundTag extends Tag implements Iterable<Map.Entry<String, Tag>>
{
    public static final int ID = 10;
    private Map<String, Tag> value;
    
    public CompoundTag() {
        this(new LinkedHashMap<String, Tag>());
    }
    
    public CompoundTag(final Map<String, Tag> value) {
        this.value = new LinkedHashMap<String, Tag>(value);
    }
    
    public CompoundTag(final LinkedHashMap<String, Tag> value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public Map<String, Tag> getValue() {
        return this.value;
    }
    
    public void setValue(final Map<String, Tag> value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = new LinkedHashMap<String, Tag>(value);
    }
    
    public void setValue(final LinkedHashMap<String, Tag> value) {
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.value = value;
    }
    
    public boolean isEmpty() {
        return this.value.isEmpty();
    }
    
    public boolean contains(final String tagName) {
        return this.value.containsKey(tagName);
    }
    
    @Nullable
    public <T extends Tag> T get(final String tagName) {
        return (T)this.value.get(tagName);
    }
    
    @Nullable
    public <T extends Tag> T put(final String tagName, final T tag) {
        return (T)this.value.put(tagName, tag);
    }
    
    @Nullable
    public <T extends Tag> T remove(final String tagName) {
        return (T)this.value.remove(tagName);
    }
    
    public Set<String> keySet() {
        return this.value.keySet();
    }
    
    public Collection<Tag> values() {
        return this.value.values();
    }
    
    public Set<Map.Entry<String, Tag>> entrySet() {
        return this.value.entrySet();
    }
    
    public int size() {
        return this.value.size();
    }
    
    public void clear() {
        this.value.clear();
    }
    
    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return this.value.entrySet().iterator();
    }
    
    @Override
    public void read(final DataInput in, final TagLimiter tagLimiter, final int nestingLevel) throws IOException {
        try {
            tagLimiter.checkLevel(nestingLevel);
            final int newNestingLevel = nestingLevel + 1;
            while (true) {
                tagLimiter.countByte();
                final int id = in.readByte();
                if (id == 0) {
                    break;
                }
                final String name = in.readUTF();
                tagLimiter.countBytes(2 * name.length());
                final Tag tag = TagRegistry.createInstance(id);
                tag.read(in, tagLimiter, newNestingLevel);
                this.value.put(name, tag);
            }
        }
        catch (final TagCreateException e) {
            throw new IOException("Failed to create tag.", e);
        }
        catch (final EOFException ignored) {
            throw new IOException("Closing tag was not found!");
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        for (final Map.Entry<String, Tag> entry : this.value.entrySet()) {
            final Tag tag = entry.getValue();
            out.writeByte(tag.getTagId());
            out.writeUTF(entry.getKey());
            tag.write(out);
        }
        out.writeByte(0);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CompoundTag tags = (CompoundTag)o;
        return this.value.equals(tags.value);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public final CompoundTag clone() {
        final LinkedHashMap<String, Tag> newMap = new LinkedHashMap<String, Tag>();
        for (final Map.Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new CompoundTag(newMap);
    }
    
    @Override
    public int getTagId() {
        return 10;
    }
}
