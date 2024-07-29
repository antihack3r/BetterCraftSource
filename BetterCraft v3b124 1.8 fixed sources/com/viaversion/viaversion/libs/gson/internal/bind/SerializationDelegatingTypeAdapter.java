/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.TypeAdapter;

public abstract class SerializationDelegatingTypeAdapter<T>
extends TypeAdapter<T> {
    public abstract TypeAdapter<T> getSerializationDelegate();
}

