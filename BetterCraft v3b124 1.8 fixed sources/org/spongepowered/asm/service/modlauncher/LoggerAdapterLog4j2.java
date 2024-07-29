/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.service.modlauncher;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class LoggerAdapterLog4j2
extends LoggerAdapterAbstract {
    private static final Level[] LEVELS = new Level[]{Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE};
    private final Logger logger;

    public LoggerAdapterLog4j2(String name) {
        super(name);
        this.logger = LogManager.getLogger(name);
    }

    @Override
    public String getType() {
        return "Log4j2 (via ModLauncher)";
    }

    @Override
    public void catching(org.spongepowered.asm.logging.Level level, Throwable t2) {
        this.logger.catching(LEVELS[level.ordinal()], t2);
    }

    @Override
    public void catching(Throwable t2) {
        this.logger.catching(t2);
    }

    @Override
    public void debug(String message, Object ... params) {
        this.logger.debug(message, params);
    }

    @Override
    public void debug(String message, Throwable t2) {
        this.logger.debug(message, t2);
    }

    @Override
    public void error(String message, Object ... params) {
        this.logger.error(message, params);
    }

    @Override
    public void error(String message, Throwable t2) {
        this.logger.error(message, t2);
    }

    @Override
    public void fatal(String message, Object ... params) {
        this.logger.fatal(message, params);
    }

    @Override
    public void fatal(String message, Throwable t2) {
        this.logger.fatal(message, t2);
    }

    @Override
    public void info(String message, Object ... params) {
        this.logger.info(message, params);
    }

    @Override
    public void info(String message, Throwable t2) {
        this.logger.info(message, t2);
    }

    @Override
    public void log(org.spongepowered.asm.logging.Level level, String message, Object ... params) {
        this.logger.log(LEVELS[level.ordinal()], message, params);
    }

    @Override
    public void log(org.spongepowered.asm.logging.Level level, String message, Throwable t2) {
        this.logger.log(LEVELS[level.ordinal()], message, t2);
    }

    @Override
    public <T extends Throwable> T throwing(T t2) {
        return this.logger.throwing(t2);
    }

    @Override
    public void trace(String message, Object ... params) {
        this.logger.trace(message, params);
    }

    @Override
    public void trace(String message, Throwable t2) {
        this.logger.trace(message, t2);
    }

    @Override
    public void warn(String message, Object ... params) {
        this.logger.warn(message, params);
    }

    @Override
    public void warn(String message, Throwable t2) {
        this.logger.warn(message, t2);
    }
}

