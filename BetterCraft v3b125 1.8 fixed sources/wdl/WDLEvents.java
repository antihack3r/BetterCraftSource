/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import wdl.EntityUtils;
import wdl.ReflectionUtils;
import wdl.WDL;
import wdl.WDLMessageTypes;
import wdl.WDLMessages;
import wdl.WDLPluginChannels;
import wdl.api.IWorldLoadListener;
import wdl.api.WDLApi;
import wdl.update.WDLUpdateChecker;

public class WDLEvents {
    private static final Profiler profiler = Minecraft.getMinecraft().mcProfiler;

    public static void onWorldLoad(WorldClient world) {
        profiler.startSection("Core");
        if (WDL.minecraft.isIntegratedServerRunning()) {
            return;
        }
        if (WDL.downloading) {
            if (!WDL.saving) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.worldChanged", new Object[0]);
                WDL.worldLoadingDeferred = true;
                WDL.startSaveThread();
            }
            profiler.endSection();
            return;
        }
        boolean sameServer = WDL.loadWorld();
        WDLUpdateChecker.startIfNeeded();
        profiler.endSection();
        for (WDLApi.ModInfo<IWorldLoadListener> info : WDLApi.getImplementingExtensions(IWorldLoadListener.class)) {
            profiler.startSection(info.id);
            ((IWorldLoadListener)info.mod).onWorldLoad(world, sameServer);
            profiler.endSection();
        }
    }

    public static void onChunkNoLongerNeeded(Chunk unneededChunk) {
        if (!WDL.downloading) {
            return;
        }
        if (unneededChunk == null) {
            return;
        }
        if (WDLPluginChannels.canSaveChunk(unneededChunk)) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_CHUNK_NO_LONGER_NEEDED, "wdl.messages.onChunkNoLongerNeeded.saved", unneededChunk.xPosition, unneededChunk.zPosition);
            WDL.saveChunk(unneededChunk);
        } else {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_CHUNK_NO_LONGER_NEEDED, "wdl.messages.onChunkNoLongerNeeded.didNotSave", unneededChunk.xPosition, unneededChunk.zPosition);
        }
    }

    public static void onItemGuiOpened() {
        if (!WDL.downloading) {
            return;
        }
        if (WDL.minecraft.objectMouseOver == null) {
            return;
        }
        if (WDL.minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            WDL.lastEntity = WDL.minecraft.objectMouseOver.entityHit;
        } else {
            WDL.lastEntity = null;
            WDL.lastClickedBlock = WDL.minecraft.objectMouseOver.getBlockPos();
        }
    }

    public static boolean onItemGuiClosed() {
        EntityHorse horseInContainer;
        if (!WDL.downloading) {
            return true;
        }
        String saveName = "";
        if (WDL.thePlayer.ridingEntity != null && WDL.thePlayer.ridingEntity instanceof EntityHorse && WDL.windowContainer instanceof ContainerHorseInventory && (horseInContainer = ReflectionUtils.stealAndGetField(WDL.windowContainer, EntityHorse.class)) == WDL.thePlayer.ridingEntity) {
            if (!WDLPluginChannels.canSaveEntities(horseInContainer.chunkCoordX, horseInContainer.chunkCoordZ)) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.cannotSaveEntities", new Object[0]);
                return true;
            }
            EntityHorse entityHorse = (EntityHorse)WDL.thePlayer.ridingEntity;
            AnimalChest horseChest = new AnimalChest("HorseChest", entityHorse.isChested() && (entityHorse.getHorseType() == 1 || entityHorse.getHorseType() == 2) ? 17 : 2);
            horseChest.setCustomName(entityHorse.getName());
            WDL.saveContainerItems(WDL.windowContainer, horseChest, 0);
            ReflectionUtils.stealAndSetField(entityHorse, AnimalChest.class, horseChest);
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedRiddenHorse", new Object[0]);
            return true;
        }
        if (WDL.lastEntity != null) {
            if (!WDLPluginChannels.canSaveEntities(WDL.lastEntity.chunkCoordX, WDL.lastEntity.chunkCoordZ)) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.cannotSaveEntities", new Object[0]);
                return true;
            }
            if (WDL.lastEntity instanceof EntityMinecartChest && WDL.windowContainer instanceof ContainerChest) {
                EntityMinecartChest emcc = (EntityMinecartChest)WDL.lastEntity;
                int i2 = 0;
                while (i2 < emcc.getSizeInventory()) {
                    emcc.setInventorySlotContents(i2, WDL.windowContainer.getSlot(i2).getStack());
                    ++i2;
                }
                saveName = "storageMinecart";
            } else if (WDL.lastEntity instanceof EntityMinecartHopper && WDL.windowContainer instanceof ContainerHopper) {
                EntityMinecartHopper emch = (EntityMinecartHopper)WDL.lastEntity;
                int i3 = 0;
                while (i3 < emch.getSizeInventory()) {
                    emch.setInventorySlotContents(i3, WDL.windowContainer.getSlot(i3).getStack());
                    ++i3;
                }
                saveName = "hopperMinecart";
            } else if (WDL.lastEntity instanceof EntityVillager && WDL.windowContainer instanceof ContainerMerchant) {
                EntityVillager ev2 = (EntityVillager)WDL.lastEntity;
                MerchantRecipeList list = ReflectionUtils.stealAndGetField(WDL.windowContainer, IMerchant.class).getRecipes(WDL.thePlayer);
                ReflectionUtils.stealAndSetField(ev2, MerchantRecipeList.class, list);
                saveName = "villager";
            } else if (WDL.lastEntity instanceof EntityHorse && WDL.windowContainer instanceof ContainerHorseInventory) {
                EntityHorse entityHorse = (EntityHorse)WDL.lastEntity;
                AnimalChest horseChest = new AnimalChest("HorseChest", entityHorse.isChested() && (entityHorse.getHorseType() == 1 || entityHorse.getHorseType() == 2) ? 17 : 2);
                horseChest.setCustomName(entityHorse.getName());
                WDL.saveContainerItems(WDL.windowContainer, horseChest, 0);
                ReflectionUtils.stealAndSetField(entityHorse, AnimalChest.class, horseChest);
                saveName = "horse";
            } else {
                return false;
            }
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedEntity." + saveName, new Object[0]);
            return true;
        }
        TileEntity te2 = WDL.worldClient.getTileEntity(WDL.lastClickedBlock);
        if (te2 == null) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_WARNING, "wdl.messages.onGuiClosedWarning.couldNotGetTE", WDL.lastClickedBlock);
            return true;
        }
        if (!WDLPluginChannels.canSaveContainers(te2.getPos().getX() << 4, te2.getPos().getZ() << 4)) {
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.cannotSaveTileEntities", new Object[0]);
            return true;
        }
        if (WDL.windowContainer instanceof ContainerChest && te2 instanceof TileEntityChest) {
            if (WDL.windowContainer.inventorySlots.size() > 63) {
                BlockPos pos1 = WDL.lastClickedBlock;
                TileEntity te1 = te2;
                BlockPos chestPos1 = null;
                BlockPos chestPos2 = null;
                TileEntityChest chest1 = null;
                TileEntityChest chest2 = null;
                BlockPos pos2 = pos1.add(0, 0, 1);
                TileEntity te22 = WDL.worldClient.getTileEntity(pos2);
                if (te22 instanceof TileEntityChest && ((TileEntityChest)te22).getChestType() == ((TileEntityChest)te1).getChestType()) {
                    chest1 = (TileEntityChest)te1;
                    chest2 = (TileEntityChest)te22;
                    chestPos1 = pos1;
                    chestPos2 = pos2;
                }
                if ((te22 = WDL.worldClient.getTileEntity(pos2 = pos1.add(0, 0, -1))) instanceof TileEntityChest && ((TileEntityChest)te22).getChestType() == ((TileEntityChest)te1).getChestType()) {
                    chest1 = (TileEntityChest)te22;
                    chest2 = (TileEntityChest)te1;
                    chestPos1 = pos2;
                    chestPos2 = pos1;
                }
                if ((te22 = WDL.worldClient.getTileEntity(pos2 = pos1.add(1, 0, 0))) instanceof TileEntityChest && ((TileEntityChest)te22).getChestType() == ((TileEntityChest)te1).getChestType()) {
                    chest1 = (TileEntityChest)te1;
                    chest2 = (TileEntityChest)te22;
                    chestPos1 = pos1;
                    chestPos2 = pos2;
                }
                if ((te22 = WDL.worldClient.getTileEntity(pos2 = pos1.add(-1, 0, 0))) instanceof TileEntityChest && ((TileEntityChest)te22).getChestType() == ((TileEntityChest)te1).getChestType()) {
                    chest1 = (TileEntityChest)te22;
                    chest2 = (TileEntityChest)te1;
                    chestPos1 = pos2;
                    chestPos2 = pos1;
                }
                if (chest1 == null || chest2 == null || chestPos1 == null || chestPos2 == null) {
                    WDLMessages.chatMessageTranslated(WDLMessageTypes.ERROR, "wdl.messages.onGuiClosedWarning.failedToFindDoubleChest", new Object[0]);
                    return true;
                }
                WDL.saveContainerItems(WDL.windowContainer, chest1, 0);
                WDL.saveContainerItems(WDL.windowContainer, chest2, 27);
                WDL.saveTileEntity(chestPos1, chest1);
                WDL.saveTileEntity(chestPos2, chest2);
                saveName = "doubleChest";
            } else {
                WDL.saveContainerItems(WDL.windowContainer, (TileEntityChest)te2, 0);
                WDL.saveTileEntity(WDL.lastClickedBlock, te2);
                saveName = "singleChest";
            }
        } else if (WDL.windowContainer instanceof ContainerChest && te2 instanceof TileEntityEnderChest) {
            InventoryEnderChest inventoryEnderChest = WDL.thePlayer.getInventoryEnderChest();
            int inventorySize = inventoryEnderChest.getSizeInventory();
            int containerSize = WDL.windowContainer.inventorySlots.size();
            int i4 = 0;
            while (i4 < containerSize && i4 < inventorySize) {
                inventoryEnderChest.setInventorySlotContents(i4, WDL.windowContainer.getSlot(i4).getStack());
                ++i4;
            }
            saveName = "enderChest";
        } else if (WDL.windowContainer instanceof ContainerBrewingStand && te2 instanceof TileEntityBrewingStand) {
            IInventory brewingInventory = ReflectionUtils.stealAndGetField(WDL.windowContainer, IInventory.class);
            WDL.saveContainerItems(WDL.windowContainer, (TileEntityBrewingStand)te2, 0);
            WDL.saveInventoryFields(brewingInventory, (TileEntityBrewingStand)te2);
            WDL.saveTileEntity(WDL.lastClickedBlock, te2);
            saveName = "brewingStand";
        } else if (WDL.windowContainer instanceof ContainerDispenser && te2 instanceof TileEntityDispenser) {
            WDL.saveContainerItems(WDL.windowContainer, (TileEntityDispenser)te2, 0);
            WDL.saveTileEntity(WDL.lastClickedBlock, te2);
            saveName = "dispenser";
        } else if (WDL.windowContainer instanceof ContainerFurnace && te2 instanceof TileEntityFurnace) {
            IInventory furnaceInventory = ReflectionUtils.stealAndGetField(WDL.windowContainer, IInventory.class);
            WDL.saveContainerItems(WDL.windowContainer, (TileEntityFurnace)te2, 0);
            WDL.saveInventoryFields(furnaceInventory, (TileEntityFurnace)te2);
            WDL.saveTileEntity(WDL.lastClickedBlock, te2);
            saveName = "furnace";
        } else if (WDL.windowContainer instanceof ContainerHopper && te2 instanceof TileEntityHopper) {
            WDL.saveContainerItems(WDL.windowContainer, (TileEntityHopper)te2, 0);
            WDL.saveTileEntity(WDL.lastClickedBlock, te2);
            saveName = "hopper";
        } else if (WDL.windowContainer instanceof ContainerBeacon && te2 instanceof TileEntityBeacon) {
            IInventory beaconInventory = ((ContainerBeacon)WDL.windowContainer).func_180611_e();
            TileEntityBeacon savedBeacon = (TileEntityBeacon)te2;
            WDL.saveContainerItems(WDL.windowContainer, savedBeacon, 0);
            WDL.saveInventoryFields(beaconInventory, savedBeacon);
            WDL.saveTileEntity(WDL.lastClickedBlock, te2);
            saveName = "beacon";
        } else {
            return false;
        }
        WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_GUI_CLOSED_INFO, "wdl.messages.onGuiClosedInfo.savedTileEntity." + saveName, new Object[0]);
        return true;
    }

    public static void onBlockEvent(BlockPos pos, Block block, int event, int param) {
        if (!WDL.downloading) {
            return;
        }
        if (!WDLPluginChannels.canSaveTileEntities(pos.getX() << 4, pos.getZ() << 4)) {
            return;
        }
        if (block == Blocks.noteblock) {
            TileEntityNote newTE = new TileEntityNote();
            newTE.note = (byte)(param % 25);
            WDL.worldClient.setTileEntity(pos, newTE);
            WDL.saveTileEntity(pos, newTE);
            WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_BLOCK_EVENT, "wdl.messages.onBlockEvent.noteblock", pos, param, newTE);
        }
    }

    public static void onMapDataLoaded(int mapID, MapData mapData) {
        if (!WDL.downloading) {
            return;
        }
        if (!WDLPluginChannels.canSaveMaps()) {
            return;
        }
        WDL.newMapDatas.put(mapID, mapData);
        WDLMessages.chatMessageTranslated(WDLMessageTypes.ON_MAP_SAVED, "wdl.messages.onMapSaved", mapID);
    }

    public static void onPluginChannelPacket(String channel, byte[] bytes) {
        WDLPluginChannels.onPluginChannelPacket(channel, bytes);
    }

    public static void onRemoveEntityFromWorld(Entity entity) {
        if (WDL.downloading && entity != null && WDLPluginChannels.canSaveEntities(entity.chunkCoordX, entity.chunkCoordZ)) {
            if (!EntityUtils.isEntityEnabled(entity)) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.allowingRemoveUserPref", entity);
                return;
            }
            int threshold = EntityUtils.getEntityTrackDistance(entity);
            if (threshold < 0) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.allowingRemoveUnrecognizedDistance", entity);
                return;
            }
            double distance = entity.getDistance(WDL.thePlayer.posX, entity.posY, WDL.thePlayer.posZ);
            if (distance > (double)threshold) {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.savingDistance", entity, distance, threshold);
                entity.chunkCoordX = MathHelper.floor_double(entity.posX / 16.0);
                entity.chunkCoordZ = MathHelper.floor_double(entity.posZ / 16.0);
                WDL.newEntities.put((Object)new ChunkCoordIntPair(entity.chunkCoordX, entity.chunkCoordZ), (Object)entity);
                return;
            }
            WDLMessages.chatMessageTranslated(WDLMessageTypes.REMOVE_ENTITY, "wdl.messages.removeEntity.allowingRemoveDistance", entity, distance, threshold);
        }
    }

    public static void onChatMessage(String msg) {
        if (WDL.downloading && msg.startsWith("Seed: ")) {
            String seed = msg.substring(6);
            WDL.worldProps.setProperty("RandomSeed", seed);
            if (WDL.worldProps.getProperty("MapGenerator", "void").equals("void")) {
                WDL.worldProps.setProperty("MapGenerator", "default");
                WDL.worldProps.setProperty("GeneratorName", "default");
                WDL.worldProps.setProperty("GeneratorVersion", "1");
                WDL.worldProps.setProperty("GeneratorOptions", "");
                WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.seedAndGenSet", seed);
            } else {
                WDLMessages.chatMessageTranslated(WDLMessageTypes.INFO, "wdl.messages.generalInfo.seedSet", seed);
            }
        }
    }
}

