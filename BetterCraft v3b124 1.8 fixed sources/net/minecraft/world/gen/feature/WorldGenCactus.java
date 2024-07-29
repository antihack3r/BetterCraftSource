/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenCactus
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = 0;
        while (i2 < 10) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos)) {
                int j2 = 1 + rand.nextInt(rand.nextInt(3) + 1);
                int k2 = 0;
                while (k2 < j2) {
                    if (Blocks.cactus.canBlockStay(worldIn, blockpos)) {
                        worldIn.setBlockState(blockpos.up(k2), Blocks.cactus.getDefaultState(), 2);
                    }
                    ++k2;
                }
            }
            ++i2;
        }
        return true;
    }
}

