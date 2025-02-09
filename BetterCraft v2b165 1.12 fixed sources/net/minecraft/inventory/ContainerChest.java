// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerChest extends Container
{
    private final IInventory lowerChestInventory;
    private final int numRows;
    
    public ContainerChest(final IInventory playerInventory, final IInventory chestInventory, final EntityPlayer player) {
        this.lowerChestInventory = chestInventory;
        this.numRows = chestInventory.getSizeInventory() / 9;
        chestInventory.openInventory(player);
        final int i = (this.numRows - 4) * 18;
        for (int j = 0; j < this.numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int j2 = 0; j2 < 9; ++j2) {
                this.addSlotToContainer(new Slot(playerInventory, j2 + l * 9 + 9, 8 + j2 * 18, 103 + l * 18 + i));
            }
        }
        for (int i2 = 0; i2 < 9; ++i2) {
            this.addSlotToContainer(new Slot(playerInventory, i2, 8 + i2 * 18, 161 + i));
        }
    }
    
    @Override
    public boolean canInteractWith(final EntityPlayer playerIn) {
        return this.lowerChestInventory.isUsableByPlayer(playerIn);
    }
    
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
        ItemStack itemstack = ItemStack.field_190927_a;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (index < this.numRows * 9) {
                if (!this.mergeItemStack(itemstack2, this.numRows * 9, this.inventorySlots.size(), true)) {
                    return ItemStack.field_190927_a;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 0, this.numRows * 9, false)) {
                return ItemStack.field_190927_a;
            }
            if (itemstack2.func_190926_b()) {
                slot.putStack(ItemStack.field_190927_a);
            }
            else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
    
    @Override
    public void onContainerClosed(final EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.lowerChestInventory.closeInventory(playerIn);
    }
    
    public IInventory getLowerChestInventory() {
        return this.lowerChestInventory;
    }
}
