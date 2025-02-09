// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;

public class WorldGenShrub extends WorldGenTrees
{
    private final IBlockState leavesMetadata;
    private final IBlockState woodMetadata;
    
    public WorldGenShrub(final IBlockState p_i46450_1_, final IBlockState p_i46450_2_) {
        super(false);
        this.woodMetadata = p_i46450_1_;
        this.leavesMetadata = p_i46450_2_;
    }
    
    @Override
    public boolean generate(final World worldIn, final Random rand, BlockPos position) {
        for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getMaterial() == Material.AIR || iblockstate.getMaterial() == Material.LEAVES) && position.getY() > 0; position = position.down(), iblockstate = worldIn.getBlockState(position)) {}
        final Block block = worldIn.getBlockState(position).getBlock();
        if (block == Blocks.DIRT || block == Blocks.GRASS) {
            position = position.up();
            this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);
            for (int i = position.getY(); i <= position.getY() + 2; ++i) {
                final int j = i - position.getY();
                for (int k = 2 - j, l = position.getX() - k; l <= position.getX() + k; ++l) {
                    final int i2 = l - position.getX();
                    for (int j2 = position.getZ() - k; j2 <= position.getZ() + k; ++j2) {
                        final int k2 = j2 - position.getZ();
                        if (Math.abs(i2) != k || Math.abs(k2) != k || rand.nextInt(2) != 0) {
                            final BlockPos blockpos = new BlockPos(l, i, j2);
                            final Material material = worldIn.getBlockState(blockpos).getMaterial();
                            if (material == Material.AIR || material == Material.LEAVES) {
                                this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
