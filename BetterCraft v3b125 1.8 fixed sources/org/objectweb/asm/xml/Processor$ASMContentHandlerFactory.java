/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.OutputStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.xml.Processor$ASMContentHandlerFactory$1;
import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.xml.sax.ContentHandler;

final class Processor$ASMContentHandlerFactory
implements Processor.ContentHandlerFactory {
    final OutputStream os;

    Processor$ASMContentHandlerFactory(OutputStream outputStream) {
        this.os = outputStream;
    }

    public final ContentHandler createContentHandler() {
        ClassWriter classWriter = new ClassWriter(1);
        return new Processor$ASMContentHandlerFactory$1(this, classWriter, classWriter);
    }
}

