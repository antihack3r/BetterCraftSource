/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

@NotThreadSafe
public class IdentityInputStream
extends InputStream {
    private final SessionInputBuffer in;
    private boolean closed = false;

    public IdentityInputStream(SessionInputBuffer in2) {
        this.in = Args.notNull(in2, "Session input buffer");
    }

    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            return ((BufferInfo)((Object)this.in)).length();
        }
        return 0;
    }

    public void close() throws IOException {
        this.closed = true;
    }

    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read();
    }

    public int read(byte[] b2, int off, int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read(b2, off, len);
    }
}

