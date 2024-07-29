/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;

public class ResUtils {
    public static String[] collectFiles(String prefix, String suffix) {
        return ResUtils.collectFiles(new String[]{prefix}, new String[]{suffix});
    }

    public static String[] collectFiles(String[] prefixes, String[] suffixes) {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        IResourcePack[] airesourcepack = Config.getResourcePacks();
        int i2 = 0;
        while (i2 < airesourcepack.length) {
            IResourcePack iresourcepack = airesourcepack[i2];
            String[] astring = ResUtils.collectFiles(iresourcepack, prefixes, suffixes, null);
            set.addAll(Arrays.asList(astring));
            ++i2;
        }
        String[] astring1 = set.toArray(new String[set.size()]);
        return astring1;
    }

    public static String[] collectFiles(IResourcePack rp2, String prefix, String suffix, String[] defaultPaths) {
        return ResUtils.collectFiles(rp2, new String[]{prefix}, new String[]{suffix}, defaultPaths);
    }

    public static String[] collectFiles(IResourcePack rp2, String[] prefixes, String[] suffixes) {
        return ResUtils.collectFiles(rp2, prefixes, suffixes, null);
    }

    public static String[] collectFiles(IResourcePack rp2, String[] prefixes, String[] suffixes, String[] defaultPaths) {
        if (rp2 instanceof DefaultResourcePack) {
            return ResUtils.collectFilesFixed(rp2, defaultPaths);
        }
        if (!(rp2 instanceof AbstractResourcePack)) {
            Config.warn("Unknown resource pack type: " + rp2);
            return new String[0];
        }
        AbstractResourcePack abstractresourcepack = (AbstractResourcePack)rp2;
        File file1 = abstractresourcepack.resourcePackFile;
        if (file1 == null) {
            return new String[0];
        }
        if (file1.isDirectory()) {
            return ResUtils.collectFilesFolder(file1, "", prefixes, suffixes);
        }
        if (file1.isFile()) {
            return ResUtils.collectFilesZIP(file1, prefixes, suffixes);
        }
        Config.warn("Unknown resource pack file: " + file1);
        return new String[0];
    }

    private static String[] collectFilesFixed(IResourcePack rp2, String[] paths) {
        if (paths == null) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        int i2 = 0;
        while (i2 < paths.length) {
            String s2 = paths[i2];
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            if (rp2.resourceExists(resourcelocation)) {
                list.add(s2);
            }
            ++i2;
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static String[] collectFilesFolder(File tpFile, String basePath, String[] prefixes, String[] suffixes) {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "assets/minecraft/";
        File[] afile = tpFile.listFiles();
        if (afile == null) {
            return new String[0];
        }
        int i2 = 0;
        while (i2 < afile.length) {
            File file1 = afile[i2];
            if (file1.isFile()) {
                String s3 = String.valueOf(basePath) + file1.getName();
                if (s3.startsWith(s2) && StrUtils.startsWith(s3 = s3.substring(s2.length()), prefixes) && StrUtils.endsWith(s3, suffixes)) {
                    list.add(s3);
                }
            } else if (file1.isDirectory()) {
                String s1 = String.valueOf(basePath) + file1.getName() + "/";
                String[] astring = ResUtils.collectFilesFolder(file1, s1, prefixes, suffixes);
                int j2 = 0;
                while (j2 < astring.length) {
                    String s22 = astring[j2];
                    list.add(s22);
                    ++j2;
                }
            }
            ++i2;
        }
        String[] astring1 = list.toArray(new String[list.size()]);
        return astring1;
    }

    private static String[] collectFilesZIP(File tpFile, String[] prefixes, String[] suffixes) {
        ArrayList<String> list = new ArrayList<String>();
        String s2 = "assets/minecraft/";
        try {
            ZipFile zipfile = new ZipFile(tpFile);
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipentry = enumeration.nextElement();
                String s1 = zipentry.getName();
                if (!s1.startsWith(s2) || !StrUtils.startsWith(s1 = s1.substring(s2.length()), prefixes) || !StrUtils.endsWith(s1, suffixes)) continue;
                list.add(s1);
            }
            zipfile.close();
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return new String[0];
        }
    }

    private static boolean isLowercase(String str) {
        return str.equals(str.toLowerCase(Locale.ROOT));
    }

    public static Properties readProperties(String path, String module) {
        InputStream inputstream;
        block4: {
            ResourceLocation resourcelocation = new ResourceLocation(path);
            inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream != null) break block4;
            return null;
        }
        try {
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            Config.dbg(module + ": Loading " + path);
            return properties;
        }
        catch (FileNotFoundException var5) {
            return null;
        }
        catch (IOException var6) {
            Config.warn(module + ": Error reading " + path);
            return null;
        }
    }

    public static Properties readProperties(InputStream in2, String module) {
        if (in2 == null) {
            return null;
        }
        try {
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(in2);
            in2.close();
            return properties;
        }
        catch (FileNotFoundException var3) {
            return null;
        }
        catch (IOException var4) {
            return null;
        }
    }
}

