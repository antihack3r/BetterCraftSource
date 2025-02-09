/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import java.io.OutputStream;

interface Processor$EntryElement {
    public OutputStream openEntry(String var1) throws IOException;

    public void closeEntry() throws IOException;
}

