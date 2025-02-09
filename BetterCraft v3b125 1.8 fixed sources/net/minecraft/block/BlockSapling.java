/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling
extends BlockBush
implements IGrowable {
    public static final PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

    protected BlockSapling() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, 0));
        float f2 = 0.4f;
        this.setBlockBounds(0.5f - f2, 0.0f, 0.5f - f2, 0.5f + f2, f2 * 2.0f, 0.5f + f2);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(String.valueOf(this.getUnlocalizedName()) + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }

    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }

    public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        WorldGenAbstractTree worldgenerator = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int i2 = 0;
        int j2 = 0;
        boolean flag = false;
        switch (state.getValue(TYPE)) {
            case SPRUCE: {
                i2 = 0;
                block7: while (i2 >= -1) {
                    j2 = 0;
                    while (j2 >= -1) {
                        if (this.func_181624_a(worldIn, pos, i2, j2, BlockPlanks.EnumType.SPRUCE)) {
                            worldgenerator = new WorldGenMegaPineTree(false, rand.nextBoolean());
                            flag = true;
                            break block7;
                        }
                        --j2;
                    }
                    --i2;
                }
                if (flag) break;
                j2 = 0;
                i2 = 0;
                worldgenerator = new WorldGenTaiga2(true);
                break;
            }
            case BIRCH: {
                worldgenerator = new WorldGenForest(true, false);
                break;
            }
            case JUNGLE: {
                IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
                IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, false);
                i2 = 0;
                block9: while (i2 >= -1) {
                    j2 = 0;
                    while (j2 >= -1) {
                        if (this.func_181624_a(worldIn, pos, i2, j2, BlockPlanks.EnumType.JUNGLE)) {
                            worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
                            flag = true;
                            break block9;
                        }
                        --j2;
                    }
                    --i2;
                }
                if (flag) break;
                j2 = 0;
                i2 = 0;
                worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
                break;
            }
            case ACACIA: {
                worldgenerator = new WorldGenSavannaTree(true);
                break;
            }
            case DARK_OAK: {
                i2 = 0;
                block11: while (i2 >= -1) {
                    j2 = 0;
                    while (j2 >= -1) {
                        if (this.func_181624_a(worldIn, pos, i2, j2, BlockPlanks.EnumType.DARK_OAK)) {
                            worldgenerator = new WorldGenCanopyTree(true);
                            flag = true;
                            break block11;
                        }
                        --j2;
                    }
                    --i2;
                }
                if (flag) break;
                return;
            }
        }
        IBlockState iblockstate2 = Blocks.air.getDefaultState();
        if (flag) {
            worldIn.setBlockState(pos.add(i2, 0, j2), iblockstate2, 4);
            worldIn.setBlockState(pos.add(i2 + 1, 0, j2), iblockstate2, 4);
            worldIn.setBlockState(pos.add(i2, 0, j2 + 1), iblockstate2, 4);
            worldIn.setBlockState(pos.add(i2 + 1, 0, j2 + 1), iblockstate2, 4);
        } else {
            worldIn.setBlockState(pos, iblockstate2, 4);
        }
        if (!((WorldGenerator)worldgenerator).generate(worldIn, rand, pos.add(i2, 0, j2))) {
            if (flag) {
                worldIn.setBlockState(pos.add(i2, 0, j2), state, 4);
                worldIn.setBlockState(pos.add(i2 + 1, 0, j2), state, 4);
                worldIn.setBlockState(pos.add(i2, 0, j2 + 1), state, 4);
                worldIn.setBlockState(pos.add(i2 + 1, 0, j2 + 1), state, 4);
            } else {
                worldIn.setBlockState(pos, state, 4);
            }
        }
    }

    private boolean func_181624_a(World p_181624_1_, BlockPos p_181624_2_, int p_181624_3_, int p_181624_4_, BlockPlanks.EnumType p_181624_5_) {
        return this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_ + 1), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), p_181624_5_);
    }

    public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanks.EnumType type) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock() == this && iblockstate.getValue(TYPE) == type;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMetadata();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        BlockPlanks.EnumType[] enumTypeArray = BlockPlanks.EnumType.values();
        int n2 = enumTypeArray.length;
        int n3 = 0;
        while (n3 < n2) {
            BlockPlanks.EnumType blockplanks$enumtype = enumTypeArray[n3];
            list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
            ++n3;
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return (double)worldIn.rand.nextFloat() < 0.45;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        this.grow(worldIn, pos, state, rand);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i2 = 0;
        i2 |= state.getValue(TYPE).getMetadata();
        return i2 |= state.getValue(STAGE) << 3;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, TYPE, STAGE);
    }
}

