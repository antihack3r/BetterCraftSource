/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.xml.SAXAdapter;
import org.objectweb.asm.xml.SAXAnnotationAdapter;
import org.xml.sax.Attributes;

public final class SAXFieldAdapter
extends FieldVisitor {
    SAXAdapter sa;

    public SAXFieldAdapter(SAXAdapter sAXAdapter, Attributes attributes) {
        super(327680);
        this.sa = sAXAdapter;
        sAXAdapter.addStart("field", attributes);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        return new SAXAnnotationAdapter(this.sa, "annotation", bl2 ? 1 : -1, null, string);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        return new SAXAnnotationAdapter(this.sa, "typeAnnotation", bl2 ? 1 : -1, null, string, n2, typePath);
    }

    public void visitEnd() {
        this.sa.addEnd("field");
    }
}

