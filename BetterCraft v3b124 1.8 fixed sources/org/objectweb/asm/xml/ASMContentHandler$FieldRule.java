/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class ASMContentHandler$FieldRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$FieldRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) throws SAXException {
        int n2 = this.getAccess(attributes.getValue("access"));
        String string2 = attributes.getValue("name");
        String string3 = attributes.getValue("signature");
        String string4 = attributes.getValue("desc");
        Object object = this.getValue(string4, attributes.getValue("value"));
        this.this$0.push(this.this$0.cv.visitField(n2, string2, string4, string3, object));
    }

    public void end(String string) {
        ((FieldVisitor)this.this$0.pop()).visitEnd();
    }
}

