/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug
implements IChunkProvider {
    private static final List<IBlockState> field_177464_a = Lists.newArrayList();
    private static final int field_177462_b;
    private static final int field_181039_c;
    private final World world;

    static {
        for (Block block : Block.blockRegistry) {
            field_177464_a.addAll(block.getBlockState().getValidStates());
        }
        field_177462_b = MathHelper.ceiling_float_int(MathHelper.sqrt_float(field_177464_a.size()));
        field_181039_c = MathHelper.ceiling_float_int((float)field_177464_a.size() / (float)field_177462_b);
    }

    public ChunkProviderDebug(World worldIn) {
        this.world = worldIn;
    }

    @Override
    public Chunk provideChunk(int x2, int z2) {
        ChunkPrimer chunkprimer = new ChunkPrimer();
        int i2 = 0;
        while (i2 < 16) {
            int j2 = 0;
            while (j2 < 16) {
                int k2 = x2 * 16 + i2;
                int l2 = z2 * 16 + j2;
                chunkprimer.setBlockState(i2, 60, j2, Blocks.barrier.getDefaultState());
                IBlockState iblockstate = ChunkProviderDebug.func_177461_b(k2, l2);
                if (iblockstate != null) {
                    chunkprimer.setBlockState(i2, 70, j2, iblockstate);
                }
                ++j2;
            }
            ++i2;
        }
        Chunk chunk = new Chunk(this.world, chunkprimer, x2, z2);
        chunk.generateSkylightMap();
        BiomeGenBase[] abiomegenbase = this.world.getWorldChunkManager().loadBlockGeneratorData(null, x2 * 16, z2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();
        int i1 = 0;
        while (i1 < abyte.length) {
            abyte[i1] = (byte)abiomegenbase[i1].biomeID;
            ++i1;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    public static IBlockState func_177461_b(int p_177461_0_, int p_177461_1_) {
        int i2;
        IBlockState iblockstate = null;
        if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0 && (p_177461_0_ /= 2) <= field_177462_b && (p_177461_1_ /= 2) <= field_181039_c && (i2 = MathHelper.abs_int(p_177461_0_ * field_177462_b + p_177461_1_)) < field_177464_a.size()) {
            iblockstate = field_177464_a.get(i2);
        }
        return iblockstate;
    }

    @Override
    public boolean chunkExists(int x2, int z2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int x2, int z2) {
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
        return "DebugLevelSource";
    }

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        BiomeGenBase biomegenbase = this.world.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x2, int z2) {
    }

    @Override
    public Chunk provideChunk(BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}

