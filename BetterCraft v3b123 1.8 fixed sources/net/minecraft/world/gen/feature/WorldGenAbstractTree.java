// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.feature;

import net.minecraft.util.BlockPos;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

public abstract class WorldGenAbstractTree extends WorldGenerator
{
    public WorldGenAbstractTree(final boolean notify) {
        super(notify);
    }
    
    protected boolean func_150523_a(final Block p_150523_1_) {
        final Material material = p_150523_1_.getMaterial();
        return material == Material.air || material == Material.leaves || p_150523_1_ == Blocks.grass || p_150523_1_ == Blocks.dirt || p_150523_1_ == Blocks.log || p_150523_1_ == Blocks.log2 || p_150523_1_ == Blocks.sapling || p_150523_1_ == Blocks.vine;
    }
    
    public void func_180711_a(final World worldIn, final Random p_180711_2_, final BlockPos p_180711_3_) {
    }
    
    protected void func_175921_a(final World worldIn, final BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.dirt) {
            this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.dirt.getDefaultState());
        }
    }
}
