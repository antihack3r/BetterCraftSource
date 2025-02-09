/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand
extends TileEntityLockable
implements ITickable,
ISidedInventory {
    private static final int[] inputSlots = new int[]{3};
    private static final int[] outputSlots;
    private ItemStack[] brewingItemStacks = new ItemStack[4];
    private int brewTime;
    private boolean[] filledSlots;
    private Item ingredientID;
    private String customName;

    static {
        int[] nArray = new int[3];
        nArray[1] = 1;
        nArray[2] = 2;
        outputSlots = nArray;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.brewing";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setName(String name) {
        this.customName = name;
    }

    @Override
    public int getSizeInventory() {
        return this.brewingItemStacks.length;
    }

    @Override
    public void update() {
        boolean[] aboolean;
        if (this.brewTime > 0) {
            --this.brewTime;
            if (this.brewTime == 0) {
                this.brewPotions();
                this.markDirty();
            } else if (!this.canBrew()) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (this.canBrew()) {
            this.brewTime = 400;
            this.ingredientID = this.brewingItemStacks[3].getItem();
        }
        if (!this.worldObj.isRemote && !Arrays.equals(aboolean = this.func_174902_m(), this.filledSlots)) {
            this.filledSlots = aboolean;
            IBlockState iblockstate = this.worldObj.getBlockState(this.getPos());
            if (!(iblockstate.getBlock() instanceof BlockBrewingStand)) {
                return;
            }
            int i2 = 0;
            while (i2 < BlockBrewingStand.HAS_BOTTLE.length) {
                iblockstate = iblockstate.withProperty(BlockBrewingStand.HAS_BOTTLE[i2], aboolean[i2]);
                ++i2;
            }
            this.worldObj.setBlockState(this.pos, iblockstate, 2);
        }
    }

    private boolean canBrew() {
        if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
            ItemStack itemstack = this.brewingItemStacks[3];
            if (!itemstack.getItem().isPotionIngredient(itemstack)) {
                return false;
            }
            boolean flag = false;
            int i2 = 0;
            while (i2 < 3) {
                if (this.brewingItemStacks[i2] != null && this.brewingItemStacks[i2].getItem() == Items.potionitem) {
                    int j2 = this.brewingItemStacks[i2].getMetadata();
                    int k2 = this.getPotionResult(j2, itemstack);
                    if (!ItemPotion.isSplash(j2) && ItemPotion.isSplash(k2)) {
                        flag = true;
                        break;
                    }
                    List<PotionEffect> list = Items.potionitem.getEffects(j2);
                    List<PotionEffect> list1 = Items.potionitem.getEffects(k2);
                    if (!(j2 > 0 && list == list1 || list != null && (list.equals(list1) || list1 == null) || j2 == k2)) {
                        flag = true;
                        break;
                    }
                }
                ++i2;
            }
            return flag;
        }
        return false;
    }

    private void brewPotions() {
        if (this.canBrew()) {
            ItemStack itemstack = this.brewingItemStacks[3];
            int i2 = 0;
            while (i2 < 3) {
                if (this.brewingItemStacks[i2] != null && this.brewingItemStacks[i2].getItem() == Items.potionitem) {
                    int j2 = this.brewingItemStacks[i2].getMetadata();
                    int k2 = this.getPotionResult(j2, itemstack);
                    List<PotionEffect> list = Items.potionitem.getEffects(j2);
                    List<PotionEffect> list1 = Items.potionitem.getEffects(k2);
                    if (j2 > 0 && list == list1 || list != null && (list.equals(list1) || list1 == null)) {
                        if (!ItemPotion.isSplash(j2) && ItemPotion.isSplash(k2)) {
                            this.brewingItemStacks[i2].setItemDamage(k2);
                        }
                    } else if (j2 != k2) {
                        this.brewingItemStacks[i2].setItemDamage(k2);
                    }
                }
                ++i2;
            }
            if (itemstack.getItem().hasContainerItem()) {
                this.brewingItemStacks[3] = new ItemStack(itemstack.getItem().getContainerItem());
            } else {
                --this.brewingItemStacks[3].stackSize;
                if (this.brewingItemStacks[3].stackSize <= 0) {
                    this.brewingItemStacks[3] = null;
                }
            }
        }
    }

    private int getPotionResult(int meta, ItemStack stack) {
        return stack == null ? meta : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.brewingItemStacks = new ItemStack[this.getSizeInventory()];
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            byte j2 = nbttagcompound.getByte("Slot");
            if (j2 >= 0 && j2 < this.brewingItemStacks.length) {
                this.brewingItemStacks[j2] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
            ++i2;
        }
        this.brewTime = compound.getShort("BrewTime");
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("BrewTime", (short)this.brewTime);
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.brewingItemStacks.length) {
            if (this.brewingItemStacks[i2] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                this.brewingItemStacks[i2].writeToNBT(nbttagcompound);
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
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            ItemStack itemstack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            ItemStack itemstack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            this.brewingItemStacks[index] = stack;
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
        return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
    }

    public boolean[] func_174902_m() {
        boolean[] aboolean = new boolean[3];
        int i2 = 0;
        while (i2 < 3) {
            if (this.brewingItemStacks[i2] != null) {
                aboolean[i2] = true;
            }
            ++i2;
        }
        return aboolean;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? inputSlots : outputSlots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public String getGuiID() {
        return "minecraft:brewing_stand";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerBrewingStand(playerInventory, this);
    }

    @Override
    public int getField(int id2) {
        switch (id2) {
            case 0: {
                return this.brewTime;
            }
        }
        return 0;
    }

    @Override
    public void setField(int id2, int value) {
        switch (id2) {
            case 0: {
                this.brewTime = value;
            }
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        int i2 = 0;
        while (i2 < this.brewingItemStacks.length) {
            this.brewingItemStacks[i2] = null;
            ++i2;
        }
    }
}

