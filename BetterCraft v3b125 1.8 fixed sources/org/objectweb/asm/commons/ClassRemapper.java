/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.AnnotationRemapper;
import org.objectweb.asm.commons.FieldRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.Remapper;

public class ClassRemapper
extends ClassVisitor {
    protected final Remapper remapper;
    protected String className;

    public ClassRemapper(ClassVisitor classVisitor, Remapper remapper) {
        this(327680, classVisitor, remapper);
    }

    protected ClassRemapper(int n2, ClassVisitor classVisitor, Remapper remapper) {
        super(n2, classVisitor);
        this.remapper = remapper;
    }

    public void visit(int n2, int n3, String string, String string2, String string3, String[] stringArray) {
        this.className = string;
        super.visit(n2, n3, this.remapper.mapType(string), this.remapper.mapSignature(string2, false), this.remapper.mapType(string3), stringArray == null ? null : this.remapper.mapTypes(stringArray));
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? null : this.createAnnotationRemapper(annotationVisitor);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(n2, typePath, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? null : this.createAnnotationRemapper(annotationVisitor);
    }

    public FieldVisitor visitField(int n2, String string, String string2, String string3, Object object) {
        FieldVisitor fieldVisitor = super.visitField(n2, this.remapper.mapFieldName(this.className, string, string2), this.remapper.mapDesc(string2), this.remapper.mapSignature(string3, true), this.remapper.mapValue(object));
        return fieldVisitor == null ? null : this.createFieldRemapper(fieldVisitor);
    }

    public MethodVisitor visitMethod(int n2, String string, String string2, String string3, String[] stringArray) {
        String string4 = this.remapper.mapMethodDesc(string2);
        MethodVisitor methodVisitor = super.visitMethod(n2, this.remapper.mapMethodName(this.className, string, string2), string4, this.remapper.mapSignature(string3, false), stringArray == null ? null : this.remapper.mapTypes(stringArray));
        return methodVisitor == null ? null : this.createMethodRemapper(methodVisitor);
    }

    public void visitInnerClass(String string, String string2, String string3, int n2) {
        super.visitInnerClass(this.remapper.mapType(string), string2 == null ? null : this.remapper.mapType(string2), string3, n2);
    }

    public void visitOuterClass(String string, String string2, String string3) {
        super.visitOuterClass(this.remapper.mapType(string), string2 == null ? null : this.remapper.mapMethodName(string, string2, string3), string3 == null ? null : this.remapper.mapMethodDesc(string3));
    }

    protected FieldVisitor createFieldRemapper(FieldVisitor fieldVisitor) {
        return new FieldRemapper(fieldVisitor, this.remapper);
    }

    protected MethodVisitor createMethodRemapper(MethodVisitor methodVisitor) {
        return new MethodRemapper(methodVisitor, this.remapper);
    }

    protected AnnotationVisitor createAnnotationRemapper(AnnotationVisitor annotationVisitor) {
        return new AnnotationRemapper(annotationVisitor, this.remapper);
    }
}
