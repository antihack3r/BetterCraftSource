// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.world.gen.structure;

import net.minecraft.block.BlockDoor;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockDispenser;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.material.Material;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import java.util.Random;
import java.util.List;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;

public abstract class StructureComponent
{
    protected StructureBoundingBox boundingBox;
    @Nullable
    private EnumFacing coordBaseMode;
    private Mirror mirror;
    private Rotation rotation;
    protected int componentType;
    
    public StructureComponent() {
    }
    
    protected StructureComponent(final int type) {
        this.componentType = type;
    }
    
    public final NBTTagCompound createStructureBaseNBT() {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.getStructureComponentName(this));
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        final EnumFacing enumfacing = this.getCoordBaseMode();
        nbttagcompound.setInteger("O", (enumfacing == null) ? -1 : enumfacing.getHorizontalIndex());
        nbttagcompound.setInteger("GD", this.componentType);
        this.writeStructureToNBT(nbttagcompound);
        return nbttagcompound;
    }
    
    protected abstract void writeStructureToNBT(final NBTTagCompound p0);
    
    public void readStructureBaseNBT(final World worldIn, final NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }
        final int i = tagCompound.getInteger("O");
        this.setCoordBaseMode((i == -1) ? null : EnumFacing.getHorizontal(i));
        this.componentType = tagCompound.getInteger("GD");
        this.readStructureFromNBT(tagCompound, worldIn.getSaveHandler().getStructureTemplateManager());
    }
    
    protected abstract void readStructureFromNBT(final NBTTagCompound p0, final TemplateManager p1);
    
    public void buildComponent(final StructureComponent componentIn, final List<StructureComponent> listIn, final Random rand) {
    }
    
    public abstract boolean addComponentParts(final World p0, final Random p1, final StructureBoundingBox p2);
    
    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }
    
    public int getComponentType() {
        return this.componentType;
    }
    
    public static StructureComponent findIntersecting(final List<StructureComponent> listIn, final StructureBoundingBox boundingboxIn) {
        for (final StructureComponent structurecomponent : listIn) {
            if (structurecomponent.getBoundingBox() != null && structurecomponent.getBoundingBox().intersectsWith(boundingboxIn)) {
                return structurecomponent;
            }
        }
        return null;
    }
    
    protected boolean isLiquidInStructureBoundingBox(final World worldIn, final StructureBoundingBox boundingboxIn) {
        final int i = Math.max(this.boundingBox.minX - 1, boundingboxIn.minX);
        final int j = Math.max(this.boundingBox.minY - 1, boundingboxIn.minY);
        final int k = Math.max(this.boundingBox.minZ - 1, boundingboxIn.minZ);
        final int l = Math.min(this.boundingBox.maxX + 1, boundingboxIn.maxX);
        final int i2 = Math.min(this.boundingBox.maxY + 1, boundingboxIn.maxY);
        final int j2 = Math.min(this.boundingBox.maxZ + 1, boundingboxIn.maxZ);
        final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int k2 = i; k2 <= l; ++k2) {
            for (int l2 = k; l2 <= j2; ++l2) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(k2, j, l2)).getMaterial().isLiquid()) {
                    return true;
                }
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(k2, i2, l2)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int i3 = i; i3 <= l; ++i3) {
            for (int k3 = j; k3 <= i2; ++k3) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i3, k3, k)).getMaterial().isLiquid()) {
                    return true;
                }
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i3, k3, j2)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        for (int j3 = k; j3 <= j2; ++j3) {
            for (int l3 = j; l3 <= i2; ++l3) {
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(i, l3, j3)).getMaterial().isLiquid()) {
                    return true;
                }
                if (worldIn.getBlockState(blockpos$mutableblockpos.setPos(l, l3, j3)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected int getXWithOffset(final int x, final int z) {
        final EnumFacing enumfacing = this.getCoordBaseMode();
        if (enumfacing == null) {
            return x;
        }
        switch (enumfacing) {
            case NORTH:
            case SOUTH: {
                return this.boundingBox.minX + x;
            }
            case WEST: {
                return this.boundingBox.maxX - z;
            }
            case EAST: {
                return this.boundingBox.minX + z;
            }
            default: {
                return x;
            }
        }
    }
    
    protected int getYWithOffset(final int y) {
        return (this.getCoordBaseMode() == null) ? y : (y + this.boundingBox.minY);
    }
    
    protected int getZWithOffset(final int x, final int z) {
        final EnumFacing enumfacing = this.getCoordBaseMode();
        if (enumfacing == null) {
            return z;
        }
        switch (enumfacing) {
            case NORTH: {
                return this.boundingBox.maxZ - z;
            }
            case SOUTH: {
                return this.boundingBox.minZ + z;
            }
            case WEST:
            case EAST: {
                return this.boundingBox.minZ + x;
            }
            default: {
                return z;
            }
        }
    }
    
    protected void setBlockState(final World worldIn, IBlockState blockstateIn, final int x, final int y, final int z, final StructureBoundingBox boundingboxIn) {
        final BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
        if (boundingboxIn.isVecInside(blockpos)) {
            if (this.mirror != Mirror.NONE) {
                blockstateIn = blockstateIn.withMirror(this.mirror);
            }
            if (this.rotation != Rotation.NONE) {
                blockstateIn = blockstateIn.withRotation(this.rotation);
            }
            worldIn.setBlockState(blockpos, blockstateIn, 2);
        }
    }
    
    protected IBlockState getBlockStateFromPos(final World worldIn, final int x, final int y, final int z, final StructureBoundingBox boundingboxIn) {
        final int i = this.getXWithOffset(x, z);
        final int j = this.getYWithOffset(y);
        final int k = this.getZWithOffset(x, z);
        final BlockPos blockpos = new BlockPos(i, j, k);
        return boundingboxIn.isVecInside(blockpos) ? worldIn.getBlockState(blockpos) : Blocks.AIR.getDefaultState();
    }
    
    protected int func_189916_b(final World p_189916_1_, final int p_189916_2_, final int p_189916_3_, final int p_189916_4_, final StructureBoundingBox p_189916_5_) {
        final int i = this.getXWithOffset(p_189916_2_, p_189916_4_);
        final int j = this.getYWithOffset(p_189916_3_ + 1);
        final int k = this.getZWithOffset(p_189916_2_, p_189916_4_);
        final BlockPos blockpos = new BlockPos(i, j, k);
        return p_189916_5_.isVecInside(blockpos) ? p_189916_1_.getLightFor(EnumSkyBlock.SKY, blockpos) : EnumSkyBlock.SKY.defaultLightValue;
    }
    
    protected void fillWithAir(final World worldIn, final StructureBoundingBox structurebb, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), j, i, k, structurebb);
                }
            }
        }
    }
    
    protected void fillWithBlocks(final World worldIn, final StructureBoundingBox boundingboxIn, final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax, final IBlockState boundaryBlockState, final IBlockState insideBlockState, final boolean existingOnly) {
        for (int i = yMin; i <= yMax; ++i) {
            for (int j = xMin; j <= xMax; ++j) {
                for (int k = zMin; k <= zMax; ++k) {
                    if (!existingOnly || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getMaterial() != Material.AIR) {
                        if (i != yMin && i != yMax && j != xMin && j != xMax && k != zMin && k != zMax) {
                            this.setBlockState(worldIn, insideBlockState, j, i, k, boundingboxIn);
                        }
                        else {
                            this.setBlockState(worldIn, boundaryBlockState, j, i, k, boundingboxIn);
                        }
                    }
                }
            }
        }
    }
    
    protected void fillWithRandomizedBlocks(final World worldIn, final StructureBoundingBox boundingboxIn, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final boolean alwaysReplace, final Random rand, final BlockSelector blockselector) {
        for (int i = minY; i <= maxY; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minZ; k <= maxZ; ++k) {
                    if (!alwaysReplace || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getMaterial() != Material.AIR) {
                        blockselector.selectBlocks(rand, j, i, k, i == minY || i == maxY || j == minX || j == maxX || k == minZ || k == maxZ);
                        this.setBlockState(worldIn, blockselector.getBlockState(), j, i, k, boundingboxIn);
                    }
                }
            }
        }
    }
    
    protected void func_189914_a(final World p_189914_1_, final StructureBoundingBox p_189914_2_, final Random p_189914_3_, final float p_189914_4_, final int p_189914_5_, final int p_189914_6_, final int p_189914_7_, final int p_189914_8_, final int p_189914_9_, final int p_189914_10_, final IBlockState p_189914_11_, final IBlockState p_189914_12_, final boolean p_189914_13_, final int p_189914_14_) {
        for (int i = p_189914_6_; i <= p_189914_9_; ++i) {
            for (int j = p_189914_5_; j <= p_189914_8_; ++j) {
                for (int k = p_189914_7_; k <= p_189914_10_; ++k) {
                    if (p_189914_3_.nextFloat() <= p_189914_4_ && (!p_189914_13_ || this.getBlockStateFromPos(p_189914_1_, j, i, k, p_189914_2_).getMaterial() != Material.AIR) && (p_189914_14_ <= 0 || this.func_189916_b(p_189914_1_, j, i, k, p_189914_2_) < p_189914_14_)) {
                        if (i != p_189914_6_ && i != p_189914_9_ && j != p_189914_5_ && j != p_189914_8_ && k != p_189914_7_ && k != p_189914_10_) {
                            this.setBlockState(p_189914_1_, p_189914_12_, j, i, k, p_189914_2_);
                        }
                        else {
                            this.setBlockState(p_189914_1_, p_189914_11_, j, i, k, p_189914_2_);
                        }
                    }
                }
            }
        }
    }
    
    protected void randomlyPlaceBlock(final World worldIn, final StructureBoundingBox boundingboxIn, final Random rand, final float chance, final int x, final int y, final int z, final IBlockState blockstateIn) {
        if (rand.nextFloat() < chance) {
            this.setBlockState(worldIn, blockstateIn, x, y, z, boundingboxIn);
        }
    }
    
    protected void randomlyRareFillWithBlocks(final World worldIn, final StructureBoundingBox boundingboxIn, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final IBlockState blockstateIn, final boolean excludeAir) {
        final float f = (float)(maxX - minX + 1);
        final float f2 = (float)(maxY - minY + 1);
        final float f3 = (float)(maxZ - minZ + 1);
        final float f4 = minX + f / 2.0f;
        final float f5 = minZ + f3 / 2.0f;
        for (int i = minY; i <= maxY; ++i) {
            final float f6 = (i - minY) / f2;
            for (int j = minX; j <= maxX; ++j) {
                final float f7 = (j - f4) / (f * 0.5f);
                for (int k = minZ; k <= maxZ; ++k) {
                    final float f8 = (k - f5) / (f3 * 0.5f);
                    if (!excludeAir || this.getBlockStateFromPos(worldIn, j, i, k, boundingboxIn).getMaterial() != Material.AIR) {
                        final float f9 = f7 * f7 + f6 * f6 + f8 * f8;
                        if (f9 <= 1.05f) {
                            this.setBlockState(worldIn, blockstateIn, j, i, k, boundingboxIn);
                        }
                    }
                }
            }
        }
    }
    
    protected void clearCurrentPositionBlocksUpwards(final World worldIn, final int x, final int y, final int z, final StructureBoundingBox structurebb) {
        BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
        if (structurebb.isVecInside(blockpos)) {
            while (!worldIn.isAirBlock(blockpos) && blockpos.getY() < 255) {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                blockpos = blockpos.up();
            }
        }
    }
    
    protected void replaceAirAndLiquidDownwards(final World worldIn, final IBlockState blockstateIn, final int x, final int y, final int z, final StructureBoundingBox boundingboxIn) {
        final int i = this.getXWithOffset(x, z);
        int j = this.getYWithOffset(y);
        final int k = this.getZWithOffset(x, z);
        if (boundingboxIn.isVecInside(new BlockPos(i, j, k))) {
            while ((worldIn.isAirBlock(new BlockPos(i, j, k)) || worldIn.getBlockState(new BlockPos(i, j, k)).getMaterial().isLiquid()) && j > 1) {
                worldIn.setBlockState(new BlockPos(i, j, k), blockstateIn, 2);
                --j;
            }
        }
    }
    
    protected boolean generateChest(final World worldIn, final StructureBoundingBox structurebb, final Random randomIn, final int x, final int y, final int z, final ResourceLocation loot) {
        final BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
        return this.func_191080_a(worldIn, structurebb, randomIn, blockpos, loot, null);
    }
    
    protected boolean func_191080_a(final World p_191080_1_, final StructureBoundingBox p_191080_2_, final Random p_191080_3_, final BlockPos p_191080_4_, final ResourceLocation p_191080_5_, @Nullable IBlockState p_191080_6_) {
        if (p_191080_2_.isVecInside(p_191080_4_) && p_191080_1_.getBlockState(p_191080_4_).getBlock() != Blocks.CHEST) {
            if (p_191080_6_ == null) {
                p_191080_6_ = Blocks.CHEST.correctFacing(p_191080_1_, p_191080_4_, Blocks.CHEST.getDefaultState());
            }
            p_191080_1_.setBlockState(p_191080_4_, p_191080_6_, 2);
            final TileEntity tileentity = p_191080_1_.getTileEntity(p_191080_4_);
            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest)tileentity).setLootTable(p_191080_5_, p_191080_3_.nextLong());
            }
            return true;
        }
        return false;
    }
    
    protected boolean createDispenser(final World p_189419_1_, final StructureBoundingBox p_189419_2_, final Random p_189419_3_, final int p_189419_4_, final int p_189419_5_, final int p_189419_6_, final EnumFacing p_189419_7_, final ResourceLocation p_189419_8_) {
        final BlockPos blockpos = new BlockPos(this.getXWithOffset(p_189419_4_, p_189419_6_), this.getYWithOffset(p_189419_5_), this.getZWithOffset(p_189419_4_, p_189419_6_));
        if (p_189419_2_.isVecInside(blockpos) && p_189419_1_.getBlockState(blockpos).getBlock() != Blocks.DISPENSER) {
            this.setBlockState(p_189419_1_, Blocks.DISPENSER.getDefaultState().withProperty((IProperty<Comparable>)BlockDispenser.FACING, p_189419_7_), p_189419_4_, p_189419_5_, p_189419_6_, p_189419_2_);
            final TileEntity tileentity = p_189419_1_.getTileEntity(blockpos);
            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser)tileentity).setLootTable(p_189419_8_, p_189419_3_.nextLong());
            }
            return true;
        }
        return false;
    }
    
    protected void func_189915_a(final World p_189915_1_, final StructureBoundingBox p_189915_2_, final Random p_189915_3_, final int p_189915_4_, final int p_189915_5_, final int p_189915_6_, final EnumFacing p_189915_7_, final BlockDoor p_189915_8_) {
        this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().withProperty((IProperty<Comparable>)BlockDoor.FACING, p_189915_7_), p_189915_4_, p_189915_5_, p_189915_6_, p_189915_2_);
        this.setBlockState(p_189915_1_, p_189915_8_.getDefaultState().withProperty((IProperty<Comparable>)BlockDoor.FACING, p_189915_7_).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), p_189915_4_, p_189915_5_ + 1, p_189915_6_, p_189915_2_);
    }
    
    public void offset(final int x, final int y, final int z) {
        this.boundingBox.offset(x, y, z);
    }
    
    @Nullable
    public EnumFacing getCoordBaseMode() {
        return this.coordBaseMode;
    }
    
    public void setCoordBaseMode(@Nullable final EnumFacing facing) {
        this.coordBaseMode = facing;
        if (facing == null) {
            this.rotation = Rotation.NONE;
            this.mirror = Mirror.NONE;
        }
        else {
            switch (facing) {
                case SOUTH: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.NONE;
                    break;
                }
            }
        }
    }
    
    public abstract static class BlockSelector
    {
        protected IBlockState blockstate;
        
        public BlockSelector() {
            this.blockstate = Blocks.AIR.getDefaultState();
        }
        
        public abstract void selectBlocks(final Random p0, final int p1, final int p2, final int p3, final boolean p4);
        
        public IBlockState getBlockState() {
            return this.blockstate;
        }
    }
}
