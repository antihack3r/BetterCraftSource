/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge
extends Item {
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 1) {
            return super.getColorFromItemStack(stack, renderPass);
        }
        NBTBase nbtbase = ItemFireworkCharge.getExplosionTag(stack, "Colors");
        if (!(nbtbase instanceof NBTTagIntArray)) {
            return 0x8A8A8A;
        }
        NBTTagIntArray nbttagintarray = (NBTTagIntArray)nbtbase;
        int[] aint = nbttagintarray.getIntArray();
        if (aint.length == 1) {
            return aint[0];
        }
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int[] nArray = aint;
        int n2 = aint.length;
        int n3 = 0;
        while (n3 < n2) {
            int l2 = nArray[n3];
            i2 += (l2 & 0xFF0000) >> 16;
            j2 += (l2 & 0xFF00) >> 8;
            k2 += (l2 & 0xFF) >> 0;
            ++n3;
        }
        return (i2 /= aint.length) << 16 | (j2 /= aint.length) << 8 | (k2 /= aint.length);
    }

    public static NBTBase getExplosionTag(ItemStack stack, String key) {
        NBTTagCompound nbttagcompound;
        if (stack.hasTagCompound() && (nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion")) != null) {
            return nbttagcompound.getTag(key);
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        NBTTagCompound nbttagcompound;
        if (stack.hasTagCompound() && (nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion")) != null) {
            ItemFireworkCharge.addExplosionInfo(nbttagcompound, tooltip);
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
        boolean flag4;
        boolean flag3;
        int[] aint1;
        int n2;
        byte b0 = nbt.getByte("Type");
        if (b0 >= 0 && b0 <= 4) {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + b0).trim());
        } else {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
        }
        int[] aint = nbt.getIntArray("Colors");
        if (aint.length > 0) {
            boolean flag = true;
            String s2 = "";
            int[] nArray = aint;
            n2 = aint.length;
            int n3 = 0;
            while (n3 < n2) {
                int i2 = nArray[n3];
                if (!flag) {
                    s2 = String.valueOf(s2) + ", ";
                }
                flag = false;
                boolean flag1 = false;
                int j2 = 0;
                while (j2 < ItemDye.dyeColors.length) {
                    if (i2 == ItemDye.dyeColors[j2]) {
                        flag1 = true;
                        s2 = String.valueOf(s2) + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j2).getUnlocalizedName());
                        break;
                    }
                    ++j2;
                }
                if (!flag1) {
                    s2 = String.valueOf(s2) + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
                ++n3;
            }
            tooltip.add(s2);
        }
        if ((aint1 = nbt.getIntArray("FadeColors")).length > 0) {
            boolean flag2 = true;
            String s1 = String.valueOf(StatCollector.translateToLocal("item.fireworksCharge.fadeTo")) + " ";
            int[] nArray = aint1;
            int n4 = aint1.length;
            n2 = 0;
            while (n2 < n4) {
                int l2 = nArray[n2];
                if (!flag2) {
                    s1 = String.valueOf(s1) + ", ";
                }
                flag2 = false;
                boolean flag5 = false;
                int k2 = 0;
                while (k2 < 16) {
                    if (l2 == ItemDye.dyeColors[k2]) {
                        flag5 = true;
                        s1 = String.valueOf(s1) + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k2).getUnlocalizedName());
                        break;
                    }
                    ++k2;
                }
                if (!flag5) {
                    s1 = String.valueOf(s1) + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
                ++n2;
            }
            tooltip.add(s1);
        }
        if (flag3 = nbt.getBoolean("Trail")) {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
        }
        if (flag4 = nbt.getBoolean("Flicker")) {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}

