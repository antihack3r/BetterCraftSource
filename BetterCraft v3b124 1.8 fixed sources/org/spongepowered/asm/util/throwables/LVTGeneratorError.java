/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util.throwables;

import org.spongepowered.asm.mixin.throwables.MixinError;

public class LVTGeneratorError
extends MixinError {
    private static final long serialVersionUID = 1L;

    public LVTGeneratorError(String message) {
        super(message);
    }

    public LVTGeneratorError(String message, Throwable cause) {
        super(message, cause);
    }
}

