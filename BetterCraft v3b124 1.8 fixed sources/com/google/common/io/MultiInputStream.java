/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nullable;

final class MultiInputStream
extends InputStream {
    private Iterator<? extends ByteSource> it;
    private InputStream in;

    public MultiInputStream(Iterator<? extends ByteSource> it2) throws IOException {
        this.it = Preconditions.checkNotNull(it2);
        this.advance();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }

    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.in = this.it.next().openStream();
        }
    }

    @Override
    public int available() throws IOException {
        if (this.in == null) {
            return 0;
        }
        return this.in.available();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read();
        if (result == -1) {
            this.advance();
            return this.read();
        }
        return result;
    }

    @Override
    public int read(@Nullable byte[] b2, int off, int len) throws IOException {
        if (this.in == null) {
            return -1;
        }
        int result = this.in.read(b2, off, len);
        if (result == -1) {
            this.advance();
            return this.read(b2, off, len);
        }
        return result;
    }

    @Override
    public long skip(long n2) throws IOException {
        if (this.in == null || n2 <= 0L) {
            return 0L;
        }
        long result = this.in.skip(n2);
        if (result != 0L) {
            return result;
        }
        if (this.read() == -1) {
            return 0L;
        }
        return 1L + this.in.skip(n2 - 1L);
    }
}

