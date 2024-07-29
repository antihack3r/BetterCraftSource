/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenTaiga2
extends WorldGenAbstractTree {
    private static final IBlockState field_181645_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState field_181646_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, false);

    public WorldGenTaiga2(boolean p_i2025_1_) {
        super(p_i2025_1_);
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int i2 = rand.nextInt(4) + 6;
        int j2 = 1 + rand.nextInt(2);
        int k2 = i2 - j2;
        int l2 = 2 + rand.nextInt(2);
        boolean flag = true;
        if (position.getY() >= 1 && position.getY() + i2 + 1 <= 256) {
            int i1 = position.getY();
            while (i1 <= position.getY() + 1 + i2 && flag) {
                int j1 = 1;
                j1 = i1 - position.getY() < j2 ? 0 : l2;
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                int k1 = position.getX() - j1;
                while (k1 <= position.getX() + j1 && flag) {
                    int l1 = position.getZ() - j1;
                    while (l1 <= position.getZ() + j1 && flag) {
                        if (i1 >= 0 && i1 < 256) {
                            Block block = worldIn.getBlockState(blockpos$mutableblockpos.set(k1, i1, l1)).getBlock();
                            if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                        ++l1;
                    }
                    ++k1;
                }
                ++i1;
            }
            if (!flag) {
                return false;
            }
            Block block1 = worldIn.getBlockState(position.down()).getBlock();
            if ((block1 == Blocks.grass || block1 == Blocks.dirt || block1 == Blocks.farmland) && position.getY() < 256 - i2 - 1) {
                this.func_175921_a(worldIn, position.down());
                int i3 = rand.nextInt(2);
                int j3 = 1;
                int k3 = 0;
                int l3 = 0;
                while (l3 <= k2) {
                    int j4 = position.getY() + i2 - l3;
                    int i22 = position.getX() - i3;
                    while (i22 <= position.getX() + i3) {
                        int j22 = i22 - position.getX();
                        int k22 = position.getZ() - i3;
                        while (k22 <= position.getZ() + i3) {
                            BlockPos blockpos;
                            int l22 = k22 - position.getZ();
                            if (!(Math.abs(j22) == i3 && Math.abs(l22) == i3 && i3 > 0 || worldIn.getBlockState(blockpos = new BlockPos(i22, j4, k22)).getBlock().isFullBlock())) {
                                this.setBlockAndNotifyAdequately(worldIn, blockpos, field_181646_b);
                            }
                            ++k22;
                        }
                        ++i22;
                    }
                    if (i3 >= j3) {
                        i3 = k3;
                        k3 = 1;
                        if (++j3 > l2) {
                            j3 = l2;
                        }
                    } else {
                        ++i3;
                    }
                    ++l3;
                }
                int i4 = rand.nextInt(3);
                int k4 = 0;
                while (k4 < i2 - i4) {
                    Block block2 = worldIn.getBlockState(position.up(k4)).getBlock();
                    if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                        this.setBlockAndNotifyAdequately(worldIn, position.up(k4), field_181645_a);
                    }
                    ++k4;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}

