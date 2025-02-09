/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook
extends Item {
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isItemTool(ItemStack stack) {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return this.getEnchantments(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
    }

    public NBTTagList getEnchantments(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return nbttagcompound != null && nbttagcompound.hasKey("StoredEnchantments", 9) ? (NBTTagList)nbttagcompound.getTag("StoredEnchantments") : new NBTTagList();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        NBTTagList nbttaglist = this.getEnchantments(stack);
        if (nbttaglist != null) {
            int i2 = 0;
            while (i2 < nbttaglist.tagCount()) {
                short j2 = nbttaglist.getCompoundTagAt(i2).getShort("id");
                short k2 = nbttaglist.getCompoundTagAt(i2).getShort("lvl");
                if (Enchantment.getEnchantmentById(j2) != null) {
                    tooltip.add(Enchantment.getEnchantmentById(j2).getTranslatedName(k2));
                }
                ++i2;
            }
        }
    }

    public void addEnchantment(ItemStack stack, EnchantmentData enchantment) {
        NBTTagList nbttaglist = this.getEnchantments(stack);
        boolean flag = true;
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            if (nbttagcompound.getShort("id") == enchantment.enchantmentobj.effectId) {
                if (nbttagcompound.getShort("lvl") < enchantment.enchantmentLevel) {
                    nbttagcompound.setShort("lvl", (short)enchantment.enchantmentLevel);
                }
                flag = false;
                break;
            }
            ++i2;
        }
        if (flag) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short)enchantment.enchantmentobj.effectId);
            nbttagcompound1.setShort("lvl", (short)enchantment.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound1);
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag("StoredEnchantments", nbttaglist);
    }

    public ItemStack getEnchantedItemStack(EnchantmentData data) {
        ItemStack itemstack = new ItemStack(this);
        this.addEnchantment(itemstack, data);
        return itemstack;
    }

    public void getAll(Enchantment enchantment, List<ItemStack> list) {
        int i2 = enchantment.getMinLevel();
        while (i2 <= enchantment.getMaxLevel()) {
            list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, i2)));
            ++i2;
        }
    }

    public WeightedRandomChestContent getRandom(Random rand) {
        return this.getRandom(rand, 1, 1, 1);
    }

    public WeightedRandomChestContent getRandom(Random rand, int minChance, int maxChance, int weight) {
        ItemStack itemstack = new ItemStack(Items.book, 1, 0);
        EnchantmentHelper.addRandomEnchantment(rand, itemstack, 30);
        return new WeightedRandomChestContent(itemstack, minChance, maxChance, weight);
    }
}

