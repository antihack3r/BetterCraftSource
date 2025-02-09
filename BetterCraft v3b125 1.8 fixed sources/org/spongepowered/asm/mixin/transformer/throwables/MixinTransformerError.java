/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer.throwables;

import org.spongepowered.asm.mixin.throwables.MixinError;

public class MixinTransformerError
extends MixinError {
    private static final long serialVersionUID = 1L;

    public MixinTransformerError(String message) {
        super(message);
    }

    public MixinTransformerError(Throwable cause) {
        super(cause);
    }

    public MixinTransformerError(String message, Throwable cause) {
        super(message, cause);
    }
}

