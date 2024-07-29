/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.tools.obfuscation.mirror;

import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.spongepowered.asm.mixin.injection.selectors.ITargetSelectorByName;
import org.spongepowered.asm.obfuscation.mapping.common.MappingMethod;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.asm.IAnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.AnnotationHandle;
import org.spongepowered.tools.obfuscation.mirror.FieldHandle;
import org.spongepowered.tools.obfuscation.mirror.MethodHandle;
import org.spongepowered.tools.obfuscation.mirror.TypeReference;
import org.spongepowered.tools.obfuscation.mirror.TypeUtils;
import org.spongepowered.tools.obfuscation.mirror.mapping.MappingMethodResolvable;

public class TypeHandle {
    private final String name;
    private final PackageElement pkg;
    private final TypeElement element;
    private TypeReference reference;

    public TypeHandle(PackageElement pkg, String name) {
        this.name = name.replace('.', '/');
        this.pkg = pkg;
        this.element = null;
    }

    public TypeHandle(TypeElement element) {
        this.pkg = TypeUtils.getPackage(element);
        this.name = TypeUtils.getInternalName(element);
        this.element = element;
    }

    public TypeHandle(DeclaredType type) {
        this((TypeElement)type.asElement());
    }

    public final String toString() {
        return this.name.replace('/', '.');
    }

    public final String getName() {
        return this.name;
    }

    public final String getSimpleName() {
        return Bytecode.getSimpleName(this.name);
    }

    public final PackageElement getPackage() {
        return this.pkg;
    }

    public final TypeElement getElement() {
        return this.element;
    }

    protected TypeElement getTargetElement() {
        return this.element;
    }

    public IAnnotationHandle getAnnotation(Class<? extends Annotation> annotationClass) {
        return AnnotationHandle.of(this.getTargetElement(), annotationClass);
    }

    protected final List<? extends Element> getEnclosedElements() {
        return TypeHandle.getEnclosedElements(this.getTargetElement());
    }

    protected <T extends Element> List<T> getEnclosedElements(ElementKind ... kind) {
        return TypeHandle.getEnclosedElements(this.getTargetElement(), kind);
    }

    public boolean hasTypeMirror() {
        return this.getTargetElement() != null;
    }

    public TypeMirror getTypeMirror() {
        return this.getTargetElement() != null ? this.getTargetElement().asType() : null;
    }

    public TypeHandle getSuperclass() {
        TypeElement targetElement = this.getTargetElement();
        if (targetElement == null) {
            return null;
        }
        TypeMirror superClass = targetElement.getSuperclass();
        if (superClass == null || superClass.getKind() == TypeKind.NONE) {
            return null;
        }
        return new TypeHandle((DeclaredType)superClass);
    }

    public List<TypeHandle> getInterfaces() {
        if (this.getTargetElement() == null) {
            return Collections.emptyList();
        }
        ImmutableList.Builder list = ImmutableList.builder();
        for (TypeMirror typeMirror : this.getTargetElement().getInterfaces()) {
            list.add(new TypeHandle((DeclaredType)typeMirror));
        }
        return list.build();
    }

    public List<MethodHandle> getMethods() {
        ArrayList<MethodHandle> methods = new ArrayList<MethodHandle>();
        for (ExecutableElement method : this.getEnclosedElements(ElementKind.METHOD)) {
            MethodHandle handle = new MethodHandle(this, method);
            methods.add(handle);
        }
        return methods;
    }

    public boolean isPublic() {
        TypeElement targetElement = this.getTargetElement();
        if (targetElement == null || !targetElement.getModifiers().contains((Object)Modifier.PUBLIC)) {
            return false;
        }
        for (Element e2 = targetElement.getEnclosingElement(); e2 != null && e2.getKind() != ElementKind.PACKAGE; e2 = e2.getEnclosingElement()) {
            if (e2.getModifiers().contains((Object)Modifier.PUBLIC)) continue;
            return false;
        }
        return true;
    }

    public boolean isImaginary() {
        return this.getTargetElement() == null;
    }

    public boolean isSimulated() {
        return false;
    }

