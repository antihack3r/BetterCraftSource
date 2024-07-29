/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomPanoramaProperties;
import net.optifine.util.MathUtils;
import net.optifine.util.PropertiesOrdered;

public class CustomPanorama {
    private static CustomPanoramaProperties customPanoramaProperties = null;
    private static final Random random = new Random();

    public static CustomPanoramaProperties getCustomPanoramaProperties() {
        return customPanoramaProperties;
    }

    public static void update() {
        customPanoramaProperties = null;
        String[] astring = CustomPanorama.getPanoramaFolders();
        if (astring.length > 1) {
            CustomPanoramaProperties custompanoramaproperties;
            Properties[] aproperties = CustomPanorama.getPanoramaProperties(astring);
            int[] aint = CustomPanorama.getWeights(aproperties);
            int i2 = CustomPanorama.getRandomIndex(aint);
            String s2 = astring[i2];
            Properties properties = aproperties[i2];
            if (properties == null) {
                properties = aproperties[0];
            }
            if (properties == null) {
                properties = new PropertiesOrdered();
            }
            customPanoramaProperties = custompanoramaproperties = new CustomPanoramaProperties(s2, properties);
        }
    }

    private static String[] getPanoramaFolders() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("textures/gui/title/background");
        int i2 = 0;
        while (i2 < 100) {
            String s2 = "optifine/gui/background" + i2;
            String s1 = String.valueOf(s2) + "/panorama_0.png";
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            if (Config.hasResource(resourcelocation)) {
                list.add(s2);
            }
            ++i2;
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static Properties[] getPanoramaProperties(String[] folders) {
        Properties[] aproperties = new Properties[folders.length];
        int i2 = 0;
        while (i2 < folders.length) {
            String s2 = folders[i2];
            if (i2 == 0) {
                s2 = "optifine/gui";
            } else {
                Config.dbg("CustomPanorama: " + s2);
            }
            ResourceLocation resourcelocation = new ResourceLocation(String.valueOf(s2) + "/background.properties");
            try {
                InputStream inputstream = Config.getResourceStream(resourcelocation);
                if (inputstream != null) {
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    Config.dbg("CustomPanorama: " + resourcelocation.getResourcePath());
                    aproperties[i2] = properties;
                    inputstream.close();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            ++i2;
        }
        return aproperties;
    }

    private static int[] getWeights(Properties[] propertiess) {
        int[] aint = new int[propertiess.length];
        int i2 = 0;
        while (i2 < aint.length) {
            Properties properties = propertiess[i2];
            if (properties == null) {
                properties = propertiess[0];
            }
            if (properties == null) {
                aint[i2] = 1;
            } else {
                String s2 = properties.getProperty("weight", null);
                aint[i2] = Config.parseInt(s2, 1);
            }
            ++i2;
        }
        return aint;
    }

    private static int getRandomIndex(int[] weights) {
        int i2 = MathUtils.getSum(weights);
        int j2 = random.nextInt(i2);
        int k2 = 0;
        int l2 = 0;
        while (l2 < weights.length) {
            if ((k2 += weights[l2]) > j2) {
                return l2;
            }
            ++l2;
        }
        return weights.length - 1;
    }
}

