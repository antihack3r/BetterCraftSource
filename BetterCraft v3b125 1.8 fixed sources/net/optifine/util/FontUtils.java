/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;

public class FontUtils {
    public static Properties readFontProperties(ResourceLocation locationFontTexture) {
        String s2 = locationFontTexture.getResourcePath();
        PropertiesOrdered properties = new PropertiesOrdered();
        String s1 = ".png";
        if (!s2.endsWith(s1)) {
            return properties;
        }
        String s22 = String.valueOf(s2.substring(0, s2.length() - s1.length())) + ".properties";
        try {
            ResourceLocation resourcelocation = new ResourceLocation(locationFontTexture.getResourceDomain(), s22);
            InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);
            if (inputstream == null) {
                return properties;
            }
            Config.log("Loading " + s22);
            properties.load(inputstream);
            inputstream.close();
        }
        catch (FileNotFoundException resourcelocation) {
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return properties;
    }

    public static void readCustomCharWidths(Properties props, float[] charWidth) {
        for (Object o2 : props.keySet()) {
            String s3;
            float f2;
            String s2;
            int i2;
            String s1;
            String s4 = (String)o2;
            if (!s4.startsWith(s1 = "width.") || (i2 = Config.parseInt(s2 = s4.substring(s1.length()), -1)) < 0 || i2 >= charWidth.length || !((f2 = Config.parseFloat(s3 = props.getProperty(s4), -1.0f)) >= 0.0f)) continue;
            charWidth[i2] = f2;
        }
    }

    public static float readFloat(Properties props, String key, float defOffset) {
        String s2 = props.getProperty(key);
        if (s2 == null) {
            return defOffset;
        }
        float f2 = Config.parseFloat(s2, Float.MIN_VALUE);
        if (f2 == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + key + ": " + s2);
            return defOffset;
        }
        return f2;
    }

    public static boolean readBoolean(Properties props, String key, boolean defVal) {
        String s2 = props.getProperty(key);
        if (s2 == null) {
            return defVal;
        }
        String s1 = s2.toLowerCase().trim();
        if (!s1.equals("true") && !s1.equals("on")) {
            if (!s1.equals("false") && !s1.equals("off")) {
                Config.warn("Invalid value for " + key + ": " + s2);
                return defVal;
            }
            return false;
        }
        return true;
    }

    public static ResourceLocation getHdFontLocation(ResourceLocation fontLoc) {
        if (!Config.isCustomFonts()) {
            return fontLoc;
        }
        if (fontLoc == null) {
            return fontLoc;
        }
        if (!Config.isMinecraftThread()) {
            return fontLoc;
        }
        String s2 = fontLoc.getResourcePath();
        String s1 = "textures/";
        String s22 = "mcpatcher/";
        if (!s2.startsWith(s1)) {
            return fontLoc;
        }
        s2 = s2.substring(s1.length());
        s2 = String.valueOf(s22) + s2;
        ResourceLocation resourcelocation = new ResourceLocation(fontLoc.getResourceDomain(), s2);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : fontLoc;
    }
}

