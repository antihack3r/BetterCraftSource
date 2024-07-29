/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.HashMap;
import java.util.Map;

public class TimedEvent {
    private static Map<String, Long> mapEventTimes = new HashMap<String, Long>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isActive(String name, long timeIntervalMs) {
        Map<String, Long> map = mapEventTimes;
        synchronized (map) {
            long i2;
            block5: {
                long j2;
                i2 = System.currentTimeMillis();
                Long olong = mapEventTimes.get(name);
                if (olong == null) {
                    olong = new Long(i2);
                    mapEventTimes.put(name, olong);
                }
                if (i2 >= (j2 = olong.longValue()) + timeIntervalMs) break block5;
                return false;
            }
            mapEventTimes.put(name, new Long(i2));
            return true;
        }
    }
}

