/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.gen;

import java.util.ArrayList;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.gen.AccessorInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.util.asm.ASM;

public abstract class AccessorGenerator {
    protected final AccessorInfo info;
    protected final boolean targetIsStatic;

    public AccessorGenerator(AccessorInfo info, boolean isStatic) {
        this.info = info;
        this.targetIsStatic = isStatic;
    }

    protected void checkModifiers() {
        if (this.info.isStatic() && !this.targetIsStatic) {
            IMixinContext context = this.info.getMixin();
            throw new InvalidInjectionException(context, String.format("%s is invalid. Accessor method is%s static but the target is not.", this.info, this.info.isStatic() ? "" : " not"));
        }
    }

    protected final MethodNode createMethod(int maxLocals, int maxStack) {
        MethodNode method = this.info.getMethod();
        MethodNode accessor = new MethodNode(ASM.API_VERSION, method.access & 0xFFFFFBFF | 0x1000, method.name, method.desc, null, null);
        accessor.visibleAnnotations = new ArrayList<AnnotationNode>();
        accessor.visibleAnnotations.add(this.info.getAnnotationNode());
        accessor.maxLocals = maxLocals;
        accessor.maxStack = maxStack;
        return accessor;
    }

    public void validate() {
    }

    public abstract MethodNode generate();
}

