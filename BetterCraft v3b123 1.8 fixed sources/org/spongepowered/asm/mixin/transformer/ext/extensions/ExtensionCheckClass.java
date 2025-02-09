// 
// Decompiled by Procyon v0.6.0
// 

package org.spongepowered.asm.mixin.transformer.ext.extensions;

import org.spongepowered.asm.mixin.throwables.MixinException;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.CheckClassAdapter;
import org.spongepowered.asm.transformers.MixinClassWriter;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

public class ExtensionCheckClass implements IExtension
{
    @Override
    public boolean checkActive(final MixinEnvironment environment) {
        return environment.getOption(MixinEnvironment.Option.DEBUG_VERIFY);
    }
    
    @Override
    public void preApply(final ITargetClassContext context) {
    }
    
    @Override
    public void postApply(final ITargetClassContext context) {
        try {
            context.getClassNode().accept(new CheckClassAdapter(new MixinClassWriter(2)));
        }
        catch (final RuntimeException ex) {
            throw new ValidationFailedException(ex.getMessage(), ex);
        }
    }
    
    @Override
    public void export(final MixinEnvironment env, final String name, final boolean force, final ClassNode classNode) {
    }
    
    public static class ValidationFailedException extends MixinException
    {
        private static final long serialVersionUID = 1L;
        
        public ValidationFailedException(final String message, final Throwable cause) {
            super(message, cause);
        }
        
        public ValidationFailedException(final String message) {
            super(message);
        }
        
        public ValidationFailedException(final Throwable cause) {
            super(cause);
        }
    }
}
