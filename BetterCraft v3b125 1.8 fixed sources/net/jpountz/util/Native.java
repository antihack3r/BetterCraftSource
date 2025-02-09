/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public enum Native {

    private static boolean loaded = false;

    private static String arch() {
        return System.getProperty("os.arch");
    }

    private static OS os() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Linux")) {
            return OS.LINUX;
        }
        if (osName.contains("Mac")) {
            return OS.MAC;
        }
        if (osName.contains("Windows")) {
            return OS.WINDOWS;
        }
        if (osName.contains("Solaris") || osName.contains("SunOS")) {
            return OS.SOLARIS;
        }
        throw new UnsupportedOperationException("Unsupported operating system: " + osName);
    }

    private static String resourceName() {
        OS os2 = Native.os();
        String packagePrefix = Native.class.getPackage().getName().replace('.', '/');
        return "/" + packagePrefix + "/" + os2.name + "/" + Native.arch() + "/liblz4-java." + os2.libExtension;
    }

    public static synchronized boolean isLoaded() {
        return loaded;
    }

    private static void cleanupOldTempLibs() {
        String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        File dir = new File(tempFolder);
        File[] tempLibFiles = dir.listFiles(new FilenameFilter(){
            private final String searchPattern = "liblz4-java-";

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("liblz4-java-") && !name.endsWith(".lck");
            }
        });
        if (tempLibFiles != null) {
            for (File tempLibFile : tempLibFiles) {
                File lckFile = new File(tempLibFile.getAbsolutePath() + ".lck");
                if (lckFile.exists()) continue;
                try {
                    tempLibFile.delete();
                }
                catch (SecurityException e2) {
                    System.err.println("Failed to delete old temp lib" + e2.getMessage());
                }
            }
        }
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        Native.cleanupOldTempLibs();
        try {
            System.loadLibrary("lz4-java");
            loaded = true;
            return;
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            String resourceName = Native.resourceName();
            InputStream is2 = Native.class.getResourceAsStream(resourceName);
            if (is2 == null) {
                throw new UnsupportedOperationException("Unsupported OS/arch, cannot find " + resourceName + ". Please try building from source.");
            }
            File tempLib = null;
            File tempLibLock = null;
            try {
                tempLibLock = File.createTempFile("liblz4-java-", "." + Native.os().libExtension + ".lck");
                tempLib = new File(tempLibLock.getAbsolutePath().replaceFirst(".lck$", ""));
                try (FileOutputStream out = new FileOutputStream(tempLib);){
                    int read;
                    byte[] buf = new byte[4096];
                    while ((read = is2.read(buf)) != -1) {
                        out.write(buf, 0, read);
                    }
                }
                System.load(tempLib.getAbsolutePath());
                loaded = true;
            }
            catch (IOException e2) {
                throw new ExceptionInInitializerError("Cannot unpack liblz4-java: " + e2);
            }
            finally {
                if (!loaded) {
                    if (tempLib != null && tempLib.exists() && !tempLib.delete()) {
                        throw new ExceptionInInitializerError("Cannot unpack liblz4-java / cannot delete a temporary native library " + tempLib);
                    }
                    if (tempLibLock != null && tempLibLock.exists() && !tempLibLock.delete()) {
                        throw new ExceptionInInitializerError("Cannot unpack liblz4-java / cannot delete a temporary lock file " + tempLibLock);
                    }
                } else {
                    tempLib.deleteOnExit();
                    tempLibLock.deleteOnExit();
                }
            }
            return;
        }
    }

    private static enum OS {
        WINDOWS("win32", "so"),
        LINUX("linux", "so"),
        MAC("darwin", "dylib"),
        SOLARIS("solaris", "so");

        public final String name;
        public final String libExtension;

        private OS(String name, String libExtension) {
            this.name = name;
            this.libExtension = libExtension;
        }
    }
}

