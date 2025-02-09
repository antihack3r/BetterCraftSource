/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.throwables;

public class MixinError
extends Error {
    private static final long serialVersionUID = 1L;

    public MixinError() {
    }

    public MixinError(String message) {
        super(message);
    }

    public MixinError(Throwable cause) {
        super(cause);
    }

    public MixinError(String message, Throwable cause) {
        super(message, cause);
    }
}

