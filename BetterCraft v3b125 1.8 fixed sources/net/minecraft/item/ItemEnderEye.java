/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye
extends Item {
    public ItemEnderEye() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (playerIn.canPlayerEdit(pos.offset(side), side, stack) && iblockstate.getBlock() == Blocks.end_portal_frame && !iblockstate.getValue(BlockEndPortalFrame.EYE).booleanValue()) {
            if (worldIn.isRemote) {
                return true;
            }
            worldIn.setBlockState(pos, iblockstate.withProperty(BlockEndPortalFrame.EYE, true), 2);
            worldIn.updateComparatorOutputLevel(pos, Blocks.end_portal_frame);
            --stack.stackSize;
            int i2 = 0;
            while (i2 < 16) {
                double d0 = (float)pos.getX() + (5.0f + itemRand.nextFloat() * 6.0f) / 16.0f;
                double d1 = (float)pos.getY() + 0.8125f;
                double d2 = (float)pos.getZ() + (5.0f + itemRand.nextFloat() * 6.0f) / 16.0f;
                double d3 = 0.0;
                double d4 = 0.0;
                double d5 = 0.0;
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
                ++i2;
            }
            EnumFacing enumfacing = iblockstate.getValue(BlockEndPortalFrame.FACING);
            int l2 = 0;
            int j2 = 0;
            boolean flag1 = false;
            boolean flag = true;
            EnumFacing enumfacing1 = enumfacing.rotateY();
            int k2 = -2;
            while (k2 <= 2) {
                BlockPos blockpos1 = pos.offset(enumfacing1, k2);
                IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);
                if (iblockstate1.getBlock() == Blocks.end_portal_frame) {
                    if (!iblockstate1.getValue(BlockEndPortalFrame.EYE).booleanValue()) {
                        flag = false;
                        break;
                    }
                    j2 = k2;
                    if (!flag1) {
                        l2 = k2;
                        flag1 = true;
                    }
                }
                ++k2;
            }
            if (flag && j2 == l2 + 2) {
                BlockPos blockpos = pos.offset(enumfacing, 4);
                int i1 = l2;
                while (i1 <= j2) {
                    BlockPos blockpos2 = blockpos.offset(enumfacing1, i1);
                    IBlockState iblockstate3 = worldIn.getBlockState(blockpos2);
                    if (iblockstate3.getBlock() != Blocks.end_portal_frame || !iblockstate3.getValue(BlockEndPortalFrame.EYE).booleanValue()) {
                        flag = false;
                        break;
                    }
                    ++i1;
                }
                int j1 = l2 - 1;
                while (j1 <= j2 + 1) {
                    blockpos = pos.offset(enumfacing1, j1);
                    int l1 = 1;
                    while (l1 <= 3) {
                        BlockPos blockpos3 = blockpos.offset(enumfacing, l1);
                        IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
                        if (iblockstate2.getBlock() != Blocks.end_portal_frame || !iblockstate2.getValue(BlockEndPortalFrame.EYE).booleanValue()) {
                            flag = false;
                            break;
                        }
                        ++l1;
                    }
                    j1 += 4;
                }
                if (flag) {
                    int k1 = l2;
                    while (k1 <= j2) {
                        blockpos = pos.offset(enumfacing1, k1);
                        int i22 = 1;
                        while (i22 <= 3) {
                            BlockPos blockpos4 = blockpos.offset(enumfacing, i22);
                            worldIn.setBlockState(blockpos4, Blocks.end_portal.getDefaultState(), 2);
                            ++i22;
                        }
                        ++k1;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        BlockPos blockpos;
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);
        if (movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldIn.getBlockState(movingobjectposition.getBlockPos()).getBlock() == Blocks.end_portal_frame) {
            return itemStackIn;
        }
        if (!worldIn.isRemote && (blockpos = worldIn.getStrongholdPos("Stronghold", new BlockPos(playerIn))) != null) {
            EntityEnderEye entityendereye = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
            entityendereye.moveTowards(blockpos);
            worldIn.spawnEntityInWorld(entityendereye);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
            worldIn.playAuxSFXAtEntity(null, 1002, new BlockPos(playerIn), 0);
            if (!playerIn.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        }
        return itemStackIn;
    }
}

