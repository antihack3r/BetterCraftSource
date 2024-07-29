/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.obfuscation;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import org.spongepowered.asm.mixin.injection.selectors.ISelectorContext;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelector;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.mixin.refmap.IMixinContext;
import org.spongepowered.asm.obfuscation.mapping.IMapping;
import org.spongepowered.asm.obfuscation.mapping.common.MappingField;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.ConstraintParser;
import org.spongepowered.asm.util.asm.IAnnotatedElement;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.asm.util.throwables.ConstraintViolationException;
import org.spongepowered.asm.util.throwables.InvalidConstraintException;
import org.spongepowered.tools.obfuscation.AnnotatedMixin;
import org.spongepowered.tools.obfuscation.Mappings;
import org.spongepowered.tools.obfuscation.ObfuscationData;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.SuppressedBy;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerEx;
import org.spongepowered.tools.obfuscation.interfaces.IMessagerSuppressible;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.interfaces.IObfuscationManager;
import org.spongepowered.tools.obfuscation.mapping.IMappingConsumer;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;

abstract class AnnotatedMixinElementHandler {
    protected final AnnotatedMixin mixin;
    protected final String classRef;
    protected final IMixinAnnotationProcessor ap;
    protected final IObfuscationManager obf;
    private IMappingConsumer mappings;

    AnnotatedMixinElementHandler(IMixinAnnotationProcessor ap2, AnnotatedMixin mixin) {
        this.ap = ap2;
        this.mixin = mixin;
        this.classRef = mixin.getClassRef();
        this.obf = ap2.getObfuscationManager();
    }

    private IMappingConsumer getMappings() {
        if (this.mappings == null) {
            IMappingConsumer mappingConsumer = this.mixin.getMappings();
            this.mappings = mappingConsumer instanceof Mappings ? ((Mappings)mappingConsumer).asUnique() : mappingConsumer;
        }
        return this.mappings;
    }

    protected final void addFieldMappings(String mcpName, String mcpSignature, ObfuscationData<MappingField> obfData) {
        for (ObfuscationType type : obfData) {
            MappingField obfField = obfData.get(type);
            this.addFieldMapping(type, mcpName, obfField.getSimpleName(), mcpSignature, obfField.getDesc());
        }
    }

    protected final void addFieldMapping(ObfuscationType type, ShadowElementName name, String mcpSignature, String obfSignature) {
        this.addFieldMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
    }

