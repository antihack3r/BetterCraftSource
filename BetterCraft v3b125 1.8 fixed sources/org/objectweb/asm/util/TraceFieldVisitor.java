/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceAnnotationVisitor;

public final class TraceFieldVisitor
extends FieldVisitor {
    public final Printer p;

    public TraceFieldVisitor(Printer printer) {
        this(null, printer);
    }

    public TraceFieldVisitor(FieldVisitor fieldVisitor, Printer printer) {
        super(327680, fieldVisitor);
        this.p = printer;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        Printer printer = this.p.visitFieldAnnotation(string, bl2);
        AnnotationVisitor annotationVisitor = this.fv == null ? null : this.fv.visitAnnotation(string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        Printer printer = this.p.visitFieldTypeAnnotation(n2, typePath, string, bl2);
        AnnotationVisitor annotationVisitor = this.fv == null ? null : this.fv.visitTypeAnnotation(n2, typePath, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitAttribute(Attribute attribute) {
        this.p.visitFieldAttribute(attribute);
        super.visitAttribute(attribute);
    }

    public void visitEnd() {
        this.p.visitFieldEnd();
        super.visitEnd();
    }
}

