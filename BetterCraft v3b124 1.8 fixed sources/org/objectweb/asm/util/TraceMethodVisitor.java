/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceAnnotationVisitor;

public final class TraceMethodVisitor
extends MethodVisitor {
    public final Printer p;

    public TraceMethodVisitor(Printer printer) {
        this(null, printer);
    }

    public TraceMethodVisitor(MethodVisitor methodVisitor, Printer printer) {
        super(327680, methodVisitor);
        this.p = printer;
    }

    public void visitParameter(String string, int n2) {
        this.p.visitParameter(string, n2);
        super.visitParameter(string, n2);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        Printer printer = this.p.visitMethodAnnotation(string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitAnnotation(string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        Printer printer = this.p.visitMethodTypeAnnotation(n2, typePath, string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitTypeAnnotation(n2, typePath, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitAttribute(Attribute attribute) {
        this.p.visitMethodAttribute(attribute);
        super.visitAttribute(attribute);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        Printer printer = this.p.visitAnnotationDefault();
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitAnnotationDefault();
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public AnnotationVisitor visitParameterAnnotation(int n2, String string, boolean bl2) {
        Printer printer = this.p.visitParameterAnnotation(n2, string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitParameterAnnotation(n2, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitCode() {
        this.p.visitCode();
        super.visitCode();
    }

    public void visitFrame(int n2, int n3, Object[] objectArray, int n4, Object[] objectArray2) {
        this.p.visitFrame(n2, n3, objectArray, n4, objectArray2);
        super.visitFrame(n2, n3, objectArray, n4, objectArray2);
    }

    public void visitInsn(int n2) {
        this.p.visitInsn(n2);
        super.visitInsn(n2);
    }

    public void visitIntInsn(int n2, int n3) {
        this.p.visitIntInsn(n2, n3);
        super.visitIntInsn(n2, n3);
    }

    public void visitVarInsn(int n2, int n3) {
        this.p.visitVarInsn(n2, n3);
        super.visitVarInsn(n2, n3);
    }

    public void visitTypeInsn(int n2, String string) {
        this.p.visitTypeInsn(n2, string);
        super.visitTypeInsn(n2, string);
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        this.p.visitFieldInsn(n2, string, string2, string3);
        super.visitFieldInsn(n2, string, string2, string3);
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3) {
        if (this.api >= 327680) {
            super.visitMethodInsn(n2, string, string2, string3);
            return;
        }
        this.p.visitMethodInsn(n2, string, string2, string3);
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, string, string2, string3);
        }
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            super.visitMethodInsn(n2, string, string2, string3, bl2);
            return;
        }
        this.p.visitMethodInsn(n2, string, string2, string3, bl2);
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, string, string2, string3, bl2);
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        this.p.visitInvokeDynamicInsn(string, string2, handle, objectArray);
        super.visitInvokeDynamicInsn(string, string2, handle, objectArray);
    }

    public void visitJumpInsn(int n2, Label label) {
        this.p.visitJumpInsn(n2, label);
        super.visitJumpInsn(n2, label);
    }

    public void visitLabel(Label label) {
        this.p.visitLabel(label);
        super.visitLabel(label);
    }

    public void visitLdcInsn(Object object) {
        this.p.visitLdcInsn(object);
        super.visitLdcInsn(object);
    }

    public void visitIincInsn(int n2, int n3) {
        this.p.visitIincInsn(n2, n3);
        super.visitIincInsn(n2, n3);
    }

    public void visitTableSwitchInsn(int n2, int n3, Label label, Label ... labelArray) {
        this.p.visitTableSwitchInsn(n2, n3, label, labelArray);
        super.visitTableSwitchInsn(n2, n3, label, labelArray);
    }

    public void visitLookupSwitchInsn(Label label, int[] nArray, Label[] labelArray) {
        this.p.visitLookupSwitchInsn(label, nArray, labelArray);
        super.visitLookupSwitchInsn(label, nArray, labelArray);
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        this.p.visitMultiANewArrayInsn(string, n2);
        super.visitMultiANewArrayInsn(string, n2);
    }

    public AnnotationVisitor visitInsnAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        Printer printer = this.p.visitInsnAnnotation(n2, typePath, string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitInsnAnnotation(n2, typePath, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        this.p.visitTryCatchBlock(label, label2, label3, string);
        super.visitTryCatchBlock(label, label2, label3, string);
    }

    public AnnotationVisitor visitTryCatchAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        Printer printer = this.p.visitTryCatchAnnotation(n2, typePath, string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitTryCatchAnnotation(n2, typePath, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n2) {
        this.p.visitLocalVariable(string, string2, string3, label, label2, n2);
        super.visitLocalVariable(string, string2, string3, label, label2, n2);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int n2, TypePath typePath, Label[] labelArray, Label[] labelArray2, int[] nArray, String string, boolean bl2) {
        Printer printer = this.p.visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray, string, bl2);
        AnnotationVisitor annotationVisitor = this.mv == null ? null : this.mv.visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitLineNumber(int n2, Label label) {
        this.p.visitLineNumber(n2, label);
        super.visitLineNumber(n2, label);
    }

    public void visitMaxs(int n2, int n3) {
        this.p.visitMaxs(n2, n3);
        super.visitMaxs(n2, n3);
    }

    public void visitEnd() {
        this.p.visitMethodEnd();
        super.visitEnd();
    }
}

