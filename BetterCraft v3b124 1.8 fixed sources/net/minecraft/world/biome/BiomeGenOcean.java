/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeGenOcean
extends BiomeGenBase {
    public BiomeGenOcean(int id2) {
        super(id2);
        this.spawnableCreatureList.clear();
    }

    @Override
    public BiomeGenBase.TempCategory getTempCategory() {
        return BiomeGenBase.TempCategory.OCEAN;
    }

    @Override
    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x2, int z2, double noiseVal) {
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x2, z2, noiseVal);
    }
}

