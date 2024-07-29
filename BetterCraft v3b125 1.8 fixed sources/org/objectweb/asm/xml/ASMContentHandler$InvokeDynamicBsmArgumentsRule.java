/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class ASMContentHandler$InvokeDynamicBsmArgumentsRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$InvokeDynamicBsmArgumentsRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) throws SAXException {
        ArrayList arrayList = (ArrayList)this.this$0.peek();
        arrayList.add(this.getValue(attributes.getValue("desc"), attributes.getValue("cst")));
    }
}

