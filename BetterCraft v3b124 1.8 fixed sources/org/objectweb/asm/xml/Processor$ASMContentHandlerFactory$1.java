/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.xml.ASMContentHandler;
import org.objectweb.asm.xml.Processor$ASMContentHandlerFactory;
import org.xml.sax.SAXException;

class Processor$ASMContentHandlerFactory$1
extends ASMContentHandler {
    final /* synthetic */ ClassWriter val$cw;
    final /* synthetic */ Processor$ASMContentHandlerFactory this$0;

    Processor$ASMContentHandlerFactory$1(Processor$ASMContentHandlerFactory aSMContentHandlerFactory, ClassVisitor classVisitor, ClassWriter classWriter) {
        this.this$0 = aSMContentHandlerFactory;
        this.val$cw = classWriter;
        super(classVisitor);
    }

    public void endDocument() throws SAXException {
        try {
            this.this$0.os.write(this.val$cw.toByteArray());
        }
        catch (IOException iOException) {
            throw new SAXException(iOException);
        }
    }
}

