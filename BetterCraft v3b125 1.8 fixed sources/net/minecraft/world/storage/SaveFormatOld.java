/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld
implements ISaveFormat {
    private static final Logger logger = LogManager.getLogger();
    protected final File savesDirectory;

    public SaveFormatOld(File savesDirectoryIn) {
        if (!savesDirectoryIn.exists()) {
            savesDirectoryIn.mkdirs();
        }
        this.savesDirectory = savesDirectoryIn;
    }

    @Override
    public String getName() {
        return "Old Format";
    }

    @Override
    public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
        ArrayList<SaveFormatComparator> list = Lists.newArrayList();
        int i2 = 0;
        while (i2 < 5) {
            String s2 = "World" + (i2 + 1);
            WorldInfo worldinfo = this.getWorldInfo(s2);
            if (worldinfo != null) {
                list.add(new SaveFormatComparator(s2, "", worldinfo.getLastTimePlayed(), worldinfo.getSizeOnDisk(), worldinfo.getGameType(), false, worldinfo.isHardcoreModeEnabled(), worldinfo.areCommandsAllowed()));
            }
            ++i2;
        }
        return list;
    }

    @Override
    public void flushCache() {
    }

    @Override
    public WorldInfo getWorldInfo(String saveName) {
        File file1 = new File(this.savesDirectory, saveName);
        if (!file1.exists()) {
            return null;
        }
        File file2 = new File(file1, "level.dat");
        if (file2.exists()) {
            try {
                NBTTagCompound nbttagcompound2 = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                NBTTagCompound nbttagcompound3 = nbttagcompound2.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound3);
            }
            catch (Exception exception1) {
                logger.error("Exception reading " + file2, (Throwable)exception1);
            }
        }
        if ((file2 = new File(file1, "level.dat_old")).exists()) {
            try {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                return new WorldInfo(nbttagcompound1);
            }
            catch (Exception exception) {
                logger.error("Exception reading " + file2, (Throwable)exception);
            }
        }
        return null;
    }

    @Override
    public void renameWorld(String dirName, String newName) {
        File file2;
        File file1 = new File(this.savesDirectory, dirName);
        if (file1.exists() && (file2 = new File(file1, "level.dat")).exists()) {
            try {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                nbttagcompound1.setString("LevelName", newName);
                CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file2));
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public boolean isNewLevelIdAcceptable(String saveName) {
        File file1 = new File(this.savesDirectory, saveName);
        if (file1.exists()) {
            return false;
        }
        try {
            file1.mkdir();
            file1.delete();
            return true;
        }
        catch (Throwable throwable) {
            logger.warn("Couldn't make new level", throwable);
            return false;
        }
    }

    @Override
    public boolean deleteWorldDirectory(String saveName) {
        File file1 = new File(this.savesDirectory, saveName);
        if (!file1.exists()) {
            return true;
        }
        logger.info("Deleting level " + saveName);
        int i2 = 1;
        while (i2 <= 5) {
            logger.info("Attempt " + i2 + "...");
            if (SaveFormatOld.deleteFiles(file1.listFiles())) break;
            logger.warn("Unsuccessful in deleting contents.");
            if (i2 < 5) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
            ++i2;
        }
        return file1.delete();
    }

    protected static boolean deleteFiles(File[] files) {
        int i2 = 0;
        while (i2 < files.length) {
            File file1 = files[i2];
            logger.debug("Deleting " + file1);
            if (file1.isDirectory() && !SaveFormatOld.deleteFiles(file1.listFiles())) {
                logger.warn("Couldn't delete directory " + file1);
                return false;
            }
            if (!file1.delete()) {
                logger.warn("Couldn't delete file " + file1);
                return false;
            }
            ++i2;
        }
        return true;
    }

    @Override
    public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
        return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
    }

    @Override
    public boolean isConvertible(String saveName) {
        return false;
    }

    @Override
    public boolean isOldMapFormat(String saveName) {
        return false;
    }

    @Override
    public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
        return false;
    }

    @Override
    public boolean canLoadWorld(String saveName) {
        File file1 = new File(this.savesDirectory, saveName);
        return file1.isDirectory();
    }
}

