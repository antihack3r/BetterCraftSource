/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.support.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CapturePrintStream
extends PrintStream {
    private static final Logger LOGGER = LogManager.getLogger();

    public CapturePrintStream(OutputStream outStream) {
        super(outStream);
    }

    @Override
    public void println(String string) {
        this.logString(string);
    }

    @Override
    public void println(Object obj) {
        if (obj == null) {
            this.logString("null");
            return;
        }
        if (obj instanceof Throwable) {
            LOGGER.catching((Throwable)obj);
        } else if (!obj.toString().startsWith("\t")) {
            this.logString(String.valueOf(obj));
        }
    }

    private void logString(String string) {
        LOGGER.info("{}", string);
    }
}

