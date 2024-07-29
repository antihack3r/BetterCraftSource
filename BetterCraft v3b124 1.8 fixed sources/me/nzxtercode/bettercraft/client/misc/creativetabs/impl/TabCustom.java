/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.creativetabs.impl;

import java.util.List;
import me.nzxtercode.bettercraft.client.misc.creativetabs.TabsCreative;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TabCustom
extends TabsCreative {
    public TabCustom(int index, String label) {
        super(index, label);
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> itemList) {
        try {
            ItemStack slim = new ItemStack(Items.spawn_egg, 1, 55);
            NBTTagCompound nbt1 = new NBTTagCompound();
            nbt1.setDouble("PosX", Double.NaN);
            slim.stackTagCompound = nbt1;
            slim.setStackDisplayName("Big Slime");
            slim.setStackDisplayLore("This is a simple slime");
            itemList.add(slim);
            ItemStack betterarmorstand = new ItemStack(Items.armor_stand);
            String nbt2 = "{EntityTag:{ShowArms:1,NoBasePlate:1}}";
            betterarmorstand.setTagCompound(JsonToNBT.getTagFromJson(nbt2));
            betterarmorstand.setStackDisplayName("BetterArmorStand");
            betterarmorstand.setStackDisplayLore("This is a better armorstand");
            itemList.add(betterarmorstand);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Custom";
    }
}

