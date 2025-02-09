/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$MethodRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$MethodRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        this.this$0.labels = new HashMap();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("access", attributes.getValue("access"));
        hashMap.put("name", attributes.getValue("name"));
        hashMap.put("desc", attributes.getValue("desc"));
        hashMap.put("signature", attributes.getValue("signature"));
        hashMap.put("exceptions", new ArrayList());
        this.this$0.push(hashMap);
    }

    public final void end(String string) {
        ((MethodVisitor)this.this$0.pop()).visitEnd();
        this.this$0.labels = null;
    }
}

