/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.logging;

import com.google.common.base.Strings;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class LoggerAdapterConsole
extends LoggerAdapterAbstract {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private PrintStream debug;

    public LoggerAdapterConsole(String name) {
        super(Strings.nullToEmpty(name));
    }

    @Override
    public String getType() {
        return "Default Console Logger";
    }

    public LoggerAdapterConsole setDebugStream(PrintStream debug) {
        this.debug = debug;
        return this;
    }

    @Override
    public void catching(Level level, Throwable t2) {
        this.log(Level.WARN, "Catching {}: {}", t2.getClass().getName(), t2.getMessage(), t2);
    }

    @Override
    public void log(Level level, String message, Object ... params) {
        PrintStream out = this.getOutputStream(level);
        if (out != null) {
            LoggerAdapterAbstract.FormattedMessage formatted = new LoggerAdapterAbstract.FormattedMessage(message, params);
            out.println(String.format("[%s] [%s/%s] %s", new Object[]{DATE_FORMAT.format(new Date()), this.getId(), level, formatted}));
            if (formatted.hasThrowable()) {
                formatted.getThrowable().printStackTrace(out);
            }
        }
    }

    @Override
    public void log(Level level, String message, Throwable t2) {
        PrintStream out = this.getOutputStream(level);
        if (out != null) {
            out.println(String.format("[%s] [%s/%s] %s", new Object[]{DATE_FORMAT.format(new Date()), this.getId(), level, message}));
            t2.printStackTrace(out);
        }
    }

    @Override
    public <T extends Throwable> T throwing(T t2) {
        this.log(Level.WARN, "Throwing {}: {}", t2.getClass().getName(), t2.getMessage(), t2);
        return t2;
    }

    private PrintStream getOutputStream(Level level) {
        if (level == Level.FATAL || level == Level.ERROR || level == Level.WARN) {
            return System.err;
        }
        if (level == Level.INFO) {
            return System.out;
        }
        if (level == Level.DEBUG) {
            return this.debug;
        }
        return null;
    }
}

