/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SAXAdapter {
    private final ContentHandler h;

    protected SAXAdapter(ContentHandler contentHandler) {
        this.h = contentHandler;
    }

    protected ContentHandler getContentHandler() {
        return this.h;
    }

    protected void addDocumentStart() {
        try {
            this.h.startDocument();
        }
        catch (SAXException sAXException) {
            throw new RuntimeException(sAXException.getMessage(), sAXException.getException());
        }
    }

    protected void addDocumentEnd() {
        try {
            this.h.endDocument();
        }
        catch (SAXException sAXException) {
            throw new RuntimeException(sAXException.getMessage(), sAXException.getException());
        }
    }

    protected final void addStart(String string, Attributes attributes) {
        try {
            this.h.startElement("", string, string, attributes);
        }
        catch (SAXException sAXException) {
            throw new RuntimeException(sAXException.getMessage(), sAXException.getException());
        }
    }

    protected final void addEnd(String string) {
        try {
            this.h.endElement("", string, string);
        }
        catch (SAXException sAXException) {
            throw new RuntimeException(sAXException.getMessage(), sAXException.getException());
        }
    }

    protected final void addElement(String string, Attributes attributes) {
        this.addStart(string, attributes);
        this.addEnd(string);
    }
}

