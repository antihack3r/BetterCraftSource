/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm;

public abstract class AnnotationVisitor {
    protected final int api;
    protected AnnotationVisitor av;

    public AnnotationVisitor(int api2) {
        this(api2, null);
    }

    public AnnotationVisitor(int api2, AnnotationVisitor annotationVisitor) {
        if (api2 != 393216 && api2 != 327680 && api2 != 262144 && api2 != 458752) {
            throw new IllegalArgumentException();
        }
        this.api = api2;
        this.av = annotationVisitor;
    }

    public void visit(String name, Object value) {
        if (this.av != null) {
            this.av.visit(name, value);
        }
    }

    public void visitEnum(String name, String descriptor, String value) {
        if (this.av != null) {
            this.av.visitEnum(name, descriptor, value);
        }
    }

    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        if (this.av != null) {
            return this.av.visitAnnotation(name, descriptor);
        }
        return null;
    }

    public AnnotationVisitor visitArray(String name) {
        if (this.av != null) {
            return this.av.visitArray(name);
        }
        return null;
    }

    public void visitEnd() {
        if (this.av != null) {
            this.av.visitEnd();
        }
    }
}

