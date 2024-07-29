/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntFunction;
import java.io.Serializable;

public abstract class AbstractInt2IntFunction
implements Int2IntFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractInt2IntFunction() {
    }

    @Override
    public void defaultReturnValue(int rv2) {
        this.defRetValue = rv2;
    }

    @Override
    public int defaultReturnValue() {
        return this.defRetValue;
    }
}

