/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectFunction;
import java.io.Serializable;

public abstract class AbstractInt2ObjectFunction<V>
implements Int2ObjectFunction<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected V defRetValue;

    protected AbstractInt2ObjectFunction() {
    }

    @Override
    public void defaultReturnValue(V rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public V defaultReturnValue() {
        return this.defRetValue;
    }
}

