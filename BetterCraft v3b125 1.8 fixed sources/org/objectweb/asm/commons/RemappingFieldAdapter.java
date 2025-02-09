/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingAnnotationAdapter;

public class RemappingFieldAdapter
extends FieldVisitor {
    private final Remapper remapper;

    public RemappingFieldAdapter(FieldVisitor fieldVisitor, Remapper remapper) {
        this(327680, fieldVisitor, remapper);
    }

    protected RemappingFieldAdapter(int n2, FieldVisitor fieldVisitor, Remapper remapper) {
        super(n2, fieldVisitor);
        this.remapper = remapper;
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = this.fv.visitAnnotation(this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? null : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(n2, typePath, this.remapper.mapDesc(string), bl2);
        return annotationVisitor == null ? null : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
    }
}

