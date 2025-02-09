/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipesMapCloning
implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i2 = 0;
        ItemStack itemstack = null;
        int j2 = 0;
        while (j2 < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1 != null) {
                if (itemstack1.getItem() == Items.filled_map) {
                    if (itemstack != null) {
                        return false;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.map) {
                        return false;
                    }
                    ++i2;
                }
            }
            ++j2;
        }
        return itemstack != null && i2 > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int i2 = 0;
        ItemStack itemstack = null;
        int j2 = 0;
        while (j2 < inv.getSizeInventory()) {
            ItemStack itemstack1 = inv.getStackInSlot(j2);
            if (itemstack1 != null) {
                if (itemstack1.getItem() == Items.filled_map) {
                    if (itemstack != null) {
                        return null;
                    }
                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.map) {
                        return null;
                    }
                    ++i2;
                }
            }
            ++j2;
        }
        if (itemstack != null && i2 >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.filled_map, i2 + 1, itemstack.getMetadata());
            if (itemstack.hasDisplayName()) {
                itemstack2.setStackDisplayName(itemstack.getDisplayName());
            }
            return itemstack2;
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
        int i2 = 0;
        while (i2 < aitemstack.length) {
            ItemStack itemstack = inv.getStackInSlot(i2);
            if (itemstack != null && itemstack.getItem().hasContainerItem()) {
                aitemstack[i2] = new ItemStack(itemstack.getItem().getContainerItem());
            }
            ++i2;
        }
        return aitemstack;
    }
}

