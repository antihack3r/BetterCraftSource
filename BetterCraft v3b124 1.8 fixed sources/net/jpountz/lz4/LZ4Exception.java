/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.lz4;

public class LZ4Exception
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LZ4Exception(String msg, Throwable t2) {
        super(msg, t2);
    }

    public LZ4Exception(String msg) {
        super(msg);
    }

    public LZ4Exception() {
    }
}

