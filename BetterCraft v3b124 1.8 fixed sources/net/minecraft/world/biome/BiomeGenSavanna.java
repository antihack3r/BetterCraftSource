/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna
extends BiomeGenBase {
    private static final WorldGenSavannaTree field_150627_aC = new WorldGenSavannaTree(false);

    protected BiomeGenSavanna(int id2) {
        super(id2);
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 1, 2, 6));
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 20;
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return rand.nextInt(5) > 0 ? field_150627_aC : this.worldGeneratorTrees;
    }

    @Override
    protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
        Mutated biomegenbase = new Mutated(p_180277_1_, this);
        biomegenbase.temperature = (this.temperature + 1.0f) * 0.5f;
        biomegenbase.minHeight = this.minHeight * 0.5f + 0.3f;
        biomegenbase.maxHeight = this.maxHeight * 0.5f + 1.2f;
        return biomegenbase;
    }

    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);
        int i2 = 0;
        while (i2 < 7) {
            int j2 = rand.nextInt(16) + 8;
            int k2 = rand.nextInt(16) + 8;
            int l2 = rand.nextInt(worldIn.getHeight(pos.add(j2, 0, k2)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j2, l2, k2));
            ++i2;
        }
        super.decorate(worldIn, rand, pos);
    }

    public static class Mutated
    extends BiomeGenMutated {
        public Mutated(int p_i45382_1_, BiomeGenBase p_i45382_2_) {
            super(p_i45382_1_, p_i45382_2_);
            this.theBiomeDecorator.treesPerChunk = 2;
            this.theBiomeDecorator.flowersPerChunk = 2;
            this.theBiomeDecorator.grassPerChunk = 5;
        }

        @Override
        public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x2, int z2, double noiseVal) {
            this.topBlock = Blocks.grass.getDefaultState();
            this.fillerBlock = Blocks.dirt.getDefaultState();
            if (noiseVal > 1.75) {
                this.topBlock = Blocks.stone.getDefaultState();
                this.fillerBlock = Blocks.stone.getDefaultState();
            } else if (noiseVal > -0.5) {
                this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
            }
            this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x2, z2, noiseVal);
        }

        @Override
        public void decorate(World worldIn, Random rand, BlockPos pos) {
            this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
        }
    }
}

