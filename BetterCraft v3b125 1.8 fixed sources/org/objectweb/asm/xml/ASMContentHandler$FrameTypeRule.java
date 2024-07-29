/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$FrameTypeRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$FrameTypeRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        ArrayList arrayList = (ArrayList)((HashMap)this.this$0.peek()).get(string);
        String string2 = attributes.getValue("type");
        if ("uninitialized".equals(string2)) {
            arrayList.add(this.getLabel(attributes.getValue("label")));
        } else {
            Integer n2 = (Integer)ASMContentHandler.TYPES.get(string2);
            if (n2 == null) {
                arrayList.add(string2);
            } else {
                arrayList.add(n2);
            }
        }
    }
}

