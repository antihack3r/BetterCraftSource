/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.spi.CallerBoundaryAware;
import org.slf4j.spi.LoggingEventAware;
import org.slf4j.spi.LoggingEventBuilder;

public class DefaultLoggingEventBuilder
implements LoggingEventBuilder,
CallerBoundaryAware {
    static String DLEB_FQCN = DefaultLoggingEventBuilder.class.getName();
    protected DefaultLoggingEvent loggingEvent;
    protected Logger logger;

    public DefaultLoggingEventBuilder(Logger logger, Level level) {
        this.logger = logger;
        this.loggingEvent = new DefaultLoggingEvent(level, logger);
    }

    @Override
    public LoggingEventBuilder addMarker(Marker marker) {
        this.loggingEvent.addMarker(marker);
        return this;
    }

    @Override
    public LoggingEventBuilder setCause(Throwable t2) {
        this.loggingEvent.setThrowable(t2);
        return this;
    }

    @Override
    public LoggingEventBuilder addArgument(Object p2) {
        this.loggingEvent.addArgument(p2);
        return this;
    }

    @Override
    public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        this.loggingEvent.addArgument(objectSupplier.get());
        return this;
    }

    @Override
    public void setCallerBoundary(String fqcn) {
        this.loggingEvent.setCallerBoundary(fqcn);
    }

    @Override
    public void log() {
        this.log(this.loggingEvent);
    }

    @Override
    public LoggingEventBuilder setMessage(String message) {
        this.loggingEvent.setMessage(message);
        return this;
    }

    @Override
    public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        this.loggingEvent.setMessage(messageSupplier.get());
        return this;
    }

    @Override
    public void log(String message) {
        this.loggingEvent.setMessage(message);
        this.log(this.loggingEvent);
    }

    @Override
    public void log(String message, Object arg2) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArgument(arg2);
        this.log(this.loggingEvent);
    }

    @Override
    public void log(String message, Object arg0, Object arg1) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArgument(arg0);
        this.loggingEvent.addArgument(arg1);
        this.log(this.loggingEvent);
    }

    @Override
    public void log(String message, Object ... args) {
        this.loggingEvent.setMessage(message);
        this.loggingEvent.addArguments(args);
        this.log(this.loggingEvent);
    }

    @Override
    public void log(Supplier<String> messageSupplier) {
        if (messageSupplier == null) {
            this.log((String)null);
        } else {
            this.log(messageSupplier.get());
        }
    }

    protected void log(LoggingEvent aLoggingEvent) {
        this.setCallerBoundary(DLEB_FQCN);
        if (this.logger instanceof LoggingEventAware) {
            ((LoggingEventAware)((Object)this.logger)).log(aLoggingEvent);
        } else {
            this.logViaPublicSLF4JLoggerAPI(aLoggingEvent);
        }
    }

    private void logViaPublicSLF4JLoggerAPI(LoggingEvent aLoggingEvent) {
        Object[] argArray = aLoggingEvent.getArgumentArray();
        int argLen = argArray == null ? 0 : argArray.length;
        Throwable t2 = aLoggingEvent.getThrowable();
        int tLen = t2 == null ? 0 : 1;
        String msg = aLoggingEvent.getMessage();
        Object[] combinedArguments = new Object[argLen + tLen];
        if (argArray != null) {
            System.arraycopy(argArray, 0, combinedArguments, 0, argLen);
        }
        if (t2 != null) {
            combinedArguments[argLen] = t2;
        }
        msg = this.mergeMarkersAndKeyValuePairs(aLoggingEvent, msg);
        switch (aLoggingEvent.getLevel()) {
            case TRACE: {
                this.logger.trace(msg, combinedArguments);
                break;
            }
            case DEBUG: {
                this.logger.debug(msg, combinedArguments);
                break;
            }
            case INFO: {
                this.logger.info(msg, combinedArguments);
                break;
            }
            case WARN: {
                this.logger.warn(msg, combinedArguments);
                break;
            }
            case ERROR: {
                this.logger.error(msg, combinedArguments);
            }
        }
    }

    private String mergeMarkersAndKeyValuePairs(LoggingEvent aLoggingEvent, String msg) {
        StringBuilder sb2 = null;
        if (aLoggingEvent.getMarkers() != null) {
            sb2 = new StringBuilder();
            for (Marker marker : aLoggingEvent.getMarkers()) {
                sb2.append(marker);
                sb2.append(' ');
            }
        }
        if (aLoggingEvent.getKeyValuePairs() != null) {
            if (sb2 == null) {
                sb2 = new StringBuilder();
            }
            for (KeyValuePair kvp : aLoggingEvent.getKeyValuePairs()) {
                sb2.append(kvp.key);
                sb2.append('=');
                sb2.append(kvp.value);
                sb2.append(' ');
            }
        }
        if (sb2 != null) {
            sb2.append(msg);
            return sb2.toString();
        }
        return msg;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Object value) {
        this.loggingEvent.addKeyValue(key, value);
        return this;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Supplier<Object> value) {
        this.loggingEvent.addKeyValue(key, value.get());
        return this;
    }
}

