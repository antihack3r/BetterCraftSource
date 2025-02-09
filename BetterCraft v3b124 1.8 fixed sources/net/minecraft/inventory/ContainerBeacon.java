/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBeacon
extends Container {
    private IInventory tileBeacon;
    private final BeaconSlot beaconSlot;

    public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
        this.tileBeacon = tileBeaconIn;
        this.beaconSlot = new BeaconSlot(tileBeaconIn, 0, 136, 110);
        this.addSlotToContainer(this.beaconSlot);
        int i2 = 36;
        int j2 = 137;
        int k2 = 0;
        while (k2 < 3) {
            int l2 = 0;
            while (l2 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, l2 + k2 * 9 + 9, i2 + l2 * 18, j2 + k2 * 18));
                ++l2;
            }
            ++k2;
        }
        int i1 = 0;
        while (i1 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, i1, i2 + i1 * 18, 58 + j2));
            ++i1;
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting listener) {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileBeacon);
    }

    @Override
    public void updateProgressBar(int id2, int data) {
        this.tileBeacon.setField(id2, data);
    }

    public IInventory func_180611_e() {
        return this.tileBeacon;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        ItemStack itemstack;
        super.onContainerClosed(playerIn);
        if (playerIn != null && !playerIn.worldObj.isRemote && (itemstack = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit())) != null) {
            playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileBeacon.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(itemstack1) && itemstack1.stackSize == 1 ? !this.mergeItemStack(itemstack1, 0, 1, false) : (index >= 1 && index < 28 ? !this.mergeItemStack(itemstack1, 28, 37, false) : (index >= 28 && index < 37 ? !this.mergeItemStack(itemstack1, 1, 28, false) : !this.mergeItemStack(itemstack1, 1, 37, false)))) {
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

    class BeaconSlot
    extends Slot {
        public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
            super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack == null ? false : stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }
}

