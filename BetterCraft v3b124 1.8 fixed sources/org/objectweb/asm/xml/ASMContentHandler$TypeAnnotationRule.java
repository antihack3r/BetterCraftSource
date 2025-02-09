/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$TypeAnnotationRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$TypeAnnotationRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        String string2 = attributes.getValue("desc");
        boolean bl2 = Boolean.valueOf(attributes.getValue("visible"));
        int n2 = Integer.parseInt(attributes.getValue("typeRef"));
        TypePath typePath = TypePath.fromString(attributes.getValue("typePath"));
        Object object = this.this$0.peek();
        if (object instanceof ClassVisitor) {
            this.this$0.push(((ClassVisitor)object).visitTypeAnnotation(n2, typePath, string2, bl2));
        } else if (object instanceof FieldVisitor) {
            this.this$0.push(((FieldVisitor)object).visitTypeAnnotation(n2, typePath, string2, bl2));
        } else if (object instanceof MethodVisitor) {
            this.this$0.push(((MethodVisitor)object).visitTypeAnnotation(n2, typePath, string2, bl2));
        }
    }

    public void end(String string) {
        AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.pop();
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
    }
}

