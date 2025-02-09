/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.throwables;

import org.spongepowered.asm.mixin.throwables.MixinError;

public class MixinApplyError
extends MixinError {
    private static final long serialVersionUID = 1L;

    public MixinApplyError(String message) {
        super(message);
    }

    public MixinApplyError(Throwable cause) {
        super(cause);
    }

    public MixinApplyError(String message, Throwable cause) {
        super(message, cause);
    }
}

