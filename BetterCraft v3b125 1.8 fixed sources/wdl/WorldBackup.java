/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.minecraft.client.resources.I18n;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class WorldBackup {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void backupWorld(File worldFolder, String worldName, WorldBackupType type, IBackupProgressMonitor monitor) throws IOException {
        String newWorldName = String.valueOf(worldName) + "_" + DATE_FORMAT.format(new Date());
        switch (type) {
            case NONE: {
                return;
            }
            case FOLDER: {
                File destination = new File(worldFolder.getParentFile(), newWorldName);
                if (destination.exists()) {
                    throw new IOException("Backup folder (" + destination + ") already exists!");
                }
                WorldBackup.copyDirectory(worldFolder, destination, monitor);
                return;
            }
            case ZIP: {
                File destination = new File(worldFolder.getParentFile(), String.valueOf(newWorldName) + ".zip");
                if (destination.exists()) {
                    throw new IOException("Backup file (" + destination + ") already exists!");
                }
                WorldBackup.zipDirectory(worldFolder, destination, monitor);
                return;
            }
        }
    }

    public static void copyDirectory(File src, File destination, IBackupProgressMonitor monitor) throws IOException {
        monitor.setNumberOfFiles(WorldBackup.countFilesInFolder(src));
        WorldBackup.copy(src, destination, src.getPath().length() + 1, monitor);
    }

    public static void zipDirectory(File src, File destination, IBackupProgressMonitor monitor) throws IOException {
        monitor.setNumberOfFiles(WorldBackup.countFilesInFolder(src));
        ZipOutputStream stream = null;
        try (FileOutputStream outStream = null;){
            outStream = new FileOutputStream(destination);
            try {
                stream = new ZipOutputStream(outStream);
                WorldBackup.zipFolder(src, stream, src.getPath().length() + 1, monitor);
            }
            finally {
                stream.close();
            }
        }
    }

    private static void zipFolder(File folder, ZipOutputStream stream, int pathStartIndex, IBackupProgressMonitor monitor) throws IOException {
        File[] fileArray = folder.listFiles();
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File file = fileArray[n3];
            if (file.isFile()) {
                String name = file.getPath().substring(pathStartIndex);
                monitor.onNextFile(name);
                ZipEntry zipEntry = new ZipEntry(name);
                stream.putNextEntry(zipEntry);
                try (FileInputStream inputStream = new FileInputStream(file);){
                    IOUtils.copy((InputStream)inputStream, (OutputStream)stream);
                }
                stream.closeEntry();
            } else if (file.isDirectory()) {
                WorldBackup.zipFolder(file, stream, pathStartIndex, monitor);
            }
            ++n3;
        }
    }

    private static void copy(File from, File to2, int pathStartIndex, IBackupProgressMonitor monitor) throws IOException {
        if (from.isDirectory()) {
            if (!to2.exists()) {
                to2.mkdir();
            }
            String[] stringArray = from.list();
            int n2 = stringArray.length;
            int n3 = 0;
            while (n3 < n2) {
                String fileName = stringArray[n3];
                WorldBackup.copy(new File(from, fileName), new File(to2, fileName), pathStartIndex, monitor);
                ++n3;
            }
        } else {
            monitor.onNextFile(to2.getPath().substring(pathStartIndex));
            FileUtils.copyFile(from, to2, true);
        }
    }

    private static int countFilesInFolder(File folder) {
        if (!folder.isDirectory()) {
            return 0;
        }
        int count = 0;
        File[] fileArray = folder.listFiles();
        int n2 = fileArray.length;
        int n3 = 0;
        while (n3 < n2) {
            File file = fileArray[n3];
            count = file.isDirectory() ? (count += WorldBackup.countFilesInFolder(file)) : ++count;
            ++n3;
        }
        return count;
    }

    private WorldBackup() {
    }

    public static interface IBackupProgressMonitor {
        public void setNumberOfFiles(int var1);

        public void onNextFile(String var1);
    }

    public static enum WorldBackupType {
        NONE("wdl.backup.none", ""),
        FOLDER("wdl.backup.folder", "wdl.saveProgress.backingUp.title.folder"),
        ZIP("wdl.backup.zip", "wdl.saveProgress.backingUp.title.zip");

        public final String descriptionKey;
        public final String titleKey;

        private WorldBackupType(String descriptionKey, String titleKey) {
            this.descriptionKey = descriptionKey;
            this.titleKey = titleKey;
        }

        public String getDescription() {
            return I18n.format(this.descriptionKey, new Object[0]);
        }

        public String getTitle() {
            return I18n.format(this.titleKey, new Object[0]);
        }

        public static WorldBackupType match(String name) {
            WorldBackupType[] worldBackupTypeArray = WorldBackupType.values();
            int n2 = worldBackupTypeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                WorldBackupType type = worldBackupTypeArray[n3];
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
                ++n3;
            }
            return NONE;
        }
    }
}

