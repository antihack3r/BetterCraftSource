/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
        InventoryHelper.dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x2, double y2, double z2, IInventory inventory) {
        int i2 = 0;
        while (i2 < inventory.getSizeInventory()) {
            ItemStack itemstack = inventory.getStackInSlot(i2);
            if (itemstack != null) {
                InventoryHelper.spawnItemStack(worldIn, x2, y2, z2, itemstack);
            }
            ++i2;
        }
    }

    private static void spawnItemStack(World worldIn, double x2, double y2, double z2, ItemStack stack) {
        float f2 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f1 = RANDOM.nextFloat() * 0.8f + 0.1f;
        float f22 = RANDOM.nextFloat() * 0.8f + 0.1f;
        while (stack.stackSize > 0) {
            int i2 = RANDOM.nextInt(21) + 10;
            if (i2 > stack.stackSize) {
                i2 = stack.stackSize;
            }
            stack.stackSize -= i2;
            EntityItem entityitem = new EntityItem(worldIn, x2 + (double)f2, y2 + (double)f1, z2 + (double)f22, new ItemStack(stack.getItem(), i2, stack.getMetadata()));
            if (stack.hasTagCompound()) {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }
            float f3 = 0.05f;
            entityitem.motionX = RANDOM.nextGaussian() * (double)f3;
            entityitem.motionY = RANDOM.nextGaussian() * (double)f3 + (double)0.2f;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)f3;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
}

