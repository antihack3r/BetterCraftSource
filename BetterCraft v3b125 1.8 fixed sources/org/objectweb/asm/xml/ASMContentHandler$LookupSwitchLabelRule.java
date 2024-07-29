/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$LookupSwitchLabelRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$LookupSwitchLabelRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        HashMap hashMap = (HashMap)this.this$0.peek();
        ((ArrayList)hashMap.get("labels")).add(this.getLabel(attributes.getValue("name")));
        ((ArrayList)hashMap.get("keys")).add(attributes.getValue("key"));
    }
}

