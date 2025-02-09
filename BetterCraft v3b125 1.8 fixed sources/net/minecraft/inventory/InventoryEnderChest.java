/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest
extends InventoryBasic {
    private TileEntityEnderChest associatedChest;

    public InventoryEnderChest() {
        super("container.enderchest", false, 27);
    }

    public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
        this.associatedChest = chestTileEntity;
    }

    public void loadInventoryFromNBT(NBTTagList p_70486_1_) {
        int i2 = 0;
        while (i2 < this.getSizeInventory()) {
            this.setInventorySlotContents(i2, null);
            ++i2;
        }
        int k2 = 0;
        while (k2 < p_70486_1_.tagCount()) {
            NBTTagCompound nbttagcompound = p_70486_1_.getCompoundTagAt(k2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            if (j2 >= 0 && j2 < this.getSizeInventory()) {
                this.setInventorySlotContents(j2, ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
            ++k2;
        }
    }

    public NBTTagList saveInventoryToNBT() {
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.getSizeInventory()) {
            ItemStack itemstack = this.getStackInSlot(i2);
            if (itemstack != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
            ++i2;
        }
        return nbttaglist;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (this.associatedChest != null) {
            this.associatedChest.openChest();
        }
        super.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (this.associatedChest != null) {
            this.associatedChest.closeChest();
        }
        super.closeInventory(player);
        this.associatedChest = null;
    }
}

