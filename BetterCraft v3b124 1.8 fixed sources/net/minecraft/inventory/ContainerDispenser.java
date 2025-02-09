/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDispenser
extends Container {
    private IInventory dispenserInventory;

    public ContainerDispenser(IInventory playerInventory, IInventory dispenserInventoryIn) {
        this.dispenserInventory = dispenserInventoryIn;
        int i2 = 0;
        while (i2 < 3) {
            int j2 = 0;
            while (j2 < 3) {
                this.addSlotToContainer(new Slot(dispenserInventoryIn, j2 + i2 * 3, 62 + j2 * 18, 17 + i2 * 18));
                ++j2;
            }
            ++i2;
        }
        int k2 = 0;
        while (k2 < 3) {
            int i1 = 0;
            while (i1 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, i1 + k2 * 9 + 9, 8 + i1 * 18, 84 + k2 * 18));
                ++i1;
            }
            ++k2;
        }
        int l2 = 0;
        while (l2 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, l2, 8 + l2 * 18, 142));
            ++l2;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.dispenserInventory.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 9 ? !this.mergeItemStack(itemstack1, 9, 45, true) : !this.mergeItemStack(itemstack1, 0, 9, false)) {
                return null;
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(playerIn, itemstack1);
        }
        return itemstack;
    }
}

