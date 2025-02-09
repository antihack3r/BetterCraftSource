/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.creativetabs.impl;

import java.util.List;
import me.nzxtercode.bettercraft.client.misc.creativetabs.TabCreativeOwnItems;
import me.nzxtercode.bettercraft.client.misc.creativetabs.TabsCreative;
import net.minecraft.item.ItemStack;

public abstract class TabOwn
extends TabsCreative {
    public TabOwn(int index, String label) {
        super(index, label);
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> itemList) {
        itemList.addAll(TabCreativeOwnItems.getItemstacks());
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Own";
    }
}

