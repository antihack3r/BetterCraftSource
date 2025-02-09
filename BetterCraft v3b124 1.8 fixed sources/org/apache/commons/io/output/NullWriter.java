/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.output;

import java.io.Writer;

public class NullWriter
extends Writer {
    public static final NullWriter NULL_WRITER = new NullWriter();

    @Override
    public Writer append(char c2) {
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) {
        return this;
    }

    @Override
    public Writer append(CharSequence csq) {
        return this;
    }

    @Override
    public void write(int idx) {
    }

    @Override
    public void write(char[] chr) {
    }

    @Override
    public void write(char[] chr, int st2, int end) {
    }

    @Override
    public void write(String str) {
    }

    @Override
    public void write(String str, int st2, int end) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}

