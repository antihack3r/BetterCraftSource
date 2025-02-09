// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.chunk.storage;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.BlockPos;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.nbt.NBTTagCompound;

public class ChunkLoader
{
    public static AnvilConverterData load(final NBTTagCompound nbt) {
        final int i = nbt.getInteger("xPos");
        final int j = nbt.getInteger("zPos");
        final AnvilConverterData chunkloader$anvilconverterdata = new AnvilConverterData(i, j);
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
        catch (final ClassCastException var5) {
            chunkloader$anvilconverterdata.lastUpdated = nbt.getInteger("LastUpdate");
        }
        return chunkloader$anvilconverterdata;
    }
    
    public static void convertToAnvilFormat(final AnvilConverterData p_76690_0_, final NBTTagCompound compound, final WorldChunkManager p_76690_2_) {
        compound.setInteger("xPos", p_76690_0_.x);
        compound.setInteger("zPos", p_76690_0_.z);
        compound.setLong("LastUpdate", p_76690_0_.lastUpdated);
        final int[] aint = new int[p_76690_0_.heightmap.length];
        for (int i = 0; i < p_76690_0_.heightmap.length; ++i) {
            aint[i] = p_76690_0_.heightmap[i];
        }
        compound.setIntArray("HeightMap", aint);
        compound.setBoolean("TerrainPopulated", p_76690_0_.terrainPopulated);
        final NBTTagList nbttaglist = new NBTTagList();
        for (int j = 0; j < 8; ++j) {
            boolean flag = true;
            for (int k = 0; k < 16 && flag; ++k) {
                for (int l = 0; l < 16 && flag; ++l) {
                    for (int i2 = 0; i2 < 16; ++i2) {
                        final int j2 = k << 11 | i2 << 7 | l + (j << 4);
                        final int k2 = p_76690_0_.blocks[j2];
                        if (k2 != 0) {
                            flag = false;
                            break;
                        }
                    }
                }
            }
            if (!flag) {
                final byte[] abyte1 = new byte[4096];
                final NibbleArray nibblearray = new NibbleArray();
                final NibbleArray nibblearray2 = new NibbleArray();
                final NibbleArray nibblearray3 = new NibbleArray();
                for (int j3 = 0; j3 < 16; ++j3) {
                    for (int l2 = 0; l2 < 16; ++l2) {
                        for (int i3 = 0; i3 < 16; ++i3) {
                            final int j4 = j3 << 11 | i3 << 7 | l2 + (j << 4);
                            final int k3 = p_76690_0_.blocks[j4];
                            abyte1[l2 << 8 | i3 << 4 | j3] = (byte)(k3 & 0xFF);
                            nibblearray.set(j3, l2, i3, p_76690_0_.data.get(j3, l2 + (j << 4), i3));
                            nibblearray2.set(j3, l2, i3, p_76690_0_.skyLight.get(j3, l2 + (j << 4), i3));
                            nibblearray3.set(j3, l2, i3, p_76690_0_.blockLight.get(j3, l2 + (j << 4), i3));
                        }
                    }
                }
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte)(j & 0xFF));
                nbttagcompound.setByteArray("Blocks", abyte1);
                nbttagcompound.setByteArray("Data", nibblearray.getData());
                nbttagcompound.setByteArray("SkyLight", nibblearray2.getData());
                nbttagcompound.setByteArray("BlockLight", nibblearray3.getData());
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        compound.setTag("Sections", nbttaglist);
        final byte[] abyte2 = new byte[256];
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int l3 = 0; l3 < 16; ++l3) {
            for (int i4 = 0; i4 < 16; ++i4) {
                blockpos$mutableblockpos.set(p_76690_0_.x << 4 | l3, 0, p_76690_0_.z << 4 | i4);
                abyte2[i4 << 4 | l3] = (byte)(p_76690_2_.getBiomeGenerator(blockpos$mutableblockpos, BiomeGenBase.field_180279_ad).biomeID & 0xFF);
            }
        }
        compound.setByteArray("Biomes", abyte2);
        compound.setTag("Entities", p_76690_0_.entities);
        compound.setTag("TileEntities", p_76690_0_.tileEntities);
        if (p_76690_0_.tileTicks != null) {
            compound.setTag("TileTicks", p_76690_0_.tileTicks);
        }
    }
    
    public static class AnvilConverterData
    {
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
        
        public AnvilConverterData(final int xIn, final int zIn) {
            this.x = xIn;
            this.z = zIn;
        }
    }
}
