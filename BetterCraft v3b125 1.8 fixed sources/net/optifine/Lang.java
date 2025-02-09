/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Lang {
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    public static void resourcesReloaded() {
        Map map = I18n.getLocaleProperties();
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "optifine/lang/";
        String s1 = "en_US";
        String s22 = ".lang";
        list.add(String.valueOf(s2) + s1 + s22);
        if (!Config.getGameSettings().language.equals(s1)) {
            list.add(String.valueOf(s2) + Config.getGameSettings().language + s22);
        }
        String[] astring = list.toArray(new String[list.size()]);
        Lang.loadResources(Config.getDefaultResourcePack(), astring, map);
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        int i2 = 0;
        while (i2 < airesourcepack.length) {
            IResourcePack iresourcepack = airesourcepack[i2];
            Lang.loadResources(iresourcepack, astring, map);
            ++i2;
        }
    }

    private static void loadResources(IResourcePack rp2, String[] files, Map localeProperties) {
        try {
            int i2 = 0;
            while (i2 < files.length) {
                InputStream inputstream;
                String s2 = files[i2];
                ResourceLocation resourcelocation = new ResourceLocation(s2);
                if (rp2.resourceExists(resourcelocation) && (inputstream = rp2.getInputStream(resourcelocation)) != null) {
                    Lang.loadLocaleData(inputstream, localeProperties);
                }
                ++i2;
            }
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public static void loadLocaleData(InputStream is2, Map localeProperties) throws IOException {
        Iterator<String> iterator = IOUtils.readLines(is2, Charsets.UTF_8).iterator();
        is2.close();
        while (iterator.hasNext()) {
            String[] astring;
            String s2 = iterator.next();
            if (s2.isEmpty() || s2.charAt(0) == '#' || (astring = Iterables.toArray(splitter.split(s2), String.class)) == null || astring.length != 2) continue;
            String s1 = astring[0];
            String s22 = pattern.matcher(astring[1]).replaceAll("%$1s");
            localeProperties.put(s1, s22);
        }
    }

    public static String get(String key) {
        return I18n.format(key, new Object[0]);
    }

    public static String get(String key, String def) {
        String s2 = I18n.format(key, new Object[0]);
        return s2 != null && !s2.equals(key) ? s2 : def;
    }

    public static String getOn() {
        return I18n.format("options.on", new Object[0]);
    }

    public static String getOff() {
        return I18n.format("options.off", new Object[0]);
    }

    public static String getFast() {
        return I18n.format("options.graphics.fast", new Object[0]);
    }

    public static String getFancy() {
        return I18n.format("options.graphics.fancy", new Object[0]);
    }

    public static String getDefault() {
        return I18n.format("generator.default", new Object[0]);
    }
}

