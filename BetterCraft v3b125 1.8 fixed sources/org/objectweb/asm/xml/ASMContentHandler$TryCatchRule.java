/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.Label;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$TryCatchRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$TryCatchRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        Label label = this.getLabel(attributes.getValue("start"));
        Label label2 = this.getLabel(attributes.getValue("end"));
        Label label3 = this.getLabel(attributes.getValue("handler"));
        String string2 = attributes.getValue("type");
        this.getCodeVisitor().visitTryCatchBlock(label, label2, label3, string2);
    }
}

