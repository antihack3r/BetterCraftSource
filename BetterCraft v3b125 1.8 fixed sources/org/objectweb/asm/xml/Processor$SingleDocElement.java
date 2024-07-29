/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import java.io.OutputStream;
import org.objectweb.asm.xml.Processor$EntryElement;

final class Processor$SingleDocElement
implements Processor$EntryElement {
    private final OutputStream os;

    Processor$SingleDocElement(OutputStream outputStream) {
        this.os = outputStream;
    }

    public OutputStream openEntry(String string) throws IOException {
        return this.os;
    }

    public void closeEntry() throws IOException {
        this.os.flush();
    }
}