    public final TypeReference getReference() {
        if (this.reference == null) {
            this.reference = new TypeReference(this);
        }
        return this.reference;
    }

    public MappingMethod getMappingMethod(String name, String desc) {
        return new MappingMethodResolvable(this, name, desc);
    }

    public String findDescriptor(ITargetSelectorByName selector) {
        String desc = selector.getDesc();
        if (desc == null) {
            for (ExecutableElement method : this.getEnclosedElements(ElementKind.METHOD)) {
                if (!method.getSimpleName().toString().equals(selector.getName())) continue;
                desc = TypeUtils.getDescriptor(method);
                break;
            }
        }
        return desc;
    }

    public final FieldHandle findField(VariableElement element) {
        return this.findField(element, true);
    }

    public final FieldHandle findField(VariableElement element, boolean matchCase) {
        return this.findField(element.getSimpleName().toString(), TypeUtils.getTypeName(element.asType()), matchCase);
    }

    public final FieldHandle findField(String name, String type) {
        return this.findField(name, type, true);
    }

    public FieldHandle findField(String name, String type, boolean matchCase) {
        String rawType = TypeUtils.stripGenerics(type);
        for (VariableElement field : this.getEnclosedElements(ElementKind.FIELD)) {
            if (TypeHandle.compareElement(field, name, type, matchCase)) {
                return new FieldHandle(this.getTargetElement(), field);
            }
            if (!TypeHandle.compareElement(field, name, rawType, matchCase)) continue;
            return new FieldHandle(this.getTargetElement(), field, true);
        }
        return null;
    }

    public final MethodHandle findMethod(ExecutableElement element) {
        return this.findMethod(element, true);
    }

    public final MethodHandle findMethod(ExecutableElement element, boolean matchCase) {
        return this.findMethod(element.getSimpleName().toString(), TypeUtils.getJavaSignature(element), matchCase);
    }

    public final MethodHandle findMethod(String name, String signature) {
        return this.findMethod(name, signature, true);
    }

    public MethodHandle findMethod(String name, String signature, boolean matchCase) {
        String rawSignature = TypeUtils.stripGenerics(signature);
        return TypeHandle.findMethod(this, name, signature, rawSignature, matchCase);
    }

    protected static MethodHandle findMethod(TypeHandle target, String name, String signature, String rawSignature, boolean matchCase) {
        for (ExecutableElement method : TypeHandle.getEnclosedElements(target.getTargetElement(), ElementKind.CONSTRUCTOR, ElementKind.METHOD)) {
            if (!TypeHandle.compareElement(method, name, signature, matchCase) && !TypeHandle.compareElement(method, name, rawSignature, matchCase)) continue;
            return new MethodHandle(target, method);
        }
        return null;
    }

    protected static boolean compareElement(Element elem, String name, String type, boolean matchCase) {
        try {
            String elementName = elem.getSimpleName().toString();
            String elementType = TypeUtils.getJavaSignature(elem);
            String rawElementType = TypeUtils.stripGenerics(elementType);
            boolean compared = matchCase ? name.equals(elementName) : name.equalsIgnoreCase(elementName);
            return compared && (type.length() == 0 || type.equals(elementType) || type.equals(rawElementType));
        }
        catch (NullPointerException ex2) {
            return false;
        }
    }

    protected static <T extends Element> List<T> getEnclosedElements(TypeElement targetElement, ElementKind ... kind) {
        if (kind == null || kind.length < 1) {
            return TypeHandle.getEnclosedElements(targetElement);
        }
        if (targetElement == null) {
            return Collections.emptyList();
        }
        ImmutableList.Builder list = ImmutableList.builder();
        block0: for (Element element : targetElement.getEnclosedElements()) {
            for (ElementKind ek2 : kind) {
                if (element.getKind() != ek2) continue;
                list.add(element);
                continue block0;
            }
        }
        return list.build();
    }

    protected static List<? extends Element> getEnclosedElements(TypeElement targetElement) {
        return targetElement != null ? targetElement.getEnclosedElements() : Collections.emptyList();
    }
}

