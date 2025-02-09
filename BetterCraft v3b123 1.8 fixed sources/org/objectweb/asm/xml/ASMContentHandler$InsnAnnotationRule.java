// 
// Decompiled by Procyon v0.6.0
// 

package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.MethodVisitor;
import org.xml.sax.Attributes;

final class ASMContentHandler$InsnAnnotationRule extends ASMContentHandler$Rule
{
    final /* synthetic */ ASMContentHandler this$0;
    
    ASMContentHandler$InsnAnnotationRule(final ASMContentHandler this$0) {
        this.this$0 = this$0;
        super(this$0);
    }
    
    public void begin(final String s, final Attributes attributes) {
        this.this$0.push(((MethodVisitor)this.this$0.peek()).visitInsnAnnotation(Integer.parseInt(attributes.getValue("typeRef")), TypePath.fromString(attributes.getValue("typePath")), attributes.getValue("desc"), Boolean.valueOf(attributes.getValue("visible"))));
    }
    
    public void end(final String s) {
        final AnnotationVisitor annotationVisitor = (AnnotationVisitor)this.this$0.pop();
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
    }
}
