/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.util.IMessageSink;

public interface IInjectionPointContext
extends IMessageSink,
ISelectorContext {
    @Override
    public MethodNode getMethod();

    public AnnotationNode getAnnotationNode();
}

