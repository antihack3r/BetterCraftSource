/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import com.google.common.collect.HashMultimap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.ReflectionUtils;
import wdl.WDLChunkLoader;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.WorldBackup;
import wdl.api.IPlayerInfoEditor;
import wdl.api.ISaveListener;
import wdl.api.IWorldInfoEditor;
import wdl.api.WDLApi;
import wdl.gui.GuiWDLMultiworld;
import wdl.gui.GuiWDLMultiworldSelect;
import wdl.gui.GuiWDLOverwriteChanges;
import wdl.gui.GuiWDLSaveProgress;

public class WDL {
    public static final String VERSION = "1.8.9a-beta2";
    public static final String EXPECTED_MINECRAFT_VERSION = "1.8.9";
    public static final String GITHUB_REPO = "Pokechu22/WorldDownloader";
    public static Minecraft minecraft;
    public static WorldClient worldClient;
    public static NetworkManager networkManager;
    public static EntityPlayerSP thePlayer;
    public static Container windowContainer;
    public static BlockPos lastClickedBlock;
    public static Entity lastEntity;
    public static SaveHandler saveHandler;
    public static IChunkLoader chunkLoader;
    public static HashMap<ChunkCoordIntPair, Map<BlockPos, TileEntity>> newTileEntities;
    public static HashMultimap<ChunkCoordIntPair, Entity> newEntities;
    public static HashMap<Integer, MapData> newMapDatas;
    public static boolean downloading;
    public static boolean isMultiworld;
    public static boolean propsFound;
    public static boolean startOnChange;
    public static boolean overrideLastModifiedCheck;
    public static boolean saving;
    public static boolean worldLoadingDeferred;
    public static String worldName;
    public static String baseFolderName;
    public static Properties baseProps;
    public static Properties worldProps;
    public static final Properties globalProps;
    public static final Properties defaultProps;
    private static Logger logger;
    private static final int ANVIL_SAVE_VERSION = 19133;

