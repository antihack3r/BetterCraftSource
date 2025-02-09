// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.item.Item;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import net.minecraft.block.properties.PropertyEnum;

public class BlockNewLog extends BlockLog
{
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT;
    
    static {
        VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
            @Override
            public boolean apply(@Nullable final BlockPlanks.EnumType p_apply_1_) {
                return p_apply_1_.getMetadata() >= 4;
            }
        });
    }
    
    public BlockNewLog() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockNewLog.LOG_AXIS, EnumAxis.Y));
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state, final IBlockAccess p_180659_2_, final BlockPos p_180659_3_) {
        final BlockPlanks.EnumType blockplanks$enumtype = state.getValue(BlockNewLog.VARIANT);
        switch (state.getValue(BlockNewLog.LOG_AXIS)) {
            default: {
                switch (blockplanks$enumtype) {
                    default: {
                        return MapColor.STONE;
                    }
                    case DARK_OAK: {
                        return BlockPlanks.EnumType.DARK_OAK.getMapColor();
                    }
                }
                break;
            }
            case Y: {
                return blockplanks$enumtype.getMapColor();
            }
        }
    }
    
    @Override
    public void getSubBlocks(final CreativeTabs itemIn, final NonNullList<ItemStack> tab) {
        tab.add(new ItemStack(this, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
        tab.add(new ItemStack(this, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.byMetadata((meta & 0x3) + 4));
        switch (meta & 0xC) {
            case 0: {
                iblockstate = iblockstate.withProperty(BlockNewLog.LOG_AXIS, EnumAxis.Y);
                break;
            }
            case 4: {
                iblockstate = iblockstate.withProperty(BlockNewLog.LOG_AXIS, EnumAxis.X);
                break;
            }
            case 8: {
                iblockstate = iblockstate.withProperty(BlockNewLog.LOG_AXIS, EnumAxis.Z);
                break;
            }
            default: {
                iblockstate = iblockstate.withProperty(BlockNewLog.LOG_AXIS, EnumAxis.NONE);
                break;
            }
        }
        return iblockstate;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockNewLog.VARIANT).getMetadata() - 4;
        switch (state.getValue(BlockNewLog.LOG_AXIS)) {
            case X: {
                i |= 0x4;
                break;
            }
            case Z: {
                i |= 0x8;
                break;
            }
            case NONE: {
                i |= 0xC;
                break;
            }
        }
        return i;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockNewLog.VARIANT, BlockNewLog.LOG_AXIS });
    }
    
    @Override
    protected ItemStack getSilkTouchDrop(final IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockNewLog.VARIANT).getMetadata() - 4);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockNewLog.VARIANT).getMetadata() - 4;
    }
}
