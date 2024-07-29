/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.launchwrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogWrapper {
    public static LogWrapper log = new LogWrapper();
    private Logger myLog;
    private static boolean configured;

    private static void configureLogging() {
        LogWrapper.log.myLog = LogManager.getLogger("LaunchWrapper");
        configured = true;
    }

    public static void retarget(Logger to2) {
        LogWrapper.log.myLog = to2;
    }

    public static void log(String logChannel, Level level, String format, Object ... data) {
        LogWrapper.makeLog(logChannel);
        LogManager.getLogger(logChannel).log(level, String.format(format, data));
    }

    public static void log(Level level, String format, Object ... data) {
        if (!configured) {
            LogWrapper.configureLogging();
        }
        LogWrapper.log.myLog.log(level, String.format(format, data));
    }

    public static void log(String logChannel, Level level, Throwable ex2, String format, Object ... data) {
        LogWrapper.makeLog(logChannel);
        LogManager.getLogger(logChannel).log(level, String.format(format, data), ex2);
    }

    public static void log(Level level, Throwable ex2, String format, Object ... data) {
        if (!configured) {
            LogWrapper.configureLogging();
        }
        LogWrapper.log.myLog.log(level, String.format(format, data), ex2);
    }

    public static void severe(String format, Object ... data) {
        LogWrapper.log(Level.ERROR, format, data);
    }

    public static void warning(String format, Object ... data) {
        LogWrapper.log(Level.WARN, format, data);
    }

    public static void info(String format, Object ... data) {
        LogWrapper.log(Level.INFO, format, data);
    }

    public static void fine(String format, Object ... data) {
        LogWrapper.log(Level.DEBUG, format, data);
    }

    public static void finer(String format, Object ... data) {
        LogWrapper.log(Level.TRACE, format, data);
    }

    public static void finest(String format, Object ... data) {
        LogWrapper.log(Level.TRACE, format, data);
    }

    public static void makeLog(String logChannel) {
        LogManager.getLogger(logChannel);
    }
}

