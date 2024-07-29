/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.AnnotationRemapper;
import org.objectweb.asm.commons.Remapper;

public class MethodRemapper
extends MethodVisitor {
    protected final Remapper remapper;

    public MethodRemapper(MethodVisitor methodVisitor, Remapper remapper) {
        this(327680, methodVisitor, remapper);
    }

    protected MethodRemapper(int n2, MethodVisitor methodVisitor, Remapper remapper) {
        super(n2, methodVisitor);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationVisitor annotationVisitor = super.visitAnnotationDefault();
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitAnnotation(this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(n2, typePath, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitParameterAnnotation(int n2, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitParameterAnnotation(n2, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public void visitFrame(int n2, int n3, Object[] objectArray, int n4, Object[] objectArray2) {
        super.visitFrame(n2, n3, this.remapEntries(n3, objectArray), n4, this.remapEntries(n4, objectArray2));
    }

    private Object[] remapEntries(int n2, Object[] objectArray) {
        for (int i2 = 0; i2 < n2; ++i2) {
            if (!(objectArray[i2] instanceof String)) continue;
            Object[] objectArray2 = new Object[n2];
            if (i2 > 0) {
                System.arraycopy(objectArray, 0, objectArray2, 0, i2);
            }
            do {
                Object object = objectArray[i2];
                Object object2 = objectArray2[i2++] = object instanceof String ? this.remapper.mapType((String)object) : object;
            } while (i2 < n2);
            return objectArray2;
        }
        return objectArray;
    }

    public void visitFieldInsn(int n2, String string, String string2, String string3) {
        super.visitFieldInsn(n2, this.remapper.mapType(string), this.remapper.mapFieldName(string, string2, string3), this.remapper.mapDesc(string3));
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3) {
        if (this.api >= 327680) {
            super.visitMethodInsn(n2, string, string2, string3);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, n2 == 185);
    }

    public void visitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        if (this.api < 327680) {
            super.visitMethodInsn(n2, string, string2, string3, bl2);
            return;
        }
        this.doVisitMethodInsn(n2, string, string2, string3, bl2);
    }

    private void doVisitMethodInsn(int n2, String string, String string2, String string3, boolean bl2) {
        if (this.mv != null) {
            this.mv.visitMethodInsn(n2, this.remapper.mapType(string), this.remapper.mapMethodName(string, string2, string3), this.remapper.mapMethodDesc(string3), bl2);
        }
    }

    public void visitInvokeDynamicInsn(String string, String string2, Handle handle, Object ... objectArray) {
        for (int i2 = 0; i2 < objectArray.length; ++i2) {
            objectArray[i2] = this.remapper.mapValue(objectArray[i2]);
        }
        super.visitInvokeDynamicInsn(this.remapper.mapInvokeDynamicMethodName(string, string2), this.remapper.mapMethodDesc(string2), (Handle)this.remapper.mapValue(handle), objectArray);
    }

    public void visitTypeInsn(int n2, String string) {
        super.visitTypeInsn(n2, this.remapper.mapType(string));
    }

    public void visitLdcInsn(Object object) {
        super.visitLdcInsn(this.remapper.mapValue(object));
    }

    public void visitMultiANewArrayInsn(String string, int n2) {
        super.visitMultiANewArrayInsn(this.remapper.mapDesc(string), n2);
    }

    public AnnotationVisitor visitInsnAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitInsnAnnotation(n2, typePath, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public void visitTryCatchBlock(Label label, Label label2, Label label3, String string) {
        super.visitTryCatchBlock(label, label2, label3, string == null ? null : this.remapper.mapType(string));
    }

    public AnnotationVisitor visitTryCatchAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitTryCatchAnnotation(n2, typePath, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }

    public void visitLocalVariable(String string, String string2, String string3, Label label, Label label2, int n2) {
        super.visitLocalVariable(string, this.remapper.mapDesc(string2), this.remapper.mapSignature(string3, true), label, label2, n2);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int n2, TypePath typePath, Label[] labelArray, Label[] labelArray2, int[] nArray, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? annotationVisitor : new AnnotationRemapper(annotationVisitor, this.remapper);
    }
}

