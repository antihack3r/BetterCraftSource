// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.throwables;

public class MixinPrepareError extends MixinError
{
    private static final long serialVersionUID = 1L;
    
    public MixinPrepareError(final String message) {
        super(message);
    }
    
    public MixinPrepareError(final Throwable cause) {
        super(cause);
    }
    
    public MixinPrepareError(final String message, final Throwable cause) {
        super(message, cause);
    }
}
