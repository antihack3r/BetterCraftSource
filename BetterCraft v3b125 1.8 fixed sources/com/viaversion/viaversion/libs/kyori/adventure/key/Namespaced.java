/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.key;

import com.viaversion.viaversion.libs.kyori.adventure.key.KeyPattern;
import org.jetbrains.annotations.NotNull;

public interface Namespaced {
    @NotNull
    @KeyPattern.Namespace
    public String namespace();
}

