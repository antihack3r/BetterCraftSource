/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.util;

import java.io.PrintWriter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceAnnotationVisitor;
import org.objectweb.asm.util.TraceFieldVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

public final class TraceClassVisitor
extends ClassVisitor {
    private final PrintWriter pw;
    public final Printer p;

    public TraceClassVisitor(PrintWriter printWriter) {
        this(null, printWriter);
    }

    public TraceClassVisitor(ClassVisitor classVisitor, PrintWriter printWriter) {
        this(classVisitor, new Textifier(), printWriter);
    }

    public TraceClassVisitor(ClassVisitor classVisitor, Printer printer, PrintWriter printWriter) {
        super(327680, classVisitor);
        this.pw = printWriter;
        this.p = printer;
    }

    public void visit(int n2, int n3, String string, String string2, String string3, String[] stringArray) {
        this.p.visit(n2, n3, string, string2, string3, stringArray);
        super.visit(n2, n3, string, string2, string3, stringArray);
    }

    public void visitSource(String string, String string2) {
        this.p.visitSource(string, string2);
        super.visitSource(string, string2);
    }

    public void visitOuterClass(String string, String string2, String string3) {
        this.p.visitOuterClass(string, string2, string3);
        super.visitOuterClass(string, string2, string3);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        Printer printer = this.p.visitClassAnnotation(string, bl2);
        AnnotationVisitor annotationVisitor = this.cv == null ? null : this.cv.visitAnnotation(string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        Printer printer = this.p.visitClassTypeAnnotation(n2, typePath, string, bl2);
        AnnotationVisitor annotationVisitor = this.cv == null ? null : this.cv.visitTypeAnnotation(n2, typePath, string, bl2);
        return new TraceAnnotationVisitor(annotationVisitor, printer);
    }

    public void visitAttribute(Attribute attribute) {
        this.p.visitClassAttribute(attribute);
        super.visitAttribute(attribute);
    }

    public void visitInnerClass(String string, String string2, String string3, int n2) {
        this.p.visitInnerClass(string, string2, string3, n2);
        super.visitInnerClass(string, string2, string3, n2);
    }

    public FieldVisitor visitField(int n2, String string, String string2, String string3, Object object) {
        Printer printer = this.p.visitField(n2, string, string2, string3, object);
        FieldVisitor fieldVisitor = this.cv == null ? null : this.cv.visitField(n2, string, string2, string3, object);
        return new TraceFieldVisitor(fieldVisitor, printer);
    }

    public MethodVisitor visitMethod(int n2, String string, String string2, String string3, String[] stringArray) {
        Printer printer = this.p.visitMethod(n2, string, string2, string3, stringArray);
        MethodVisitor methodVisitor = this.cv == null ? null : this.cv.visitMethod(n2, string, string2, string3, stringArray);
        return new TraceMethodVisitor(methodVisitor, printer);
    }

    public void visitEnd() {
        this.p.visitClassEnd();
        if (this.pw != null) {
            this.p.print(this.pw);
            this.pw.flush();
        }
        super.visitEnd();
    }
}

