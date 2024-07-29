/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.Writer;
import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.objectweb.asm.xml.Processor$SAXWriter;
import org.xml.sax.ContentHandler;

final class Processor$SAXWriterFactory
implements Processor$ContentHandlerFactory {
    private final Writer w;
    private final boolean optimizeEmptyElements;

    Processor$SAXWriterFactory(Writer writer, boolean bl2) {
        this.w = writer;
        this.optimizeEmptyElements = bl2;
    }

    public final ContentHandler createContentHandler() {
        return new Processor$SAXWriter(this.w, this.optimizeEmptyElements);
    }
}

