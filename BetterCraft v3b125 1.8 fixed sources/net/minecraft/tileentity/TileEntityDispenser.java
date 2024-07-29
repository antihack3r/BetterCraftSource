/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;

public class TileEntityDispenser
extends TileEntityLockable
implements IInventory {
    private static final Random RNG = new Random();
    private ItemStack[] stacks = new ItemStack[9];
    protected String customName;

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.stacks[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.stacks[index] != null) {
            if (this.stacks[index].stackSize <= count) {
                ItemStack itemstack1 = this.stacks[index];
                this.stacks[index] = null;
                this.markDirty();
                return itemstack1;
            }
            ItemStack itemstack = this.stacks[index].splitStack(count);
            if (this.stacks[index].stackSize == 0) {
                this.stacks[index] = null;
            }
            this.markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.stacks[index] != null) {
            ItemStack itemstack = this.stacks[index];
            this.stacks[index] = null;
            return itemstack;
        }
        return null;
    }

    public int getDispenseSlot() {
        int i2 = -1;
        int j2 = 1;
        int k2 = 0;
        while (k2 < this.stacks.length) {
            if (this.stacks[k2] != null && RNG.nextInt(j2++) == 0) {
                i2 = k2;
            }
            ++k2;
        }
        return i2;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stacks[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    public int addItemStack(ItemStack stack) {
        int i2 = 0;
        while (i2 < this.stacks.length) {
            if (this.stacks[i2] == null || this.stacks[i2].getItem() == null) {
                this.setInventorySlotContents(i2, stack);
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.dispenser";
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.stacks = new ItemStack[this.getSizeInventory()];
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            if (j2 >= 0 && j2 < this.stacks.length) {
                this.stacks[j2] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
            ++i2;
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.stacks.length) {
            if (this.stacks[i2] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                this.stacks[i2].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
            ++i2;
        }
        compound.setTag("Items", nbttaglist);
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public String getGuiID() {
        return "minecraft:dispenser";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDispenser(playerInventory, this);
    }

    @Override
    public int getField(int id2) {
        return 0;
    }

    @Override
    public void setField(int id2, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        int i2 = 0;
        while (i2 < this.stacks.length) {
            this.stacks[i2] = null;
            ++i2;
        }
    }
}

