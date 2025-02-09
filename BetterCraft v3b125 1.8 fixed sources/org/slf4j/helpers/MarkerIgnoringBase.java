/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.NamedLoggerBase;

public abstract class MarkerIgnoringBase
extends NamedLoggerBase
implements Logger {
    private static final long serialVersionUID = 9044267456635152283L;

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return this.isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        this.trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg2) {
        this.trace(format, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        this.trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object ... arguments) {
        this.trace(format, arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t2) {
        this.trace(msg, t2);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return this.isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        this.debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg2) {
        this.debug(format, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        this.debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object ... arguments) {
        this.debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t2) {
        this.debug(msg, t2);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return this.isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        this.info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg2) {
        this.info(format, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        this.info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object ... arguments) {
        this.info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t2) {
        this.info(msg, t2);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return this.isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        this.warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg2) {
        this.warn(format, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        this.warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object ... arguments) {
        this.warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t2) {
        this.warn(msg, t2);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return this.isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        this.error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg2) {
        this.error(format, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        this.error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object ... arguments) {
        this.error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t2) {
        this.error(msg, t2);
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.getName() + ")";
    }
}

