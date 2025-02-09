/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass
extends BlockBush
implements IGrowable {
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    protected BlockTallGrass() {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.DEAD_BUSH));
        float f2 = 0.4f;
        this.setBlockBounds(0.5f - f2, 0.0f, 0.5f - f2, 0.5f + f2, 0.8f, 0.5f + f2);
    }

    @Override
    public int getBlockColor() {
        return ColorizerGrass.getGrassColor(0.5, 1.0);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    @Override
    public boolean isReplaceable(World worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public int getRenderColor(IBlockState state) {
        if (state.getBlock() != this) {
            return super.getRenderColor(state);
        }
        EnumType blocktallgrass$enumtype = state.getValue(TYPE);
        return blocktallgrass$enumtype == EnumType.DEAD_BUSH ? 0xFFFFFF : ColorizerGrass.getGrassColor(0.5, 1.0);
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
        return worldIn.getBiomeGenForCoords(pos).getGrassColorAtPos(pos);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return rand.nextInt(8) == 0 ? Items.wheat_seeds : null;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te2) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            BlockTallGrass.spawnAsEntity(worldIn, pos, new ItemStack(Blocks.tallgrass, 1, state.getValue(TYPE).getMeta()));
        } else {
            super.harvestBlock(worldIn, player, pos, state, te2);
        }
    }

    @Override
    public int getDamageValue(World worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        int i2 = 1;
        while (i2 < 3) {
            list.add(new ItemStack(itemIn, 1, i2));
            ++i2;
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(TYPE) != EnumType.DEAD_BUSH;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.GRASS;
        if (state.getValue(TYPE) == EnumType.FERN) {
            blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.FERN;
        }
        if (Blocks.double_plant.canPlaceBlockAt(worldIn, pos)) {
            Blocks.double_plant.placeAt(worldIn, pos, blockdoubleplant$enumplanttype, 2);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, TYPE);
    }

    @Override
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XYZ;
    }

    public static enum EnumType implements IStringSerializable
    {
        DEAD_BUSH(0, "dead_bush"),
        GRASS(1, "tall_grass"),
        FERN(2, "fern");

        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;

        static {
            META_LOOKUP = new EnumType[EnumType.values().length];
            EnumType[] enumTypeArray = EnumType.values();
            int n2 = enumTypeArray.length;
            int n3 = 0;
            while (n3 < n2) {
                EnumType blocktallgrass$enumtype;
                EnumType.META_LOOKUP[blocktallgrass$enumtype.getMeta()] = blocktallgrass$enumtype = enumTypeArray[n3];
                ++n3;
            }
        }

        private EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

