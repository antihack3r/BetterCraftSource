/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes
implements IRecipe {
    private final int recipeWidth;
    private final int recipeHeight;
    private final ItemStack[] recipeItems;
    private final ItemStack recipeOutput;
    private boolean copyIngredientNBT;

    public ShapedRecipes(int width, int height, ItemStack[] p_i1917_3_, ItemStack output) {
        this.recipeWidth = width;
        this.recipeHeight = height;
        this.recipeItems = p_i1917_3_;
        this.recipeOutput = output;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
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

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int i2 = 0;
        while (i2 <= 3 - this.recipeWidth) {
            int j2 = 0;
            while (j2 <= 3 - this.recipeHeight) {
                if (this.checkMatch(inv, i2, j2, true)) {
                    return true;
                }
                if (this.checkMatch(inv, i2, j2, false)) {
                    return true;
                }
                ++j2;
            }
            ++i2;
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
        int i2 = 0;
        while (i2 < 3) {
            int j2 = 0;
            while (j2 < 3) {
                ItemStack itemstack1;
                int k2 = i2 - p_77573_2_;
                int l2 = j2 - p_77573_3_;
                ItemStack itemstack = null;
                if (k2 >= 0 && l2 >= 0 && k2 < this.recipeWidth && l2 < this.recipeHeight) {
                    itemstack = p_77573_4_ ? this.recipeItems[this.recipeWidth - k2 - 1 + l2 * this.recipeWidth] : this.recipeItems[k2 + l2 * this.recipeWidth];
                }
                if ((itemstack1 = p_77573_1_.getStackInRowAndColumn(i2, j2)) != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }
                    if (itemstack.getItem() != itemstack1.getItem()) {
                        return false;
                    }
                    if (itemstack.getMetadata() != Short.MAX_VALUE && itemstack.getMetadata() != itemstack1.getMetadata()) {
                        return false;
                    }
                }
                ++j2;
            }
            ++i2;
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = this.getRecipeOutput().copy();
        if (this.copyIngredientNBT) {
            int i2 = 0;
            while (i2 < inv.getSizeInventory()) {
                ItemStack itemstack1 = inv.getStackInSlot(i2);
                if (itemstack1 != null && itemstack1.hasTagCompound()) {
                    itemstack.setTagCompound((NBTTagCompound)itemstack1.getTagCompound().copy());
                }
                ++i2;
            }
        }
        return itemstack;
    }

    @Override
    public int getRecipeSize() {
        return this.recipeWidth * this.recipeHeight;
    }
}

