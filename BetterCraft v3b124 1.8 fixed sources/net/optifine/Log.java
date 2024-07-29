/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final boolean logDetail = System.getProperty("log.detail", "false").equals("true");

    public static void detail(String s2) {
        if (logDetail) {
            LOGGER.info("[OptiFine] " + s2);
        }
    }

    public static void dbg(String s2) {
        LOGGER.info("[OptiFine] " + s2);
    }

    public static void warn(String s2) {
        LOGGER.warn("[OptiFine] " + s2);
    }

    public static void warn(String s2, Throwable t2) {
        LOGGER.warn("[OptiFine] " + s2, t2);
    }

    public static void error(String s2) {
        LOGGER.error("[OptiFine] " + s2);
    }

    public static void error(String s2, Throwable t2) {
        LOGGER.error("[OptiFine] " + s2, t2);
    }

    public static void log(String s2) {
        Log.dbg(s2);
    }
}

