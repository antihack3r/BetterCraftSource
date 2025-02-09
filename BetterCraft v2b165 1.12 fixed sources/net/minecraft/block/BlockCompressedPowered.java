// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockCompressedPowered extends Block
{
    public BlockCompressedPowered(final Material materialIn, final MapColor color) {
        super(materialIn, color);
    }
    
    @Override
    public boolean canProvidePower(final IBlockState state) {
        return true;
    }
    
    @Override
    public int getWeakPower(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        return 15;
    }
}
