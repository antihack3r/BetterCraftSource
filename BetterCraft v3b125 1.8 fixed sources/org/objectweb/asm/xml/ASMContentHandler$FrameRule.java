/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$FrameRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$FrameRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public void begin(String string, Attributes attributes) {
        HashMap hashMap = new HashMap();
        hashMap.put("local", new ArrayList());
        hashMap.put("stack", new ArrayList());
        this.this$0.push(attributes.getValue("type"));
        this.this$0.push(attributes.getValue("count") == null ? "0" : attributes.getValue("count"));
        this.this$0.push(hashMap);
    }

    public void end(String string) {
        HashMap hashMap = (HashMap)this.this$0.pop();
        ArrayList arrayList = (ArrayList)hashMap.get("local");
        int n2 = arrayList.size();
        Object[] objectArray = arrayList.toArray();
        ArrayList arrayList2 = (ArrayList)hashMap.get("stack");
        int n3 = arrayList2.size();
        Object[] objectArray2 = arrayList2.toArray();
        String string2 = (String)this.this$0.pop();
        String string3 = (String)this.this$0.pop();
        if ("NEW".equals(string3)) {
            this.getCodeVisitor().visitFrame(-1, n2, objectArray, n3, objectArray2);
        } else if ("FULL".equals(string3)) {
            this.getCodeVisitor().visitFrame(0, n2, objectArray, n3, objectArray2);
        } else if ("APPEND".equals(string3)) {
            this.getCodeVisitor().visitFrame(1, n2, objectArray, 0, null);
        } else if ("CHOP".equals(string3)) {
            this.getCodeVisitor().visitFrame(2, Integer.parseInt(string2), null, 0, null);
        } else if ("SAME".equals(string3)) {
            this.getCodeVisitor().visitFrame(3, 0, null, 0, null);
        } else if ("SAME1".equals(string3)) {
            this.getCodeVisitor().visitFrame(4, 0, null, n3, objectArray2);
        }
    }
}

