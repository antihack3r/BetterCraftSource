/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$LocalVariableAnnotationRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$LocalVariableAnnotationRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        String string2 = attributes.getValue("desc");
        boolean bl2 = Boolean.valueOf(attributes.getValue("visible"));
        int n2 = Integer.parseInt(attributes.getValue("typeRef"));
        TypePath typePath = TypePath.fromString(attributes.getValue("typePath"));
        String[] stringArray = attributes.getValue("start").split(" ");
        Label[] labelArray = new Label[stringArray.length];
        for (int i2 = 0; i2 < labelArray.length; ++i2) {
            labelArray[i2] = this.getLabel(stringArray[i2]);
        }
        String[] stringArray2 = attributes.getValue("end").split(" ");
        Label[] labelArray2 = new Label[stringArray2.length];
        for (int i3 = 0; i3 < labelArray2.length; ++i3) {
            labelArray2[i3] = this.getLabel(stringArray2[i3]);
        }
        String[] stringArray3 = attributes.getValue("index").split(" ");
        int[] nArray = new int[stringArray3.length];
        for (int i4 = 0; i4 < nArray.length; ++i4) {
            nArray[i4] = Integer.parseInt(stringArray3[i4]);
        }
        this.this$0.push(((MethodVisitor)this.this$0.peek()).visitLocalVariableAnnotation(n2, typePath, labelArray, labelArray2, nArray, string2, bl2));
    }

    public void end(String string) {
        AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.pop();
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
    }
}

