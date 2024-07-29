/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

public class MemoryMonitor {
    private static long startTimeMs = System.currentTimeMillis();
    private static long startMemory = MemoryMonitor.getMemoryUsed();
    private static long lastTimeMs = startTimeMs;
    private static long lastMemory = startMemory;
    private static boolean gcEvent = false;
    private static int memBytesSec = 0;
    private static long MB = 0x100000L;

    public static void update() {
        long i2 = System.currentTimeMillis();
        long j2 = MemoryMonitor.getMemoryUsed();
        boolean bl2 = gcEvent = j2 < lastMemory;
        if (gcEvent) {
            long l2 = lastMemory - startMemory;
            long k2 = lastTimeMs - startTimeMs;
            double d0 = (double)k2 / 1000.0;
            int i1 = (int)((double)l2 / d0);
            if (i1 > 0) {
                memBytesSec = i1;
            }
            startTimeMs = i2;
            startMemory = j2;
        }
        lastTimeMs = i2;
        lastMemory = j2;
    }

    private static long getMemoryUsed() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long getStartTimeMs() {
        return startTimeMs;
    }

    public static long getStartMemoryMb() {
        return startMemory / MB;
    }

    public static boolean isGcEvent() {
        return gcEvent;
    }

    public static long getAllocationRateMb() {
        return (long)memBytesSec / MB;
    }
}

