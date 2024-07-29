/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills
extends BiomeGenBase {
    private WorldGenerator theWorldGenerator = new WorldGenMinable(Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONE), 9);
    private WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
    private int field_150635_aE = 0;
    private int field_150636_aF = 1;
    private int field_150637_aG = 2;
    private int field_150638_aH = this.field_150635_aE;

    protected BiomeGenHills(int id2, boolean p_i45373_2_) {
        super(id2);
        if (p_i45373_2_) {
            this.theBiomeDecorator.treesPerChunk = 3;
            this.field_150638_aH = this.field_150636_aF;
        }
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return rand.nextInt(3) > 0 ? this.field_150634_aD : super.genBigTreeChance(rand);
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
        int i2 = 3 + rand.nextInt(6);
        int j2 = 0;
        while (j2 < i2) {
            int i1;
            int l2;
            int k2 = rand.nextInt(16);
            BlockPos blockpos = pos.add(k2, l2 = rand.nextInt(28) + 4, i1 = rand.nextInt(16));
            if (worldIn.getBlockState(blockpos).getBlock() == Blocks.stone) {
                worldIn.setBlockState(blockpos, Blocks.emerald_ore.getDefaultState(), 2);
            }
            ++j2;
        }
        i2 = 0;
        while (i2 < 7) {
            int j1 = rand.nextInt(16);
            int k1 = rand.nextInt(64);
            int l1 = rand.nextInt(16);
            this.theWorldGenerator.generate(worldIn, rand, pos.add(j1, k1, l1));
            ++i2;
        }
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x2, int z2, double noiseVal) {
        this.topBlock = Blocks.grass.getDefaultState();
        this.fillerBlock = Blocks.dirt.getDefaultState();
        if ((noiseVal < -1.0 || noiseVal > 2.0) && this.field_150638_aH == this.field_150637_aG) {
            this.topBlock = Blocks.gravel.getDefaultState();
            this.fillerBlock = Blocks.gravel.getDefaultState();
        } else if (noiseVal > 1.0 && this.field_150638_aH != this.field_150636_aF) {
            this.topBlock = Blocks.stone.getDefaultState();
            this.fillerBlock = Blocks.stone.getDefaultState();
        }
        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x2, z2, noiseVal);
    }

    private BiomeGenHills mutateHills(BiomeGenBase p_150633_1_) {
        this.field_150638_aH = this.field_150637_aG;
        this.func_150557_a(p_150633_1_.color, true);
        this.setBiomeName(String.valueOf(p_150633_1_.biomeName) + " M");
        this.setHeight(new BiomeGenBase.Height(p_150633_1_.minHeight, p_150633_1_.maxHeight));
        this.setTemperatureRainfall(p_150633_1_.temperature, p_150633_1_.rainfall);
        return this;
    }

    @Override
    protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
        return new BiomeGenHills(p_180277_1_, false).mutateHills(this);
    }
}

