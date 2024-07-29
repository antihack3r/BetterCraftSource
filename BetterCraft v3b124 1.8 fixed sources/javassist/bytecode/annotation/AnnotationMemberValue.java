/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationImpl;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.MemberValueVisitor;

public class AnnotationMemberValue
extends MemberValue {
    Annotation value;

    public AnnotationMemberValue(ConstPool cp2) {
        this(null, cp2);
    }

    public AnnotationMemberValue(Annotation a2, ConstPool cp2) {
        super('@', cp2);
        this.value = a2;
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) throws ClassNotFoundException {
        return AnnotationImpl.make(cl2, this.getType(cl2), cp2, this.value);
    }

    @Override
    Class<?> getType(ClassLoader cl2) throws ClassNotFoundException {
        if (this.value == null) {
            throw new ClassNotFoundException("no type specified");
        }
        return AnnotationMemberValue.loadClass(cl2, this.value.getTypeName());
    }

    public Annotation getValue() {
        return this.value;
    }

    public void setValue(Annotation newValue) {
        this.value = newValue;
    }

    public String toString() {
        return this.value.toString();
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.annotationValue();
        this.value.write(writer);
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitAnnotationMemberValue(this);
    }
}

