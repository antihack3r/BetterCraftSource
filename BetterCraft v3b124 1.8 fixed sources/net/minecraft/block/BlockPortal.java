/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal
extends BlockBreakable {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create((String)"axis", EnumFacing.Axis.class, (Enum[])new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z});

    public BlockPortal() {
        super(Material.portal, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        if (worldIn.provider.isSurfaceWorld() && worldIn.getGameRules().getBoolean("doMobSpawning") && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
            Entity entity;
            int i2 = pos.getY();
            BlockPos blockpos = pos;
            while (!World.doesBlockHaveSolidTopSurface(worldIn, blockpos) && blockpos.getY() > 0) {
                blockpos = blockpos.down();
            }
            if (i2 > 0 && !worldIn.getBlockState(blockpos.up()).getBlock().isNormalCube() && (entity = ItemMonsterPlacer.spawnCreature(worldIn, 57, (double)blockpos.getX() + 0.5, (double)blockpos.getY() + 1.1, (double)blockpos.getZ() + 0.5)) != null) {
                entity.timeUntilPortal = entity.getPortalCooldown();
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(AXIS);
        float f2 = 0.125f;
        float f1 = 0.125f;
        if (enumfacing$axis == EnumFacing.Axis.X) {
            f2 = 0.5f;
        }
        if (enumfacing$axis == EnumFacing.Axis.Z) {
            f1 = 0.5f;
        }
        this.setBlockBounds(0.5f - f2, 0.0f, 0.5f - f1, 0.5f + f2, 1.0f, 0.5f + f1);
    }

    public static int getMetaForAxis(EnumFacing.Axis axis) {
        return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    public boolean func_176548_d(World worldIn, BlockPos p_176548_2_) {
        Size blockportal$size = new Size(worldIn, p_176548_2_, EnumFacing.Axis.X);
        if (blockportal$size.func_150860_b() && blockportal$size.field_150864_e == 0) {
            blockportal$size.func_150859_c();
            return true;
        }
        Size blockportal$size1 = new Size(worldIn, p_176548_2_, EnumFacing.Axis.Z);
        if (blockportal$size1.func_150860_b() && blockportal$size1.field_150864_e == 0) {
            blockportal$size1.func_150859_c();
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        Size blockportal$size1;
        EnumFacing.Axis enumfacing$axis = state.getValue(AXIS);
        if (enumfacing$axis == EnumFacing.Axis.X) {
            Size blockportal$size = new Size(worldIn, pos, EnumFacing.Axis.X);
            if (!blockportal$size.func_150860_b() || blockportal$size.field_150864_e < blockportal$size.field_150868_h * blockportal$size.field_150862_g) {
                worldIn.setBlockState(pos, Blocks.air.getDefaultState());
            }
        } else if (!(enumfacing$axis != EnumFacing.Axis.Z || (blockportal$size1 = new Size(worldIn, pos, EnumFacing.Axis.Z)).func_150860_b() && blockportal$size1.field_150864_e >= blockportal$size1.field_150868_h * blockportal$size1.field_150862_g)) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
        }
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        boolean flag5;
        EnumFacing.Axis enumfacing$axis = null;
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (worldIn.getBlockState(pos).getBlock() == this) {
            enumfacing$axis = iblockstate.getValue(AXIS);
            if (enumfacing$axis == null) {
                return false;
            }
            if (enumfacing$axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
                return false;
            }
            if (enumfacing$axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
                return false;
            }
        }
        boolean flag = worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this;
        boolean flag1 = worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this;
        boolean flag2 = worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this;
        boolean flag3 = worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this;
        boolean flag4 = flag || flag1 || enumfacing$axis == EnumFacing.Axis.X;
        boolean bl2 = flag5 = flag2 || flag3 || enumfacing$axis == EnumFacing.Axis.Z;
        return flag4 && side == EnumFacing.WEST ? true : (flag4 && side == EnumFacing.EAST ? true : (flag5 && side == EnumFacing.NORTH ? true : flag5 && side == EnumFacing.SOUTH));
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
            entityIn.setPortal(pos);
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (rand.nextInt(100) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "portal.portal", 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
        int i2 = 0;
        while (i2 < 4) {
            double d0 = (float)pos.getX() + rand.nextFloat();
            double d1 = (float)pos.getY() + rand.nextFloat();
            double d2 = (float)pos.getZ() + rand.nextFloat();
            double d3 = ((double)rand.nextFloat() - 0.5) * 0.5;
            double d4 = ((double)rand.nextFloat() - 0.5) * 0.5;
            double d5 = ((double)rand.nextFloat() - 0.5) * 0.5;
            int j2 = rand.nextInt(2) * 2 - 1;
            if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
                d0 = (double)pos.getX() + 0.5 + 0.25 * (double)j2;
                d3 = rand.nextFloat() * 2.0f * (float)j2;
            } else {
                d2 = (double)pos.getZ() + 0.5 + 0.25 * (double)j2;
                d5 = rand.nextFloat() * 2.0f * (float)j2;
            }
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5, new int[0]);
            ++i2;
        }
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return BlockPortal.getMetaForAxis(state.getValue(AXIS));
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, AXIS);
    }

    public BlockPattern.PatternHelper func_181089_f(World p_181089_1_, BlockPos p_181089_2_) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Z;
        Size blockportal$size = new Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.X);
        LoadingCache<BlockPos, BlockWorldState> loadingcache = BlockPattern.func_181627_a(p_181089_1_, true);
        if (!blockportal$size.func_150860_b()) {
            enumfacing$axis = EnumFacing.Axis.X;
            blockportal$size = new Size(p_181089_1_, p_181089_2_, EnumFacing.Axis.Z);
        }
        if (!blockportal$size.func_150860_b()) {
            return new BlockPattern.PatternHelper(p_181089_2_, EnumFacing.NORTH, EnumFacing.UP, loadingcache, 1, 1, 1);
        }
        int[] aint = new int[EnumFacing.AxisDirection.values().length];
        EnumFacing enumfacing = blockportal$size.field_150866_c.rotateYCCW();
        BlockPos blockpos = blockportal$size.field_150861_f.up(blockportal$size.func_181100_a() - 1);
        EnumFacing.AxisDirection[] axisDirectionArray = EnumFacing.AxisDirection.values();
        int n2 = axisDirectionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumFacing.AxisDirection enumfacing$axisdirection = axisDirectionArray[n3];
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.field_150866_c, blockportal$size.func_181101_b() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.func_181101_b(), blockportal$size.func_181100_a(), 1);
            int i2 = 0;
            while (i2 < blockportal$size.func_181101_b()) {
                int j2 = 0;
                while (j2 < blockportal$size.func_181100_a()) {
                    BlockWorldState blockworldstate = blockpattern$patternhelper.translateOffset(i2, j2, 1);
                    if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getBlock().getMaterial() != Material.air) {
                        int n4 = enumfacing$axisdirection.ordinal();
                        aint[n4] = aint[n4] + 1;
                    }
                    ++j2;
                }
                ++i2;
            }
            ++n3;
        }
        EnumFacing.AxisDirection enumfacing$axisdirection1 = EnumFacing.AxisDirection.POSITIVE;
        EnumFacing.AxisDirection[] axisDirectionArray2 = EnumFacing.AxisDirection.values();
        int n5 = axisDirectionArray2.length;
        n2 = 0;
        while (n2 < n5) {
            EnumFacing.AxisDirection enumfacing$axisdirection2 = axisDirectionArray2[n2];
            if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()]) {
                enumfacing$axisdirection1 = enumfacing$axisdirection2;
            }
            ++n2;
        }
        return new BlockPattern.PatternHelper(enumfacing.getAxisDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.field_150866_c, blockportal$size.func_181101_b() - 1), EnumFacing.getFacingFromAxis(enumfacing$axisdirection1, enumfacing$axis), EnumFacing.UP, loadingcache, blockportal$size.func_181101_b(), blockportal$size.func_181100_a(), 1);
    }

    public static class Size {
        private final World world;
        private final EnumFacing.Axis axis;
        private final EnumFacing field_150866_c;
        private final EnumFacing field_150863_d;
        private int field_150864_e = 0;
        private BlockPos field_150861_f;
        private int field_150862_g;
        private int field_150868_h;

        public Size(World worldIn, BlockPos p_i45694_2_, EnumFacing.Axis p_i45694_3_) {
            this.world = worldIn;
            this.axis = p_i45694_3_;
            if (p_i45694_3_ == EnumFacing.Axis.X) {
                this.field_150863_d = EnumFacing.EAST;
                this.field_150866_c = EnumFacing.WEST;
            } else {
                this.field_150863_d = EnumFacing.NORTH;
                this.field_150866_c = EnumFacing.SOUTH;
            }
            BlockPos blockpos = p_i45694_2_;
            while (p_i45694_2_.getY() > blockpos.getY() - 21 && p_i45694_2_.getY() > 0 && this.func_150857_a(worldIn.getBlockState(p_i45694_2_.down()).getBlock())) {
                p_i45694_2_ = p_i45694_2_.down();
            }
            int i2 = this.func_180120_a(p_i45694_2_, this.field_150863_d) - 1;
            if (i2 >= 0) {
                this.field_150861_f = p_i45694_2_.offset(this.field_150863_d, i2);
                this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);
                if (this.field_150868_h < 2 || this.field_150868_h > 21) {
                    this.field_150861_f = null;
                    this.field_150868_h = 0;
                }
            }
            if (this.field_150861_f != null) {
                this.field_150862_g = this.func_150858_a();
            }
        }

        protected int func_180120_a(BlockPos p_180120_1_, EnumFacing p_180120_2_) {
            Block block;
            int i2 = 0;
            while (i2 < 22) {
                BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i2);
                if (!this.func_150857_a(this.world.getBlockState(blockpos).getBlock()) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.obsidian) break;
                ++i2;
            }
            return (block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i2)).getBlock()) == Blocks.obsidian ? i2 : 0;
        }

        public int func_181100_a() {
            return this.field_150862_g;
        }

        public int func_181101_b() {
            return this.field_150868_h;
        }

        protected int func_150858_a() {
            this.field_150862_g = 0;
            block0: while (this.field_150862_g < 21) {
                int i2 = 0;
                while (i2 < this.field_150868_h) {
                    BlockPos blockpos = this.field_150861_f.offset(this.field_150866_c, i2).up(this.field_150862_g);
                    Block block = this.world.getBlockState(blockpos).getBlock();
                    if (!this.func_150857_a(block)) break block0;
                    if (block == Blocks.portal) {
                        ++this.field_150864_e;
                    }
                    if (i2 == 0 ? (block = this.world.getBlockState(blockpos.offset(this.field_150863_d)).getBlock()) != Blocks.obsidian : i2 == this.field_150868_h - 1 && (block = this.world.getBlockState(blockpos.offset(this.field_150866_c)).getBlock()) != Blocks.obsidian) break block0;
                    ++i2;
                }
                ++this.field_150862_g;
            }
            int j2 = 0;
            while (j2 < this.field_150868_h) {
                if (this.world.getBlockState(this.field_150861_f.offset(this.field_150866_c, j2).up(this.field_150862_g)).getBlock() != Blocks.obsidian) {
                    this.field_150862_g = 0;
                    break;
                }
                ++j2;
            }
            if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
                return this.field_150862_g;
            }
            this.field_150861_f = null;
            this.field_150868_h = 0;
            this.field_150862_g = 0;
            return 0;
        }

        protected boolean func_150857_a(Block p_150857_1_) {
            return p_150857_1_.blockMaterial == Material.air || p_150857_1_ == Blocks.fire || p_150857_1_ == Blocks.portal;
        }

        public boolean func_150860_b() {
            return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
        }

        public void func_150859_c() {
            int i2 = 0;
            while (i2 < this.field_150868_h) {
                BlockPos blockpos = this.field_150861_f.offset(this.field_150866_c, i2);
                int j2 = 0;
                while (j2 < this.field_150862_g) {
                    this.world.setBlockState(blockpos.up(j2), Blocks.portal.getDefaultState().withProperty(AXIS, this.axis), 2);
                    ++j2;
                }
                ++i2;
            }
        }
    }
}

