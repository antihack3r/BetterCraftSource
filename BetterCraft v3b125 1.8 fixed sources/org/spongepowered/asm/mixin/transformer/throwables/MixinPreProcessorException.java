/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer.throwables;

import org.spongepowered.asm.mixin.extensibility.IActivityContext;
import org.spongepowered.asm.mixin.throwables.MixinException;

public class MixinPreProcessorException
extends MixinException {
    private static final long serialVersionUID = 1L;

    public MixinPreProcessorException(String message, IActivityContext context) {
        super(message, context);
    }

    public MixinPreProcessorException(String message, Throwable cause, IActivityContext context) {
        super(message, cause, context);
    }
}

