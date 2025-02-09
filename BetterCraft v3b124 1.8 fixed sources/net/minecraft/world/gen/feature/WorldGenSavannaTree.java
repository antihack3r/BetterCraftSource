/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenSavannaTree
extends WorldGenAbstractTree {
    private static final IBlockState field_181643_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
    private static final IBlockState field_181644_b = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, false);

    public WorldGenSavannaTree(boolean p_i45463_1_) {
        super(p_i45463_1_);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = rand.nextInt(3) + rand.nextInt(3) + 5;
        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i2 + 1 <= 256) {
            int j2 = position.getY();
            while (j2 <= position.getY() + 1 + i2) {
                int k2 = 1;
                if (j2 == position.getY()) {
                    k2 = 0;
                }
                if (j2 >= position.getY() + 1 + i2 - 2) {
                    k2 = 2;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                int l2 = position.getX() - k2;
                while (l2 <= position.getX() + k2 && flag) {
                    int i1 = position.getZ() - k2;
                    while (i1 <= position.getZ() + k2 && flag) {
                        if (j2 >= 0 && j2 < 256) {
                            if (!this.func_150523_a(worldIn.getBlockState(blockpos$mutableblockpos.set(l2, j2, i1)).getBlock())) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                        ++i1;
                    }
                    ++l2;
                }
                ++j2;
            }
            if (!flag) {
                return false;
            }
            Block block = worldIn.getBlockState(position.down()).getBlock();
            if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - i2 - 1) {
                this.func_175921_a(worldIn, position.down());
                EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(rand);
                int k2 = i2 - rand.nextInt(4) - 1;
                int l2 = 3 - rand.nextInt(3);
                int i3 = position.getX();
                int j1 = position.getZ();
                int k1 = 0;
                int l1 = 0;
                while (l1 < i2) {
                    BlockPos blockpos;
                    Material material;
                    int i22 = position.getY() + l1;
                    if (l1 >= k2 && l2 > 0) {
                        i3 += enumfacing.getFrontOffsetX();
                        j1 += enumfacing.getFrontOffsetZ();
                        --l2;
                    }
                    if ((material = worldIn.getBlockState(blockpos = new BlockPos(i3, i22, j1)).getBlock().getMaterial()) == Material.air || material == Material.leaves) {
                        this.func_181642_b(worldIn, blockpos);
                        k1 = i22;
                    }
                    ++l1;
                }
                BlockPos blockpos2 = new BlockPos(i3, k1, j1);
                int j3 = -3;
                while (j3 <= 3) {
                    int i4 = -3;
                    while (i4 <= 3) {
                        if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
                            this.func_175924_b(worldIn, blockpos2.add(j3, 0, i4));
                        }
                        ++i4;
                    }
                    ++j3;
                }
                blockpos2 = blockpos2.up();
                int k3 = -1;
                while (k3 <= 1) {
                    int j4 = -1;
                    while (j4 <= 1) {
                        this.func_175924_b(worldIn, blockpos2.add(k3, 0, j4));
                        ++j4;
                    }
                    ++k3;
                }
                this.func_175924_b(worldIn, blockpos2.east(2));
                this.func_175924_b(worldIn, blockpos2.west(2));
                this.func_175924_b(worldIn, blockpos2.south(2));
                this.func_175924_b(worldIn, blockpos2.north(2));
                i3 = position.getX();
                j1 = position.getZ();
                EnumFacing enumfacing1 = EnumFacing.Plane.HORIZONTAL.random(rand);
                if (enumfacing1 != enumfacing) {
                    int l3 = k2 - rand.nextInt(2) - 1;
                    int k4 = 1 + rand.nextInt(3);
                    k1 = 0;
                    int l4 = l3;
                    while (l4 < i2 && k4 > 0) {
                        if (l4 >= 1) {
                            int j22 = position.getY() + l4;
                            BlockPos blockpos1 = new BlockPos(i3 += enumfacing1.getFrontOffsetX(), j22, j1 += enumfacing1.getFrontOffsetZ());
                            Material material1 = worldIn.getBlockState(blockpos1).getBlock().getMaterial();
                            if (material1 == Material.air || material1 == Material.leaves) {
                                this.func_181642_b(worldIn, blockpos1);
                                k1 = j22;
                            }
                        }
                        ++l4;
                        --k4;
                    }
                    if (k1 > 0) {
                        BlockPos blockpos3 = new BlockPos(i3, k1, j1);
                        int i5 = -2;
                        while (i5 <= 2) {
                            int k5 = -2;
                            while (k5 <= 2) {
                                if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
                                    this.func_175924_b(worldIn, blockpos3.add(i5, 0, k5));
                                }
                                ++k5;
                            }
                            ++i5;
                        }
                        blockpos3 = blockpos3.up();
                        int j5 = -1;
                        while (j5 <= 1) {
                            int l5 = -1;
                            while (l5 <= 1) {
                                this.func_175924_b(worldIn, blockpos3.add(j5, 0, l5));
                                ++l5;
                            }
                            ++j5;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private void func_181642_b(World p_181642_1_, BlockPos p_181642_2_) {
        this.setBlockAndNotifyAdequately(p_181642_1_, p_181642_2_, field_181643_a);
    }

    private void func_175924_b(World worldIn, BlockPos p_175924_2_) {
        Material material = worldIn.getBlockState(p_175924_2_).getBlock().getMaterial();
        if (material == Material.air || material == Material.leaves) {
            this.setBlockAndNotifyAdequately(worldIn, p_175924_2_, field_181644_b);
        }
    }
}

