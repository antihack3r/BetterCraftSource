/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.src.Config;
import net.optifine.CustomLoadingScreen;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CustomLoadingScreens {
    private static CustomLoadingScreen[] screens = null;
    private static int screensMinDimensionId = 0;

    public static CustomLoadingScreen getCustomLoadingScreen() {
        if (screens == null) {
            return null;
        }
        int i2 = PacketThreadUtil.lastDimensionId;
        int j2 = i2 - screensMinDimensionId;
        CustomLoadingScreen customloadingscreen = null;
        if (j2 >= 0 && j2 < screens.length) {
            customloadingscreen = screens[j2];
        }
        return customloadingscreen;
    }

    public static void update() {
        screens = null;
        screensMinDimensionId = 0;
        Pair<CustomLoadingScreen[], Integer> pair = CustomLoadingScreens.parseScreens();
        screens = pair.getLeft();
        screensMinDimensionId = pair.getRight();
    }

    private static Pair<CustomLoadingScreen[], Integer> parseScreens() {
        String s2 = "optifine/gui/loading/background";
        String s1 = ".png";
        String[] astring = ResUtils.collectFiles(s2, s1);
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        int i2 = 0;
        while (i2 < astring.length) {
            String s22 = astring[i2];
            String s3 = StrUtils.removePrefixSuffix(s22, s2, s1);
            int j2 = Config.parseInt(s3, Integer.MIN_VALUE);
            if (j2 == Integer.MIN_VALUE) {
                CustomLoadingScreens.warn("Invalid dimension ID: " + s3 + ", path: " + s22);
            } else {
                map.put(j2, s22);
            }
            ++i2;
        }
        Set set = map.keySet();
        Object[] ainteger = set.toArray(new Integer[set.size()]);
        Arrays.sort(ainteger);
        if (ainteger.length <= 0) {
            return new ImmutablePair<Object, Integer>(null, 0);
        }
        String s5 = "optifine/gui/loading/loading.properties";
        Properties properties = ResUtils.readProperties(s5, "CustomLoadingScreens");
        int k2 = (Integer)ainteger[0];
        int l2 = (Integer)ainteger[ainteger.length - 1];
        int i1 = l2 - k2 + 1;
        CustomLoadingScreen[] acustomloadingscreen = new CustomLoadingScreen[i1];
        int j1 = 0;
        while (j1 < ainteger.length) {
            Object integer = ainteger[j1];
            String s4 = (String)map.get(integer);
            acustomloadingscreen[((Integer)integer).intValue() - k2] = CustomLoadingScreen.parseScreen(s4, (Integer)integer, properties);
            ++j1;
        }
        return new ImmutablePair<CustomLoadingScreen[], Integer>(acustomloadingscreen, k2);
    }

    public static void warn(String str) {
        Config.warn("CustomLoadingScreen: " + str);
    }

    public static void dbg(String str) {
        Config.dbg("CustomLoadingScreen: " + str);
    }
}

