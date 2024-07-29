/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.Label;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;

final class ASMContentHandler$TableSwitchRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$TableSwitchRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("min", attributes.getValue("min"));
        hashMap.put("max", attributes.getValue("max"));
        hashMap.put("dflt", attributes.getValue("dflt"));
        hashMap.put("labels", new ArrayList());
        this.this$0.push(hashMap);
    }

    public final void end(String string) {
        HashMap hashMap = (HashMap)this.this$0.pop();
        int n2 = Integer.parseInt((String)hashMap.get("min"));
        int n3 = Integer.parseInt((String)hashMap.get("max"));
        Label label = this.getLabel(hashMap.get("dflt"));
        ArrayList arrayList = (ArrayList)hashMap.get("labels");
        Label[] labelArray = arrayList.toArray(new Label[arrayList.size()]);
        this.getCodeVisitor().visitTableSwitchInsn(n2, n3, label, labelArray);
    }
}

