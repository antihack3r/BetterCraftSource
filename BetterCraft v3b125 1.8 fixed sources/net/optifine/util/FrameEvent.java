/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class FrameEvent {
    private static Map<String, Integer> mapEventFrames = new HashMap<String, Integer>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isActive(String name, int frameInterval) {
        Map<String, Integer> map = mapEventFrames;
        synchronized (map) {
            int i2;
            block5: {
                int j2;
                i2 = Minecraft.getMinecraft().entityRenderer.frameCount;
                Integer integer = mapEventFrames.get(name);
                if (integer == null) {
                    integer = new Integer(i2);
                    mapEventFrames.put(name, integer);
                }
                if (i2 <= (j2 = integer.intValue()) || i2 >= j2 + frameInterval) break block5;
                return false;
            }
            mapEventFrames.put(name, new Integer(i2));
            return true;
        }
    }
}

