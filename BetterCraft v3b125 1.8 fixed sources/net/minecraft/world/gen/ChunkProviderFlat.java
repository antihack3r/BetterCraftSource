/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat
implements IChunkProvider {
    private World worldObj;
    private Random random;
    private final IBlockState[] cachedBlockIDs = new IBlockState[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final List<MapGenStructure> structureGenerators = Lists.newArrayList();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;

    public ChunkProviderFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
        this.worldObj = worldIn;
        this.random = new Random(seed);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);
        if (generateStructures) {
            Map<String, Map<String, String>> map = this.flatWorldGenInfo.getWorldFeatures();
            if (map.containsKey("village")) {
                Map<String, String> map1 = map.get("village");
                if (!map1.containsKey("size")) {
                    map1.put("size", "1");
                }
                this.structureGenerators.add(new MapGenVillage(map1));
            }
            if (map.containsKey("biome_1")) {
                this.structureGenerators.add(new MapGenScatteredFeature(map.get("biome_1")));
            }
            if (map.containsKey("mineshaft")) {
                this.structureGenerators.add(new MapGenMineshaft(map.get("mineshaft")));
            }
            if (map.containsKey("stronghold")) {
                this.structureGenerators.add(new MapGenStronghold(map.get("stronghold")));
            }
            if (map.containsKey("oceanmonument")) {
                this.structureGenerators.add(new StructureOceanMonument(map.get("oceanmonument")));
            }
        }
        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
            this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
        }
        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
            this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
        }
        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        int j2 = 0;
        int k2 = 0;
        boolean flag = true;
        for (FlatLayerInfo flatlayerinfo : this.flatWorldGenInfo.getFlatLayers()) {
            int i2 = flatlayerinfo.getMinY();
            while (i2 < flatlayerinfo.getMinY() + flatlayerinfo.getLayerCount()) {
                IBlockState iblockstate = flatlayerinfo.getLayerMaterial();
                if (iblockstate.getBlock() != Blocks.air) {
                    flag = false;
                    this.cachedBlockIDs[i2] = iblockstate;
                }
                ++i2;
            }
            if (flatlayerinfo.getLayerMaterial().getBlock() == Blocks.air) {
                k2 += flatlayerinfo.getLayerCount();
                continue;
            }
            j2 += flatlayerinfo.getLayerCount() + k2;
            k2 = 0;
        }
        worldIn.setSeaLevel(j2);
        this.hasDecoration = flag ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public Chunk provideChunk(int x2, int z2) {
        void var4_5;
        ChunkPrimer chunkprimer = new ChunkPrimer();
        boolean bl2 = false;
        while (var4_5 < this.cachedBlockIDs.length) {
            IBlockState iblockstate = this.cachedBlockIDs[var4_5];
            if (iblockstate != null) {
                int j2 = 0;
                while (j2 < 16) {
                    int k2 = 0;
                    while (k2 < 16) {
                        chunkprimer.setBlockState(j2, (int)var4_5, k2, iblockstate);
                        ++k2;
                    }
                    ++j2;
                }
            }
            ++var4_5;
        }
        for (MapGenBase mapGenBase : this.structureGenerators) {
            mapGenBase.generate(this, this.worldObj, x2, z2, chunkprimer);
        }
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x2, z2);
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, x2 * 16, z2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();
        int l2 = 0;
        while (l2 < abyte.length) {
            abyte[l2] = (byte)abiomegenbase[l2].biomeID;
            ++l2;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
        BlockPos blockpos1;
        int i2 = x2 * 16;
        int j2 = z2 * 16;
        BlockPos blockpos = new BlockPos(i2, 0, j2);
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(i2 + 16, 0, j2 + 16));
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        long k2 = this.random.nextLong() / 2L * 2L + 1L;
        long l2 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)x2 * k2 + (long)z2 * l2 ^ this.worldObj.getSeed());
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x2, z2);
        for (MapGenStructure mapgenstructure : this.structureGenerators) {
            boolean flag1 = mapgenstructure.generateStructure(this.worldObj, this.random, chunkcoordintpair);
            if (!(mapgenstructure instanceof MapGenVillage)) continue;
            flag |= flag1;
        }
        if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
            this.waterLakeGenerator.generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
        }
        if (!(this.lavaLakeGenerator == null || flag || this.random.nextInt(8) != 0 || (blockpos1 = blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8)).getY() >= this.worldObj.getSeaLevel() && this.random.nextInt(10) != 0)) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, blockpos1);
        }
        if (this.hasDungeons) {
            int i1 = 0;
            while (i1 < 8) {
                new WorldGenDungeons().generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
                ++i1;
            }
        }
        if (this.hasDecoration) {
            biomegenbase.decorate(this.worldObj, this.random, blockpos);
        }
    }

    @Override
    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x2, int z2) {
        return false;
    }

    @Override
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "FlatLevelSource";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        if ("Stronghold".equals(structureName)) {
            for (MapGenStructure mapgenstructure : this.structureGenerators) {
                if (!(mapgenstructure instanceof MapGenStronghold)) continue;
                return mapgenstructure.getClosestStrongholdPos(worldIn, position);
            }
        }
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
        for (MapGenStructure mapgenstructure : this.structureGenerators) {
            mapgenstructure.generate(this, this.worldObj, x2, z2, null);
        }
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

