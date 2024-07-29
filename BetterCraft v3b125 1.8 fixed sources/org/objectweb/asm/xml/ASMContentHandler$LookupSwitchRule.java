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

final class ASMContentHandler$LookupSwitchRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$LookupSwitchRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("dflt", attributes.getValue("dflt"));
        hashMap.put("labels", new ArrayList());
        hashMap.put("keys", new ArrayList());
        this.this$0.push(hashMap);
    }

    public final void end(String string) {
        HashMap hashMap = (HashMap)this.this$0.pop();
        Label label = this.getLabel(hashMap.get("dflt"));
        ArrayList arrayList = (ArrayList)hashMap.get("keys");
        ArrayList arrayList2 = (ArrayList)hashMap.get("labels");
        Label[] labelArray = arrayList2.toArray(new Label[arrayList2.size()]);
        int[] nArray = new int[arrayList.size()];
        for (int i2 = 0; i2 < nArray.length; ++i2) {
            nArray[i2] = Integer.parseInt((String)arrayList.get(i2));
        }
        this.getCodeVisitor().visitLookupSwitchInsn(label, nArray, labelArray);
    }
}

