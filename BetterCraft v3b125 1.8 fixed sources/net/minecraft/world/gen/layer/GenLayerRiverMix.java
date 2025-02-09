/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRiverMix
extends GenLayer {
    private GenLayer biomePatternGeneratorChain;
    private GenLayer riverPatternGeneratorChain;

    public GenLayerRiverMix(long p_i2129_1_, GenLayer p_i2129_3_, GenLayer p_i2129_4_) {
        super(p_i2129_1_);
        this.biomePatternGeneratorChain = p_i2129_3_;
        this.riverPatternGeneratorChain = p_i2129_4_;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        this.biomePatternGeneratorChain.initWorldGenSeed(seed);
        this.riverPatternGeneratorChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint1 = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint2 = IntCache.getIntCache(areaWidth * areaHeight);
        int i2 = 0;
        while (i2 < areaWidth * areaHeight) {
            aint2[i2] = aint[i2] != BiomeGenBase.ocean.biomeID && aint[i2] != BiomeGenBase.deepOcean.biomeID ? (aint1[i2] == BiomeGenBase.river.biomeID ? (aint[i2] == BiomeGenBase.icePlains.biomeID ? BiomeGenBase.frozenRiver.biomeID : (aint[i2] != BiomeGenBase.mushroomIsland.biomeID && aint[i2] != BiomeGenBase.mushroomIslandShore.biomeID ? aint1[i2] & 0xFF : BiomeGenBase.mushroomIslandShore.biomeID)) : aint[i2]) : aint[i2];
            ++i2;
        }
        return aint2;
    }
}

