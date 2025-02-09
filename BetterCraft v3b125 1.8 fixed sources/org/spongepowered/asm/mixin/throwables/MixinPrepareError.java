/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.throwables;

import org.spongepowered.asm.mixin.throwables.MixinError;

public class MixinPrepareError
extends MixinError {
    private static final long serialVersionUID = 1L;

    public MixinPrepareError(String message) {
        super(message);
    }

    public MixinPrepareError(Throwable cause) {
        super(cause);
    }

    public MixinPrepareError(String message, Throwable cause) {
        super(message, cause);
    }
}

