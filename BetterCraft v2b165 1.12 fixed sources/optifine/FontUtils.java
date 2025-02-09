// 
// Decompiled by Procyon v0.6.0
// 

package optifine;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;

public class FontUtils
{
    public static Properties readFontProperties(final ResourceLocation p_readFontProperties_0_) {
        final String s = p_readFontProperties_0_.getResourcePath();
        final Properties properties = new Properties();
        final String s2 = ".png";
        if (!s.endsWith(s2)) {
            return properties;
        }
        final String s3 = String.valueOf(s.substring(0, s.length() - s2.length())) + ".properties";
        try {
            final ResourceLocation resourcelocation = new ResourceLocation(p_readFontProperties_0_.getResourceDomain(), s3);
            final InputStream inputstream = Config.getResourceStream(Config.getResourceManager(), resourcelocation);
            if (inputstream == null) {
                return properties;
            }
            Config.log("Loading " + s3);
            properties.load(inputstream);
        }
        catch (final FileNotFoundException ex) {}
        catch (final IOException ioexception) {
            ioexception.printStackTrace();
        }
        return properties;
    }
    
    public static void readCustomCharWidths(final Properties p_readCustomCharWidths_0_, final float[] p_readCustomCharWidths_1_) {
        for (final Object s : ((Hashtable<Object, V>)p_readCustomCharWidths_0_).keySet()) {
            final String s2 = "width.";
            if (((String)s).startsWith(s2)) {
                final String s3 = ((String)s).substring(s2.length());
                final int i = Config.parseInt(s3, -1);
                if (i < 0 || i >= p_readCustomCharWidths_1_.length) {
                    continue;
                }
                final String s4 = p_readCustomCharWidths_0_.getProperty((String)s);
                final float f = Config.parseFloat(s4, -1.0f);
                if (f < 0.0f) {
                    continue;
                }
                p_readCustomCharWidths_1_[i] = f;
            }
        }
    }
    
    public static float readFloat(final Properties p_readFloat_0_, final String p_readFloat_1_, final float p_readFloat_2_) {
        final String s = p_readFloat_0_.getProperty(p_readFloat_1_);
        if (s == null) {
            return p_readFloat_2_;
        }
        final float f = Config.parseFloat(s, Float.MIN_VALUE);
        if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value for " + p_readFloat_1_ + ": " + s);
            return p_readFloat_2_;
        }
        return f;
    }
    
    public static boolean readBoolean(final Properties p_readBoolean_0_, final String p_readBoolean_1_, final boolean p_readBoolean_2_) {
        final String s = p_readBoolean_0_.getProperty(p_readBoolean_1_);
        if (s == null) {
            return p_readBoolean_2_;
        }
        final String s2 = s.toLowerCase().trim();
        if (s2.equals("true") || s2.equals("on")) {
            return true;
        }
        if (!s2.equals("false") && !s2.equals("off")) {
            Config.warn("Invalid value for " + p_readBoolean_1_ + ": " + s);
            return p_readBoolean_2_;
        }
        return false;
    }
    
    public static ResourceLocation getHdFontLocation(final ResourceLocation p_getHdFontLocation_0_) {
        if (!Config.isCustomFonts()) {
            return p_getHdFontLocation_0_;
        }
        if (p_getHdFontLocation_0_ == null) {
            return p_getHdFontLocation_0_;
        }
        if (!Config.isMinecraftThread()) {
            return p_getHdFontLocation_0_;
        }
        String s = p_getHdFontLocation_0_.getResourcePath();
        final String s2 = "textures/";
        final String s3 = "mcpatcher/";
        if (!s.startsWith(s2)) {
            return p_getHdFontLocation_0_;
        }
        s = s.substring(s2.length());
        s = String.valueOf(s3) + s;
        final ResourceLocation resourcelocation = new ResourceLocation(p_getHdFontLocation_0_.getResourceDomain(), s);
        return Config.hasResource(Config.getResourceManager(), resourcelocation) ? resourcelocation : p_getHdFontLocation_0_;
    }
}
