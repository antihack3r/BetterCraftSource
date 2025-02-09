/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLogger;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4JLoggerFactory
extends InternalLoggerFactory {
    public Slf4JLoggerFactory() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Slf4JLoggerFactory(boolean failIfNOP) {
        assert (failIfNOP);
        final StringBuffer buf = new StringBuffer();
        PrintStream err = System.err;
        try {
            System.setErr(new PrintStream(new OutputStream(){

                @Override
                public void write(int b2) {
                    buf.append((char)b2);
                }
            }, true, "US-ASCII"));
        }
        catch (UnsupportedEncodingException e2) {
            throw new Error(e2);
        }
        try {
            if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
                throw new NoClassDefFoundError(buf.toString());
            }
            err.print(buf);
            err.flush();
        }
        finally {
            System.setErr(err);
        }
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new Slf4JLogger(LoggerFactory.getLogger(name));
    }
}

