// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block;

import net.minecraft.util.IStringSerializable;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;

public class BlockStone extends Block
{
    public static final PropertyEnum<EnumType> VARIANT;
    
    static {
        VARIANT = PropertyEnum.create("variant", EnumType.class);
    }
    
    public BlockStone() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStone.VARIANT, EnumType.STONE));
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    public String getLocalizedName() {
        return I18n.translateToLocal(String.valueOf(this.getUnlocalizedName()) + "." + EnumType.STONE.getUnlocalizedName() + ".name");
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state, final IBlockAccess p_180659_2_, final BlockPos p_180659_3_) {
        return state.getValue(BlockStone.VARIANT).getMapColor();
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return (state.getValue(BlockStone.VARIANT) == EnumType.STONE) ? Item.getItemFromBlock(Blocks.COBBLESTONE) : Item.getItemFromBlock(Blocks.STONE);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockStone.VARIANT).getMetadata();
    }
    
    @Override
    public void getSubBlocks(final CreativeTabs itemIn, final NonNullList<ItemStack> tab) {
        EnumType[] values;
        for (int length = (values = EnumType.values()).length, i = 0; i < length; ++i) {
            final EnumType blockstone$enumtype = values[i];
            tab.add(new ItemStack(this, 1, blockstone$enumtype.getMetadata()));
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockStone.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, (IProperty<?>[])new IProperty[] { BlockStone.VARIANT });
    }
    
    public enum EnumType implements IStringSerializable
    {
        STONE("STONE", 0, 0, MapColor.STONE, "stone", true), 
        GRANITE("GRANITE", 1, 1, MapColor.DIRT, "granite", true), 
        GRANITE_SMOOTH("GRANITE_SMOOTH", 2, 2, MapColor.DIRT, "smooth_granite", "graniteSmooth", false), 
        DIORITE("DIORITE", 3, 3, MapColor.QUARTZ, "diorite", true), 
        DIORITE_SMOOTH("DIORITE_SMOOTH", 4, 4, MapColor.QUARTZ, "smooth_diorite", "dioriteSmooth", false), 
        ANDESITE("ANDESITE", 5, 5, MapColor.STONE, "andesite", true), 
        ANDESITE_SMOOTH("ANDESITE_SMOOTH", 6, 6, MapColor.STONE, "smooth_andesite", "andesiteSmooth", false);
        
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private final String unlocalizedName;
        private final MapColor mapColor;
        private final boolean field_190913_m;
        
        static {
            META_LOOKUP = new EnumType[values().length];
            EnumType[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final EnumType blockstone$enumtype = values[i];
                EnumType.META_LOOKUP[blockstone$enumtype.getMetadata()] = blockstone$enumtype;
            }
        }
        
        private EnumType(final String s, final int n, final int p_i46383_3_, final MapColor p_i46383_4_, final String p_i46383_5_, final boolean p_i46383_6_) {
            this(s, n, p_i46383_3_, p_i46383_4_, p_i46383_5_, p_i46383_5_, p_i46383_6_);
        }
        
        private EnumType(final String s, final int n, final int p_i46384_3_, final MapColor p_i46384_4_, final String p_i46384_5_, final String p_i46384_6_, final boolean p_i46384_7_) {
            this.meta = p_i46384_3_;
            this.name = p_i46384_5_;
            this.unlocalizedName = p_i46384_6_;
            this.mapColor = p_i46384_4_;
            this.field_190913_m = p_i46384_7_;
        }
        
        public int getMetadata() {
            return this.meta;
        }
        
        public MapColor getMapColor() {
            return this.mapColor;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= EnumType.META_LOOKUP.length) {
                meta = 0;
            }
            return EnumType.META_LOOKUP[meta];
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
        
        public boolean func_190912_e() {
            return this.field_190913_m;
        }
    }
}
