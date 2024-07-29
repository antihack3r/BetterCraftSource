/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.commons.Remapper;

public class RemappingAnnotationAdapter
extends AnnotationVisitor {
    protected final Remapper remapper;

    public RemappingAnnotationAdapter(AnnotationVisitor annotationVisitor, Remapper remapper) {
        this(327680, annotationVisitor, remapper);
    }

    protected RemappingAnnotationAdapter(int n2, AnnotationVisitor annotationVisitor, Remapper remapper) {
        super(n2, annotationVisitor);
        this.remapper = remapper;
    }

    public void visit(String string, Object object) {
        this.av.visit(string, this.remapper.mapValue(object));
    }

    public void visitEnum(String string, String string2, String string3) {
        this.av.visitEnum(string, this.remapper.mapDesc(string2), string3);
    }

    public AnnotationVisitor visitAnnotation(String string, String string2) {
        AnnotationVisitor annotationVisitor = this.av.visitAnnotation(string, this.remapper.mapDesc(string2));
        return annotationVisitor == null ? null : (annotationVisitor == this.av ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
    }

    public AnnotationVisitor visitArray(String string) {
        AnnotationVisitor annotationVisitor = this.av.visitArray(string);
        return annotationVisitor == null ? null : (annotationVisitor == this.av ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
    }
}

