/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenGlowStone2
extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (!worldIn.isAirBlock(position)) {
            return false;
        }
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack) {
            return false;
        }
        worldIn.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);
        int i2 = 0;
        while (i2 < 1500) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                int j2 = 0;
                EnumFacing[] enumFacingArray = EnumFacing.values();
                int n2 = enumFacingArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    EnumFacing enumfacing = enumFacingArray[n3];
                    if (worldIn.getBlockState(blockpos.offset(enumfacing)).getBlock() == Blocks.glowstone) {
                        ++j2;
                    }
                    if (j2 > 1) break;
                    ++n3;
                }
                if (j2 == 1) {
                    worldIn.setBlockState(blockpos, Blocks.glowstone.getDefaultState(), 2);
                }
            }
            ++i2;
        }
        return true;
    }
}

