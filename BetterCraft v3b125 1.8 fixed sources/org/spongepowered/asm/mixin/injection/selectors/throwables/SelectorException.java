/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.selectors.throwables;

import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.throwables.MixinException;

public class SelectorException
extends MixinException {
    private static final long serialVersionUID = 1L;
    private final ITargetSelector selector;

    public SelectorException(ITargetSelector selector, String message) {
        super(message);
        this.selector = selector;
    }

    public SelectorException(ITargetSelector selector, Throwable cause) {
        super(cause);
        this.selector = selector;
    }

    public SelectorException(ITargetSelector selector, String message, Throwable cause) {
        super(message, cause);
        this.selector = selector;
    }

    public ITargetSelector getSelector() {
        return this.selector;
    }
}