    protected final void addFieldMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
        MappingField from = new MappingField(this.classRef, mcpName, mcpSignature);
        MappingField to2 = new MappingField(this.classRef, obfName, obfSignature);
        this.getMappings().addFieldMapping(type, from, to2);
    }

    protected final void addMethodMappings(String mcpName, String mcpSignature, ObfuscationData<MappingMethod> obfData) {
        for (ObfuscationType type : obfData) {
            MappingMethod obfMethod = obfData.get(type);
            this.addMethodMapping(type, mcpName, obfMethod.getSimpleName(), mcpSignature, obfMethod.getDesc());
        }
    }

    protected final void addMethodMapping(ObfuscationType type, ShadowElementName name, String mcpSignature, String obfSignature) {
        this.addMethodMapping(type, name.name(), name.obfuscated(), mcpSignature, obfSignature);
    }

    protected final void addMethodMapping(ObfuscationType type, String mcpName, String obfName, String mcpSignature, String obfSignature) {
        MappingMethod from = new MappingMethod(this.classRef, mcpName, mcpSignature);
        MappingMethod to2 = new MappingMethod(this.classRef, obfName, obfSignature);
        this.getMappings().addMethodMapping(type, from, to2);
    }

    protected final void checkConstraints(ExecutableElement method, AnnotationHandle annotation) {
        try {
            ConstraintParser.Constraint constraint = ConstraintParser.parse((String)annotation.getValue("constraints"));
            try {
                constraint.check(this.ap.getTokenProvider());
            }
            catch (ConstraintViolationException ex2) {
                this.ap.printMessage(IMessagerEx.MessageType.CONSTRAINT_VIOLATION, (CharSequence)ex2.getMessage(), (Element)method, annotation.asMirror());
            }
        }
        catch (InvalidConstraintException ex3) {
            this.ap.printMessage(IMessagerEx.MessageType.INVALID_CONSTRAINT, (CharSequence)ex3.getMessage(), (Element)method, annotation.asMirror(), SuppressedBy.CONSTRAINTS);
        }
    }

    protected final void validateTarget(Element element, AnnotationHandle annotation, AliasedElementName name, String type) {
        if (element instanceof ExecutableElement) {
            this.validateTargetMethod((ExecutableElement)element, annotation, name, type, false, false);
        } else if (element instanceof VariableElement) {
            this.validateTargetField((VariableElement)element, annotation, name, type);
        }
    }

    protected final void validateTargetMethod(ExecutableElement method, AnnotationHandle annotation, AliasedElementName name, String type, boolean overwrite, boolean merge) {
        String signature = TypeUtils.getJavaSignature(method);
        for (TypeHandle target : this.mixin.getTargets()) {
            if (target.isImaginary()) continue;
            MethodHandle targetMethod = target.findMethod(method);
            if (targetMethod == null && name.hasPrefix()) {
                targetMethod = target.findMethod(name.baseName(), signature);
            }
            if (targetMethod == null && name.hasAliases()) {
                String alias;
                Iterator<String> iterator = name.getAliases().iterator();
                while (iterator.hasNext() && (targetMethod = target.findMethod(alias = iterator.next(), signature)) == null) {
                }
            }
            if (targetMethod != null) {
                if (!overwrite) continue;
                this.validateMethodVisibility(method, annotation, type, target, targetMethod);
                continue;
            }
            if (merge) continue;
            this.printMessage(IMessagerEx.MessageType.TARGET_ELEMENT_NOT_FOUND, "Cannot find target for " + type + " method in " + target, method, annotation, SuppressedBy.TARGET);
        }
    }

    private void validateMethodVisibility(ExecutableElement method, AnnotationHandle annotation, String type, TypeHandle target, MethodHandle targetMethod) {
        Bytecode.Visibility visTarget = targetMethod.getVisibility();
        if (visTarget == null) {
            return;
        }
        Bytecode.Visibility visMethod = TypeUtils.getVisibility(method);
        String visibility = "visibility of " + (Object)((Object)visTarget) + " method in " + target;
        if (visTarget.ordinal() > visMethod.ordinal()) {
            this.printMessage(IMessagerEx.MessageType.METHOD_VISIBILITY, (Object)((Object)visMethod) + " " + type + " method cannot reduce " + visibility, method, annotation, SuppressedBy.VISIBILITY);
        } else if (visTarget == Bytecode.Visibility.PRIVATE && visMethod.ordinal() > visTarget.ordinal()) {
            this.printMessage(IMessagerEx.MessageType.METHOD_VISIBILITY, (Object)((Object)visMethod) + " " + type + " method will upgrade " + visibility, method, annotation, SuppressedBy.VISIBILITY);
        }
    }

    protected final void validateTargetField(VariableElement field, AnnotationHandle annotation, AliasedElementName name, String type) {
        String fieldType = field.asType().toString();
        for (TypeHandle target : this.mixin.getTargets()) {
            String alias;
            FieldHandle targetField;
            if (target.isImaginary() || (targetField = target.findField(field)) != null) continue;
            List<String> aliases = name.getAliases();
            Iterator<String> iterator = aliases.iterator();
            while (iterator.hasNext() && (targetField = target.findField(alias = iterator.next(), fieldType)) == null) {
            }
            if (targetField != null) continue;
            this.ap.printMessage(IMessagerEx.MessageType.TARGET_ELEMENT_NOT_FOUND, (CharSequence)("Cannot find target for " + type + " field in " + target), (Element)field, annotation.asMirror(), SuppressedBy.TARGET);
        }
    }

    protected final void validateReferencedTarget(AnnotatedElementExecutable elem, String reference, ITargetSelector targetSelector, String subject) {
        if (!(targetSelector instanceof ITargetSelectorByName)) {
            return;
        }
        ITargetSelectorByName nameRef = (ITargetSelectorByName)targetSelector;
        String signature = nameRef.toDescriptor();
        for (TypeHandle target : this.mixin.getTargets()) {
            MethodHandle targetMethod;
            if (target.isImaginary() || (targetMethod = target.findMethod(nameRef.getName(), signature)) != null) continue;
            this.ap.printMessage(IMessagerEx.MessageType.TARGET_ELEMENT_NOT_FOUND, (CharSequence)("Cannot find target method \"" + nameRef.getName() + nameRef.getDesc() + "\" for " + subject + " in " + target), (Element)elem.getElement(), ((AnnotationHandle)elem.getAnnotation()).asMirror(), SuppressedBy.TARGET);
        }
    }

    private void printMessage(IMessagerEx.MessageType type, String msg, Element e2, AnnotationHandle annotation, SuppressedBy suppressedBy) {
        if (annotation == null) {
            this.ap.printMessage(type, (CharSequence)msg, e2, suppressedBy);
        } else {
            this.ap.printMessage(type, (CharSequence)msg, e2, annotation.asMirror(), suppressedBy);
        }
    }

    protected static <T extends IMapping<T>> ObfuscationData<T> stripOwnerData(ObfuscationData<T> data) {
        ObfuscationData stripped = new ObfuscationData();
        for (ObfuscationType type : data) {
            IMapping mapping = (IMapping)data.get(type);
            stripped.put(type, mapping.move(null));
        }
        return stripped;
    }

    protected static <T extends IMapping<T>> ObfuscationData<T> stripDescriptors(ObfuscationData<T> data) {
        ObfuscationData stripped = new ObfuscationData();
        for (ObfuscationType type : data) {
            IMapping mapping = (IMapping)data.get(type);
            stripped.put(type, mapping.transform(null));
        }
        return stripped;
    }

    static class ShadowElementName
    extends AliasedElementName {
        private final boolean hasPrefix;
        private final String prefix;
        private final String baseName;
        private String obfuscated;

        ShadowElementName(Element element, AnnotationHandle shadow) {
            super(element, shadow);
            this.prefix = shadow.getValue("prefix", "shadow$");
            boolean hasPrefix = false;
            String name = this.originalName;
            if (name.startsWith(this.prefix)) {
                hasPrefix = true;
                name = name.substring(this.prefix.length());
            }
            this.hasPrefix = hasPrefix;
            this.obfuscated = this.baseName = name;
        }

        public String toString() {
            return this.baseName;
        }

        @Override
        public String baseName() {
            return this.baseName;
        }

        public ShadowElementName setObfuscatedName(IMapping<?> name) {
            this.obfuscated = name.getName();
            return this;
        }

        public ShadowElementName setObfuscatedName(String name) {
            this.obfuscated = name;
            return this;
        }

        @Override
        public boolean hasPrefix() {
            return this.hasPrefix;
        }

        public String prefix() {
            return this.hasPrefix ? this.prefix : "";
        }

        public String name() {
            return this.prefix(this.baseName);
        }

        public String obfuscated() {
            return this.prefix(this.obfuscated);
        }

        public String prefix(String name) {
            return this.hasPrefix ? this.prefix + name : name;
        }
    }

    static class AliasedElementName {
        protected final String originalName;
        private final List<String> aliases;
        private boolean caseSensitive;

        public AliasedElementName(Element element, AnnotationHandle annotation) {
            this.originalName = element.getSimpleName().toString();
            this.aliases = annotation.getList("aliases");
        }

        public AliasedElementName(MethodHandle method, AnnotationHandle annotation) {
            this.originalName = method.getName();
            this.aliases = annotation.getList("aliases");
        }

        public AliasedElementName setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public boolean isCaseSensitive() {
            return this.caseSensitive;
        }

        public boolean hasAliases() {
            return this.aliases.size() > 0;
        }

        public List<String> getAliases() {
            return this.aliases;
        }

        public String elementName() {
            return this.originalName;
        }

        public String baseName() {
            return this.originalName;
        }

        public boolean hasPrefix() {
            return false;
        }
    }

    static abstract class AnnotatedElementExecutable
    extends AnnotatedElement<ExecutableElement>
    implements ISelectorContext {
        private final IMixinContext context;
        private final String selectorCoordinate;

        public AnnotatedElementExecutable(ExecutableElement element, AnnotationHandle annotation, IMixinContext context, String selectorCoordinate) {
            super(element, annotation);
            this.context = context;
            this.selectorCoordinate = selectorCoordinate;
        }

        @Override
        public ISelectorContext getParent() {
            return null;
        }

        @Override
        public IMixinContext getMixin() {
            return this.context;
        }

        @Override
        public Object getMethod() {
            return new IAnnotatedElement(){

                @Override
                public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
                    return AnnotationHandle.of(AnnotatedElementExecutable.this.getElement(), annotationClass);
                }

                public String toString() {
                    return ((ExecutableElement)AnnotatedElementExecutable.this.getElement()).getSimpleName().toString();
                }
            };
        }

        @Override
        public IAnnotationHandle getSelectorAnnotation() {
            return this.getAnnotation();
        }

        @Override
        public String getSelectorCoordinate(boolean leaf) {
            return leaf ? this.selectorCoordinate : TypeUtils.getName((ExecutableElement)this.element);
        }

        @Override
        public String remap(String reference) {
            return reference;
        }

        public String toString() {
            return TypeUtils.getName((ExecutableElement)this.element);
        }
    }

    static abstract class AnnotatedElement<E extends Element>
    implements IAnnotatedElement {
        protected final E element;
        protected final AnnotationHandle annotation;
        private final String desc;

        public AnnotatedElement(E element, AnnotationHandle annotation) {
            this.element = element;
            this.annotation = annotation;
            this.desc = TypeUtils.getDescriptor(element);
        }

        public E getElement() {
            return this.element;
        }

        public AnnotationHandle getAnnotation() {
            return this.annotation;
        }

        public String getSimpleName() {
            return this.getElement().getSimpleName().toString();
        }

        public String getDesc() {
            return this.desc;
        }

        public final void printMessage(IMessagerEx messager, IMessagerEx.MessageType type, CharSequence msg) {
            messager.printMessage(type, msg, (Element)this.element, this.annotation.asMirror());
        }

        public final void printMessage(IMessagerSuppressible messager, IMessagerEx.MessageType type, CharSequence msg, SuppressedBy suppressedBy) {
            messager.printMessage(type, msg, (Element)this.element, this.annotation.asMirror(), suppressedBy);
        }

        @Override
        public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
            return AnnotationHandle.of(this.element, annotationClass);
        }
    }
}

