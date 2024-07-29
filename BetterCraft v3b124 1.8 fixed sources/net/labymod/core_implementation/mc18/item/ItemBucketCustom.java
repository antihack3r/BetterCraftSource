/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.item;

import net.labymod.api.permissions.Permissions;
import net.labymod.api.protocol.liquid.FixedLiquidBucketProtocol;
import net.labymod.main.LabyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucketCustom
extends ItemBucket {
    private Block isFull;

    public ItemBucketCustom(Block containedBlock) {
        super(containedBlock);
        this.isFull = containedBlock;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        boolean allowed;
        boolean flag = this.isFull == Blocks.air;
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, flag);
        if (movingobjectposition == null) {
            return itemStackIn;
        }
        boolean bl2 = allowed = worldIn.isRemote && LabyMod.getSettings().improvedLavaFixedGhostBlocks && Permissions.isAllowed(Permissions.Permission.IMPROVED_LAVA);
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockpos = movingobjectposition.getBlockPos();
            if (!worldIn.isBlockModifiable(playerIn, blockpos)) {
                return itemStackIn;
            }
            if (flag) {
                if (!playerIn.canPlayerEdit(blockpos.offset(movingobjectposition.sideHit), movingobjectposition.sideHit, itemStackIn)) {
                    return itemStackIn;
                }
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Material material = iblockstate.getBlock().getMaterial();
                if (material == Material.water && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
                    if (!allowed) {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.water_bucket);
                    }
                    FixedLiquidBucketProtocol.handleBucketAction(FixedLiquidBucketProtocol.Action.FILL_BUCKET, blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
                if (material == Material.lava && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
                    if (!allowed) {
                        worldIn.setBlockToAir(blockpos);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.lava_bucket);
                    }
                    FixedLiquidBucketProtocol.handleBucketAction(FixedLiquidBucketProtocol.Action.FILL_BUCKET, blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
            } else {
                if (this.isFull == Blocks.air) {
                    return new ItemStack(Items.bucket);
                }
                BlockPos blockpos2 = blockpos.offset(movingobjectposition.sideHit);
                if (!playerIn.canPlayerEdit(blockpos2, movingobjectposition.sideHit, itemStackIn)) {
                    return itemStackIn;
                }
                if (this.tryPlaceContainedLiquid(worldIn, blockpos2) && !playerIn.capabilities.isCreativeMode && !allowed) {
                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                    return new ItemStack(Items.bucket);
                }
            }
        }
        return itemStackIn;
    }

    private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket) {
        if (player.capabilities.isCreativeMode) {
            return emptyBuckets;
        }
        if (--emptyBuckets.stackSize <= 0) {
            return new ItemStack(fullBucket);
        }
        if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
            player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);
        }
        return emptyBuckets;
    }

    @Override
    public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos) {
        boolean flag;
        if (this.isFull == Blocks.air) {
            return false;
        }
        Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
        boolean bl2 = flag = !material.isSolid();
        if (!worldIn.isAirBlock(pos) && !flag) {
            return false;
        }
        if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water) {
            int i2 = pos.getX();
            int j2 = pos.getY();
            int k2 = pos.getZ();
            worldIn.playSoundEffect((float)i2 + 0.5f, (float)j2 + 0.5f, (float)k2 + 0.5f, "random.fizz", 0.5f, 2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f);
            int l2 = 0;
            while (l2 < 8) {
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)i2 + Math.random(), (double)j2 + Math.random(), (double)k2 + Math.random(), 0.0, 0.0, 0.0, new int[0]);
                ++l2;
            }
        } else {
            boolean allowed;
            if (!worldIn.isRemote && flag && !material.isLiquid()) {
                worldIn.destroyBlock(pos, true);
            }
            boolean bl3 = allowed = worldIn.isRemote && LabyMod.getSettings().improvedLavaFixedGhostBlocks && Permissions.isAllowed(Permissions.Permission.IMPROVED_LAVA);
            if (allowed) {
                FixedLiquidBucketProtocol.handleBucketAction(FixedLiquidBucketProtocol.Action.EMPTY_BUCKET, pos.getX(), pos.getY(), pos.getZ());
            } else {
                worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
            }
        }
        return true;
    }
}

