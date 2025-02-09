/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class StaticInitMerger
extends ClassVisitor {
    private String name;
    private MethodVisitor clinit;
    private final String prefix;
    private int counter;

    public StaticInitMerger(String string, ClassVisitor classVisitor) {
        this(327680, string, classVisitor);
    }

    protected StaticInitMerger(int n2, String string, ClassVisitor classVisitor) {
        super(n2, classVisitor);
        this.prefix = string;
    }

    public void visit(int n2, int n3, String string, String string2, String string3, String[] stringArray) {
        this.cv.visit(n2, n3, string, string2, string3, stringArray);
        this.name = string;
    }

    public MethodVisitor visitMethod(int n2, String string, String string2, String string3, String[] stringArray) {
        MethodVisitor methodVisitor;
        if ("<clinit>".equals(string)) {
            int n3 = 10;
            String string4 = this.prefix + this.counter++;
            methodVisitor = this.cv.visitMethod(n3, string4, string2, string3, stringArray);
            if (this.clinit == null) {
                this.clinit = this.cv.visitMethod(n3, string, string2, null, null);
            }
            this.clinit.visitMethodInsn(184, this.name, string4, string2, false);
        } else {
            methodVisitor = this.cv.visitMethod(n2, string, string2, string3, stringArray);
        }
        return methodVisitor;
    }

    public void visitEnd() {
        if (this.clinit != null) {
            this.clinit.visitInsn(177);
            this.clinit.visitMaxs(0, 0);
        }
        this.cv.visitEnd();
    }
}

