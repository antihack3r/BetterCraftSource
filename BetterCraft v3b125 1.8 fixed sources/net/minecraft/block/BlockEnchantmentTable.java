/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnchantmentTable
extends BlockContainer {
    protected BlockEnchantmentTable() {
        super(Material.rock, MapColor.redColor);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.randomDisplayTick(worldIn, pos, state, rand);
        int i2 = -2;
        while (i2 <= 2) {
            int j2 = -2;
            while (j2 <= 2) {
                if (i2 > -2 && i2 < 2 && j2 == -1) {
                    j2 = 2;
                }
                if (rand.nextInt(16) == 0) {
                    int k2 = 0;
                    while (k2 <= 1) {
                        BlockPos blockpos = pos.add(i2, k2, j2);
                        if (worldIn.getBlockState(blockpos).getBlock() == Blocks.bookshelf) {
                            if (!worldIn.isAirBlock(pos.add(i2 / 2, 0, j2 / 2))) break;
                            worldIn.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double)pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5, (double)((float)i2 + rand.nextFloat()) - 0.5, (double)((float)k2 - rand.nextFloat() - 1.0f), (double)((float)j2 + rand.nextFloat()) - 0.5, new int[0]);
                        }
                        ++k2;
                    }
                }
                ++j2;
            }
            ++i2;
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnchantmentTable();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityEnchantmentTable) {
            playerIn.displayGui((TileEntityEnchantmentTable)tileentity);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileentity;
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasDisplayName() && (tileentity = worldIn.getTileEntity(pos)) instanceof TileEntityEnchantmentTable) {
            ((TileEntityEnchantmentTable)tileentity).setCustomName(stack.getDisplayName());
        }
    }
}

