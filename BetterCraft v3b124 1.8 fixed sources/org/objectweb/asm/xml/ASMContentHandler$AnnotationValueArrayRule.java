/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$AnnotationValueArrayRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$AnnotationValueArrayRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.peek();
        this.this$0.push(annotationVisitor == null ? null : annotationVisitor.visitArray(attributes.getValue("name")));
    }

    public void end(String string) {
        AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.pop();
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
    }
}

