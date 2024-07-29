/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.NibbleArrayReader;

public class ChunkLoader {
    public static AnvilConverterData load(NBTTagCompound nbt) {
        int i2 = nbt.getInteger("xPos");
        int j2 = nbt.getInteger("zPos");
        AnvilConverterData chunkloader$anvilconverterdata = new AnvilConverterData(i2, j2);
        chunkloader$anvilconverterdata.blocks = nbt.getByteArray("Blocks");
        chunkloader$anvilconverterdata.data = new NibbleArrayReader(nbt.getByteArray("Data"), 7);
        chunkloader$anvilconverterdata.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"), 7);
        chunkloader$anvilconverterdata.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"), 7);
        chunkloader$anvilconverterdata.heightmap = nbt.getByteArray("HeightMap");
        chunkloader$anvilconverterdata.terrainPopulated = nbt.getBoolean("TerrainPopulated");
        chunkloader$anvilconverterdata.entities = nbt.getTagList("Entities", 10);
        chunkloader$anvilconverterdata.tileEntities = nbt.getTagList("TileEntities", 10);
        chunkloader$anvilconverterdata.tileTicks = nbt.getTagList("TileTicks", 10);
        try {
            chunkloader$anvilconverterdata.lastUpdated = nbt.getLong("LastUpdate");
        }
        catch (ClassCastException var5) {
            chunkloader$anvilconverterdata.lastUpdated = nbt.getInteger("LastUpdate");
        }
        return chunkloader$anvilconverterdata;
    }

    public static void convertToAnvilFormat(AnvilConverterData p_76690_0_, NBTTagCompound compound, WorldChunkManager p_76690_2_) {
        compound.setInteger("xPos", p_76690_0_.x);
        compound.setInteger("zPos", p_76690_0_.z);
        compound.setLong("LastUpdate", p_76690_0_.lastUpdated);
        int[] aint = new int[p_76690_0_.heightmap.length];
        int i2 = 0;
        while (i2 < p_76690_0_.heightmap.length) {
            aint[i2] = p_76690_0_.heightmap[i2];
            ++i2;
        }
        compound.setIntArray("HeightMap", aint);
        compound.setBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
        NBTTagList nbttaglist = new NBTTagList();
        int j2 = 0;
        while (j2 < 8) {
            boolean flag = true;
            int k2 = 0;
            while (k2 < 16 && flag) {
                int l2 = 0;
                while (l2 < 16 && flag) {
                    int i1 = 0;
                    while (i1 < 16) {
                        int j1 = k2 << 11 | i1 << 7 | l2 + (j2 << 4);
                        byte k1 = p_76690_0_.blocks[j1];
                        if (k1 != 0) {
                            flag = false;
                            break;
                        }
                        ++i1;
                    }
                    ++l2;
                }
                ++k2;
            }
            if (!flag) {
                byte[] abyte1 = new byte[4096];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = new NibbleArray();
                NibbleArray nibblearray2 = new NibbleArray();
                int j3 = 0;
                while (j3 < 16) {
                    int l1 = 0;
                    while (l1 < 16) {
                        int i22 = 0;
                        while (i22 < 16) {
                            int j22 = j3 << 11 | i22 << 7 | l1 + (j2 << 4);
                            byte k22 = p_76690_0_.blocks[j22];
                            abyte1[l1 << 8 | i22 << 4 | j3] = (byte)(k22 & 0xFF);
                            nibblearray.set(j3, l1, i22, p_76690_0_.data.get(j3, l1 + (j2 << 4), i22));
                            nibblearray1.set(j3, l1, i22, p_76690_0_.skyLight.get(j3, l1 + (j2 << 4), i22));
                            nibblearray2.set(j3, l1, i22, p_76690_0_.blockLight.get(j3, l1 + (j2 << 4), i22));
                            ++i22;
                        }
                        ++l1;
                    }
                    ++j3;
                }
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte)(j2 & 0xFF));
                nbttagcompound.setByteArray("Blocks", abyte1);
                nbttagcompound.setByteArray("Data", nibblearray.getData());
                nbttagcompound.setByteArray("SkyLight", nibblearray1.getData());
                nbttagcompound.setByteArray("BlockLight", nibblearray2.getData());
                nbttaglist.appendTag(nbttagcompound);
            }
            ++j2;
        }
        compound.setTag("Sections", nbttaglist);
        byte[] abyte = new byte[256];
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int l2 = 0;
        while (l2 < 16) {
            int i3 = 0;
            while (i3 < 16) {
                blockpos$mutableblockpos.set(p_76690_0_.x << 4 | l2, 0, p_76690_0_.z << 4 | i3);
                abyte[i3 << 4 | l2] = (byte)(p_76690_2_.getBiomeGenerator((BlockPos)blockpos$mutableblockpos, (BiomeGenBase)BiomeGenBase.field_180279_ad).biomeID & 0xFF);
                ++i3;
            }
            ++l2;
        }
        compound.setByteArray("Biomes", abyte);
        compound.setTag("Entities", p_76690_0_.entities);
        compound.setTag("TileEntities", p_76690_0_.tileEntities);
        if (p_76690_0_.tileTicks != null) {
            compound.setTag("TileTicks", p_76690_0_.tileTicks);
        }
    }

    public static class AnvilConverterData {
        public long lastUpdated;
        public boolean terrainPopulated;
        public byte[] heightmap;
        public NibbleArrayReader blockLight;
        public NibbleArrayReader skyLight;
        public NibbleArrayReader data;
        public byte[] blocks;
        public NBTTagList entities;
        public NBTTagList tileEntities;
        public NBTTagList tileTicks;
        public final int x;
        public final int z;

        public AnvilConverterData(int xIn, int zIn) {
            this.x = xIn;
            this.z = zIn;
        }
    }
}

