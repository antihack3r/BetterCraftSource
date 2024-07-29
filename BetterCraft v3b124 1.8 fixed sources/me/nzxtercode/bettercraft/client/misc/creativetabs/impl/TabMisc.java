/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.creativetabs.impl;

import java.util.List;
import me.nzxtercode.bettercraft.client.misc.creativetabs.TabsCreative;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public abstract class TabMisc
extends TabsCreative {
    public TabMisc(int index, String label) {
        super(index, label);
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> itemList) {
        try {
            ItemStack commandBlock = new ItemStack(Blocks.command_block);
            itemList.add(commandBlock);
            ItemStack commandblockminecart = new ItemStack(Items.command_block_minecart);
            itemList.add(commandblockminecart);
            ItemStack enderDragonEgg = new ItemStack(Blocks.dragon_egg);
            itemList.add(enderDragonEgg);
            ItemStack barrier = new ItemStack(Blocks.barrier);
            itemList.add(barrier);
            ItemStack spawner = new ItemStack(Blocks.mob_spawner);
            itemList.add(spawner);
            ItemStack redmushroom = new ItemStack(Blocks.red_mushroom_block);
            itemList.add(redmushroom);
            ItemStack brownmushroom = new ItemStack(Blocks.brown_mushroom_block);
            itemList.add(brownmushroom);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Misc";
    }
}

