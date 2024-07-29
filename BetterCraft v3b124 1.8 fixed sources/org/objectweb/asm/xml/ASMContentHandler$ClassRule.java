/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$ClassRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$ClassRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        int n2 = Integer.parseInt(attributes.getValue("major"));
        int n3 = Integer.parseInt(attributes.getValue("minor"));
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("version", new Integer(n3 << 16 | n2));
        hashMap.put("access", attributes.getValue("access"));
        hashMap.put("name", attributes.getValue("name"));
        hashMap.put("parent", attributes.getValue("parent"));
        hashMap.put("source", attributes.getValue("source"));
        hashMap.put("signature", attributes.getValue("signature"));
        hashMap.put("interfaces", new ArrayList());
        this.this$0.push(hashMap);
    }
}

