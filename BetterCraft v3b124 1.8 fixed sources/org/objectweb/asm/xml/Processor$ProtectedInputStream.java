/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import java.io.IOException;
import java.io.InputStream;

final class Processor$ProtectedInputStream
extends InputStream {
    private final InputStream is;

    Processor$ProtectedInputStream(InputStream inputStream) {
        this.is = inputStream;
    }

    public final void close() throws IOException {
    }

    public final int read() throws IOException {
        return this.is.read();
    }

    public final int read(byte[] byArray, int n2, int n3) throws IOException {
        return this.is.read(byArray, n2, n3);
    }

    public final int available() throws IOException {
        return this.is.available();
    }
}

