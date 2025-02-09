// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.inventory;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import java.util.Iterator;
import net.minecraft.entity.player.InventoryPlayer;
import javax.annotation.Nullable;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public abstract class Container
{
    public NonNullList<ItemStack> inventoryItemStacks;
    public List<Slot> inventorySlots;
    public int windowId;
    private short transactionID;
    private int dragMode;
    private int dragEvent;
    private final Set<Slot> dragSlots;
    protected List<IContainerListener> listeners;
    private final Set<EntityPlayer> playerList;
    
    public Container() {
        this.inventoryItemStacks = NonNullList.func_191196_a();
        this.inventorySlots = (List<Slot>)Lists.newArrayList();
        this.dragMode = -1;
        this.dragSlots = (Set<Slot>)Sets.newHashSet();
        this.listeners = (List<IContainerListener>)Lists.newArrayList();
        this.playerList = (Set<EntityPlayer>)Sets.newHashSet();
    }
    
    protected Slot addSlotToContainer(final Slot slotIn) {
        slotIn.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slotIn);
        this.inventoryItemStacks.add(ItemStack.field_190927_a);
        return slotIn;
    }
    
    public void addListener(final IContainerListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already listening");
        }
        this.listeners.add(listener);
        listener.updateCraftingInventory(this, this.getInventory());
        this.detectAndSendChanges();
    }
    
    public void removeListener(final IContainerListener listener) {
        this.listeners.remove(listener);
    }
    
    public NonNullList<ItemStack> getInventory() {
        final NonNullList<ItemStack> nonnulllist = NonNullList.func_191196_a();
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            nonnulllist.add(this.inventorySlots.get(i).getStack());
        }
        return nonnulllist;
    }
    
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            final ItemStack itemstack = this.inventorySlots.get(i).getStack();
            ItemStack itemstack2 = this.inventoryItemStacks.get(i);
            if (!ItemStack.areItemStacksEqual(itemstack2, itemstack)) {
                itemstack2 = (itemstack.func_190926_b() ? ItemStack.field_190927_a : itemstack.copy());
                this.inventoryItemStacks.set(i, itemstack2);
                for (int j = 0; j < this.listeners.size(); ++j) {
                    this.listeners.get(j).sendSlotContents(this, i, itemstack2);
                }
            }
        }
    }
    
    public boolean enchantItem(final EntityPlayer playerIn, final int id) {
        return false;
    }
    
    @Nullable
    public Slot getSlotFromInventory(final IInventory inv, final int slotIn) {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            final Slot slot = this.inventorySlots.get(i);
            if (slot.isHere(inv, slotIn)) {
                return slot;
            }
        }
        return null;
    }
    
    public Slot getSlot(final int slotId) {
        return this.inventorySlots.get(slotId);
    }
    
    public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
        final Slot slot = this.inventorySlots.get(index);
        return (slot != null) ? slot.getStack() : ItemStack.field_190927_a;
    }
    
    public ItemStack slotClick(final int slotId, final int dragType, final ClickType clickTypeIn, final EntityPlayer player) {
        ItemStack itemstack = ItemStack.field_190927_a;
        final InventoryPlayer inventoryplayer = player.inventory;
        if (clickTypeIn == ClickType.QUICK_CRAFT) {
            final int j1 = this.dragEvent;
            this.dragEvent = getDragEvent(dragType);
            if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent) {
                this.resetDrag();
            }
            else if (inventoryplayer.getItemStack().func_190926_b()) {
                this.resetDrag();
            }
            else if (this.dragEvent == 0) {
                this.dragMode = extractDragMode(dragType);
                if (isValidDragMode(this.dragMode, player)) {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1) {
                final Slot slot7 = this.inventorySlots.get(slotId);
                final ItemStack itemstack2 = inventoryplayer.getItemStack();
                if (slot7 != null && canAddItemToSlot(slot7, itemstack2, true) && slot7.isItemValid(itemstack2) && (this.dragMode == 2 || itemstack2.func_190916_E() > this.dragSlots.size()) && this.canDragIntoSlot(slot7)) {
                    this.dragSlots.add(slot7);
                }
            }
            else if (this.dragEvent == 2) {
                if (!this.dragSlots.isEmpty()) {
                    final ItemStack itemstack3 = inventoryplayer.getItemStack().copy();
                    int k1 = inventoryplayer.getItemStack().func_190916_E();
                    for (final Slot slot8 : this.dragSlots) {
                        final ItemStack itemstack4 = inventoryplayer.getItemStack();
                        if (slot8 != null && canAddItemToSlot(slot8, itemstack4, true) && slot8.isItemValid(itemstack4) && (this.dragMode == 2 || itemstack4.func_190916_E() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8)) {
                            final ItemStack itemstack5 = itemstack3.copy();
                            final int j2 = slot8.getHasStack() ? slot8.getStack().func_190916_E() : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemstack5, j2);
                            final int k2 = Math.min(itemstack5.getMaxStackSize(), slot8.getItemStackLimit(itemstack5));
                            if (itemstack5.func_190916_E() > k2) {
                                itemstack5.func_190920_e(k2);
                            }
                            k1 -= itemstack5.func_190916_E() - j2;
                            slot8.putStack(itemstack5);
                        }
                    }
                    itemstack3.func_190920_e(k1);
                    inventoryplayer.setItemStack(itemstack3);
                }
                this.resetDrag();
            }
            else {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0) {
            this.resetDrag();
        }
        else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1)) {
            if (slotId == -999) {
                if (!inventoryplayer.getItemStack().func_190926_b()) {
                    if (dragType == 0) {
                        player.dropItem(inventoryplayer.getItemStack(), true);
                        inventoryplayer.setItemStack(ItemStack.field_190927_a);
                    }
                    if (dragType == 1) {
                        player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
                    }
                }
            }
            else if (clickTypeIn == ClickType.QUICK_MOVE) {
                if (slotId < 0) {
                    return ItemStack.field_190927_a;
                }
                final Slot slot9 = this.inventorySlots.get(slotId);
                if (slot9 == null || !slot9.canTakeStack(player)) {
                    return ItemStack.field_190927_a;
                }
                for (ItemStack itemstack6 = this.transferStackInSlot(player, slotId); !itemstack6.func_190926_b(); itemstack6 = this.transferStackInSlot(player, slotId)) {
                    if (!ItemStack.areItemsEqual(slot9.getStack(), itemstack6)) {
                        break;
                    }
                    itemstack = itemstack6.copy();
                }
            }
            else {
                if (slotId < 0) {
                    return ItemStack.field_190927_a;
                }
                final Slot slot10 = this.inventorySlots.get(slotId);
                if (slot10 != null) {
                    ItemStack itemstack7 = slot10.getStack();
                    final ItemStack itemstack8 = inventoryplayer.getItemStack();
                    if (!itemstack7.func_190926_b()) {
                        itemstack = itemstack7.copy();
                    }
                    if (itemstack7.func_190926_b()) {
                        if (!itemstack8.func_190926_b() && slot10.isItemValid(itemstack8)) {
                            int i3 = (dragType == 0) ? itemstack8.func_190916_E() : 1;
                            if (i3 > slot10.getItemStackLimit(itemstack8)) {
                                i3 = slot10.getItemStackLimit(itemstack8);
                            }
                            slot10.putStack(itemstack8.splitStack(i3));
                        }
                    }
                    else if (slot10.canTakeStack(player)) {
                        if (itemstack8.func_190926_b()) {
                            if (itemstack7.func_190926_b()) {
                                slot10.putStack(ItemStack.field_190927_a);
                                inventoryplayer.setItemStack(ItemStack.field_190927_a);
                            }
                            else {
                                final int l2 = (dragType == 0) ? itemstack7.func_190916_E() : ((itemstack7.func_190916_E() + 1) / 2);
                                inventoryplayer.setItemStack(slot10.decrStackSize(l2));
                                if (itemstack7.func_190926_b()) {
                                    slot10.putStack(ItemStack.field_190927_a);
                                }
                                slot10.func_190901_a(player, inventoryplayer.getItemStack());
                            }
                        }
                        else if (slot10.isItemValid(itemstack8)) {
                            if (itemstack7.getItem() == itemstack8.getItem() && itemstack7.getMetadata() == itemstack8.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack7, itemstack8)) {
                                int k3 = (dragType == 0) ? itemstack8.func_190916_E() : 1;
                                if (k3 > slot10.getItemStackLimit(itemstack8) - itemstack7.func_190916_E()) {
                                    k3 = slot10.getItemStackLimit(itemstack8) - itemstack7.func_190916_E();
                                }
                                if (k3 > itemstack8.getMaxStackSize() - itemstack7.func_190916_E()) {
                                    k3 = itemstack8.getMaxStackSize() - itemstack7.func_190916_E();
                                }
                                itemstack8.func_190918_g(k3);
                                itemstack7.func_190917_f(k3);
                            }
                            else if (itemstack8.func_190916_E() <= slot10.getItemStackLimit(itemstack8)) {
                                slot10.putStack(itemstack8);
                                inventoryplayer.setItemStack(itemstack7);
                            }
                        }
                        else if (itemstack7.getItem() == itemstack8.getItem() && itemstack8.getMaxStackSize() > 1 && (!itemstack7.getHasSubtypes() || itemstack7.getMetadata() == itemstack8.getMetadata()) && ItemStack.areItemStackTagsEqual(itemstack7, itemstack8) && !itemstack7.func_190926_b()) {
                            final int j3 = itemstack7.func_190916_E();
                            if (j3 + itemstack8.func_190916_E() <= itemstack8.getMaxStackSize()) {
                                itemstack8.func_190917_f(j3);
                                itemstack7 = slot10.decrStackSize(j3);
                                if (itemstack7.func_190926_b()) {
                                    slot10.putStack(ItemStack.field_190927_a);
                                }
                                slot10.func_190901_a(player, inventoryplayer.getItemStack());
                            }
                        }
                    }
                    slot10.onSlotChanged();
                }
            }
        }
        else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
            final Slot slot11 = this.inventorySlots.get(slotId);
            final ItemStack itemstack9 = inventoryplayer.getStackInSlot(dragType);
            final ItemStack itemstack10 = slot11.getStack();
            if (!itemstack9.func_190926_b() || !itemstack10.func_190926_b()) {
                if (itemstack9.func_190926_b()) {
                    if (slot11.canTakeStack(player)) {
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot11.func_190900_b(itemstack10.func_190916_E());
                        slot11.putStack(ItemStack.field_190927_a);
                        slot11.func_190901_a(player, itemstack10);
                    }
                }
                else if (itemstack10.func_190926_b()) {
                    if (slot11.isItemValid(itemstack9)) {
                        final int l3 = slot11.getItemStackLimit(itemstack9);
                        if (itemstack9.func_190916_E() > l3) {
                            slot11.putStack(itemstack9.splitStack(l3));
                        }
                        else {
                            slot11.putStack(itemstack9);
                            inventoryplayer.setInventorySlotContents(dragType, ItemStack.field_190927_a);
                        }
                    }
                }
                else if (slot11.canTakeStack(player) && slot11.isItemValid(itemstack9)) {
                    final int i4 = slot11.getItemStackLimit(itemstack9);
                    if (itemstack9.func_190916_E() > i4) {
                        slot11.putStack(itemstack9.splitStack(i4));
                        slot11.func_190901_a(player, itemstack10);
                        if (!inventoryplayer.addItemStackToInventory(itemstack10)) {
                            player.dropItem(itemstack10, true);
                        }
                    }
                    else {
                        slot11.putStack(itemstack9);
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot11.func_190901_a(player, itemstack10);
                    }
                }
            }
        }
        else if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().func_190926_b() && slotId >= 0) {
            final Slot slot12 = this.inventorySlots.get(slotId);
            if (slot12 != null && slot12.getHasStack()) {
                final ItemStack itemstack11 = slot12.getStack().copy();
                itemstack11.func_190920_e(itemstack11.getMaxStackSize());
                inventoryplayer.setItemStack(itemstack11);
            }
        }
        else if (clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack().func_190926_b() && slotId >= 0) {
            final Slot slot13 = this.inventorySlots.get(slotId);
            if (slot13 != null && slot13.getHasStack() && slot13.canTakeStack(player)) {
                final ItemStack itemstack12 = slot13.decrStackSize((dragType == 0) ? 1 : slot13.getStack().func_190916_E());
                slot13.func_190901_a(player, itemstack12);
                player.dropItem(itemstack12, true);
            }
        }
        else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
            final Slot slot14 = this.inventorySlots.get(slotId);
            final ItemStack itemstack13 = inventoryplayer.getItemStack();
            if (!itemstack13.func_190926_b() && (slot14 == null || !slot14.getHasStack() || !slot14.canTakeStack(player))) {
                final int m = (dragType == 0) ? 0 : (this.inventorySlots.size() - 1);
                final int j4 = (dragType == 0) ? 1 : -1;
                for (int k4 = 0; k4 < 2; ++k4) {
                    for (int l4 = m; l4 >= 0 && l4 < this.inventorySlots.size() && itemstack13.func_190916_E() < itemstack13.getMaxStackSize(); l4 += j4) {
                        final Slot slot15 = this.inventorySlots.get(l4);
                        if (slot15.getHasStack() && canAddItemToSlot(slot15, itemstack13, true) && slot15.canTakeStack(player) && this.canMergeSlot(itemstack13, slot15)) {
                            final ItemStack itemstack14 = slot15.getStack();
                            if (k4 != 0 || itemstack14.func_190916_E() != itemstack14.getMaxStackSize()) {
                                final int i5 = Math.min(itemstack13.getMaxStackSize() - itemstack13.func_190916_E(), itemstack14.func_190916_E());
                                final ItemStack itemstack15 = slot15.decrStackSize(i5);
                                itemstack13.func_190917_f(i5);
                                if (itemstack15.func_190926_b()) {
                                    slot15.putStack(ItemStack.field_190927_a);
                                }
                                slot15.func_190901_a(player, itemstack15);
                            }
                        }
                    }
                }
            }
            this.detectAndSendChanges();
        }
        return itemstack;
    }
    
    public boolean canMergeSlot(final ItemStack stack, final Slot slotIn) {
        return true;
    }
    
    public void onContainerClosed(final EntityPlayer playerIn) {
        final InventoryPlayer inventoryplayer = playerIn.inventory;
        if (!inventoryplayer.getItemStack().func_190926_b()) {
            playerIn.dropItem(inventoryplayer.getItemStack(), false);
            inventoryplayer.setItemStack(ItemStack.field_190927_a);
        }
    }
    
    protected void func_193327_a(final EntityPlayer p_193327_1_, final World p_193327_2_, final IInventory p_193327_3_) {
        if (!p_193327_1_.isEntityAlive() || (p_193327_1_ instanceof EntityPlayerMP && ((EntityPlayerMP)p_193327_1_).func_193105_t())) {
            for (int j = 0; j < p_193327_3_.getSizeInventory(); ++j) {
                p_193327_1_.dropItem(p_193327_3_.removeStackFromSlot(j), false);
            }
        }
        else {
            for (int i = 0; i < p_193327_3_.getSizeInventory(); ++i) {
                p_193327_1_.inventory.func_191975_a(p_193327_2_, p_193327_3_.removeStackFromSlot(i));
            }
        }
    }
    
    public void onCraftMatrixChanged(final IInventory inventoryIn) {
        this.detectAndSendChanges();
    }
    
    public void putStackInSlot(final int slotID, final ItemStack stack) {
        this.getSlot(slotID).putStack(stack);
    }
    
    public void func_190896_a(final List<ItemStack> p_190896_1_) {
        for (int i = 0; i < p_190896_1_.size(); ++i) {
            this.getSlot(i).putStack(p_190896_1_.get(i));
        }
    }
    
    public void updateProgressBar(final int id, final int data) {
    }
    
    public short getNextTransactionID(final InventoryPlayer invPlayer) {
        return (short)(++this.transactionID);
    }
    
    public boolean getCanCraft(final EntityPlayer player) {
        return !this.playerList.contains(player);
    }
    
    public void setCanCraft(final EntityPlayer player, final boolean canCraft) {
        if (canCraft) {
            this.playerList.remove(player);
        }
        else {
            this.playerList.add(player);
        }
    }
    
    public abstract boolean canInteractWith(final EntityPlayer p0);
    
    protected boolean mergeItemStack(final ItemStack stack, final int startIndex, final int endIndex, final boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }
        if (stack.isStackable()) {
            while (!stack.func_190926_b()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                }
                else if (i >= endIndex) {
                    break;
                }
                final Slot slot = this.inventorySlots.get(i);
                final ItemStack itemstack = slot.getStack();
                if (!itemstack.func_190926_b() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
                    final int j = itemstack.func_190916_E() + stack.func_190916_E();
                    if (j <= stack.getMaxStackSize()) {
                        stack.func_190920_e(0);
                        itemstack.func_190920_e(j);
                        slot.onSlotChanged();
                        flag = true;
                    }
                    else if (itemstack.func_190916_E() < stack.getMaxStackSize()) {
                        stack.func_190918_g(stack.getMaxStackSize() - itemstack.func_190916_E());
                        itemstack.func_190920_e(stack.getMaxStackSize());
                        slot.onSlotChanged();
                        flag = true;
                    }
                }
                if (reverseDirection) {
                    --i;
                }
                else {
                    ++i;
                }
            }
        }
        if (!stack.func_190926_b()) {
            if (reverseDirection) {
                i = endIndex - 1;
            }
            else {
                i = startIndex;
            }
            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                }
                else if (i >= endIndex) {
                    break;
                }
                final Slot slot2 = this.inventorySlots.get(i);
                final ItemStack itemstack2 = slot2.getStack();
                if (itemstack2.func_190926_b() && slot2.isItemValid(stack)) {
                    if (stack.func_190916_E() > slot2.getSlotStackLimit()) {
                        slot2.putStack(stack.splitStack(slot2.getSlotStackLimit()));
                    }
                    else {
                        slot2.putStack(stack.splitStack(stack.func_190916_E()));
                    }
                    slot2.onSlotChanged();
                    flag = true;
                    break;
                }
                if (reverseDirection) {
                    --i;
                }
                else {
                    ++i;
                }
            }
        }
        return flag;
    }
    
    public static int extractDragMode(final int eventButton) {
        return eventButton >> 2 & 0x3;
    }
    
    public static int getDragEvent(final int clickedButton) {
        return clickedButton & 0x3;
    }
    
    public static int getQuickcraftMask(final int p_94534_0_, final int p_94534_1_) {
        return (p_94534_0_ & 0x3) | (p_94534_1_ & 0x3) << 2;
    }
    
    public static boolean isValidDragMode(final int dragModeIn, final EntityPlayer player) {
        return dragModeIn == 0 || dragModeIn == 1 || (dragModeIn == 2 && player.capabilities.isCreativeMode);
    }
    
    protected void resetDrag() {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }
    
    public static boolean canAddItemToSlot(@Nullable final Slot slotIn, final ItemStack stack, final boolean stackSizeMatters) {
        final boolean flag = slotIn == null || !slotIn.getHasStack();
        if (!flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack)) {
            return slotIn.getStack().func_190916_E() + (stackSizeMatters ? 0 : stack.func_190916_E()) <= stack.getMaxStackSize();
        }
        return flag;
    }
    
    public static void computeStackSize(final Set<Slot> dragSlotsIn, final int dragModeIn, final ItemStack stack, final int slotStackSize) {
        switch (dragModeIn) {
            case 0: {
                stack.func_190920_e(MathHelper.floor(stack.func_190916_E() / (float)dragSlotsIn.size()));
                break;
            }
            case 1: {
                stack.func_190920_e(1);
                break;
            }
            case 2: {
                stack.func_190920_e(stack.getItem().getItemStackLimit());
                break;
            }
        }
        stack.func_190917_f(slotStackSize);
    }
    
    public boolean canDragIntoSlot(final Slot slotIn) {
        return true;
    }
    
    public static int calcRedstone(@Nullable final TileEntity te) {
        return (te instanceof IInventory) ? calcRedstoneFromInventory((IInventory)te) : 0;
    }
    
    public static int calcRedstoneFromInventory(@Nullable final IInventory inv) {
        if (inv == null) {
            return 0;
        }
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            final ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.func_190926_b()) {
                f += itemstack.func_190916_E() / (float)Math.min(inv.getInventoryStackLimit(), itemstack.getMaxStackSize());
                ++i;
            }
        }
        f /= inv.getSizeInventory();
        return MathHelper.floor(f * 14.0f) + ((i > 0) ? 1 : 0);
    }
    
    protected void func_192389_a(final World p_192389_1_, final EntityPlayer p_192389_2_, final InventoryCrafting p_192389_3_, final InventoryCraftResult p_192389_4_) {
        if (!p_192389_1_.isRemote) {
            final EntityPlayerMP entityplayermp = (EntityPlayerMP)p_192389_2_;
            ItemStack itemstack = ItemStack.field_190927_a;
            final IRecipe irecipe = CraftingManager.func_192413_b(p_192389_3_, p_192389_1_);
            if (irecipe != null && (irecipe.func_192399_d() || !p_192389_1_.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.func_192037_E().func_193830_f(irecipe))) {
                p_192389_4_.func_193056_a(irecipe);
                itemstack = irecipe.getCraftingResult(p_192389_3_);
            }
            p_192389_4_.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
        }
    }
}
