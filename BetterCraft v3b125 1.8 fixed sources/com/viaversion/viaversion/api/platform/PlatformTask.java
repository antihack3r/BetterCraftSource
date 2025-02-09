/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.platform;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface PlatformTask<T> {
    @Deprecated
    public @Nullable T getObject();

    public void cancel();
}

