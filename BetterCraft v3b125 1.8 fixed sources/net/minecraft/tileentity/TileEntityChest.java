/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityChest
extends TileEntityLockable
implements ITickable,
IInventory {
    private ItemStack[] chestContents = new ItemStack[27];
    public boolean adjacentChestChecked;
    public TileEntityChest adjacentChestZNeg;
    public TileEntityChest adjacentChestXPos;
    public TileEntityChest adjacentChestXNeg;
    public TileEntityChest adjacentChestZPos;
    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    private int ticksSinceSync;
    private int cachedChestType;
    private String customName;

    public TileEntityChest() {
        this.cachedChestType = -1;
    }

    public TileEntityChest(int chestType) {
        this.cachedChestType = chestType;
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.chestContents[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.chestContents[index] != null) {
            if (this.chestContents[index].stackSize <= count) {
                ItemStack itemstack1 = this.chestContents[index];
                this.chestContents[index] = null;
                this.markDirty();
                return itemstack1;
            }
            ItemStack itemstack = this.chestContents[index].splitStack(count);
            if (this.chestContents[index].stackSize == 0) {
                this.chestContents[index] = null;
            }
            this.markDirty();
            return itemstack;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (this.chestContents[index] != null) {
            ItemStack itemstack = this.chestContents[index];
            this.chestContents[index] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.chestContents[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.chest";
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            if (j2 >= 0 && j2 < this.chestContents.length) {
                this.chestContents[j2] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
            ++i2;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.chestContents.length) {
            if (this.chestContents[i2] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i2);
                this.chestContents[i2].writeToNBT(nbttagcompound);
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
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    private void func_174910_a(TileEntityChest chestTe, EnumFacing side) {
        if (chestTe.isInvalid()) {
            this.adjacentChestChecked = false;
        } else if (this.adjacentChestChecked) {
            switch (side) {
                case NORTH: {
                    if (this.adjacentChestZNeg == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case SOUTH: {
                    if (this.adjacentChestZPos == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case EAST: {
                    if (this.adjacentChestXPos == chestTe) break;
                    this.adjacentChestChecked = false;
                    break;
                }
                case WEST: {
                    if (this.adjacentChestXNeg == chestTe) break;
                    this.adjacentChestChecked = false;
                }
            }
        }
    }

    public void checkForAdjacentChests() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true;
            this.adjacentChestXNeg = this.getAdjacentChest(EnumFacing.WEST);
            this.adjacentChestXPos = this.getAdjacentChest(EnumFacing.EAST);
            this.adjacentChestZNeg = this.getAdjacentChest(EnumFacing.NORTH);
            this.adjacentChestZPos = this.getAdjacentChest(EnumFacing.SOUTH);
        }
    }

    protected TileEntityChest getAdjacentChest(EnumFacing side) {
        TileEntity tileentity;
        BlockPos blockpos = this.pos.offset(side);
        if (this.isChestAt(blockpos) && (tileentity = this.worldObj.getTileEntity(blockpos)) instanceof TileEntityChest) {
            TileEntityChest tileentitychest = (TileEntityChest)tileentity;
            tileentitychest.func_174910_a(this, side.getOpposite());
            return tileentitychest;
        }
        return null;
    }

    private boolean isChestAt(BlockPos posIn) {
        if (this.worldObj == null) {
            return false;
        }
        Block block = this.worldObj.getBlockState(posIn).getBlock();
        return block instanceof BlockChest && ((BlockChest)block).chestType == this.getChestType();
    }

    @Override
    public void update() {
        this.checkForAdjacentChests();
        int i2 = this.pos.getX();
        int j2 = this.pos.getY();
        int k2 = this.pos.getZ();
        ++this.ticksSinceSync;
        if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i2 + j2 + k2) % 200 == 0) {
            this.numPlayersUsing = 0;
            float f2 = 5.0f;
            for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((float)i2 - f2, (float)j2 - f2, (float)k2 - f2, (float)(i2 + 1) + f2, (float)(j2 + 1) + f2, (float)(k2 + 1) + f2))) {
                IInventory iinventory;
                if (!(entityplayer.openContainer instanceof ContainerChest) || (iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory()) != this && (!(iinventory instanceof InventoryLargeChest) || !((InventoryLargeChest)iinventory).isPartOfLargeChest(this))) continue;
                ++this.numPlayersUsing;
            }
        }
        this.prevLidAngle = this.lidAngle;
        float f1 = 0.1f;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0f && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d1 = (double)i2 + 0.5;
            double d2 = (double)k2 + 0.5;
            if (this.adjacentChestZPos != null) {
                d2 += 0.5;
            }
            if (this.adjacentChestXPos != null) {
                d1 += 0.5;
            }
            this.worldObj.playSoundEffect(d1, (double)j2 + 0.5, d2, "random.chestopen", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0f || this.numPlayersUsing > 0 && this.lidAngle < 1.0f) {
            float f3;
            float f2 = this.lidAngle;
            this.lidAngle = this.numPlayersUsing > 0 ? (this.lidAngle += f1) : (this.lidAngle -= f1);
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            if (this.lidAngle < (f3 = 0.5f) && f2 >= f3 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                double d3 = (double)i2 + 0.5;
                double d0 = (double)k2 + 0.5;
                if (this.adjacentChestZPos != null) {
                    d0 += 0.5;
                }
                if (this.adjacentChestXPos != null) {
                    d3 += 0.5;
                }
                this.worldObj.playSoundEffect(d3, (double)j2 + 0.5, d0, "random.chestclosed", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id2, int type) {
        if (id2 == 1) {
            this.numPlayersUsing = type;
            return true;
        }
        return super.receiveClientEvent(id2, type);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
            this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockChest) {
            --this.numPlayersUsing;
            this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
            this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
        this.checkForAdjacentChests();
    }

    public int getChestType() {
        if (this.cachedChestType == -1) {
            if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
                return 0;
            }
            this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
        }
        return this.cachedChestType;
    }

    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
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
        while (i2 < this.chestContents.length) {
            this.chestContents[i2] = null;
            ++i2;
        }
    }
}

