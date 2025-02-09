/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.struct;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.invoke.ModifyConstantInjector;
import org.spongepowered.asm.mixin.injection.points.BeforeConstant;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

@InjectionInfo.AnnotationType(value=ModifyConstant.class)
@InjectionInfo.HandlerPrefix(value="constant")
public class ModifyConstantInjectionInfo
extends InjectionInfo {
    private static final String CONSTANT_ANNOTATION_CLASS = Constant.class.getName().replace('.', '/');

    public ModifyConstantInjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
        super(mixin, method, annotation, "constant");
    }

    @Override
    protected List<AnnotationNode> readInjectionPoints() {
        List<AnnotationNode> ats2 = super.readInjectionPoints();
        if (ats2.isEmpty()) {
            AnnotationNode c2 = new AnnotationNode(CONSTANT_ANNOTATION_CLASS);
            c2.visit("log", Boolean.TRUE);
            ats2 = ImmutableList.of(c2);
        }
        return ats2;
    }

    @Override
    protected void parseInjectionPoints(List<AnnotationNode> ats2) {
        Type returnType = Type.getReturnType(this.method.desc);
        for (AnnotationNode at2 : ats2) {
            this.injectionPoints.add(new BeforeConstant(this.getMixin(), at2, returnType.getDescriptor()));
        }
    }

    @Override
    protected Injector parseInjector(AnnotationNode injectAnnotation) {
        return new ModifyConstantInjector(this);
    }

    @Override
    protected String getDescription() {
        return "Constant modifier method";
    }

    @Override
    public String getSliceId(String id2) {
        return Strings.nullToEmpty(id2);
    }
}

