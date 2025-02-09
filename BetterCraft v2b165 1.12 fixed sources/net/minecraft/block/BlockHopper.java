// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.stats.StatList;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import com.google.common.base.Predicate;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;

public class BlockHopper extends BlockContainer
{
    public static final PropertyDirection FACING;
    public static final PropertyBool ENABLED;
    protected static final AxisAlignedBB BASE_AABB;
    protected static final AxisAlignedBB SOUTH_AABB;
    protected static final AxisAlignedBB NORTH_AABB;
    protected static final AxisAlignedBB WEST_AABB;
    protected static final AxisAlignedBB EAST_AABB;
    
    static {
        FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
            @Override
            public boolean apply(@Nullable final EnumFacing p_apply_1_) {
                return p_apply_1_ != EnumFacing.UP;
            }
        });
        ENABLED = PropertyBool.create("enabled");
        BASE_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0);
        SOUTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
        NORTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
        WEST_AABB = new AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
        EAST_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);
    }
    
    public BlockHopper() {
        super(Material.IRON, MapColor.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockHopper.FACING, EnumFacing.DOWN).withProperty((IProperty<Comparable>)BlockHopper.ENABLED, true));
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return BlockHopper.FULL_BLOCK_AABB;
    }
    
    @Override
    public void addCollisionBoxToList(final IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn, final boolean p_185477_7_) {
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockHopper.BASE_AABB);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockHopper.EAST_AABB);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockHopper.WEST_AABB);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockHopper.SOUTH_AABB);
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockHopper.NORTH_AABB);
    }
    
    @Override
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        EnumFacing enumfacing = facing.getOpposite();
        if (enumfacing == EnumFacing.UP) {
            enumfacing = EnumFacing.DOWN;
        }
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockHopper.FACING, enumfacing).withProperty((IProperty<Comparable>)BlockHopper.ENABLED, true);
    }
    
    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new TileEntityHopper();
    }
    
    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasDisplayName()) {
            final TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper)tileentity).func_190575_a(stack.getDisplayName());
            }
        }
    }
    
    @Override
    public boolean isFullyOpaque(final IBlockState state) {
        return true;
    }
    
    @Override
    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.updateState(worldIn, pos, state);
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumHand hand, final EnumFacing heldItem, final float side, final float hitX, final float hitY) {
        if (worldIn.isRemote) {
            return true;
        }
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityHopper) {
            playerIn.displayGUIChest((IInventory)tileentity);
            playerIn.addStat(StatList.HOPPER_INSPECTED);
        }
        return true;
    }
    
    @Override
    public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos p_189540_5_) {
        this.updateState(worldIn, pos, state);
    }
    
    private void updateState(final World worldIn, final BlockPos pos, final IBlockState state) {
        final boolean flag = !worldIn.isBlockPowered(pos);
        if (flag != state.getValue((IProperty<Boolean>)BlockHopper.ENABLED)) {
            worldIn.setBlockState(pos, state.withProperty((IProperty<Comparable>)BlockHopper.ENABLED, flag), 4);
        }
    }
    
    @Override
    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityHopper) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public boolean shouldSideBeRendered(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        return true;
    }
    
    public static EnumFacing getFacing(final int meta) {
        return EnumFacing.getFront(meta & 0x7);
    }
    
    public static boolean isEnabled(final int meta) {
        return (meta & 0x8) != 0x8;
    }
    
    @Override
    public boolean hasComparatorInputOverride(final IBlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(final IBlockState blockState, final World worldIn, final BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }
    
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockHopper.FACING, getFacing(meta)).withProperty((IProperty<Comparable>)BlockHopper.ENABLED, isEnabled(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue((IProperty<EnumFacing>)BlockHopper.FACING).getIndex();
        if (!state.getValue((IProperty<Boolean>)BlockHopper.ENABLED)) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        return state.withProperty((IProperty<Comparable>)BlockHopper.FACING, rot.rotate(state.getValue((IProperty<EnumFacing>)BlockHopper.FACING)));
    }
    
    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue((IProperty<EnumFacing>)BlockHopper.FACING)));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockHopper.FACING, BlockHopper.ENABLED });
    }
    
    @Override
    public BlockFaceShape func_193383_a(final IBlockAccess p_193383_1_, final IBlockState p_193383_2_, final BlockPos p_193383_3_, final EnumFacing p_193383_4_) {
        return (p_193383_4_ == EnumFacing.UP) ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }
}
