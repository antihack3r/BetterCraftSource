/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$OuterClassRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$OuterClassRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        String string2 = attributes.getValue("owner");
        String string3 = attributes.getValue("name");
        String string4 = attributes.getValue("desc");
        this.this$0.cv.visitOuterClass(string2, string3, string4);
    }
}

