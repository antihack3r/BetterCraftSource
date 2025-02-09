/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.minecraft.metadata;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Metadata {
    private int id;
    private MetaType metaType;
    private Object value;

    public Metadata(int id2, MetaType metaType, @Nullable Object value) {
        this.id = id2;
        this.metaType = metaType;
        this.value = this.checkValue(metaType, value);
    }

    public int id() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public MetaType metaType() {
        return this.metaType;
    }

    public void setMetaType(MetaType metaType) {
        this.checkValue(metaType, this.value);
        this.metaType = metaType;
    }

    public <T> @Nullable T value() {
        return (T)this.value;
    }

    public @Nullable Object getValue() {
        return this.value;
    }

    public void setValue(@Nullable Object value) {
        this.value = this.checkValue(this.metaType, value);
    }

    public void setTypeAndValue(MetaType metaType, @Nullable Object value) {
        this.value = this.checkValue(metaType, value);
        this.metaType = metaType;
    }

    private Object checkValue(MetaType metaType, @Nullable Object value) {
        Preconditions.checkNotNull(metaType);
        if (value != null && !metaType.type().getOutputClass().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Metadata value and metaType are incompatible. Type=" + metaType + ", value=" + value + " (" + value.getClass().getSimpleName() + ")");
        }
        return value;
    }

    @Deprecated
    public void setMetaTypeUnsafe(MetaType type) {
        this.metaType = type;
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || this.getClass() != o2.getClass()) {
            return false;
        }
        Metadata metadata = (Metadata)o2;
        if (this.id != metadata.id) {
            return false;
        }
        if (this.metaType != metadata.metaType) {
            return false;
        }
        return Objects.equals(this.value, metadata.value);
    }

    public int hashCode() {
        int result = this.id;
        result = 31 * result + this.metaType.hashCode();
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Metadata{id=" + this.id + ", metaType=" + this.metaType + ", value=" + this.value + '}';
    }
}

