// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import java.util.Random;
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
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.properties.PropertyBool;

public class BlockPane extends Block
{
    public static final PropertyBool NORTH;
    public static final PropertyBool EAST;
    public static final PropertyBool SOUTH;
    public static final PropertyBool WEST;
    protected static final AxisAlignedBB[] AABB_BY_INDEX;
    private final boolean canDrop;
    
    static {
        NORTH = PropertyBool.create("north");
        EAST = PropertyBool.create("east");
        SOUTH = PropertyBool.create("south");
        WEST = PropertyBool.create("west");
        AABB_BY_INDEX = new AxisAlignedBB[] { new AxisAlignedBB(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625), new AxisAlignedBB(0.4375, 0.0, 0.4375, 0.5625, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.4375, 0.5625, 1.0, 0.5625), new AxisAlignedBB(0.0, 0.0, 0.4375, 0.5625, 1.0, 1.0), new AxisAlignedBB(0.4375, 0.0, 0.0, 0.5625, 1.0, 0.5625), new AxisAlignedBB(0.4375, 0.0, 0.0, 0.5625, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.5625, 1.0, 0.5625), new AxisAlignedBB(0.0, 0.0, 0.0, 0.5625, 1.0, 1.0), new AxisAlignedBB(0.4375, 0.0, 0.4375, 1.0, 1.0, 0.5625), new AxisAlignedBB(0.4375, 0.0, 0.4375, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.4375, 1.0, 1.0, 0.5625), new AxisAlignedBB(0.0, 0.0, 0.4375, 1.0, 1.0, 1.0), new AxisAlignedBB(0.4375, 0.0, 0.0, 1.0, 1.0, 0.5625), new AxisAlignedBB(0.4375, 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5625), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0) };
    }
    
    protected BlockPane(final Material materialIn, final boolean canDrop) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockPane.NORTH, false).withProperty((IProperty<Comparable>)BlockPane.EAST, false).withProperty((IProperty<Comparable>)BlockPane.SOUTH, false).withProperty((IProperty<Comparable>)BlockPane.WEST, false));
        this.canDrop = canDrop;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }
    
    @Override
    public void addCollisionBoxToList(IBlockState state, final World worldIn, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, @Nullable final Entity entityIn, final boolean p_185477_7_) {
        if (!p_185477_7_) {
            state = this.getActualState(state, worldIn, pos);
        }
        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockPane.AABB_BY_INDEX[0]);
        if (state.getValue((IProperty<Boolean>)BlockPane.NORTH)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockPane.AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.NORTH)]);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.SOUTH)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockPane.AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.SOUTH)]);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.EAST)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockPane.AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.EAST)]);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.WEST)) {
            Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockPane.AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.WEST)]);
        }
    }
    
    private static int getBoundingBoxIndex(final EnumFacing p_185729_0_) {
        return 1 << p_185729_0_.getHorizontalIndex();
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, final IBlockAccess source, final BlockPos pos) {
        state = this.getActualState(state, source, pos);
        return BlockPane.AABB_BY_INDEX[getBoundingBoxIndex(state)];
    }
    
    private static int getBoundingBoxIndex(final IBlockState state) {
        int i = 0;
        if (state.getValue((IProperty<Boolean>)BlockPane.NORTH)) {
            i |= getBoundingBoxIndex(EnumFacing.NORTH);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.EAST)) {
            i |= getBoundingBoxIndex(EnumFacing.EAST);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.SOUTH)) {
            i |= getBoundingBoxIndex(EnumFacing.SOUTH);
        }
        if (state.getValue((IProperty<Boolean>)BlockPane.WEST)) {
            i |= getBoundingBoxIndex(EnumFacing.WEST);
        }
        return i;
    }
    
    @Override
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty((IProperty<Comparable>)BlockPane.NORTH, this.func_193393_b(worldIn, worldIn.getBlockState(pos.north()), pos.north(), EnumFacing.SOUTH)).withProperty((IProperty<Comparable>)BlockPane.SOUTH, this.func_193393_b(worldIn, worldIn.getBlockState(pos.south()), pos.south(), EnumFacing.NORTH)).withProperty((IProperty<Comparable>)BlockPane.WEST, this.func_193393_b(worldIn, worldIn.getBlockState(pos.west()), pos.west(), EnumFacing.EAST)).withProperty((IProperty<Comparable>)BlockPane.EAST, this.func_193393_b(worldIn, worldIn.getBlockState(pos.east()), pos.east(), EnumFacing.WEST));
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return this.canDrop ? super.getItemDropped(state, rand, fortune) : Items.field_190931_a;
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
    public boolean shouldSideBeRendered(final IBlockState blockState, final IBlockAccess blockAccess, final BlockPos pos, final EnumFacing side) {
        return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
    public final boolean func_193393_b(final IBlockAccess p_193393_1_, final IBlockState p_193393_2_, final BlockPos p_193393_3_, final EnumFacing p_193393_4_) {
        final Block block = p_193393_2_.getBlock();
        final BlockFaceShape blockfaceshape = p_193393_2_.func_193401_d(p_193393_1_, p_193393_3_, p_193393_4_);
        return (!func_193394_e(block) && blockfaceshape == BlockFaceShape.SOLID) || blockfaceshape == BlockFaceShape.MIDDLE_POLE_THIN;
    }
    
    protected static boolean func_193394_e(final Block p_193394_0_) {
        return p_193394_0_ instanceof BlockShulkerBox || p_193394_0_ instanceof BlockLeaves || p_193394_0_ == Blocks.BEACON || p_193394_0_ == Blocks.CAULDRON || p_193394_0_ == Blocks.GLOWSTONE || p_193394_0_ == Blocks.ICE || p_193394_0_ == Blocks.SEA_LANTERN || p_193394_0_ == Blocks.PISTON || p_193394_0_ == Blocks.STICKY_PISTON || p_193394_0_ == Blocks.PISTON_HEAD || p_193394_0_ == Blocks.MELON_BLOCK || p_193394_0_ == Blocks.PUMPKIN || p_193394_0_ == Blocks.LIT_PUMPKIN || p_193394_0_ == Blocks.BARRIER;
    }
    
    @Override
    protected boolean canSilkHarvest() {
        return true;
    }
    
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }
    
    @Override
    public IBlockState withRotation(final IBlockState state, final Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.withProperty((IProperty<Comparable>)BlockPane.NORTH, (Boolean)state.getValue((IProperty<V>)BlockPane.SOUTH)).withProperty((IProperty<Comparable>)BlockPane.EAST, (Boolean)state.getValue((IProperty<V>)BlockPane.WEST)).withProperty((IProperty<Comparable>)BlockPane.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockPane.NORTH)).withProperty((IProperty<Comparable>)BlockPane.WEST, (Boolean)state.getValue((IProperty<V>)BlockPane.EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return state.withProperty((IProperty<Comparable>)BlockPane.NORTH, (Boolean)state.getValue((IProperty<V>)BlockPane.EAST)).withProperty((IProperty<Comparable>)BlockPane.EAST, (Boolean)state.getValue((IProperty<V>)BlockPane.SOUTH)).withProperty((IProperty<Comparable>)BlockPane.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockPane.WEST)).withProperty((IProperty<Comparable>)BlockPane.WEST, (Boolean)state.getValue((IProperty<V>)BlockPane.NORTH));
            }
            case CLOCKWISE_90: {
                return state.withProperty((IProperty<Comparable>)BlockPane.NORTH, (Boolean)state.getValue((IProperty<V>)BlockPane.WEST)).withProperty((IProperty<Comparable>)BlockPane.EAST, (Boolean)state.getValue((IProperty<V>)BlockPane.NORTH)).withProperty((IProperty<Comparable>)BlockPane.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockPane.EAST)).withProperty((IProperty<Comparable>)BlockPane.WEST, (Boolean)state.getValue((IProperty<V>)BlockPane.SOUTH));
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
                return state.withProperty((IProperty<Comparable>)BlockPane.NORTH, (Boolean)state.getValue((IProperty<V>)BlockPane.SOUTH)).withProperty((IProperty<Comparable>)BlockPane.SOUTH, (Boolean)state.getValue((IProperty<V>)BlockPane.NORTH));
            }
            case FRONT_BACK: {
                return state.withProperty((IProperty<Comparable>)BlockPane.EAST, (Boolean)state.getValue((IProperty<V>)BlockPane.WEST)).withProperty((IProperty<Comparable>)BlockPane.WEST, (Boolean)state.getValue((IProperty<V>)BlockPane.EAST));
            }
            default: {
                return super.withMirror(state, mirrorIn);
            }
        }
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockPane.NORTH, BlockPane.EAST, BlockPane.WEST, BlockPane.SOUTH });
    }
    
    @Override
    public BlockFaceShape func_193383_a(final IBlockAccess p_193383_1_, final IBlockState p_193383_2_, final BlockPos p_193383_3_, final EnumFacing p_193383_4_) {
        return (p_193383_4_ != EnumFacing.UP && p_193383_4_ != EnumFacing.DOWN) ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
    }
}
