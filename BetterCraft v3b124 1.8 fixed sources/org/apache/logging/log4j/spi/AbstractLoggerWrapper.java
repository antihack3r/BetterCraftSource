/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;

public class AbstractLoggerWrapper
extends AbstractLogger {
    protected final AbstractLogger logger;

    public AbstractLoggerWrapper(AbstractLogger logger, String name, MessageFactory messageFactory) {
        super(name, messageFactory);
        this.logger = logger;
    }

    @Override
    public void log(Marker marker, String fqcn, Level level, Message data, Throwable t2) {
        this.logger.log(marker, fqcn, level, data, t2);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String data) {
        return this.logger.isEnabled(level, marker, data);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String data, Throwable t2) {
        return this.logger.isEnabled(level, marker, data, t2);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String data, Object ... p1) {
        return this.logger.isEnabled(level, marker, data, p1);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object data, Throwable t2) {
        return this.logger.isEnabled(level, marker, data, t2);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message data, Throwable t2) {
        return this.logger.isEnabled(level, marker, data, t2);
    }
}

