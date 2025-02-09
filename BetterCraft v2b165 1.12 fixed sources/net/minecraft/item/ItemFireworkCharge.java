// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.item;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;

public class ItemFireworkCharge extends Item
{
    public static NBTBase getExplosionTag(final ItemStack stack, final String key) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");
            if (nbttagcompound != null) {
                return nbttagcompound.getTag(key);
            }
        }
        return null;
    }
    
    @Override
    public void addInformation(final ItemStack stack, @Nullable final World playerIn, final List<String> tooltip, final ITooltipFlag advanced) {
        if (stack.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("Explosion");
            if (nbttagcompound != null) {
                addExplosionInfo(nbttagcompound, tooltip);
            }
        }
    }
    
    public static void addExplosionInfo(final NBTTagCompound nbt, final List<String> tooltip) {
        final byte b0 = nbt.getByte("Type");
        if (b0 >= 0 && b0 <= 4) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type." + b0).trim());
        }
        else {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.type").trim());
        }
        final int[] aint = nbt.getIntArray("Colors");
        if (aint.length > 0) {
            boolean flag = true;
            String s = "";
            int[] array;
            for (int length = (array = aint).length, n = 0; n < length; ++n) {
                final int i = array[n];
                if (!flag) {
                    s = String.valueOf(s) + ", ";
                }
                flag = false;
                boolean flag2 = false;
                for (int j = 0; j < ItemDye.DYE_COLORS.length; ++j) {
                    if (i == ItemDye.DYE_COLORS[j]) {
                        flag2 = true;
                        s = String.valueOf(s) + I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(j).getUnlocalizedName());
                        break;
                    }
                }
                if (!flag2) {
                    s = String.valueOf(s) + I18n.translateToLocal("item.fireworksCharge.customColor");
                }
            }
            tooltip.add(s);
        }
        final int[] aint2 = nbt.getIntArray("FadeColors");
        if (aint2.length > 0) {
            boolean flag3 = true;
            String s2 = String.valueOf(I18n.translateToLocal("item.fireworksCharge.fadeTo")) + " ";
            int[] array2;
            for (int length2 = (array2 = aint2).length, n2 = 0; n2 < length2; ++n2) {
                final int l = array2[n2];
                if (!flag3) {
                    s2 = String.valueOf(s2) + ", ";
                }
                flag3 = false;
                boolean flag4 = false;
                for (int k = 0; k < 16; ++k) {
                    if (l == ItemDye.DYE_COLORS[k]) {
                        flag4 = true;
                        s2 = String.valueOf(s2) + I18n.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(k).getUnlocalizedName());
                        break;
                    }
                }
                if (!flag4) {
                    s2 = String.valueOf(s2) + I18n.translateToLocal("item.fireworksCharge.customColor");
                }
            }
            tooltip.add(s2);
        }
        final boolean flag5 = nbt.getBoolean("Trail");
        if (flag5) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.trail"));
        }
        final boolean flag6 = nbt.getBoolean("Flicker");
        if (flag6) {
            tooltip.add(I18n.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}
