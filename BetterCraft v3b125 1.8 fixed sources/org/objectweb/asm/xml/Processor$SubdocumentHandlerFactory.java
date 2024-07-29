/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.xml.sax.ContentHandler;

final class Processor$SubdocumentHandlerFactory
implements Processor$ContentHandlerFactory {
    private final ContentHandler subdocumentHandler;

    Processor$SubdocumentHandlerFactory(ContentHandler contentHandler) {
        this.subdocumentHandler = contentHandler;
    }

    public final ContentHandler createContentHandler() {
        return this.subdocumentHandler;
    }
}

