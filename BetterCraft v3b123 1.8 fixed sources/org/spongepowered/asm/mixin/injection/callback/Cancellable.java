// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.injection.callback;

public interface Cancellable
{
    boolean isCancellable();
    
    boolean isCancelled();
    
    void cancel() throws CancellationException;
}
