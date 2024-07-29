/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.Label;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$LocalVarRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$LocalVarRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        String string2 = attributes.getValue("name");
        String string3 = attributes.getValue("desc");
        String string4 = attributes.getValue("signature");
        Label label = this.getLabel(attributes.getValue("start"));
        Label label2 = this.getLabel(attributes.getValue("end"));
        int n2 = Integer.parseInt(attributes.getValue("var"));
        this.getCodeVisitor().visitLocalVariable(string2, string3, string4, label, label2, n2);
    }
}

