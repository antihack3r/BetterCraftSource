/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.resourcepacks24.Resourcepacks24;

public class ResourcepackMethods {
    public static String modifyName(String nameIn, File file) {
        if (!file.getParentFile().equals(Resourcepacks24.getInstance().resourcepacksDir)) {
            return String.valueOf(file.getParentFile().getName()) + "/" + nameIn;
        }
        return nameIn;
    }

    public static List<File> getFiles(Object screen) {
        ArrayList<File> files = new ArrayList<File>();
        File[] fileArray = Resourcepacks24.getInstance().resourcepacksDir.listFiles();
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File file = fileArray[n3];
            if (ResourcepackMethods.isValidPack(file)) {
                files.add(file);
            } else if (file.isDirectory()) {
                File[] fileArray2 = file.listFiles();
                int n4 = fileArray2.length;
                int n5 = 0;
                while (n5 < n4) {
                    File subFile = fileArray2[n5];
                    if (ResourcepackMethods.isValidPack(subFile)) {
                        files.add(subFile);
                    }
                    ++n5;
                }
            }
            ++n3;
        }
        return files;
    }

    private static boolean isValidPack(File file) {
        boolean flag2;
        boolean flag = file.isFile() && file.getName().endsWith(".zip");
        boolean bl2 = flag2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
        return flag || flag2;
    }
}

