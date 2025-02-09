// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.Mirror;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;

public class BlockGlazedTerracotta extends BlockHorizontal
{
    public BlockGlazedTerracotta(final EnumDyeColor p_i47400_1_) {
        super(Material.ROCK, MapColor.func_193558_a(p_i47400_1_));
        this.setHardness(1.4f);
        this.setSoundType(SoundType.STONE);
        final String s = p_i47400_1_.getUnlocalizedName();
        if (s.length() > 1) {
            final String s2 = String.valueOf(s.substring(0, 1).toUpperCase()) + s.substring(1, s.length());
            this.setUnlocalizedName("glazedTerracotta" + s2);
        }
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockGlazedTerracotta.FACING });
    }
    
    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        return state.withProperty((IProperty<Comparable>)BlockGlazedTerracotta.FACING, rot.rotate(state.getValue((IProperty<EnumFacing>)BlockGlazedTerracotta.FACING)));
    }
    
    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue((IProperty<EnumFacing>)BlockGlazedTerracotta.FACING)));
    }
    
    @Override
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockGlazedTerracotta.FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue((IProperty<EnumFacing>)BlockGlazedTerracotta.FACING).getHorizontalIndex();
        return i;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockGlazedTerracotta.FACING, EnumFacing.getHorizontal(meta));
    }
    
    @Override
    public EnumPushReaction getMobilityFlag(final IBlockState state) {
        return EnumPushReaction.PUSH_ONLY;
    }
}
