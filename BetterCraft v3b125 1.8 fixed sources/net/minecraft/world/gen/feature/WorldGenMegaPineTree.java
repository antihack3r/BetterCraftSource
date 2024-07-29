/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;

public class WorldGenMegaPineTree
extends WorldGenHugeTrees {
    private static final IBlockState field_181633_e = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState field_181634_f = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);
    private static final IBlockState field_181635_g = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
    private boolean useBaseHeight;

    public WorldGenMegaPineTree(boolean p_i45457_1_, boolean p_i45457_2_) {
        super(p_i45457_1_, 13, 15, field_181633_e, field_181634_f);
        this.useBaseHeight = p_i45457_2_;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = this.func_150533_a(rand);
        if (!this.func_175929_a(worldIn, rand, position, i2)) {
            return false;
        }
        this.func_150541_c(worldIn, position.getX(), position.getZ(), position.getY() + i2, 0, rand);
        int j2 = 0;
        while (j2 < i2) {
            Block block = worldIn.getBlockState(position.up(j2)).getBlock();
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                this.setBlockAndNotifyAdequately(worldIn, position.up(j2), this.woodMetadata);
            }
            if (j2 < i2 - 1) {
                block = worldIn.getBlockState(position.add(1, j2, 0)).getBlock();
                if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(worldIn, position.add(1, j2, 0), this.woodMetadata);
                }
                if ((block = worldIn.getBlockState(position.add(1, j2, 1)).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(worldIn, position.add(1, j2, 1), this.woodMetadata);
                }
                if ((block = worldIn.getBlockState(position.add(0, j2, 1)).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
                    this.setBlockAndNotifyAdequately(worldIn, position.add(0, j2, 1), this.woodMetadata);
                }
            }
            ++j2;
        }
        return true;
    }

    private void func_150541_c(World worldIn, int p_150541_2_, int p_150541_3_, int p_150541_4_, int p_150541_5_, Random p_150541_6_) {
        int i2 = p_150541_6_.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
        int j2 = 0;
        int k2 = p_150541_4_ - i2;
        while (k2 <= p_150541_4_) {
            int l2 = p_150541_4_ - k2;
            int i1 = p_150541_5_ + MathHelper.floor_float((float)l2 / (float)i2 * 3.5f);
            this.func_175925_a(worldIn, new BlockPos(p_150541_2_, k2, p_150541_3_), i1 + (l2 > 0 && i1 == j2 && (k2 & 1) == 0 ? 1 : 0));
            j2 = i1;
            ++k2;
        }
    }

    @Override
    public void func_180711_a(World worldIn, Random p_180711_2_, BlockPos p_180711_3_) {
        this.func_175933_b(worldIn, p_180711_3_.west().north());
        this.func_175933_b(worldIn, p_180711_3_.east(2).north());
        this.func_175933_b(worldIn, p_180711_3_.west().south(2));
        this.func_175933_b(worldIn, p_180711_3_.east(2).south(2));
        int i2 = 0;
        while (i2 < 5) {
            int j2 = p_180711_2_.nextInt(64);
            int k2 = j2 % 8;
            int l2 = j2 / 8;
            if (k2 == 0 || k2 == 7 || l2 == 0 || l2 == 7) {
                this.func_175933_b(worldIn, p_180711_3_.add(-3 + k2, 0, -3 + l2));
            }
            ++i2;
        }
    }

    private void func_175933_b(World worldIn, BlockPos p_175933_2_) {
        int i2 = -2;
        while (i2 <= 2) {
            int j2 = -2;
            while (j2 <= 2) {
                if (Math.abs(i2) != 2 || Math.abs(j2) != 2) {
                    this.func_175934_c(worldIn, p_175933_2_.add(i2, 0, j2));
                }
                ++j2;
            }
            ++i2;
        }
    }

    private void func_175934_c(World worldIn, BlockPos p_175934_2_) {
        int i2 = 2;
        while (i2 >= -3) {
            BlockPos blockpos = p_175934_2_.up(i2);
            Block block = worldIn.getBlockState(blockpos).getBlock();
            if (block == Blocks.grass || block == Blocks.dirt) {
                this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181635_g);
                break;
            }
            if (block.getMaterial() != Material.air && i2 < 0) break;
            --i2;
        }
    }
}

