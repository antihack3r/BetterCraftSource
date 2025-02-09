// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container
{
    public InventoryCrafting craftMatrix;
    public InventoryCraftResult craftResult;
    private final World worldObj;
    private final BlockPos pos;
    private final EntityPlayer field_192390_i;
    
    public ContainerWorkbench(final InventoryPlayer playerInventory, final World worldIn, final BlockPos posIn) {
        this.craftMatrix = new InventoryCrafting(this, 3, 3);
        this.craftResult = new InventoryCraftResult();
        this.worldObj = worldIn;
        this.pos = posIn;
        this.field_192390_i = playerInventory.player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int i2 = 0; i2 < 9; ++i2) {
                this.addSlotToContainer(new Slot(playerInventory, i2 + k * 9 + 9, 8 + i2 * 18, 84 + k * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }
    
    @Override
    public void onCraftMatrixChanged(final IInventory inventoryIn) {
        this.func_192389_a(this.worldObj, this.field_192390_i, this.craftMatrix, this.craftResult);
    }
    
    @Override
    public void onContainerClosed(final EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!this.worldObj.isRemote) {
            this.func_193327_a(playerIn, this.worldObj, this.craftMatrix);
        }
    }
    
    @Override
    public boolean canInteractWith(final EntityPlayer playerIn) {
        return this.worldObj.getBlockState(this.pos).getBlock() == Blocks.CRAFTING_TABLE && playerIn.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
        ItemStack itemstack = ItemStack.field_190927_a;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack itemstack2 = slot.getStack();
            itemstack = itemstack2.copy();
            if (index == 0) {
                itemstack2.getItem().onCreated(itemstack2, this.worldObj, playerIn);
                if (!this.mergeItemStack(itemstack2, 10, 46, true)) {
                    return ItemStack.field_190927_a;
                }
                slot.onSlotChange(itemstack2, itemstack);
            }
            else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemstack2, 37, 46, false)) {
                    return ItemStack.field_190927_a;
                }
            }
            else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemstack2, 10, 37, false)) {
                    return ItemStack.field_190927_a;
                }
            }
            else if (!this.mergeItemStack(itemstack2, 10, 46, false)) {
                return ItemStack.field_190927_a;
            }
            if (itemstack2.func_190926_b()) {
                slot.putStack(ItemStack.field_190927_a);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack2.func_190916_E() == itemstack.func_190916_E()) {
                return ItemStack.field_190927_a;
            }
            final ItemStack itemstack3 = slot.func_190901_a(playerIn, itemstack2);
            if (index == 0) {
                playerIn.dropItem(itemstack3, false);
            }
        }
        return itemstack;
    }
    
    @Override
    public boolean canMergeSlot(final ItemStack stack, final Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}
