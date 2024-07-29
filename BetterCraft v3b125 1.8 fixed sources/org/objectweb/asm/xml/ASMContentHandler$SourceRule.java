/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$SourceRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$SourceRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        String string2 = attributes.getValue("file");
        String string3 = attributes.getValue("debug");
        this.this$0.cv.visitSource(string2, string3);
    }
}

