/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer
extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;

    public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));
        int i2 = 0;
        while (i2 < 2) {
            int j2 = 0;
            while (j2 < 2) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j2 + i2 * 2, 88 + j2 * 18, 26 + i2 * 18));
                ++j2;
            }
            ++i2;
        }
        int k2 = 0;
        while (k2 < 4) {
            final int k_f = k2;
            this.addSlotToContainer(new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - k2, 8, 8 + k2 * 18){

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack == null ? false : (stack.getItem() instanceof ItemArmor ? ((ItemArmor)stack.getItem()).armorType == k_f : (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull ? false : k_f == 0));
                }

                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[k_f];
                }
            });
            ++k2;
        }
        int l2 = 0;
        while (l2 < 3) {
            int j1 = 0;
            while (j1 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, j1 + (l2 + 1) * 9, 8 + j1 * 18, 84 + l2 * 18));
                ++j1;
            }
            ++l2;
        }
        int i1 = 0;
        while (i1 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
            ++i1;
        }
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        int i2 = 0;
        while (i2 < 4) {
            ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i2);
            if (itemstack != null) {
                playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
            }
            ++i2;
        }
        this.craftResult.setInventorySlotContents(0, null);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            int i2;
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 1 && index < 5 ? !this.mergeItemStack(itemstack1, 9, 45, false) : (index >= 5 && index < 9 ? !this.mergeItemStack(itemstack1, 9, 45, false) : (itemstack.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack() ? !this.mergeItemStack(itemstack1, i2 = 5 + ((ItemArmor)itemstack.getItem()).armorType, i2 + 1, false) : (index >= 9 && index < 36 ? !this.mergeItemStack(itemstack1, 36, 45, false) : (index >= 36 && index < 45 ? !this.mergeItemStack(itemstack1, 9, 36, false) : !this.mergeItemStack(itemstack1, 9, 45, false)))))) {
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

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}

