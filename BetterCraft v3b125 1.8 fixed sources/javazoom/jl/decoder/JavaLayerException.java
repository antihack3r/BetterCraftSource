/*
 * Decompiled with CFR 0.152.
 */
package javazoom.jl.decoder;

import java.io.PrintStream;

public class JavaLayerException
extends Exception {
    private Throwable exception;

    public JavaLayerException() {
    }

    public JavaLayerException(String msg) {
        super(msg);
    }

    public JavaLayerException(String msg, Throwable t2) {
        super(msg);
        this.exception = t2;
    }

    public Throwable getException() {
        return this.exception;
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream ps2) {
        if (this.exception == null) {
            super.printStackTrace(ps2);
        } else {
            this.exception.printStackTrace();
        }
    }
}

