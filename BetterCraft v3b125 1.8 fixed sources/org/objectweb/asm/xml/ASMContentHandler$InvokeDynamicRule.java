/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.util.ArrayList;
import org.objectweb.asm.Handle;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.ASMContentHandler$Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class ASMContentHandler$InvokeDynamicRule
extends ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;

    ASMContentHandler$InvokeDynamicRule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
        super(aSMContentHandler);
    }

    public final void begin(String string, Attributes attributes) throws SAXException {
        this.this$0.push(attributes.getValue("name"));
        this.this$0.push(attributes.getValue("desc"));
        this.this$0.push(this.decodeHandle(attributes.getValue("bsm")));
        this.this$0.push(new ArrayList());
    }

    public final void end(String string) {
        ArrayList arrayList = (ArrayList)this.this$0.pop();
        Handle handle = (Handle)this.this$0.pop();
        String string2 = (String)this.this$0.pop();
        String string3 = (String)this.this$0.pop();
        this.getCodeVisitor().visitInvokeDynamicInsn(string3, string2, handle, arrayList.toArray());
    }
}

