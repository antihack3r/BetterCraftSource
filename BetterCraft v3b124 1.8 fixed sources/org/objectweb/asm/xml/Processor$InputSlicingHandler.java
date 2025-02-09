/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class Processor$InputSlicingHandler
extends DefaultHandler {
    private String subdocumentRoot;
    private final ContentHandler rootHandler;
    private Processor$ContentHandlerFactory subdocumentHandlerFactory;
    private boolean subdocument = false;
    private ContentHandler subdocumentHandler;

    Processor$InputSlicingHandler(String string, ContentHandler contentHandler, Processor$ContentHandlerFactory processor$ContentHandlerFactory) {
        this.subdocumentRoot = string;
        this.rootHandler = contentHandler;
        this.subdocumentHandlerFactory = processor$ContentHandlerFactory;
    }

    public final void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.startElement(string, string2, string3, attributes);
        } else if (string2.equals(this.subdocumentRoot)) {
            this.subdocumentHandler = this.subdocumentHandlerFactory.createContentHandler();
            this.subdocumentHandler.startDocument();
            this.subdocumentHandler.startElement(string, string2, string3, attributes);
            this.subdocument = true;
        } else if (this.rootHandler != null) {
            this.rootHandler.startElement(string, string2, string3, attributes);
        }
    }

    public final void endElement(String string, String string2, String string3) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.endElement(string, string2, string3);
            if (string2.equals(this.subdocumentRoot)) {
                this.subdocumentHandler.endDocument();
                this.subdocument = false;
            }
        } else if (this.rootHandler != null) {
            this.rootHandler.endElement(string, string2, string3);
        }
    }

    public final void startDocument() throws SAXException {
        if (this.rootHandler != null) {
            this.rootHandler.startDocument();
        }
    }

    public final void endDocument() throws SAXException {
        if (this.rootHandler != null) {
            this.rootHandler.endDocument();
        }
    }

    public final void characters(char[] cArray, int n2, int n3) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.characters(cArray, n2, n3);
        } else if (this.rootHandler != null) {
            this.rootHandler.characters(cArray, n2, n3);
        }
    }
}

