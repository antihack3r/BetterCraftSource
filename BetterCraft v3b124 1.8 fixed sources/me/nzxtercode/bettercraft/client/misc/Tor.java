/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.nzxtercode.bettercraft.client.BetterCraft;
import net.minecraft.util.Util;

public class Tor {
    private static final Tor INSTANCE = new Tor();
    private static Process torProcess;
    private static final File file;
    private static final File output;

    static {
        BetterCraft.getInstance();
        file = new File(BetterCraft.clientName, "tor.zip");
        BetterCraft.getInstance();
        output = new File(BetterCraft.clientName, "tor");
    }

    public static final Tor getInstance() {
        return INSTANCE;
    }

    public static void init() {
        try {
            if (!output.exists() || !file.exists()) {
                Files.copy(Tor.class.getResourceAsStream("/assets/minecraft/client/tor.zip"), Paths.get(file.getPath(), new String[0]), StandardCopyOption.REPLACE_EXISTING);
                if (!output.exists()) {
                    output.mkdir();
                    Tor.decompress(file, output);
                }
                file.delete();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        Tor.startTor();
    }

    public static void startTor() {
        try {
            if (!Tor.isActive()) {
                boolean isWindows = Util.getOSType() == Util.EnumOS.WINDOWS;
                ProcessBuilder process = new ProcessBuilder(String.format("%s%s%s%s%s%s", output.getPath(), File.separator, isWindows ? "windows" : "linux", File.separator, isWindows ? "" : "sudo bash ./tor", String.format("tor%s", isWindows ? ".exe" : "")));
                torProcess = process.start();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void stopTor() {
        if (torProcess.isAlive()) {
            torProcess.destroy();
        }
    }

    public static boolean isActive() {
        return Objects.nonNull(torProcess) && torProcess.isAlive();
    }

    public static void decompress(File inIn, File outOut) throws Exception {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(inIn));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = Tor.newFile(outOut, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                int len;
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                FileOutputStream fos = new FileOutputStream(newFile);
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(String.valueOf(destDirPath) + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}

