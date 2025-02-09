// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.properties.PropertyBool;

public class BlockFence extends Block
{
    public static final PropertyBool NORTH;
    public static final PropertyBool EAST;
    public static final PropertyBool SOUTH;
    public static final PropertyBool WEST;
    protected static final AxisAlignedBB[] BOUNDING_BOXES;
    public static final AxisAlignedBB PILLAR_AABB;
    public static final AxisAlignedBB SOUTH_AABB;
    public static final AxisAlignedBB WEST_AABB;
    public static final AxisAlignedBB NORTH_AABB;
    public static final AxisAlignedBB EAST_AABB;
    
    static {
        NORTH = PropertyBool.create("north");
        EAST = PropertyBool.create("east");
        SOUTH = PropertyBool.create("south");
        WEST = PropertyBool.create("west");
        BOUNDING_BOXES = new AxisAlignedBB[] { new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625), new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.375, 0.625, 1.0, 0.625), new AxisAlignedBB(0.0, 0.0, 0.375, 0.625, 1.0, 1.0), new AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 0.625), new AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.625, 1.0, 0.625), new AxisAlignedBB(0.0, 0.0, 0.0, 0.625, 1.0, 1.0), new AxisAlignedBB(0.375, 0.0, 0.375, 1.0, 1.0, 0.625), new AxisAlignedBB(0.375, 0.0, 0.375, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 0.625), new AxisAlignedBB(0.0, 0.0, 0.375, 1.0, 1.0, 1.0), new AxisAlignedBB(0.375, 0.0, 0.0, 1.0, 1.0, 0.625), new AxisAlignedBB(0.375, 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.625), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) };
        PILLAR_AABB = new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.5, 0.625);
        SOUTH_AABB = new AxisAlignedBB(0.375, 0.0, 0.625, 0.625, 1.5, 1.0);
        WEST_AABB = new AxisAlignedBB(0.0, 0.0, 0.375, 0.375, 1.5, 0.625);
        NORTH_AABB = new AxisAlignedBB(0.375, 0.0, 0.0, 0.625, 1.5, 0.375);
        EAST_AABB = new AxisAlignedBB(0.625, 0.0, 0.375, 1.0, 1.5, 0.625);
    }
    
    public BlockFence(final Material p_i46395_1_, final MapColor p_i46395_2_) {
        super(p_i46395_1_, p_i46395_2_);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockFence.NORTH, false).withProperty((IProperty<Comparable>)BlockFence.EAST, false).withProperty((IProperty<Comparable>)BlockFence.SOUTH, false).withProperty((IProperty<Comparable>)BlockFence.WEST, false));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }
    
    @Override
    public void addCollisionBoxToList(IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn, final boolean p_185477_7_) {
        if (!p_185477_7_) {
            state = state.getActualState(worldIn, pos);
        }
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.PILLAR_AABB);
        if (state.getValue((IProperty<Boolean>)BlockFence.NORTH)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.NORTH_AABB);
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.EAST)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.EAST_AABB);
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.SOUTH)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.SOUTH_AABB);
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.WEST)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockFence.WEST_AABB);
        }
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, final IBlockAccess source, final BlockPos pos) {
        state = this.getActualState(state, source, pos);
        return BlockFence.BOUNDING_BOXES[getBoundingBoxIdx(state)];
    }
    
    private static int getBoundingBoxIdx(final IBlockState state) {
        int i = 0;
        if (state.getValue((IProperty<Boolean>)BlockFence.NORTH)) {
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.EAST)) {
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.SOUTH)) {
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        }
        if (state.getValue((IProperty<Boolean>)BlockFence.WEST)) {
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();
        }
        return i;
    }
    
    @Override
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(final IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return false;
    }
    
    public boolean canConnectTo(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing p_176524_3_) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        final BlockFaceShape blockfaceshape = iblockstate.func_193401_d(worldIn, pos, p_176524_3_);
        final Block block = iblockstate.getBlock();
        final boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE && (iblockstate.getMaterial() == this.blockMaterial || block instanceof BlockFenceGate);
        return (!func_194142_e(block) && blockfaceshape == BlockFaceShape.SOLID) || flag;
    }
    
    protected static boolean func_194142_e(final Block p_194142_0_) {
        return Block.func_193382_c(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON_BLOCK || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.LIT_PUMPKIN;
    }
    
    @Override
    public boolean shouldSideBeRendered(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        return true;
    }
    
    @Override
    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumHand hand, final EnumFacing heldItem, final float side, final float hitX, final float hitY) {
        if (!worldIn.isRemote) {
            return ItemLead.attachToFence(playerIn, worldIn, pos);
        }
        final ItemStack itemstack = playerIn.getHeldItem(hand);
        return itemstack.getItem() == Items.LEAD || itemstack.func_190926_b();
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }
    
    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty((IProperty<Comparable>)BlockFence.NORTH, this.canConnectTo(worldIn, pos.north(), EnumFacing.SOUTH)).withProperty((IProperty<Comparable>)BlockFence.EAST, this.canConnectTo(worldIn, pos.east(), EnumFacing.WEST)).withProperty((IProperty<Comparable>)BlockFence.SOUTH, this.canConnectTo(worldIn, pos.south(), EnumFacing.NORTH)).withProperty((IProperty<Comparable>)BlockFence.WEST, this.canConnectTo(worldIn, pos.west(), EnumFacing.EAST));
    }
    
    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.withProperty((IProperty<Comparable>)BlockFence.NORTH, (Boolean)state.getValue((IProperty<V>)BlockFence.SOUTH)).withProperty((IProperty<Comparable>)BlockFence.EAST, (Boolean)state.getValue((IProperty<V>)BlockFence.WEST)).withProperty((IProperty<Comparable>)BlockFence.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockFence.NORTH)).withProperty((IProperty<Comparable>)BlockFence.WEST, (Boolean)state.getValue((IProperty<V>)BlockFence.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return state.withProperty((IProperty<Comparable>)BlockFence.NORTH, (Boolean)state.getValue((IProperty<V>)BlockFence.EAST)).withProperty((IProperty<Comparable>)BlockFence.EAST, (Boolean)state.getValue((IProperty<V>)BlockFence.SOUTH)).withProperty((IProperty<Comparable>)BlockFence.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockFence.WEST)).withProperty((IProperty<Comparable>)BlockFence.WEST, (Boolean)state.getValue((IProperty<V>)BlockFence.NORTH));
            }
            case CLOCKWISE_90: {
                return state.withProperty((IProperty<Comparable>)BlockFence.NORTH, (Boolean)state.getValue((IProperty<V>)BlockFence.WEST)).withProperty((IProperty<Comparable>)BlockFence.EAST, (Boolean)state.getValue((IProperty<V>)BlockFence.NORTH)).withProperty((IProperty<Comparable>)BlockFence.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockFence.EAST)).withProperty((IProperty<Comparable>)BlockFence.WEST, (Boolean)state.getValue((IProperty<V>)BlockFence.SOUTH));
            }
            default: {
                return state;
            }
        }
    }
    
    @Override
    public IBlockState withMirror(final IBlockState state, final Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return state.withProperty((IProperty<Comparable>)BlockFence.NORTH, (Boolean)state.getValue((IProperty<V>)BlockFence.SOUTH)).withProperty((IProperty<Comparable>)BlockFence.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockFence.NORTH));
            }
            case FRONT_BACK: {
                return state.withProperty((IProperty<Comparable>)BlockFence.EAST, (Boolean)state.getValue((IProperty<V>)BlockFence.WEST)).withProperty((IProperty<Comparable>)BlockFence.WEST, (Boolean)state.getValue((IProperty<V>)BlockFence.EAST));
            }
            default: {
                return super.withMirror(state, mirrorIn);
            }
        }
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockFence.NORTH, BlockFence.EAST, BlockFence.WEST, BlockFence.SOUTH });
    }
    
    @Override
    public BlockFaceShape func_193383_a(final IBlockAccess p_193383_1_, final IBlockState p_193383_2_, final BlockPos p_193383_3_, final EnumFacing p_193383_4_) {
        return (p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN) ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
    }
}
