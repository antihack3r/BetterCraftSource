// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.util.EnumParticleTypes;
import java.util.Random;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;

public class BlockSponge extends Block
{
    public static final PropertyBool WET;
    
    static {
        WET = PropertyBool.create("wet");
    }
    
    protected BlockSponge() {
        super(Material.SPONGE);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockSponge.WET, false));
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    public String getLocalizedName() {
        return I18n.translateToLocal(String.valueOf(this.getUnlocalizedName()) + ".dry.name");
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return ((boolean)state.getValue((IProperty<Boolean>)BlockSponge.WET)) ? 1 : 0;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.tryAbsorb(worldIn, pos, state);
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos p_189540_5_) {
        this.tryAbsorb(worldIn, pos, state);
        super.neighborChanged(state, worldIn, pos, blockIn, p_189540_5_);
    }
    
    protected void tryAbsorb(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!state.getValue((IProperty<Boolean>)BlockSponge.WET) && this.absorb(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty((IProperty<Comparable>)BlockSponge.WET, true), 2);
            worldIn.playEvent(2001, pos, Block.getIdFromBlock(Blocks.WATER));
        }
    }
    
    private boolean absorb(final World worldIn, final BlockPos pos) {
        final Queue<Tuple<BlockPos, Integer>> queue = (Queue<Tuple<BlockPos, Integer>>)Lists.newLinkedList();
        final List<BlockPos> list = (List<BlockPos>)Lists.newArrayList();
        queue.add(new Tuple<BlockPos, Integer>(pos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            final Tuple<BlockPos, Integer> tuple = queue.poll();
            final BlockPos blockpos = tuple.getFirst();
            final int j = tuple.getSecond();
            EnumFacing[] values;
            for (int length = (values = EnumFacing.values()).length, k = 0; k < length; ++k) {
                final EnumFacing enumfacing = values[k];
                final BlockPos blockpos2 = blockpos.offset(enumfacing);
                if (worldIn.getBlockState(blockpos2).getMaterial() == Material.WATER) {
                    worldIn.setBlockState(blockpos2, Blocks.AIR.getDefaultState(), 2);
                    list.add(blockpos2);
                    ++i;
                    if (j < 6) {
                        queue.add(new Tuple<BlockPos, Integer>(blockpos2, j + 1));
                    }
                }
            }
            if (i > 64) {
                break;
            }
        }
        for (final BlockPos blockpos3 : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos3, Blocks.AIR, false);
        }
        return i > 0;
    }
    
    @Override
    public void getSubBlocks(final CreativeTabs itemIn, final NonNullList<ItemStack> tab) {
        tab.add(new ItemStack(this, 1, 0));
        tab.add(new ItemStack(this, 1, 1));
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockSponge.WET, (meta & 0x1) == 0x1);
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return ((boolean)state.getValue((IProperty<Boolean>)BlockSponge.WET)) ? 1 : 0;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockSponge.WET });
    }
    
    @Override
    public void randomDisplayTick(final IBlockState stateIn, final World worldIn, final BlockPos pos, final Random rand) {
        if (stateIn.getValue((IProperty<Boolean>)BlockSponge.WET)) {
            final EnumFacing enumfacing = EnumFacing.random(rand);
            if (enumfacing != EnumFacing.UP && !worldIn.getBlockState(pos.offset(enumfacing)).isFullyOpaque()) {
                double d0 = pos.getX();
                double d2 = pos.getY();
                double d3 = pos.getZ();
                if (enumfacing == EnumFacing.DOWN) {
                    d2 -= 0.05;
                    d0 += rand.nextDouble();
                    d3 += rand.nextDouble();
                }
                else {
                    d2 += rand.nextDouble() * 0.8;
                    if (enumfacing.getAxis() == EnumFacing.Axis.X) {
                        d3 += rand.nextDouble();
                        if (enumfacing == EnumFacing.EAST) {
                            ++d0;
                        }
                        else {
                            d0 += 0.05;
                        }
                    }
                    else {
                        d0 += rand.nextDouble();
                        if (enumfacing == EnumFacing.SOUTH) {
                            ++d3;
                        }
                        else {
                            d3 += 0.05;
                        }
                    }
                }
                worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d2, d3, 0.0, 0.0, 0.0, new int[0]);
            }
        }
    }
}
