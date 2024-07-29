/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.struct;

import org.spongepowered.asm.mixin.injection.selectors.InvalidSelectorException;

public class TargetNotSupportedException
extends InvalidSelectorException {
    private static final long serialVersionUID = 1L;

    public TargetNotSupportedException(String message) {
        super(message);
    }

    public TargetNotSupportedException(Throwable cause) {
        super(cause);
    }

    public TargetNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}

