/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.Source;

public class InputStreamSource
implements Source {
    private final InputStream in;

    public InputStreamSource(InputStream in2) {
        if (in2 == null) {
            throw new NullPointerException("in");
        }
        this.in = in2;
    }

    @Override
    public int read(byte[] b2, int offs, int len) throws IOException {
        int read = this.in.read(b2, offs, len);
        return read;
    }

    @Override
    public boolean willReadBlock() {
        return true;
    }

    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public long tell() {
        return -1L;
    }

    @Override
    public long seek(long to2) {
        return -1L;
    }

    @Override
    public long length() {
        return -1L;
    }
}