    static {
        block14: {
            networkManager = null;
            newTileEntities = new HashMap();
            newEntities = HashMultimap.create();
            newMapDatas = new HashMap();
            downloading = false;
            isMultiworld = false;
            propsFound = false;
            startOnChange = false;
            overrideLastModifiedCheck = false;
            saving = false;
            worldLoadingDeferred = false;
            worldName = "WorldDownloaderERROR";
            baseFolderName = "WorldDownloaderERROR";
            logger = LogManager.getLogger();
            minecraft = Minecraft.getMinecraft();
            defaultProps = new Properties();
            defaultProps.setProperty("ServerName", "");
            defaultProps.setProperty("WorldName", "");
            defaultProps.setProperty("LinkedWorlds", "");
            defaultProps.setProperty("Backup", "ZIP");
            defaultProps.setProperty("AllowCheats", "true");
            defaultProps.setProperty("GameType", "keep");
            defaultProps.setProperty("Time", "keep");
            defaultProps.setProperty("Weather", "keep");
            defaultProps.setProperty("MapFeatures", "false");
            defaultProps.setProperty("RandomSeed", "");
            defaultProps.setProperty("MapGenerator", "void");
            defaultProps.setProperty("GeneratorName", "flat");
            defaultProps.setProperty("GeneratorVersion", "0");
            defaultProps.setProperty("GeneratorOptions", ";0");
            defaultProps.setProperty("Spawn", "player");
            defaultProps.setProperty("SpawnX", "8");
            defaultProps.setProperty("SpawnY", "127");
            defaultProps.setProperty("SpawnZ", "8");
            defaultProps.setProperty("PlayerPos", "keep");
            defaultProps.setProperty("PlayerX", "8");
            defaultProps.setProperty("PlayerY", "127");
            defaultProps.setProperty("PlayerZ", "8");
            defaultProps.setProperty("PlayerHealth", "20");
            defaultProps.setProperty("PlayerFood", "20");
            defaultProps.setProperty("Messages.enableAll", "false");
            defaultProps.setProperty("Entity.TrackDistanceMode", "server");
            defaultProps.setProperty("Entity.FireworksRocketEntity.Enabled", "false");
            defaultProps.setProperty("Entity.EnderDragon.Enabled", "false");
            defaultProps.setProperty("Entity.WitherBoss.Enabled", "false");
            defaultProps.setProperty("Entity.PrimedTnt.Enabled", "false");
            defaultProps.setProperty("Entity.null.Enabled", "false");
            defaultProps.setProperty("EntityGroup.Other.Enabled", "true");
            defaultProps.setProperty("EntityGroup.Hostile.Enabled", "true");
            defaultProps.setProperty("EntityGroup.Passive.Enabled", "true");
            defaultProps.setProperty("LastSaved", "-1");
            defaultProps.setProperty("TutorialShown", "false");
            defaultProps.setProperty("UpdateMinecraftVersion", "client");
            defaultProps.setProperty("UpdateAllowBetas", "true");
            globalProps = new Properties(defaultProps);
            InputStreamReader reader = null;
            try {
                try {
                    reader = new FileReader(new File(WDL.minecraft.mcDataDir, "WorldDownloader.txt"));
                    globalProps.load(reader);
                }
                catch (Exception e2) {
                    logger.debug("Failed to load global properties", (Throwable)e2);
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Exception e3) {
                            logger.warn("Failed to close global properties reader", (Throwable)e3);
                        }
                    }
                    break block14;
                }
            }
            catch (Throwable throwable) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception e4) {
                        logger.warn("Failed to close global properties reader", (Throwable)e4);
                    }
                }
                throw throwable;
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e5) {
                    logger.warn("Failed to close global properties reader", (Throwable)e5);
                }
            }
        }
        baseProps = new Properties(globalProps);
        worldProps = new Properties(baseProps);
    }

    public static void startDownload() {
        worldClient = WDL.minecraft.theWorld;
        if (!WDLPluginChannels.canDownloadAtAll()) {
            return;
        }
        if (isMultiworld && worldName.isEmpty()) {
            minecraft.displayGuiScreen(new GuiWDLMultiworldSelect(I18n.format("wdl.gui.multiworldSelect.title.startDownload", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback(){

                @Override
                public void onWorldSelected(String selectedWorld) {
                    worldName = selectedWorld;
                    isMultiworld = true;
                    propsFound = true;
                    minecraft.displayGuiScreen(null);
                    WDL.startDownload();
                }

                @Override
                public void onCancel() {
                    minecraft.displayGuiScreen(null);
                    WDL.cancelDownload();
                }
            }));
            return;
        }
        if (!propsFound) {
            minecraft.displayGuiScreen(new GuiWDLMultiworld(new GuiWDLMultiworld.MultiworldCallback(){

                @Override
                public void onSelect(boolean enableMutliworld) {
                    isMultiworld = enableMutliworld;
                    if (isMultiworld) {
                        minecraft.displayGuiScreen(new GuiWDLMultiworldSelect(I18n.format("wdl.gui.multiworldSelect.title.startDownload", new Object[0]), new GuiWDLMultiworldSelect.WorldSelectionCallback(){

                            @Override
                            public void onWorldSelected(String selectedWorld) {
                                worldName = selectedWorld;
                                isMultiworld = true;
                                propsFound = true;
                                minecraft.displayGuiScreen(null);
                                WDL.startDownload();
                            }

                            @Override
                            public void onCancel() {
                                minecraft.displayGuiScreen(null);
                                WDL.cancelDownload();
                            }
                        }));
                    } else {
                        baseProps.setProperty("LinkedWorlds", "");
                        WDL.saveProps();
                        propsFound = true;
                        minecraft.displayGuiScreen(null);
                        WDL.startDownload();
                    }
                }

                @Override
                public void onCancel() {
                    minecraft.displayGuiScreen(null);
                    WDL.cancelDownload();
                }
            }));
            return;
        }
        worldProps = WDL.loadWorldProps(worldName);
        saveHandler = (SaveHandler)minecraft.getSaveLoader().getSaveLoader(WDL.getWorldFolderName(worldName), true);
        FileInputStream worldDat = null;
        try {
            long lastSaved = Long.parseLong(worldProps.getProperty("LastSaved", "-1"));
            worldDat = new FileInputStream(new File(saveHandler.getWorldDirectory(), "level.dat"));
            long lastPlayed = CompressedStreamTools.readCompressed(worldDat).getCompoundTag("Data").getLong("LastPlayed");
            if (!overrideLastModifiedCheck && lastPlayed > lastSaved) {
                minecraft.displayGuiScreen(new GuiWDLOverwriteChanges(lastSaved, lastPlayed));
                return;
            }
        }
        catch (Exception e2) {
            logger.warn("Error while checking if the map has been played andneeds to be backed up (this is normal if this world has not been saved before): ", (Throwable)e2);
        }
        finally {
            if (worldDat != null) {
                try {
                    worldDat.close();
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
        minecraft.displayGuiScreen(null);
        minecraft.setIngameFocus();
        chunkLoader = WDLChunkLoader.create(saveHandler, WDL.worldClient.provider);
        newTileEntities = new HashMap();
        newEntities = HashMultimap.create();
        newMapDatas = new HashMap();
        if (baseProps.getProperty("ServerName").isEmpty()) {
            baseProps.setProperty("ServerName", WDL.getServerName());
        }
        startOnChange = true;
        downloading = true;
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadStarted", new Object[0]);
    }

    public static void stopDownload() {
        if (downloading) {
            downloading = false;
            startOnChange = false;
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadStopped", new Object[0]);
            WDL.startSaveThread();
        }
    }

    public static void cancelDownload() {
        boolean wasDownloading = downloading;
        if (wasDownloading) {
            minecraft.getSaveLoader().flushCache();
            saveHandler.flush();
            startOnChange = false;
            saving = false;
            downloading = false;
            worldLoadingDeferred = false;
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.downloadCanceled", new Object[0]);
        }
    }

    static void startSaveThread() {
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveStarted", new Object[0]);
        saving = true;
        Thread thread = new Thread("WDL Save Thread"){

            @Override
            public void run() {
                try {
                    WDL.saveEverything();
                    saving = false;
                    WDL.onSaveComplete();
                }
                catch (Throwable e2) {
                    WDL.crashed(e2, "World Downloader Mod: Saving world");
                }
            }
        };
        thread.start();
    }

    public static boolean loadWorld() {
        worldName = "";
        worldClient = WDL.minecraft.theWorld;
        thePlayer = WDL.minecraft.thePlayer;
        windowContainer = WDL.thePlayer.openContainer;
        overrideLastModifiedCheck = false;
        NetworkManager newNM = WDL.thePlayer.sendQueue.getNetworkManager();
        if (networkManager != newNM) {
            WDL.loadBaseProps();
            WDLMessages.onNewServer();
        }
        WDLPluginChannels.onWorldLoad();
        if (networkManager != newNM) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.differentServer", new Object[0]);
            networkManager = newNM;
            if (WDL.isSpigot()) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.spigot", thePlayer.getClientBrand());
            } else {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.vanilla", thePlayer.getClientBrand());
            }
            startOnChange = false;
            return true;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.sameServer", new Object[0]);
        if (WDL.isSpigot()) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.spigot", thePlayer.getClientBrand());
        } else {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_WORLD_LOAD, "wdl.messages.onWorldLoad.vanilla", thePlayer.getClientBrand());
        }
        if (startOnChange) {
            WDL.startDownload();
        }
        return false;
    }

    public static void onSaveComplete() {
        minecraft.getSaveLoader().flushCache();
        saveHandler.flush();
        worldClient = null;
        worldLoadingDeferred = false;
        if (downloading) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveComplete.startingAgain", new Object[0]);
            WDL.loadWorld();
            return;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.saveComplete.done", new Object[0]);
    }

    public static void saveEverything() throws Exception {
        if (!WDLPluginChannels.canDownloadAtAll()) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.forbidden", new Object[0]);
            return;
        }
        WorldBackup.WorldBackupType backupType = WorldBackup.WorldBackupType.match(baseProps.getProperty("Backup", "ZIP"));
        final GuiWDLSaveProgress progressScreen = new GuiWDLSaveProgress(I18n.format("wdl.saveProgress.title", new Object[0]), (backupType != WorldBackup.WorldBackupType.NONE ? 6 : 5) + WDLApi.getImplementingExtensions(ISaveListener.class).size());
        minecraft.addScheduledTask(new Runnable(){

            @Override
            public void run() {
                minecraft.displayGuiScreen(progressScreen);
            }
        });
        WDL.saveProps();
        try {
            saveHandler.checkSessionLock();
        }
        catch (MinecraftException e2) {
            throw new RuntimeException("WorldDownloader: Couldn't get session lock for saving the world!", e2);
        }
        NBTTagCompound playerNBT = WDL.savePlayer(progressScreen);
        WDL.saveWorldInfo(progressScreen, playerNBT);
        WDL.saveMapData(progressScreen);
        WDL.saveChunks(progressScreen);
        WDL.saveProps();
        for (WDLApi.ModInfo<ISaveListener> info : WDLApi.getImplementingExtensions(ISaveListener.class)) {
            progressScreen.startMajorTask(I18n.format("wdl.saveProgress.extension.title", info.getDisplayName()), 1);
            ((ISaveListener)info.mod).afterChunksSaved(saveHandler.getWorldDirectory());
        }
        try {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.flushingIO", new Object[0]);
            progressScreen.startMajorTask(I18n.format("wdl.saveProgress.flushingIO.title", new Object[0]), 1);
            progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.flushingIO.subtitle", new Object[0]), 1);
            ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
        }
        catch (Exception e3) {
            throw new RuntimeException("Threw exception waiting for asynchronous IO to finish. Hmmm.", e3);
        }
        if (backupType != WorldBackup.WorldBackupType.NONE) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.backingUp", new Object[0]);
            progressScreen.startMajorTask(backupType.getTitle(), 1);
            progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.backingUp.preparing", new Object[0]), 1);
            try {
                WorldBackup.backupWorld(saveHandler.getWorldDirectory(), WDL.getWorldFolderName(worldName), backupType, progressScreen);
            }
            catch (IOException e4) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToBackUp", new Object[0]);
            }
        }
        progressScreen.setDoneWorking();
    }

    public static NBTTagCompound savePlayer(GuiWDLSaveProgress progressScreen) {
        if (!WDLPluginChannels.canDownloadAtAll()) {
            return new NBTTagCompound();
        }
        progressScreen.startMajorTask(I18n.format("wdl.saveProgress.playerData.title", new Object[0]), 3 + WDLApi.getImplementingExtensions(IPlayerInfoEditor.class).size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingPlayer", new Object[0]);
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.playerData.creatingNBT", new Object[0]), 1);
        NBTTagCompound playerNBT = new NBTTagCompound();
        thePlayer.writeToNBT(playerNBT);
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.playerData.editingNBT", new Object[0]), 2);
        WDL.applyOverridesToPlayer(playerNBT);
        int taskNum = 3;
        for (WDLApi.ModInfo<IPlayerInfoEditor> info : WDLApi.getImplementingExtensions(IPlayerInfoEditor.class)) {
            progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.playerData.extension", info.getDisplayName()), taskNum);
            ((IPlayerInfoEditor)info.mod).editPlayerInfo(thePlayer, saveHandler, playerNBT);
            ++taskNum;
        }
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.playerData.writingNBT", new Object[0]), taskNum);
        FileOutputStream stream = null;
        try {
            try {
                File playersDirectory = new File(saveHandler.getWorldDirectory(), "playerdata");
                File playerFileTmp = new File(playersDirectory, String.valueOf(thePlayer.getUniqueID().toString()) + ".dat.tmp");
                File playerFile = new File(playersDirectory, String.valueOf(thePlayer.getUniqueID().toString()) + ".dat");
                stream = new FileOutputStream(playerFileTmp);
                CompressedStreamTools.writeCompressed(playerNBT, stream);
                if (playerFile.exists()) {
                    playerFile.delete();
                }
                playerFileTmp.renameTo(playerFile);
            }
            catch (Exception e2) {
                throw new RuntimeException("Couldn't save the player!", e2);
            }
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException e3) {
                    throw new RuntimeException(e3);
                }
            }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.playerSaved", new Object[0]);
        return playerNBT;
    }

    public static void saveWorldInfo(GuiWDLSaveProgress progressScreen, NBTTagCompound playerInfoNBT) {
        if (!WDLPluginChannels.canDownloadAtAll()) {
            return;
        }
        progressScreen.startMajorTask(I18n.format("wdl.saveProgress.worldMetadata.title", new Object[0]), 3 + WDLApi.getImplementingExtensions(IWorldInfoEditor.class).size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingWorld", new Object[0]);
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.worldMetadata.creatingNBT", new Object[0]), 1);
        worldClient.getWorldInfo().setSaveVersion(19133);
        NBTTagCompound worldInfoNBT = worldClient.getWorldInfo().cloneNBTCompound(playerInfoNBT);
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.worldMetadata.editingNBT", new Object[0]), 2);
        WDL.applyOverridesToWorldInfo(worldInfoNBT);
        int taskNum = 3;
        for (WDLApi.ModInfo<IWorldInfoEditor> info : WDLApi.getImplementingExtensions(IWorldInfoEditor.class)) {
            progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.worldMetadata.extension", info.getDisplayName()), taskNum);
            ((IWorldInfoEditor)info.mod).editWorldInfo(worldClient, worldClient.getWorldInfo(), saveHandler, worldInfoNBT);
            ++taskNum;
        }
        progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.worldMetadata.writingNBT", new Object[0]), taskNum);
        File saveDirectory = saveHandler.getWorldDirectory();
        NBTTagCompound dataNBT = new NBTTagCompound();
        dataNBT.setTag("Data", worldInfoNBT);
        worldProps.setProperty("LastSaved", Long.toString(worldInfoNBT.getLong("LastPlayed")));
        FileOutputStream stream = null;
        try {
            try {
                File dataFile = new File(saveDirectory, "level.dat_new");
                File dataFileBackup = new File(saveDirectory, "level.dat_old");
                File dataFileOld = new File(saveDirectory, "level.dat");
                stream = new FileOutputStream(dataFile);
                CompressedStreamTools.writeCompressed(dataNBT, stream);
                if (dataFileBackup.exists()) {
                    dataFileBackup.delete();
                }
                dataFileOld.renameTo(dataFileBackup);
                if (dataFileOld.exists()) {
                    dataFileOld.delete();
                }
                dataFile.renameTo(dataFileOld);
                if (dataFile.exists()) {
                    dataFile.delete();
                }
            }
            catch (Exception e2) {
                throw new RuntimeException("Couldn't save the world metadata!", e2);
            }
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException e3) {
                    throw new RuntimeException(e3);
                }
            }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.worldSaved", new Object[0]);
    }

    public static void saveChunks(GuiWDLSaveProgress progressScreen) throws IllegalArgumentException, IllegalAccessException {
        if (!WDLPluginChannels.canDownloadAtAll()) {
            return;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingChunks", new Object[0]);
        ChunkProviderClient chunkProvider = (ChunkProviderClient)worldClient.getChunkProvider();
        List chunks = ReflectionUtils.stealAndGetField(chunkProvider, List.class);
        progressScreen.startMajorTask(I18n.format("wdl.saveProgress.chunk.title", new Object[0]), chunks.size());
        int currentChunk = 0;
        while (currentChunk < chunks.size()) {
            Chunk c2 = (Chunk)chunks.get(currentChunk);
            if (c2 != null && WDLPluginChannels.canSaveChunk(c2)) {
                progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.chunk.saving", c2.xPosition, c2.zPosition), currentChunk);
                WDL.saveChunk(c2);
            }
            ++currentChunk;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.chunksSaved", new Object[0]);
    }

    public static void saveChunk(Chunk c2) {
        if (!WDLPluginChannels.canDownloadAtAll()) {
            return;
        }
        if (!WDLPluginChannels.canSaveChunk(c2)) {
            return;
        }
        c2.setTerrainPopulated(true);
        try {
            chunkLoader.saveChunk(worldClient, c2);
        }
        catch (Exception e2) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveChunk", c2.xPosition, c2.zPosition, e2);
        }
    }

    public static void loadBaseProps() {
        block16: {
            baseFolderName = WDL.getBaseFolderName();
            baseProps = new Properties(globalProps);
            InputStreamReader reader = null;
            try {
                try {
                    File savesFolder = new File(WDL.minecraft.mcDataDir, "saves");
                    File baseFolder = new File(savesFolder, baseFolderName);
                    reader = new FileReader(new File(baseFolder, "WorldDownloader.txt"));
                    baseProps.load(reader);
                    propsFound = true;
                }
                catch (Exception e2) {
                    propsFound = false;
                    logger.debug("Failed to load base properties", (Throwable)e2);
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Exception e3) {
                            logger.warn("Failed to close base properties reader", (Throwable)e3);
                        }
                    }
                    break block16;
                }
            }
            catch (Throwable throwable) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception e4) {
                        logger.warn("Failed to close base properties reader", (Throwable)e4);
                    }
                }
                throw throwable;
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e5) {
                    logger.warn("Failed to close base properties reader", (Throwable)e5);
                }
            }
        }
        if (baseProps.getProperty("LinkedWorlds").isEmpty()) {
            isMultiworld = false;
            worldProps = new Properties(baseProps);
        } else {
            isMultiworld = true;
        }
    }

    public static Properties loadWorldProps(String theWorldName) {
        Properties ret = new Properties(baseProps);
        if (theWorldName.isEmpty()) {
            return ret;
        }
        File savesDir = new File(WDL.minecraft.mcDataDir, "saves");
        String folder = WDL.getWorldFolderName(theWorldName);
        File worldFolder = new File(savesDir, folder);
        InputStreamReader reader = null;
        try {
            ret.load(new FileReader(new File(worldFolder, "WorldDownloader.txt")));
            Properties properties = ret;
            return properties;
        }
        catch (Exception e2) {
            logger.debug("Failed to load world props for " + worldName, (Throwable)e2);
            Properties properties = ret;
            return properties;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e3) {
                    logger.warn("Failed to close world props reader for " + worldName, (Throwable)e3);
                }
            }
        }
    }

    public static void saveProps() {
        WDL.saveProps(worldName, worldProps);
    }

    public static void saveProps(String theWorldName, Properties theWorldProps) {
        File savesDir = new File(WDL.minecraft.mcDataDir, "saves");
        if (theWorldName.length() > 0) {
            String folder = WDL.getWorldFolderName(theWorldName);
            File worldFolder = new File(savesDir, folder);
            worldFolder.mkdirs();
            try {
                theWorldProps.store(new FileWriter(new File(worldFolder, "WorldDownloader.txt")), I18n.format("wdl.props.world.title", new Object[0]));
            }
            catch (Exception exception) {}
        } else if (!isMultiworld) {
            baseProps.putAll((Map<?, ?>)theWorldProps);
        }
        File baseFolder = new File(savesDir, baseFolderName);
        baseFolder.mkdirs();
        try {
            baseProps.store(new FileWriter(new File(baseFolder, "WorldDownloader.txt")), I18n.format("wdl.props.base.title", new Object[0]));
        }
        catch (Exception exception) {
            // empty catch block
        }
        WDL.saveGlobalProps();
    }

    public static void saveGlobalProps() {
        try {
            globalProps.store(new FileWriter(new File(WDL.minecraft.mcDataDir, "WorldDownloader.txt")), I18n.format("wdl.props.global.title", new Object[0]));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void applyOverridesToPlayer(NBTTagCompound playerNBT) {
        String playerPos;
        String food;
        String health = worldProps.getProperty("PlayerHealth");
        if (!health.equals("keep")) {
            short h2 = Short.parseShort(health);
            playerNBT.setShort("Health", h2);
        }
        if (!(food = worldProps.getProperty("PlayerFood")).equals("keep")) {
            int f2 = Integer.parseInt(food);
            playerNBT.setInteger("foodLevel", f2);
            playerNBT.setInteger("foodTickTimer", 0);
            if (f2 == 20) {
                playerNBT.setFloat("foodSaturationLevel", 5.0f);
            } else {
                playerNBT.setFloat("foodSaturationLevel", 0.0f);
            }
            playerNBT.setFloat("foodExhaustionLevel", 0.0f);
        }
        if ((playerPos = worldProps.getProperty("PlayerPos")).equals("xyz")) {
            int x2 = Integer.parseInt(worldProps.getProperty("PlayerX"));
            int y2 = Integer.parseInt(worldProps.getProperty("PlayerY"));
            int z2 = Integer.parseInt(worldProps.getProperty("PlayerZ"));
            NBTTagList pos = new NBTTagList();
            pos.appendTag(new NBTTagDouble((double)x2 + 0.5));
            pos.appendTag(new NBTTagDouble((double)y2 + 0.621));
            pos.appendTag(new NBTTagDouble((double)z2 + 0.5));
            playerNBT.setTag("Pos", pos);
            NBTTagList motion = new NBTTagList();
            motion.appendTag(new NBTTagDouble(0.0));
            motion.appendTag(new NBTTagDouble(-1.0E-4));
            motion.appendTag(new NBTTagDouble(0.0));
            playerNBT.setTag("Motion", motion);
            NBTTagList rotation = new NBTTagList();
            rotation.appendTag(new NBTTagFloat(0.0f));
            rotation.appendTag(new NBTTagFloat(0.0f));
            playerNBT.setTag("Rotation", rotation);
        }
        if (WDL.thePlayer.capabilities.allowFlying) {
            playerNBT.getCompoundTag("abilities").setBoolean("flying", true);
        }
    }

    public static void applyOverridesToWorldInfo(NBTTagCompound worldInfoNBT) {
        String baseName = baseProps.getProperty("ServerName");
        String worldName = worldProps.getProperty("WorldName");
        if (worldName.isEmpty()) {
            worldInfoNBT.setString("LevelName", baseName);
        } else {
            worldInfoNBT.setString("LevelName", String.valueOf(baseName) + " - " + worldName);
        }
        if (worldProps.getProperty("AllowCheats").equals("true")) {
            worldInfoNBT.setBoolean("allowCommands", true);
        } else {
            worldInfoNBT.setBoolean("allowCommands", false);
        }
        String gametypeOption = worldProps.getProperty("GameType");
        if (gametypeOption.equals("keep")) {
            if (WDL.thePlayer.capabilities.isCreativeMode) {
                worldInfoNBT.setInteger("GameType", 1);
            } else {
                worldInfoNBT.setInteger("GameType", 0);
            }
        } else if (gametypeOption.equals("survival")) {
            worldInfoNBT.setInteger("GameType", 0);
        } else if (gametypeOption.equals("creative")) {
            worldInfoNBT.setInteger("GameType", 1);
        } else if (gametypeOption.equals("hardcore")) {
            worldInfoNBT.setInteger("GameType", 0);
            worldInfoNBT.setBoolean("hardcore", true);
        }
        String timeOption = worldProps.getProperty("Time");
        if (!timeOption.equals("keep")) {
            long t2 = Integer.parseInt(timeOption);
            worldInfoNBT.setLong("Time", t2);
        }
        String randomSeed = worldProps.getProperty("RandomSeed");
        long seed = 0L;
        if (!randomSeed.isEmpty()) {
            try {
                seed = Long.parseLong(randomSeed);
            }
            catch (NumberFormatException numberformatexception) {
                seed = randomSeed.hashCode();
            }
        }
        worldInfoNBT.setLong("RandomSeed", seed);
        boolean mapFeatures = Boolean.parseBoolean(worldProps.getProperty("MapFeatures"));
        worldInfoNBT.setBoolean("MapFeatures", mapFeatures);
        String generatorName = worldProps.getProperty("GeneratorName");
        worldInfoNBT.setString("generatorName", generatorName);
        String generatorOptions = worldProps.getProperty("GeneratorOptions");
        worldInfoNBT.setString("generatorOptions", generatorOptions);
        int generatorVersion = Integer.parseInt(worldProps.getProperty("GeneratorVersion"));
        worldInfoNBT.setInteger("generatorVersion", generatorVersion);
        String weather = worldProps.getProperty("Weather");
        if (weather.equals("sunny")) {
            worldInfoNBT.setBoolean("raining", false);
            worldInfoNBT.setInteger("rainTime", 0);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        } else if (weather.equals("rain")) {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", false);
            worldInfoNBT.setInteger("thunderTime", 0);
        } else if (weather.equals("thunderstorm")) {
            worldInfoNBT.setBoolean("raining", true);
            worldInfoNBT.setInteger("rainTime", 24000);
            worldInfoNBT.setBoolean("thundering", true);
            worldInfoNBT.setInteger("thunderTime", 24000);
        }
        String spawn = worldProps.getProperty("Spawn");
        if (spawn.equals("player")) {
            int x2 = MathHelper.floor_double(WDL.thePlayer.posX);
            int y2 = MathHelper.floor_double(WDL.thePlayer.posY);
            int z2 = MathHelper.floor_double(WDL.thePlayer.posZ);
            worldInfoNBT.setInteger("SpawnX", x2);
            worldInfoNBT.setInteger("SpawnY", y2);
            worldInfoNBT.setInteger("SpawnZ", z2);
            worldInfoNBT.setBoolean("initialized", true);
        } else if (spawn.equals("xyz")) {
            int x3 = Integer.parseInt(worldProps.getProperty("SpawnX"));
            int y3 = Integer.parseInt(worldProps.getProperty("SpawnY"));
            int z3 = Integer.parseInt(worldProps.getProperty("SpawnZ"));
            worldInfoNBT.setInteger("SpawnX", x3);
            worldInfoNBT.setInteger("SpawnY", y3);
            worldInfoNBT.setInteger("SpawnZ", z3);
            worldInfoNBT.setBoolean("initialized", true);
        }
    }

    public static void saveMapData(GuiWDLSaveProgress progressScreen) {
        if (!WDLPluginChannels.canSaveMaps()) {
            return;
        }
        File dataDirectory = new File(saveHandler.getWorldDirectory(), "data");
        dataDirectory.mkdirs();
        progressScreen.startMajorTask(I18n.format("wdl.saveProgress.map.title", new Object[0]), newMapDatas.size());
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.savingMapItemData", new Object[0]);
        int count = 0;
        for (Map.Entry<Integer, MapData> e2 : newMapDatas.entrySet()) {
            progressScreen.setMinorTaskProgress(I18n.format("wdl.saveProgress.map.saving", e2.getKey()), ++count);
            File mapFile = new File(dataDirectory, "map_" + e2.getKey() + ".dat");
            NBTTagCompound mapNBT = new NBTTagCompound();
            NBTTagCompound data = new NBTTagCompound();
            e2.getValue().writeToNBT(data);
            mapNBT.setTag("data", data);
            try {
                CompressedStreamTools.writeCompressed(mapNBT, new FileOutputStream(mapFile));
            }
            catch (IOException ex2) {
                throw new RuntimeException("WDL: Exception while writing map data for map " + e2.getKey() + "!", ex2);
            }
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.SAVING, "wdl.messages.saving.mapItemDataSaved", new Object[0]);
    }

    public static String getServerName() {
        try {
            if (minecraft.getCurrentServerData() != null) {
                String name = WDL.minecraft.getCurrentServerData().serverName;
                if (name.equals(I18n.format("selectServer.defaultName", new Object[0]))) {
                    name = WDL.minecraft.getCurrentServerData().serverIP;
                }
                return name;
            }
        }
        catch (Exception e2) {
            logger.warn("Exception while getting server name: ", (Throwable)e2);
        }
        return "Unidentified Server";
    }

    public static String getBaseFolderName() {
        return WDL.getServerName().replaceAll("\\W+", "_");
    }

    public static String getWorldFolderName(String theWorldName) {
        if (theWorldName.isEmpty()) {
            return baseFolderName;
        }
        return String.valueOf(baseFolderName) + " - " + theWorldName;
    }

    public static void saveContainerItems(Container container, IInventory tileEntity, int containerStartIndex) {
        int containerSize = container.inventorySlots.size();
        int inventorySize = tileEntity.getSizeInventory();
        int containerIndex = containerStartIndex;
        int inventoryIndex = 0;
        while (containerIndex < containerSize && inventoryIndex < inventorySize) {
            ItemStack item = container.getSlot(containerIndex).getStack();
            tileEntity.setInventorySlotContents(inventoryIndex, item);
            ++inventoryIndex;
            ++containerIndex;
        }
    }

    public static void saveInventoryFields(IInventory inventory, IInventory tileEntity) {
        int i2 = 0;
        while (i2 < inventory.getFieldCount()) {
            tileEntity.setField(i2, inventory.getField(i2));
            ++i2;
        }
    }

    public static void saveTileEntity(BlockPos pos, TileEntity te2) {
        int chunkZ;
        int chunkX = pos.getX() / 16;
        ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(chunkX, chunkZ = pos.getZ() / 16);
        if (!newTileEntities.containsKey(chunkPos)) {
            newTileEntities.put(chunkPos, new HashMap());
        }
        newTileEntities.get(chunkPos).put(pos, te2);
    }

    public static boolean isSpigot() {
        if (thePlayer != null && thePlayer.getClientBrand() != null) {
            return thePlayer.getClientBrand().toLowerCase().contains("spigot");
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    public static String getDebugInfo() {
        info = new StringBuilder();
        info.append("### CORE INFO\n\n");
        info.append("WDL version: ").append("1.8.9a-beta2").append('\n');
        info.append("Launched version: ").append(Minecraft.getMinecraft().getVersion()).append('\n');
        info.append("Client brand: ").append(ClientBrandRetriever.getClientModName()).append('\n');
        info.append("File location: ");
        try {
            path = new File(WDL.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            username = System.getProperty("user.name");
            path = path.replace(username, "<USERNAME>");
            info.append(path);
        }
        catch (Exception e) {
            info.append("Unknown (").append(e.toString()).append(')');
        }
        info.append("\n\n### EXTENSIONS\n\n");
        extensions = WDLApi.getWDLMods();
        info.append(extensions.size()).append(" loaded\n");
        block4: for (Map.Entry<String, WDLApi.ModInfo<?>> e : extensions.entrySet()) {
            info.append("\n#### ").append(e.getKey()).append("\n\n");
            try {
                info.append(e.getValue().getInfo());
                continue;
            }
            catch (Exception ex) {
                info.append("ERROR: ").append(ex).append('\n');
                var8_10 = ex.getStackTrace();
                var7_9 = var8_10.length;
                var6_8 = 0;
                ** while (var6_8 < var7_9)
            }
lbl-1000:
            // 1 sources

            {
                elm = var8_10[var6_8];
                info.append(elm).append('\n');
                ++var6_8;
                continue;
lbl46:
                // 1 sources

            }
        }
        info.append("\n### STATE\n\n");
        info.append("minecraft: ").append(WDL.minecraft).append('\n');
        info.append("worldClient: ").append(WDL.worldClient).append('\n');
        info.append("networkManager: ").append(WDL.networkManager).append('\n');
        info.append("thePlayer: ").append(WDL.thePlayer).append('\n');
        info.append("windowContainer: ").append(WDL.windowContainer).append('\n');
        info.append("lastClickedBlock: ").append(WDL.lastClickedBlock).append('\n');
        info.append("lastEntity: ").append(WDL.lastEntity).append('\n');
        info.append("saveHandler: ").append(WDL.saveHandler).append('\n');
        info.append("chunkLoader: ").append(WDL.chunkLoader).append('\n');
        info.append("newTileEntities: ").append(WDL.newTileEntities).append('\n');
        info.append("newEntities: ").append(WDL.newEntities).append('\n');
        info.append("newMapDatas: ").append(WDL.newMapDatas).append('\n');
        info.append("downloading: ").append(WDL.downloading).append('\n');
        info.append("isMultiworld: ").append(WDL.isMultiworld).append('\n');
        info.append("propsFound: ").append(WDL.propsFound).append('\n');
        info.append("startOnChange: ").append(WDL.startOnChange).append('\n');
        info.append("overrideLastModifiedCheck: ").append(WDL.overrideLastModifiedCheck).append('\n');
        info.append("saving: ").append(WDL.saving).append('\n');
        info.append("worldLoadingDeferred: ").append(WDL.worldLoadingDeferred).append('\n');
        info.append("worldName: ").append(WDL.worldName).append('\n');
        info.append("baseFolderName: ").append(WDL.baseFolderName).append('\n');
        info.append("### CONNECTED SERVER\n\n");
        data = Minecraft.getMinecraft().getCurrentServerData();
        if (data == null) {
            info.append("No data\n");
        } else {
            info.append("Name: ").append(data.serverName).append('\n');
            info.append("IP: ").append(data.serverIP).append('\n');
        }
        info.append("\n### PROPERTIES\n\n");
        info.append("\n#### BASE\n\n");
        if (WDL.baseProps != null) {
            if (!WDL.baseProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : WDL.baseProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        info.append("\n#### WORLD\n\n");
        if (WDL.worldProps != null) {
            if (!WDL.worldProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : WDL.worldProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        info.append("\n#### DEFAULT\n\n");
        if (WDL.globalProps != null) {
            if (!WDL.globalProps.isEmpty()) {
                for (Map.Entry<Object, Object> e : WDL.globalProps.entrySet()) {
                    info.append(e.getKey()).append(": ").append(e.getValue());
                    info.append('\n');
                }
            } else {
                info.append("empty\n");
            }
        } else {
            info.append("null\n");
        }
        return info.toString();
    }

    public static void crashed(Throwable t2, String category) {
        CrashReport report;
        if (t2 instanceof ReportedException) {
            CrashReport oldReport = ((ReportedException)t2).getCrashReport();
            report = CrashReport.makeCrashReport(oldReport.getCrashCause(), String.valueOf(category) + " (" + oldReport.getCauseStackTraceOrString() + ")");
            try {
                List crashReportSectionsOld = ReflectionUtils.stealAndGetField(oldReport, List.class);
                List crashReportSectionsNew = ReflectionUtils.stealAndGetField(report, List.class);
                crashReportSectionsNew.addAll(crashReportSectionsOld);
            }
            catch (Exception e2) {
                report.makeCategory("An exception occured while trying to copy the origional categories.").addCrashSectionThrowable(":(", e2);
            }
        } else {
            report = CrashReport.makeCrashReport(t2, category);
        }
        minecraft.crashed(report);
    }

    public static String getMinecraftVersion() {
        Map<String, String> map = Minecraft.getSessionInfo();
        if (map.containsKey("X-Minecraft-Version")) {
            return map.get("X-Minecraft-Version");
        }
        return EXPECTED_MINECRAFT_VERSION;
    }

    public static String getMinecraftVersionInfo() {
        String version = WDL.getMinecraftVersion();
        String launchedVersion = Minecraft.getMinecraft().getVersion();
        String brand = ClientBrandRetriever.getClientModName();
        return String.format("Minecraft %s (%s/%s)", version, launchedVersion, brand);
    }
}

