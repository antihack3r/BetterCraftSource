/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import java.util.HashMap;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;

final class ASMContentHandler$InterfacesRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$InterfacesRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void end(String string) {
        HashMap hashMap = (HashMap)this.this$0.pop();
        int n2 = (Integer)hashMap.get("version");
        int n3 = this.getAccess((String)hashMap.get("access"));
        String string2 = (String)hashMap.get("name");
        String string3 = (String)hashMap.get("signature");
        String string4 = (String)hashMap.get("parent");
        ArrayList arrayList = (ArrayList)hashMap.get("interfaces");
        String[] stringArray = arrayList.toArray(new String[arrayList.size()]);
        this.this$0.cv.visit(n2, n3, string2, string3, string4, stringArray);
        this.this$0.push(this.this$0.cv);
    }
}

