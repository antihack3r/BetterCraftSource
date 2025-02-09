/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockNote;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.SaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.EntityUtils;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.api.IEntityEditor;
import wdl.api.ITileEntityEditor;
import wdl.api.ITileEntityImportationIdentifier;
import wdl.api.WDLApi;

public class WDLChunkLoader
extends AnvilChunkLoader {
    private static Logger logger = LogManager.getLogger();
    private final File chunkSaveLocation;

    public static WDLChunkLoader create(SaveHandler handler, WorldProvider provider) {
        return new WDLChunkLoader(WDLChunkLoader.getWorldSaveFolder(handler, provider));
    }

    private static File getWorldSaveFolder(SaveHandler handler, WorldProvider provider) {
        File baseFolder = handler.getWorldDirectory();
        try {
            Method forgeGetSaveFolderMethod = provider.getClass().getMethod("getSaveFolder", new Class[0]);
            String name = (String)forgeGetSaveFolderMethod.invoke((Object)provider, new Object[0]);
            if (name != null) {
                File file = new File(baseFolder, name);
                file.mkdirs();
                return file;
            }
            return baseFolder;
        }
        catch (Exception e2) {
            if (provider instanceof WorldProviderHell) {
                File file = new File(baseFolder, "DIM-1");
                file.mkdirs();
                return file;
            }
            if (provider instanceof WorldProviderEnd) {
                File file = new File(baseFolder, "DIM1");
                file.mkdirs();
                return file;
            }
            return baseFolder;
        }
    }

    public WDLChunkLoader(File file) {
        super(file);
        this.chunkSaveLocation = file;
    }

    @Override
    public void saveChunk(World world, Chunk chunk) throws MinecraftException, IOException {
        world.checkSessionLock();
        NBTTagCompound levelTag = this.writeChunkToNBT(chunk, world);
        NBTTagCompound rootTag = new NBTTagCompound();
        rootTag.setTag("Level", levelTag);
        this.addChunkToPending(chunk.getChunkCoordIntPair(), rootTag);
    }

    private NBTTagCompound writeChunkToNBT(Chunk chunk, World world) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("V", (byte)1);
        compound.setInteger("xPos", chunk.xPosition);
        compound.setInteger("zPos", chunk.zPosition);
        compound.setLong("LastUpdate", world.getTotalWorldTime());
        compound.setIntArray("HeightMap", chunk.getHeightMap());
        compound.setBoolean("TerrainPopulated", chunk.isTerrainPopulated());
        compound.setBoolean("LightPopulated", chunk.isLightPopulated());
        compound.setLong("InhabitedTime", chunk.getInhabitedTime());
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        NBTTagList blockStorageList = new NBTTagList();
        boolean hasNoSky = !world.provider.getHasNoSky();
        ExtendedBlockStorage[] extendedBlockStorageArray = blockStorageArray;
        int n2 = blockStorageArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ExtendedBlockStorage blockStorage = extendedBlockStorageArray[n3];
            if (blockStorage != null) {
                NBTTagCompound blockData = new NBTTagCompound();
                blockData.setByte("Y", (byte)(blockStorage.getYLocation() >> 4 & 0xFF));
                byte[] var12 = new byte[blockStorage.getData().length];
                NibbleArray var13 = new NibbleArray();
                NibbleArray var14 = null;
                int var15 = 0;
                while (var15 < blockStorage.getData().length) {
                    char var16 = blockStorage.getData()[var15];
                    int var17 = var15 & 0xF;
                    int var18 = var15 >> 8 & 0xF;
                    int var19 = var15 >> 4 & 0xF;
                    if (var16 >> 12 != 0) {
                        if (var14 == null) {
                            var14 = new NibbleArray();
                        }
                        var14.set(var17, var18, var19, var16 >> 12);
                    }
                    var12[var15] = (byte)(var16 >> 4 & 0xFF);
                    var13.set(var17, var18, var19, var16 & 0xF);
                    ++var15;
                }
                blockData.setByteArray("Blocks", var12);
                blockData.setByteArray("Data", var13.getData());
                if (var14 != null) {
                    blockData.setByteArray("Add", var14.getData());
                }
                blockData.setByteArray("BlockLight", blockStorage.getBlocklightArray().getData());
                if (hasNoSky) {
                    blockData.setByteArray("SkyLight", blockStorage.getSkylightArray().getData());
                } else {
                    blockData.setByteArray("SkyLight", new byte[blockStorage.getBlocklightArray().getData().length]);
                }
                blockStorageList.appendTag(blockData);
            }
            ++n3;
        }
        compound.setTag("Sections", blockStorageList);
        compound.setByteArray("Biomes", chunk.getBiomeArray());
        chunk.setHasEntities(false);
        NBTTagList entityList = this.getEntityList(chunk);
        compound.setTag("Entities", entityList);
        NBTTagList tileEntityList = this.getTileEntityList(chunk);
        compound.setTag("TileEntities", tileEntityList);
        List<NextTickListEntry> updateList = world.getPendingBlockUpdates(chunk, false);
        if (updateList != null) {
            long worldTime = world.getTotalWorldTime();
            NBTTagList entries = new NBTTagList();
            for (NextTickListEntry entry : updateList) {
                NBTTagCompound entryTag = new NBTTagCompound();
                ResourceLocation location = (ResourceLocation)Block.blockRegistry.getNameForObject(entry.getBlock());
                entryTag.setString("i", location == null ? "" : location.toString());
                entryTag.setInteger("x", entry.position.getX());
                entryTag.setInteger("y", entry.position.getY());
                entryTag.setInteger("z", entry.position.getZ());
                entryTag.setInteger("t", (int)(entry.scheduledTime - worldTime));
                entryTag.setInteger("p", entry.priority);
                entries.appendTag(entryTag);
            }
            compound.setTag("TileTicks", entries);
        }
        return compound;
    }

    public NBTTagList getEntityList(Chunk chunk) {
        NBTTagList entityList = new NBTTagList();
        if (!WDLPluginChannels.canSaveEntities(chunk)) {
            return entityList;
        }
        ArrayList<Entity> entities = new ArrayList<Entity>();
        ClassInheritanceMultiMap<Entity>[] classInheritanceMultiMapArray = chunk.getEntityLists();
        int n2 = classInheritanceMultiMapArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ClassInheritanceMultiMap<Entity> map = classInheritanceMultiMapArray[n3];
            entities.addAll(map);
            ++n3;
        }
        for (Entity e2 : WDL.newEntities.get((Object)chunk.getChunkCoordIntPair())) {
            e2.isDead = false;
            entities.add(e2);
        }
        for (Entity entity : entities) {
            if (entity == null) {
                logger.warn("[WDL] Null entity in chunk at " + chunk.getChunkCoordIntPair());
                continue;
            }
            if (!WDLChunkLoader.shouldSaveEntity(entity)) continue;
            for (WDLApi.ModInfo modInfo : WDLApi.getImplementingExtensions(IEntityEditor.class)) {
                try {
                    if (!((IEntityEditor)modInfo.mod).shouldEdit(entity)) continue;
                    ((IEntityEditor)modInfo.mod).editEntity(entity);
                }
                catch (Exception ex2) {
                    throw new RuntimeException("Failed to edit entity " + entity + " for chunk at " + chunk.getChunkCoordIntPair() + " with extension " + modInfo, ex2);
                }
            }
            NBTTagCompound nBTTagCompound = new NBTTagCompound();
            try {
                if (!entity.writeToNBTOptional(nBTTagCompound)) continue;
                chunk.setHasEntities(true);
                entityList.appendTag(nBTTagCompound);
                continue;
            }
            catch (Exception e3) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveEntity", entity, chunk.xPosition, chunk.zPosition, e3);
                logger.warn("Compound: " + nBTTagCompound);
                logger.warn("Entity metadata dump:");
                try {
                    List<DataWatcher.WatchableObject> objects = entity.getDataWatcher().getAllWatched();
                    if (objects == null) {
                        logger.warn("No entries (getAllWatched() returned null)");
                    } else {
                        logger.warn(objects);
                        for (DataWatcher.WatchableObject obj : objects) {
                            if (obj == null) continue;
                            logger.warn("WatchableObject [getDataValueId()=" + obj.getDataValueId() + ", getObject()=" + obj.getObject() + ", getObjectType()=" + obj.getObjectType() + ", isWatched()=" + obj.isWatched() + "]");
                        }
                    }
                }
                catch (Exception e2) {
                    logger.warn("Failed to complete dump: ", (Throwable)e3);
                }
            }
            logger.warn("End entity metadata dump");
        }
        return entityList;
    }

    public static boolean shouldSaveEntity(Entity e2) {
        if (e2 instanceof EntityPlayer) {
            return false;
        }
        if (!EntityUtils.isEntityEnabled(e2)) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.notSavingUserPreference", e2);
            return false;
        }
        return true;
    }

    public NBTTagList getTileEntityList(Chunk chunk) {
        NBTTagList tileEntityList = new NBTTagList();
        if (!WDLPluginChannels.canSaveTileEntities(chunk)) {
            return tileEntityList;
        }
        Map<BlockPos, TileEntity> chunkTEMap = chunk.getTileEntityMap();
        Map<BlockPos, NBTTagCompound> oldTEMap = this.getOldTileEntities(chunk);
        Map<BlockPos, TileEntity> newTEMap = WDL.newTileEntities.get(chunk.getChunkCoordIntPair());
        if (newTEMap == null) {
            newTEMap = new HashMap<BlockPos, TileEntity>();
        }
        HashSet<BlockPos> allTELocations = new HashSet<BlockPos>();
        allTELocations.addAll(chunkTEMap.keySet());
        allTELocations.addAll(oldTEMap.keySet());
        allTELocations.addAll(newTEMap.keySet());
        for (BlockPos pos : allTELocations) {
            NBTTagCompound compound;
            if (newTEMap.containsKey(pos)) {
                compound = new NBTTagCompound();
                TileEntity te2 = newTEMap.get(pos);
                try {
                    te2.writeToNBT(compound);
                }
                catch (Exception e2) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveTE", te2, pos, chunk.xPosition, chunk.zPosition, e2);
                    logger.warn("Compound: " + compound);
                    continue;
                }
                String entityType = String.valueOf(compound.getString("id")) + " (" + te2.getClass().getCanonicalName() + ")";
                WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.usingNew", entityType, pos);
                WDLChunkLoader.editTileEntity(pos, compound, ITileEntityEditor.TileEntityCreationMode.NEW);
                tileEntityList.appendTag(compound);
                continue;
            }
            if (oldTEMap.containsKey(pos)) {
                compound = oldTEMap.get(pos);
                String entityType = compound.getString("id");
                WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.usingOld", entityType, pos);
                WDLChunkLoader.editTileEntity(pos, compound, ITileEntityEditor.TileEntityCreationMode.IMPORTED);
                tileEntityList.appendTag(compound);
                continue;
            }
            if (!chunkTEMap.containsKey(pos)) continue;
            TileEntity te3 = chunkTEMap.get(pos);
            NBTTagCompound compound2 = new NBTTagCompound();
            try {
                te3.writeToNBT(compound2);
            }
            catch (Exception e3) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToSaveTE", te3, pos, chunk.xPosition, chunk.zPosition, e3);
                logger.warn("Compound: " + compound2);
                continue;
            }
            WDLChunkLoader.editTileEntity(pos, compound2, ITileEntityEditor.TileEntityCreationMode.EXISTING);
            tileEntityList.appendTag(compound2);
        }
        return tileEntityList;
    }

    public Map<BlockPos, NBTTagCompound> getOldTileEntities(Chunk chunk) {
        HashMap<BlockPos, NBTTagCompound> returned;
        block20: {
            FilterInputStream dis = null;
            returned = new HashMap<BlockPos, NBTTagCompound>();
            try {
                dis = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, chunk.xPosition, chunk.zPosition);
                if (dis == null) {
                    HashMap<BlockPos, NBTTagCompound> hashMap = returned;
                    return hashMap;
                }
                try {
                    NBTTagCompound chunkNBT = CompressedStreamTools.read((DataInputStream)dis);
                    NBTTagCompound levelNBT = chunkNBT.getCompoundTag("Level");
                    NBTTagList oldList = levelNBT.getTagList("TileEntities", 10);
                    if (oldList != null) {
                        int i2 = 0;
                        while (i2 < oldList.tagCount()) {
                            Block block;
                            BlockPos pos;
                            NBTTagCompound oldNBT = oldList.getCompoundTagAt(i2);
                            String entityID = oldNBT.getString("id");
                            if (this.shouldImportTileEntity(entityID, pos = new BlockPos(oldNBT.getInteger("x"), oldNBT.getInteger("y"), oldNBT.getInteger("z")), block = chunk.getBlock(pos), oldNBT, chunk)) {
                                returned.put(pos, oldNBT);
                            } else {
                                WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.notImporting", entityID, pos);
                            }
                            ++i2;
                        }
                    }
                }
                catch (Exception e2) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.generalError.failedToImportTE", chunk.xPosition, chunk.zPosition, e2);
                    if (dis == null) break block20;
                    try {
                        dis.close();
                    }
                    catch (Exception e3) {
                        throw new RuntimeException(e3);
                    }
                }
            }
            finally {
                if (dis != null) {
                    try {
                        dis.close();
                    }
                    catch (Exception e4) {
                        throw new RuntimeException(e4);
                    }
                }
            }
        }
        return returned;
    }

    public boolean shouldImportTileEntity(String entityID, BlockPos pos, Block block, NBTTagCompound tileEntityNBT, Chunk chunk) {
        if (block instanceof BlockChest && entityID.equals("Chest")) {
            return true;
        }
        if (block instanceof BlockDispenser && entityID.equals("Trap")) {
            return true;
        }
        if (block instanceof BlockDropper && entityID.equals("Dropper")) {
            return true;
        }
        if (block instanceof BlockFurnace && entityID.equals("Furnace")) {
            return true;
        }
        if (block instanceof BlockNote && entityID.equals("Music")) {
            return true;
        }
        if (block instanceof BlockBrewingStand && entityID.equals("Cauldron")) {
            return true;
        }
        if (block instanceof BlockHopper && entityID.equals("Hopper")) {
            return true;
        }
        if (block instanceof BlockBeacon && entityID.equals("Beacon")) {
            return true;
        }
        for (WDLApi.ModInfo<ITileEntityImportationIdentifier> info : WDLApi.getImplementingExtensions(ITileEntityImportationIdentifier.class)) {
            if (!((ITileEntityImportationIdentifier)info.mod).shouldImportTileEntity(entityID, pos, block, tileEntityNBT, chunk)) continue;
            return true;
        }
        return false;
    }

    public static void editTileEntity(BlockPos pos, NBTTagCompound compound, ITileEntityEditor.TileEntityCreationMode creationMode) {
        for (WDLApi.ModInfo<ITileEntityEditor> info : WDLApi.getImplementingExtensions(ITileEntityEditor.class)) {
            try {
                if (!((ITileEntityEditor)info.mod).shouldEdit(pos, compound, creationMode)) continue;
                ((ITileEntityEditor)info.mod).editTileEntity(pos, compound, creationMode);
                WDLMessages.chatMessageTranslated(WDLMessageTypes.LOAD_TILE_ENTITY, "wdl.messages.tileEntity.edited", pos, info.getDisplayName());
            }
            catch (Exception ex2) {
                throw new RuntimeException("Failed to edit tile entity at " + pos + " with extension " + info + "; NBT is now " + compound + " (this may be the " + "initial value, an edited value, or a partially " + "edited value)", ex2);
            }
        }
    }
}

