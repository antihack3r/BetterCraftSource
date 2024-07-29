/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenIcePath
extends WorldGenerator {
    private Block block = Blocks.packed_ice;
    private int basePathWidth;

    public WorldGenIcePath(int p_i45454_1_) {
        this.basePathWidth = p_i45454_1_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.isAirBlock(position) && position.getY() > 2) {
            position = position.down();
        }
        if (worldIn.getBlockState(position).getBlock() != Blocks.snow) {
            return false;
        }
        int i2 = rand.nextInt(this.basePathWidth - 2) + 2;
        int j2 = 1;
        int k2 = position.getX() - i2;
        while (k2 <= position.getX() + i2) {
            int l2 = position.getZ() - i2;
            while (l2 <= position.getZ() + i2) {
                int j1;
                int i1 = k2 - position.getX();
                if (i1 * i1 + (j1 = l2 - position.getZ()) * j1 <= i2 * i2) {
                    int k1 = position.getY() - j2;
                    while (k1 <= position.getY() + j2) {
                        BlockPos blockpos = new BlockPos(k2, k1, l2);
                        Block block = worldIn.getBlockState(blockpos).getBlock();
                        if (block == Blocks.dirt || block == Blocks.snow || block == Blocks.ice) {
                            worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
                        }
                        ++k1;
                    }
                }
                ++l2;
            }
            ++k2;
        }
        return true;
    }
}

