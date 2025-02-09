/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.xml.Processor$EntryElement;

final class Processor$ZipEntryElement
implements Processor$EntryElement {
    private ZipOutputStream zos;

    Processor$ZipEntryElement(ZipOutputStream zipOutputStream) {
        this.zos = zipOutputStream;
    }

    public OutputStream openEntry(String string) throws IOException {
        ZipEntry zipEntry = new ZipEntry(string);
        this.zos.putNextEntry(zipEntry);
        return this.zos;
    }

    public void closeEntry() throws IOException {
        this.zos.flush();
        this.zos.closeEntry();
    }
}

