// 
// Decompiled by Procyon v0.6.0
// 

package org.objectweb.asm.xml;

import java.util.HashMap;
import java.util.ArrayList;
import org.xml.sax.Attributes;

final class ASMContentHandler$InterfaceRule extends ASMContentHandler$Rule
{
    final /* synthetic */ ASMContentHandler this$0;
    
    ASMContentHandler$InterfaceRule(final ASMContentHandler this$0) {
        this.this$0 = this$0;
        super(this$0);
    }
    
    public final void begin(final String s, final Attributes attributes) {
        ((HashMap)this.this$0.peek()).get("interfaces").add(attributes.getValue("name"));
    }
}
