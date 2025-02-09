/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerBrewingStand
extends Container {
    private IInventory tileBrewingStand;
    private final Slot theSlot;
    private int brewTime;

    public ContainerBrewingStand(InventoryPlayer playerInventory, IInventory tileBrewingStandIn) {
        this.tileBrewingStand = tileBrewingStandIn;
        this.addSlotToContainer(new Potion(playerInventory.player, tileBrewingStandIn, 0, 56, 46));
        this.addSlotToContainer(new Potion(playerInventory.player, tileBrewingStandIn, 1, 79, 53));
        this.addSlotToContainer(new Potion(playerInventory.player, tileBrewingStandIn, 2, 102, 46));
        this.theSlot = this.addSlotToContainer(new Ingredient(tileBrewingStandIn, 3, 79, 17));
        int i2 = 0;
        while (i2 < 3) {
            int j2 = 0;
            while (j2 < 9) {
                this.addSlotToContainer(new Slot(playerInventory, j2 + i2 * 9 + 9, 8 + j2 * 18, 84 + i2 * 18));
                ++j2;
            }
            ++i2;
        }
        int k2 = 0;
        while (k2 < 9) {
            this.addSlotToContainer(new Slot(playerInventory, k2, 8 + k2 * 18, 142));
            ++k2;
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting listener) {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileBrewingStand);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int i2 = 0;
        while (i2 < this.crafters.size()) {
            ICrafting icrafting = (ICrafting)this.crafters.get(i2);
            if (this.brewTime != this.tileBrewingStand.getField(0)) {
                icrafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getField(0));
            }
            ++i2;
        }
        this.brewTime = this.tileBrewingStand.getField(0);
    }

    @Override
    public void updateProgressBar(int id2, int data) {
        this.tileBrewingStand.setField(id2, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileBrewingStand.isUseableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if ((index < 0 || index > 2) && index != 3) {
                if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(itemstack1) ? !this.mergeItemStack(itemstack1, 3, 4, false) : (Potion.canHoldPotion(itemstack) ? !this.mergeItemStack(itemstack1, 0, 3, false) : (index >= 4 && index < 31 ? !this.mergeItemStack(itemstack1, 31, 40, false) : (index >= 31 && index < 40 ? !this.mergeItemStack(itemstack1, 4, 31, false) : !this.mergeItemStack(itemstack1, 4, 40, false))))) {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(itemstack1, 4, 40, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
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

    class Ingredient
    extends Slot {
        public Ingredient(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack != null ? stack.getItem().isPotionIngredient(stack) : false;
        }

        @Override
        public int getSlotStackLimit() {
            return 64;
        }
    }

    static class Potion
    extends Slot {
        private EntityPlayer player;

        public Potion(EntityPlayer playerIn, IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
            this.player = playerIn;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return Potion.canHoldPotion(stack);
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
            if (stack.getItem() == Items.potionitem && stack.getMetadata() > 0) {
                this.player.triggerAchievement(AchievementList.potion);
            }
            super.onPickupFromSlot(playerIn, stack);
        }

        public static boolean canHoldPotion(ItemStack stack) {
            return stack != null && (stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle);
        }
    }
}

