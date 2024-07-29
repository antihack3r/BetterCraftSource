/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenSwamp
extends WorldGenAbstractTree {
    private static final IBlockState field_181648_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
    private static final IBlockState field_181649_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockOldLeaf.CHECK_DECAY, false);

    public WorldGenSwamp() {
        super(false);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = rand.nextInt(4) + 5;
        while (worldIn.getBlockState(position.down()).getBlock().getMaterial() == Material.water) {
            position = position.down();
        }
        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i2 + 1 <= 256) {
            int j2 = position.getY();
            while (j2 <= position.getY() + 1 + i2) {
                int k2 = 1;
                if (j2 == position.getY()) {
                    k2 = 0;
                }
                if (j2 >= position.getY() + 1 + i2 - 2) {
                    k2 = 3;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                int l2 = position.getX() - k2;
                while (l2 <= position.getX() + k2 && flag) {
                    int i1 = position.getZ() - k2;
                    while (i1 <= position.getZ() + k2 && flag) {
                        if (j2 >= 0 && j2 < 256) {
                            Block block = worldIn.getBlockState(blockpos$mutableblockpos.set(l2, j2, i1)).getBlock();
                            if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                                if (block != Blocks.water && block != Blocks.flowing_water) {
                                    flag = false;
                                } else if (j2 > position.getY()) {
                                    flag = false;
                                }
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
            Block block1 = worldIn.getBlockState(position.down()).getBlock();
            if ((block1 == Blocks.grass || block1 == Blocks.dirt) && position.getY() < 256 - i2 - 1) {
                this.func_175921_a(worldIn, position.down());
                int l1 = position.getY() - 3 + i2;
                while (l1 <= position.getY() + i2) {
                    int k2 = l1 - (position.getY() + i2);
                    int i3 = 2 - k2 / 2;
                    int k3 = position.getX() - i3;
                    while (k3 <= position.getX() + i3) {
                        int l3 = k3 - position.getX();
                        int j1 = position.getZ() - i3;
                        while (j1 <= position.getZ() + i3) {
                            BlockPos blockpos;
                            int k1 = j1 - position.getZ();
                            if ((Math.abs(l3) != i3 || Math.abs(k1) != i3 || rand.nextInt(2) != 0 && k2 != 0) && !worldIn.getBlockState(blockpos = new BlockPos(k3, l1, j1)).getBlock().isFullBlock()) {
                                this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181649_b);
                            }
                            ++j1;
                        }
                        ++k3;
                    }
                    ++l1;
                }
                int i22 = 0;
                while (i22 < i2) {
                    Block block2 = worldIn.getBlockState(position.up(i22)).getBlock();
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves || block2 == Blocks.flowing_water || block2 == Blocks.water) {
                        this.setBlockAndNotifyAdequately(worldIn, position.up(i22), field_181648_a);
                    }
                    ++i22;
                }
                int j22 = position.getY() - 3 + i2;
                while (j22 <= position.getY() + i2) {
                    int l2 = j22 - (position.getY() + i2);
                    int j3 = 2 - l2 / 2;
                    BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
                    int i4 = position.getX() - j3;
                    while (i4 <= position.getX() + j3) {
                        int j4 = position.getZ() - j3;
                        while (j4 <= position.getZ() + j3) {
                            blockpos$mutableblockpos1.set(i4, j22, j4);
                            if (worldIn.getBlockState(blockpos$mutableblockpos1).getBlock().getMaterial() == Material.leaves) {
                                BlockPos blockpos3 = blockpos$mutableblockpos1.west();
                                BlockPos blockpos4 = blockpos$mutableblockpos1.east();
                                BlockPos blockpos1 = blockpos$mutableblockpos1.north();
                                BlockPos blockpos2 = blockpos$mutableblockpos1.south();
                                if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockpos3).getBlock().getMaterial() == Material.air) {
                                    this.func_181647_a(worldIn, blockpos3, BlockVine.EAST);
                                }
                                if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockpos4).getBlock().getMaterial() == Material.air) {
                                    this.func_181647_a(worldIn, blockpos4, BlockVine.WEST);
                                }
                                if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockpos1).getBlock().getMaterial() == Material.air) {
                                    this.func_181647_a(worldIn, blockpos1, BlockVine.SOUTH);
                                }
                                if (rand.nextInt(4) == 0 && worldIn.getBlockState(blockpos2).getBlock().getMaterial() == Material.air) {
                                    this.func_181647_a(worldIn, blockpos2, BlockVine.NORTH);
                                }
                            }
                            ++j4;
                        }
                        ++i4;
                    }
                    ++j22;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private void func_181647_a(World p_181647_1_, BlockPos p_181647_2_, PropertyBool p_181647_3_) {
        IBlockState iblockstate = Blocks.vine.getDefaultState().withProperty(p_181647_3_, true);
        this.setBlockAndNotifyAdequately(p_181647_1_, p_181647_2_, iblockstate);
        int i2 = 4;
        p_181647_2_ = p_181647_2_.down();
        while (p_181647_1_.getBlockState(p_181647_2_).getBlock().getMaterial() == Material.air && i2 > 0) {
            this.setBlockAndNotifyAdequately(p_181647_1_, p_181647_2_, iblockstate);
            p_181647_2_ = p_181647_2_.down();
            --i2;
        }
    }
}

