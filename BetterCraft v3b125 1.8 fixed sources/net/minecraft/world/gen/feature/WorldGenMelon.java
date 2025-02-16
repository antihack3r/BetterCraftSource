/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMelon
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = 0;
        while (i2 < 64) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (Blocks.melon_block.canPlaceBlockAt(worldIn, blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.grass) {
                worldIn.setBlockState(blockpos, Blocks.melon_block.getDefaultState(), 2);
            }
            ++i2;
        }
        return true;
    }
}

