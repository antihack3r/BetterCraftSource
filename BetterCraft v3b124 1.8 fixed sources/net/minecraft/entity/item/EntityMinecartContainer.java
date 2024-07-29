/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer
extends EntityMinecart
implements ILockableContainer {
    private ItemStack[] minecartContainerItems = new ItemStack[36];
    private boolean dropContentsWhenDead = true;

    public EntityMinecartContainer(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartContainer(World worldIn, double x2, double y2, double z2) {
        super(worldIn, x2, y2, z2);
    }

    @Override
    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
            InventoryHelper.dropInventoryItems(this.worldObj, this, (IInventory)this);
        }
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.minecartContainerItems[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.minecartContainerItems[index] != null) {
            if (this.minecartContainerItems[index].stackSize <= count) {
                ItemStack itemstack1 = this.minecartContainerItems[index];
                this.minecartContainerItems[index] = null;
                return itemstack1;
            }
            ItemStack itemstack = this.minecartContainerItems[index].splitStack(count);
            if (this.minecartContainerItems[index].stackSize == 0) {
                this.minecartContainerItems[index] = null;
            }
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.minecartContainerItems[index] != null) {
            ItemStack itemstack = this.minecartContainerItems[index];
            this.minecartContainerItems[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.minecartContainerItems[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.isDead ? false : player.getDistanceSqToEntity(this) <= 64.0;
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
    public String getName() {
        return this.hasCustomName() ? this.getCustomNameTag() : "container.minecart";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void travelToDimension(int dimensionId) {
        this.dropContentsWhenDead = false;
        super.travelToDimension(dimensionId);
    }

    @Override
    public void setDead() {
        if (this.dropContentsWhenDead) {
            InventoryHelper.dropInventoryItems(this.worldObj, this, (IInventory)this);
        }
        super.setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.minecartContainerItems.length) {
            if (this.minecartContainerItems[i2] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                this.minecartContainerItems[i2].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
            ++i2;
        }
        tagCompound.setTag("Items", nbttaglist);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);
        this.minecartContainerItems = new ItemStack[this.getSizeInventory()];
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            if (j2 >= 0 && j2 < this.minecartContainerItems.length) {
                this.minecartContainerItems[j2] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
            ++i2;
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer playerIn) {
        if (!this.worldObj.isRemote) {
            playerIn.displayGUIChest(this);
        }
        return true;
    }

    @Override
    protected void applyDrag() {
        int i2 = 15 - Container.calcRedstoneFromInventory(this);
        float f2 = 0.98f + (float)i2 * 0.001f;
        this.motionX *= (double)f2;
        this.motionY *= 0.0;
        this.motionZ *= (double)f2;
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
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setLockCode(LockCode code) {
    }

    @Override
    public LockCode getLockCode() {
        return LockCode.EMPTY_CODE;
    }

    @Override
    public void clear() {
        int i2 = 0;
        while (i2 < this.minecartContainerItems.length) {
            this.minecartContainerItems[i2] = null;
            ++i2;
        }
    }
}

