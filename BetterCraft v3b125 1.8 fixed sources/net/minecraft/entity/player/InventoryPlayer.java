/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;

public class InventoryPlayer
implements IInventory {
    public ItemStack[] mainInventory = new ItemStack[36];
    public ItemStack[] armorInventory = new ItemStack[4];
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack;
    public boolean inventoryChanged;

    public InventoryPlayer(EntityPlayer playerIn) {
        this.player = playerIn;
    }

    public ItemStack getCurrentItem() {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    public static int getHotbarSize() {
        return 9;
    }

    private int getInventorySlotContainItem(Item itemIn) {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null && this.mainInventory[i2].getItem() == itemIn) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    private int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn) {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null && this.mainInventory[i2].getItem() == itemIn && this.mainInventory[i2].getMetadata() == metadataIn) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    private int storeItemStack(ItemStack itemStackIn) {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null && this.mainInventory[i2].getItem() == itemStackIn.getItem() && this.mainInventory[i2].isStackable() && this.mainInventory[i2].stackSize < this.mainInventory[i2].getMaxStackSize() && this.mainInventory[i2].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i2].getHasSubtypes() || this.mainInventory[i2].getMetadata() == itemStackIn.getMetadata()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i2], itemStackIn)) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    public int getFirstEmptyStack() {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] == null) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean p_146030_4_) {
        int i2;
        ItemStack itemstack = this.getCurrentItem();
        int n2 = i2 = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);
        if (i2 >= 0 && i2 < 9) {
            this.currentItem = i2;
        } else if (p_146030_4_ && itemIn != null) {
            int j2 = this.getFirstEmptyStack();
            if (j2 >= 0 && j2 < 9) {
                this.currentItem = j2;
            }
            if (itemstack == null || !itemstack.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(itemstack.getItem(), itemstack.getItemDamage()) != this.currentItem) {
                int l2;
                int k2 = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
                if (k2 >= 0) {
                    l2 = this.mainInventory[k2].stackSize;
                    this.mainInventory[k2] = this.mainInventory[this.currentItem];
                } else {
                    l2 = 1;
                }
                this.mainInventory[this.currentItem] = new ItemStack(itemIn, l2, metadataIn);
            }
        }
    }

    public void changeCurrentItem(int direction) {
        if (direction > 0) {
            direction = 1;
        }
        if (direction < 0) {
            direction = -1;
        }
        this.currentItem -= direction;
        while (this.currentItem < 0) {
            this.currentItem += 9;
        }
        while (this.currentItem >= 9) {
            this.currentItem -= 9;
        }
    }

    public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
        int i2 = 0;
        int j2 = 0;
        while (j2 < this.mainInventory.length) {
            ItemStack itemstack = this.mainInventory[j2];
            if (!(itemstack == null || itemIn != null && itemstack.getItem() != itemIn || metadataIn > -1 && itemstack.getMetadata() != metadataIn || itemNBT != null && !NBTUtil.func_181123_a(itemNBT, itemstack.getTagCompound(), true))) {
                int k2 = removeCount <= 0 ? itemstack.stackSize : Math.min(removeCount - i2, itemstack.stackSize);
                i2 += k2;
                if (removeCount != 0) {
                    this.mainInventory[j2].stackSize -= k2;
                    if (this.mainInventory[j2].stackSize == 0) {
                        this.mainInventory[j2] = null;
                    }
                    if (removeCount > 0 && i2 >= removeCount) {
                        return i2;
                    }
                }
            }
            ++j2;
        }
        int l2 = 0;
        while (l2 < this.armorInventory.length) {
            ItemStack itemstack1 = this.armorInventory[l2];
            if (!(itemstack1 == null || itemIn != null && itemstack1.getItem() != itemIn || metadataIn > -1 && itemstack1.getMetadata() != metadataIn || itemNBT != null && !NBTUtil.func_181123_a(itemNBT, itemstack1.getTagCompound(), false))) {
                int j1 = removeCount <= 0 ? itemstack1.stackSize : Math.min(removeCount - i2, itemstack1.stackSize);
                i2 += j1;
                if (removeCount != 0) {
                    this.armorInventory[l2].stackSize -= j1;
                    if (this.armorInventory[l2].stackSize == 0) {
                        this.armorInventory[l2] = null;
                    }
                    if (removeCount > 0 && i2 >= removeCount) {
                        return i2;
                    }
                }
            }
            ++l2;
        }
        if (this.itemStack != null) {
            if (itemIn != null && this.itemStack.getItem() != itemIn) {
                return i2;
            }
            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
                return i2;
            }
            if (itemNBT != null && !NBTUtil.func_181123_a(itemNBT, this.itemStack.getTagCompound(), false)) {
                return i2;
            }
            int i1 = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - i2, this.itemStack.stackSize);
            i2 += i1;
            if (removeCount != 0) {
                this.itemStack.stackSize -= i1;
                if (this.itemStack.stackSize == 0) {
                    this.itemStack = null;
                }
                if (removeCount > 0 && i2 >= removeCount) {
                    return i2;
                }
            }
        }
        return i2;
    }

    private int storePartialItemStack(ItemStack itemStackIn) {
        Item item = itemStackIn.getItem();
        int i2 = itemStackIn.stackSize;
        int j2 = this.storeItemStack(itemStackIn);
        if (j2 < 0) {
            j2 = this.getFirstEmptyStack();
        }
        if (j2 < 0) {
            return i2;
        }
        if (this.mainInventory[j2] == null) {
            this.mainInventory[j2] = new ItemStack(item, 0, itemStackIn.getMetadata());
            if (itemStackIn.hasTagCompound()) {
                this.mainInventory[j2].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
            }
        }
        int k2 = i2;
        if (i2 > this.mainInventory[j2].getMaxStackSize() - this.mainInventory[j2].stackSize) {
            k2 = this.mainInventory[j2].getMaxStackSize() - this.mainInventory[j2].stackSize;
        }
        if (k2 > this.getInventoryStackLimit() - this.mainInventory[j2].stackSize) {
            k2 = this.getInventoryStackLimit() - this.mainInventory[j2].stackSize;
        }
        if (k2 == 0) {
            return i2;
        }
        this.mainInventory[j2].stackSize += k2;
        this.mainInventory[j2].animationsToGo = 5;
        return i2 -= k2;
    }

    public void decrementAnimations() {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null) {
                try {
                    this.mainInventory[i2].updateAnimation(this.player.worldObj, this.player, i2, this.currentItem == i2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            ++i2;
        }
    }

    public boolean consumeInventoryItem(Item itemIn) {
        int i2 = this.getInventorySlotContainItem(itemIn);
        if (i2 < 0) {
            return false;
        }
        if (--this.mainInventory[i2].stackSize <= 0) {
            this.mainInventory[i2] = null;
        }
        return true;
    }

    public boolean hasItem(Item itemIn) {
        int i2 = this.getInventorySlotContainItem(itemIn);
        return i2 >= 0;
    }

    public boolean addItemStackToInventory(final ItemStack itemStackIn) {
        if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null) {
            int i2;
            block10: {
                block8: {
                    block9: {
                        if (!itemStackIn.isItemDamaged()) break block8;
                        int j2 = this.getFirstEmptyStack();
                        if (j2 < 0) break block9;
                        this.mainInventory[j2] = ItemStack.copyItemStack(itemStackIn);
                        this.mainInventory[j2].animationsToGo = 5;
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    if (this.player.capabilities.isCreativeMode) {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    return false;
                }
                try {
                    do {
                        i2 = itemStackIn.stackSize;
                        itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);
                    } while (itemStackIn.stackSize > 0 && itemStackIn.stackSize < i2);
                    if (itemStackIn.stackSize != i2 || !this.player.capabilities.isCreativeMode) break block10;
                    itemStackIn.stackSize = 0;
                    return true;
                }
                catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                    crashreportcategory.addCrashSection("Item ID", Item.getIdFromItem(itemStackIn.getItem()));
                    crashreportcategory.addCrashSection("Item data", itemStackIn.getMetadata());
                    crashreportcategory.addCrashSectionCallable("Item name", new Callable<String>(){

                        @Override
                        public String call() throws Exception {
                            return itemStackIn.getDisplayName();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
            return itemStackIn.stackSize < i2;
        }
        return false;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack[] aitemstack = this.mainInventory;
        if (index >= this.mainInventory.length) {
            aitemstack = this.armorInventory;
            index -= this.mainInventory.length;
        }
        if (aitemstack[index] != null) {
            if (aitemstack[index].stackSize <= count) {
                ItemStack itemstack1 = aitemstack[index];
                aitemstack[index] = null;
                return itemstack1;
            }
            ItemStack itemstack = aitemstack[index].splitStack(count);
            if (aitemstack[index].stackSize == 0) {
                aitemstack[index] = null;
            }
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack[] aitemstack = this.mainInventory;
        if (index >= this.mainInventory.length) {
            aitemstack = this.armorInventory;
            index -= this.mainInventory.length;
        }
        if (aitemstack[index] != null) {
            ItemStack itemstack = aitemstack[index];
            aitemstack[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack[] aitemstack = this.mainInventory;
        if (index >= aitemstack.length) {
            index -= aitemstack.length;
            aitemstack = this.armorInventory;
        }
        aitemstack[index] = stack;
    }

    public float getStrVsBlock(Block blockIn) {
        float f2 = 1.0f;
        if (this.mainInventory[this.currentItem] != null) {
            f2 *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
        }
        return f2;
    }

    public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                this.mainInventory[i2].writeToNBT(nbttagcompound);
                nbtTagListIn.appendTag(nbttagcompound);
            }
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.armorInventory.length) {
            if (this.armorInventory[j2] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)(j2 + 100));
                this.armorInventory[j2].writeToNBT(nbttagcompound1);
                nbtTagListIn.appendTag(nbttagcompound1);
            }
            ++j2;
        }
        return nbtTagListIn;
    }

    public void readFromNBT(NBTTagList nbtTagListIn) {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];
        int i2 = 0;
        while (i2 < nbtTagListIn.tagCount()) {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
            if (itemstack != null) {
                if (j2 >= 0 && j2 < this.mainInventory.length) {
                    this.mainInventory[j2] = itemstack;
                }
                if (j2 >= 100 && j2 < this.armorInventory.length + 100) {
                    this.armorInventory[j2 - 100] = itemstack;
                }
            }
            ++i2;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.length + 4;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        ItemStack[] aitemstack = this.mainInventory;
        if (index >= aitemstack.length) {
            index -= aitemstack.length;
            aitemstack = this.armorInventory;
        }
        return aitemstack[index];
    }

    @Override
    public String getName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canHeldItemHarvest(Block blockIn) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack itemstack = this.getStackInSlot(this.currentItem);
        return itemstack != null ? itemstack.canHarvestBlock(blockIn) : false;
    }

    public ItemStack armorItemInSlot(int slotIn) {
        return this.armorInventory[slotIn];
    }

    public int getTotalArmorValue() {
        int i2 = 0;
        int j2 = 0;
        while (j2 < this.armorInventory.length) {
            if (this.armorInventory[j2] != null && this.armorInventory[j2].getItem() instanceof ItemArmor) {
                int k2 = ((ItemArmor)this.armorInventory[j2].getItem()).damageReduceAmount;
                i2 += k2;
            }
            ++j2;
        }
        return i2;
    }

    public void damageArmor(float damage) {
        if ((damage /= 4.0f) < 1.0f) {
            damage = 1.0f;
        }
        int i2 = 0;
        while (i2 < this.armorInventory.length) {
            if (this.armorInventory[i2] != null && this.armorInventory[i2].getItem() instanceof ItemArmor) {
                this.armorInventory[i2].damageItem((int)damage, this.player);
                if (this.armorInventory[i2].stackSize == 0) {
                    this.armorInventory[i2] = null;
                }
            }
            ++i2;
        }
    }

    public void dropAllItems() {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            if (this.mainInventory[i2] != null) {
                this.player.dropItem(this.mainInventory[i2], true, false);
                this.mainInventory[i2] = null;
            }
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.armorInventory.length) {
            if (this.armorInventory[j2] != null) {
                this.player.dropItem(this.armorInventory[j2], true, false);
                this.armorInventory[j2] = null;
            }
            ++j2;
        }
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    public void setItemStack(ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0;
    }

    public boolean hasItemStack(ItemStack itemStackIn) {
        int i2 = 0;
        while (i2 < this.armorInventory.length) {
            if (this.armorInventory[i2] != null && this.armorInventory[i2].isItemEqual(itemStackIn)) {
                return true;
            }
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.mainInventory.length) {
            if (this.mainInventory[j2] != null && this.mainInventory[j2].isItemEqual(itemStackIn)) {
                return true;
            }
            ++j2;
        }
        return false;
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

    public void copyInventory(InventoryPlayer playerInventory) {
        int i2 = 0;
        while (i2 < this.mainInventory.length) {
            this.mainInventory[i2] = ItemStack.copyItemStack(playerInventory.mainInventory[i2]);
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.armorInventory.length) {
            this.armorInventory[j2] = ItemStack.copyItemStack(playerInventory.armorInventory[j2]);
            ++j2;
        }
        this.currentItem = playerInventory.currentItem;
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
        while (i2 < this.mainInventory.length) {
            this.mainInventory[i2] = null;
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.armorInventory.length) {
            this.armorInventory[j2] = null;
            ++j2;
        }
    }
}

