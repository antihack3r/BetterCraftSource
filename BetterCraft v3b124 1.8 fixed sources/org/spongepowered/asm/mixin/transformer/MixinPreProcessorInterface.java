/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.transformer;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinInfo;
import org.spongepowered.asm.mixin.transformer.MixinPreProcessorStandard;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidInterfaceMixinException;
import org.spongepowered.asm.util.Bytecode;

class MixinPreProcessorInterface
extends MixinPreProcessorStandard {
    MixinPreProcessorInterface(MixinInfo mixin, MixinInfo.MixinClassNode classNode) {
        super(mixin, classNode);
    }

    @Override
    protected void prepareMethod(MixinInfo.MixinMethodNode mixinMethod, ClassInfo.Method method) {
        if (!Bytecode.hasFlag(mixinMethod, 1)) {
            if (!Bytecode.hasFlag(mixinMethod, 4096)) {
                throw new InvalidInterfaceMixinException((IMixinInfo)this.mixin, String.format("Interface mixin contains a non-public method! Found %s in %s", method, this.mixin));
            }
            MixinEnvironment.CompatibilityLevel requiredLevel = MixinEnvironment.CompatibilityLevel.requiredFor(2);
            if (MixinEnvironment.getCompatibilityLevel().isLessThan(requiredLevel)) {
                throw new InvalidInterfaceMixinException((IMixinInfo)this.mixin, String.format("Interface mixin contains a synthetic private method but compatibility level %s is required! Found %s in %s", new Object[]{requiredLevel, method, this.mixin}));
            }
        }
        super.prepareMethod(mixinMethod, method);
    }

    @Override
    protected boolean validateField(MixinTargetContext context, FieldNode field, AnnotationNode shadow) {
        if (!Bytecode.isStatic(field)) {
            throw new InvalidInterfaceMixinException((IMixinInfo)this.mixin, String.format("Interface mixin contains an instance field! Found %s in %s", field.name, this.mixin));
        }
        return super.validateField(context, field, shadow);
    }
}

