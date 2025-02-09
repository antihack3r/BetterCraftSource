/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending
extends ShapedRecipes {
    public RecipesMapExtending() {
        super(3, 3, new ItemStack[]{new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.filled_map, 0, Short.MAX_VALUE), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper)}, new ItemStack(Items.map, 0, 0));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        if (!super.matches(inv, worldIn)) {
            return false;
        }
        ItemStack itemstack = null;
        int i2 = 0;
        while (i2 < inv.getSizeInventory() && itemstack == null) {
            ItemStack itemstack1 = inv.getStackInSlot(i2);
            if (itemstack1 != null && itemstack1.getItem() == Items.filled_map) {
                itemstack = itemstack1;
            }
            ++i2;
        }
        if (itemstack == null) {
            return false;
        }
        MapData mapdata = Items.filled_map.getMapData(itemstack, worldIn);
        return mapdata == null ? false : mapdata.scale < 4;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = null;
        int i2 = 0;
        while (i2 < inv.getSizeInventory() && itemstack == null) {
            ItemStack itemstack1 = inv.getStackInSlot(i2);
            if (itemstack1 != null && itemstack1.getItem() == Items.filled_map) {
                itemstack = itemstack1;
            }
            ++i2;
        }
        itemstack = itemstack.copy();
        itemstack.stackSize = 1;
        if (itemstack.getTagCompound() == null) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
        itemstack.getTagCompound().setBoolean("map_is_scaling", true);
        return itemstack;
    }
}

