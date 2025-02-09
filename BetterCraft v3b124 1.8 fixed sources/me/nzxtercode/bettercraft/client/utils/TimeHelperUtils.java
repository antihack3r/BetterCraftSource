/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.utils;

import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public final class TimeHelperUtils {
    private static TimeHelperUtils tpsTimer = new TimeHelperUtils();
    private static ArrayList<Long> times = new ArrayList();
    public static double lastTps = 20.0;
    private static long currentCalcLag;
    private long time;
    private long tick;
    public static Packet event1;

    public static long bytesToMb(long l2) {
        return l2 / 1024L / 1024L;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L - this.time;
    }

    public void reset() {
        this.time = System.nanoTime() / 1000000L;
    }

    public static long getLag() {
        return System.currentTimeMillis() - currentCalcLag;
    }

    public static long getFormattedLag() {
        long currentLag = TimeHelperUtils.getLag();
        if (currentLag >= 2500L && currentLag < 5000000L) {
            return currentLag;
        }
        return 0L;
    }

    public static void onPacketRecieved(Packet event) {
        if (event instanceof S03PacketTimeUpdate) {
            currentCalcLag = System.currentTimeMillis();
            times.add(Math.max(1000L, tpsTimer.getTime()));
            long timesAdded = 0L;
            if (times.size() > 5) {
                times.remove(0);
            }
            for (long l2 : times) {
                timesAdded += l2;
            }
            long roundedTps = timesAdded / (long)times.size();
            lastTps = 20.0 / (double)roundedTps * 1000.0;
            tpsTimer.reset();
        }
    }

    public static char getTPSColorCode(double tps2) {
        if (tps2 >= 17.0) {
            return 'a';
        }
        if (tps2 >= 13.0) {
            return 'e';
        }
        if (tps2 > 9.0) {
            return 'c';
        }
        return '4';
    }
}

