/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.struct;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.struct.AnnotatedMethodInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.asm.MethodNodeEx;

public class SpecialMethodInfo
extends AnnotatedMethodInfo {
    protected final String annotationType;
    protected final ClassNode classNode;
    protected final String methodName;
    protected final MixinTargetContext mixin;

    public SpecialMethodInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        super(mixin, method, annotation);
        this.mixin = mixin;
        this.annotationType = this.annotation != null ? "@" + Annotations.getSimpleName(this.annotation) : "Undecorated injector";
        this.classNode = mixin.getTargetClassNode();
        this.methodName = MethodNodeEx.getName(method);
    }

    public final ClassNode getClassNode() {
        return this.classNode;
    }

    public final ClassInfo getClassInfo() {
        return this.mixin.getClassInfo();
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }
}

