/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHopper
extends Container {
    private final IInventory hopperInventory;

    public ContainerHopper(InventoryPlayer playerInventory, IInventory hopperInventoryIn, EntityPlayer player) {
        this.hopperInventory = hopperInventoryIn;
        hopperInventoryIn.openInventory(player);
        int i2 = 51;
        int j2 = 0;
        while (j2 < hopperInventoryIn.getSizeInventory()) {
            this.addSlotToContainer(new Slot(hopperInventoryIn, j2, 44 + j2 * 18, 20));
            ++j2;
        }
        int l2 = 0;
        while (l2 < 3) {
            int k2 = 0;
            while (k2 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, k2 + l2 * 9 + 9, 8 + k2 * 18, l2 * 18 + i2));
                ++k2;
            }
            ++l2;
        }
        int i1 = 0;
        while (i1 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 58 + i2));
            ++i1;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.hopperInventory.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.hopperInventory.getSizeInventory() ? !this.mergeItemStack(itemstack1, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true) : !this.mergeItemStack(itemstack1, 0, this.hopperInventory.getSizeInventory(), false)) {
                return null;
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.hopperInventory.closeInventory(playerIn);
    }
}

