/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.objectweb.asm.xml.Processor$EntryElement;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class Processor$OutputSlicingHandler
extends DefaultHandler {
    private final String subdocumentRoot;
    private Processor$ContentHandlerFactory subdocumentHandlerFactory;
    private final Processor$EntryElement entryElement;
    private boolean isXml;
    private boolean subdocument = false;
    private ContentHandler subdocumentHandler;

    Processor$OutputSlicingHandler(Processor$ContentHandlerFactory processor$ContentHandlerFactory, Processor$EntryElement processor$EntryElement, boolean bl2) {
        this.subdocumentRoot = "class";
        this.subdocumentHandlerFactory = processor$ContentHandlerFactory;
        this.entryElement = processor$EntryElement;
        this.isXml = bl2;
    }

    public final void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.startElement(string, string2, string3, attributes);
        } else if (string2.equals(this.subdocumentRoot)) {
            String string4 = attributes.getValue("name");
            if (string4 == null || string4.length() == 0) {
                throw new SAXException("Class element without name attribute.");
            }
            try {
                this.entryElement.openEntry(this.isXml ? string4 + ".class.xml" : string4 + ".class");
            }
            catch (IOException iOException) {
                throw new SAXException(iOException.toString(), iOException);
            }
            this.subdocumentHandler = this.subdocumentHandlerFactory.createContentHandler();
            this.subdocumentHandler.startDocument();
            this.subdocumentHandler.startElement(string, string2, string3, attributes);
            this.subdocument = true;
        }
    }

    public final void endElement(String string, String string2, String string3) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.endElement(string, string2, string3);
            if (string2.equals(this.subdocumentRoot)) {
                this.subdocumentHandler.endDocument();
                this.subdocument = false;
                try {
                    this.entryElement.closeEntry();
                }
                catch (IOException iOException) {
                    throw new SAXException(iOException.toString(), iOException);
                }
            }
        }
    }

    public final void startDocument() throws SAXException {
    }

    public final void endDocument() throws SAXException {
    }

    public final void characters(char[] cArray, int n2, int n3) throws SAXException {
        if (this.subdocument) {
            this.subdocumentHandler.characters(cArray, n2, n3);
        }
    }
}

