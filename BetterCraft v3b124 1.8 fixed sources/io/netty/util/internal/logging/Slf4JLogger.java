/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import org.slf4j.Logger;

class Slf4JLogger
extends AbstractInternalLogger {
    private static final long serialVersionUID = 108038972685130825L;
    private final transient Logger logger;

    Slf4JLogger(Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        this.logger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg2) {
        this.logger.trace(format, arg2);
    }

    @Override
    public void trace(String format, Object argA, Object argB) {
        this.logger.trace(format, argA, argB);
    }

    @Override
    public void trace(String format, Object ... argArray) {
        this.logger.trace(format, argArray);
    }

    @Override
    public void trace(String msg, Throwable t2) {
        this.logger.trace(msg, t2);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        this.logger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg2) {
        this.logger.debug(format, arg2);
    }

    @Override
    public void debug(String format, Object argA, Object argB) {
        this.logger.debug(format, argA, argB);
    }

    @Override
    public void debug(String format, Object ... argArray) {
        this.logger.debug(format, argArray);
    }

    @Override
    public void debug(String msg, Throwable t2) {
        this.logger.debug(msg, t2);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        this.logger.info(msg);
    }

    @Override
    public void info(String format, Object arg2) {
        this.logger.info(format, arg2);
    }

    @Override
    public void info(String format, Object argA, Object argB) {
        this.logger.info(format, argA, argB);
    }

    @Override
    public void info(String format, Object ... argArray) {
        this.logger.info(format, argArray);
    }

    @Override
    public void info(String msg, Throwable t2) {
        this.logger.info(msg, t2);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        this.logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg2) {
        this.logger.warn(format, arg2);
    }

    @Override
    public void warn(String format, Object ... argArray) {
        this.logger.warn(format, argArray);
    }

    @Override
    public void warn(String format, Object argA, Object argB) {
        this.logger.warn(format, argA, argB);
    }

    @Override
    public void warn(String msg, Throwable t2) {
        this.logger.warn(msg, t2);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        this.logger.error(msg);
    }

    @Override
    public void error(String format, Object arg2) {
        this.logger.error(format, arg2);
    }

    @Override
    public void error(String format, Object argA, Object argB) {
        this.logger.error(format, argA, argB);
    }

    @Override
    public void error(String format, Object ... argArray) {
        this.logger.error(format, argArray);
    }

    @Override
    public void error(String msg, Throwable t2) {
        this.logger.error(msg, t2);
    }
}

