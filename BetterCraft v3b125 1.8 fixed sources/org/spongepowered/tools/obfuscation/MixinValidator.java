/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.obfuscation;

import java.util.Collection;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.tools.obfuscation.AnnotatedMixins;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerSuppressible;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IMixinValidator;
import org.spongepowered.tools.obfuscation.interfaces.IOptionProvider;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;

public abstract class MixinValidator
implements IMixinValidator {
    protected final ProcessingEnvironment processingEnv;
    protected final IMessagerSuppressible messager;
    protected final IOptionProvider options;
    protected final IMixinValidator.ValidationPass pass;

    public MixinValidator(IMixinAnnotationProcessor ap2, IMixinValidator.ValidationPass pass) {
        this.processingEnv = ap2.getProcessingEnvironment();
        this.messager = ap2;
        this.options = ap2;
        this.pass = pass;
    }

    @Override
    public final boolean validate(IMixinValidator.ValidationPass pass, TypeElement mixin, IAnnotationHandle annotation, Collection<TypeHandle> targets) {
        if (pass != this.pass) {
            return true;
        }
        return this.validate(mixin, annotation, targets);
    }

    protected abstract boolean validate(TypeElement var1, IAnnotationHandle var2, Collection<TypeHandle> var3);

    protected final Collection<TypeMirror> getMixinsTargeting(TypeMirror targetType) {
        return AnnotatedMixins.getMixinsForEnvironment(this.processingEnv).getMixinsTargeting(targetType);
    }
}

