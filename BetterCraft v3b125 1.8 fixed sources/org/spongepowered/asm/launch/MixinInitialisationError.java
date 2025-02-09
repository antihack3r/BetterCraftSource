/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch;

import org.spongepowered.asm.mixin.throwables.MixinError;

public class MixinInitialisationError
extends MixinError {
    private static final long serialVersionUID = 1L;

    public MixinInitialisationError() {
    }

    public MixinInitialisationError(String message) {
        super(message);
    }

    public MixinInitialisationError(Throwable cause) {
        super(cause);
    }

    public MixinInitialisationError(String message, Throwable cause) {
        super(message, cause);
    }
}

