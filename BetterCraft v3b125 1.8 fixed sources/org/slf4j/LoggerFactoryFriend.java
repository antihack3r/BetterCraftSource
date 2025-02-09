/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j;

import org.slf4j.LoggerFactory;

public class LoggerFactoryFriend {
    public static void reset() {
        LoggerFactory.reset();
    }

    public static void setDetectLoggerNameMismatch(boolean enabled) {
        LoggerFactory.DETECT_LOGGER_NAME_MISMATCH = enabled;
    }
}

