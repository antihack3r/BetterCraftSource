/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.Mappings;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface FullMappings
extends Mappings {
    @Deprecated
    default public Mappings mappings() {
        return this;
    }

    public int id(String var1);

    public int mappedId(String var1);

    public String identifier(int var1);

    public String mappedIdentifier(int var1);

    public @Nullable String mappedIdentifier(String var1);

    @Override
    public FullMappings inverse();
}

