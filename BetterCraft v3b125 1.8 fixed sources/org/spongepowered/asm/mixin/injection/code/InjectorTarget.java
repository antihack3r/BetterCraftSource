/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.code;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.InsnListReadOnly;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.util.Annotations;

public class InjectorTarget {
    private final ISliceContext context;
    private final Map<String, InsnListReadOnly> cache = new HashMap<String, InsnListReadOnly>();
    private final Target target;
    private final ITargetSelector selector;
    private final String mergedBy;
    private final int mergedPriority;

    public InjectorTarget(ISliceContext context, Target target, ITargetSelector selector) {
        this.context = context;
        this.target = target;
        this.selector = selector;
        AnnotationNode merged = Annotations.getVisible(target.method, MixinMerged.class);
        this.mergedBy = (String)Annotations.getValue(merged, "mixin");
        this.mergedPriority = Annotations.getValue(merged, "priority", 1000);
    }

    public String toString() {
        return this.target.toString();
    }

    public Target getTarget() {
        return this.target;
    }

    public MethodNode getMethod() {
        return this.target.method;
    }

    public ITargetSelector getSelector() {
        return this.selector;
    }

    public boolean isMerged() {
        return this.mergedBy != null;
    }

    public String getMergedBy() {
        return this.mergedBy;
    }

    public int getMergedPriority() {
        return this.mergedPriority;
    }

    public InsnList getSlice(String id2) {
        InsnListReadOnly slice = this.cache.get(id2);
        if (slice == null) {
            MethodSlice sliceInfo = this.context.getSlice(id2);
            slice = sliceInfo != null ? sliceInfo.getSlice(this.target.method) : new InsnListReadOnly(this.target.method.instructions);
            this.cache.put(id2, slice);
        }
        return slice;
    }

    public InsnList getSlice(InjectionPoint injectionPoint) {
        return this.getSlice(injectionPoint.getSlice());
    }

    public void dispose() {
        for (InsnListReadOnly insns : this.cache.values()) {
            insns.dispose();
        }
        this.cache.clear();
    }
}

