/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.event;

public enum Level {
    ERROR(40, "ERROR"),
    WARN(30, "WARN"),
    INFO(20, "INFO"),
    DEBUG(10, "DEBUG"),
    TRACE(0, "TRACE");

    private final int levelInt;
    private final String levelStr;

    private Level(int i2, String s2) {
        this.levelInt = i2;
        this.levelStr = s2;
    }

    public int toInt() {
        return this.levelInt;
    }

    public static Level intToLevel(int levelInt) {
        switch (levelInt) {
            case 0: {
                return TRACE;
            }
            case 10: {
                return DEBUG;
            }
            case 20: {
                return INFO;
            }
            case 30: {
                return WARN;
            }
            case 40: {
                return ERROR;
            }
        }
        throw new IllegalArgumentException("Level integer [" + levelInt + "] not recognized.");
    }

    public String toString() {
        return this.levelStr;
    }
}

