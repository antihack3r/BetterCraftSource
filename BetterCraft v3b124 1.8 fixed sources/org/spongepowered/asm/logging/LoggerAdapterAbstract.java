/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.logging;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;

public abstract class LoggerAdapterAbstract
implements ILogger {
    private final String id;

    protected LoggerAdapterAbstract(String id2) {
        this.id = id2;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void catching(Throwable t2) {
        this.catching(Level.WARN, t2);
    }

    @Override
    public void debug(String message, Object ... params) {
        this.log(Level.DEBUG, message, params);
    }

    @Override
    public void debug(String message, Throwable t2) {
        this.log(Level.DEBUG, message, t2);
    }

    @Override
    public void error(String message, Object ... params) {
        this.log(Level.ERROR, message, params);
    }

    @Override
    public void error(String message, Throwable t2) {
        this.log(Level.ERROR, message, t2);
    }

    @Override
    public void fatal(String message, Object ... params) {
        this.log(Level.FATAL, message, params);
    }

    @Override
    public void fatal(String message, Throwable t2) {
        this.log(Level.FATAL, message, t2);
    }

    @Override
    public void info(String message, Object ... params) {
        this.log(Level.INFO, message, params);
    }

    @Override
    public void info(String message, Throwable t2) {
        this.log(Level.INFO, message, t2);
    }

    @Override
    public void trace(String message, Object ... params) {
        this.log(Level.TRACE, message, params);
    }

    @Override
    public void trace(String message, Throwable t2) {
        this.log(Level.TRACE, message, t2);
    }

    @Override
    public void warn(String message, Object ... params) {
        this.log(Level.WARN, message, params);
    }

    @Override
    public void warn(String message, Throwable t2) {
        this.log(Level.WARN, message, t2);
    }

    public static class FormattedMessage {
        private String message;
        private Throwable t;

        public FormattedMessage(String message, Object ... params) {
            int delimPos;
            int param;
            if (params.length == 0) {
                this.message = message;
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            int pos = 0;
            for (param = 0; pos < message.length() && param < params.length && (delimPos = message.indexOf("{}", pos)) >= 0; ++param) {
                sb2.append(message.substring(pos, delimPos)).append(params[param]);
                pos = delimPos + 2;
            }
            if (pos < message.length()) {
                sb2.append(message.substring(pos));
            }
            if (param < params.length && params[params.length - 1] instanceof Throwable) {
                this.t = (Throwable)params[params.length - 1];
            }
            this.message = sb2.toString();
        }

        public String toString() {
            return this.message;
        }

        public String getMessage() {
            return this.message;
        }

        public boolean hasThrowable() {
            return this.t != null;
        }

        public Throwable getThrowable() {
            return this.t;
        }
    }
}

