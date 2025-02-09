/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$InnerClassRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$InnerClassRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        int n2 = this.getAccess(attributes.getValue("access"));
        String string2 = attributes.getValue("name");
        String string3 = attributes.getValue("outerName");
        String string4 = attributes.getValue("innerName");
        this.this$0.cv.visitInnerClass(string2, string3, string4, n2);
    }
}

