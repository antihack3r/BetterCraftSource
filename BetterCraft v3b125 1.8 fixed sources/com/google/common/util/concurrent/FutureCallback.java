/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import javax.annotation.Nullable;

public interface FutureCallback<V> {
    public void onSuccess(@Nullable V var1);

    public void onFailure(Throwable var1);
}

