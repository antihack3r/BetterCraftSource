/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.objectweb.asm.xml.Processor$ContentHandlerFactory;
import org.xml.sax.ContentHandler;

final class Processor$TransformerHandlerFactory
implements Processor$ContentHandlerFactory {
    private SAXTransformerFactory saxtf;
    private final Templates templates;
    private ContentHandler outputHandler;

    Processor$TransformerHandlerFactory(SAXTransformerFactory sAXTransformerFactory, Templates templates, ContentHandler contentHandler) {
        this.saxtf = sAXTransformerFactory;
        this.templates = templates;
        this.outputHandler = contentHandler;
    }

    public final ContentHandler createContentHandler() {
        try {
            TransformerHandler transformerHandler = this.saxtf.newTransformerHandler(this.templates);
            transformerHandler.setResult(new SAXResult(this.outputHandler));
            return transformerHandler;
        }
        catch (TransformerConfigurationException transformerConfigurationException) {
            throw new RuntimeException(transformerConfigurationException.toString());
        }
    }
}

