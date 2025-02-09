// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.biome;

import net.minecraft.init.Blocks;

public class BiomeGenBeach extends BiomeGenBase
{
    public BiomeGenBeach(final int id) {
        super(id);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState();
        this.fillerBlock = Blocks.sand.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.reedsPerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
    }
}
