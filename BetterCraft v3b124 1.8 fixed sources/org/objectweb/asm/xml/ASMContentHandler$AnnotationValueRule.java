/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class ASMContentHandler$AnnotationValueRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$AnnotationValueRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) throws SAXException {
        AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.peek();
        if (annotationVisitor != null) {
            annotationVisitor.visit(attributes.getValue("name"), this.getValue(attributes.getValue("desc"), attributes.getValue("value")));
        }
    }
}

