/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.src.Config;
import net.optifine.shaders.IShaderPack;
import net.optifine.util.StrUtils;

public class ShaderPackZip
implements IShaderPack {
    protected File packFile;
    protected ZipFile packZipFile;
    protected String baseFolder;

    public ShaderPackZip(String name, File file) {
        this.packFile = file;
        this.packZipFile = null;
        this.baseFolder = "";
    }

    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.packZipFile = null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        try {
            ZipEntry zipentry;
            String s2;
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
                this.baseFolder = this.detectBaseFolder(this.packZipFile);
            }
            if ((s2 = StrUtils.removePrefix(resName, "/")).contains("..")) {
                s2 = this.resolveRelative(s2);
            }
            return (zipentry = this.packZipFile.getEntry(String.valueOf(this.baseFolder) + s2)) == null ? null : this.packZipFile.getInputStream(zipentry);
        }
        catch (Exception var4) {
            return null;
        }
    }

    private String resolveRelative(String name) {
        ArrayDeque<String> deque = new ArrayDeque<String>();
        String[] astring = Config.tokenize(name, "/");
        int i2 = 0;
        while (i2 < astring.length) {
            String s2 = astring[i2];
            if (s2.equals("..")) {
                if (deque.isEmpty()) {
                    return "";
                }
                deque.removeLast();
            } else {
                deque.add(s2);
            }
            ++i2;
        }
        String s1 = Joiner.on('/').join(deque);
        return s1;
    }

    private String detectBaseFolder(ZipFile zip) {
        ZipEntry zipentry = zip.getEntry("shaders/");
        if (zipentry != null && zipentry.isDirectory()) {
            return "";
        }
        Pattern pattern = Pattern.compile("([^/]+/)shaders/");
        Enumeration<? extends ZipEntry> enumeration = zip.entries();
        while (enumeration.hasMoreElements()) {
            String s1;
            ZipEntry zipentry1 = enumeration.nextElement();
            String s2 = zipentry1.getName();
            Matcher matcher = pattern.matcher(s2);
            if (!matcher.matches() || (s1 = matcher.group(1)) == null) continue;
            if (s1.equals("shaders/")) {
                return "";
            }
            return s1;
        }
        return "";
    }

    @Override
    public boolean hasDirectory(String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
                this.baseFolder = this.detectBaseFolder(this.packZipFile);
            }
            String s2 = StrUtils.removePrefix(resName, "/");
            ZipEntry zipentry = this.packZipFile.getEntry(String.valueOf(this.baseFolder) + s2);
            return zipentry != null;
        }
        catch (IOException var4) {
            return false;
        }
    }

    @Override
    public String getName() {
        return this.packFile.getName();
    }
}

