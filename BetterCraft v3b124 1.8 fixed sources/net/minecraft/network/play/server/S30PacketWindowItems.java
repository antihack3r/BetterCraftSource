/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems
implements Packet<INetHandlerPlayClient> {
    private int windowId;
    private ItemStack[] itemStacks;

    public S30PacketWindowItems() {
    }

    public S30PacketWindowItems(int windowIdIn, List<ItemStack> p_i45186_2_) {
        this.windowId = windowIdIn;
        this.itemStacks = new ItemStack[p_i45186_2_.size()];
        int i2 = 0;
        while (i2 < this.itemStacks.length) {
            ItemStack itemstack = p_i45186_2_.get(i2);
            this.itemStacks[i2] = itemstack == null ? null : itemstack.copy();
            ++i2;
        }
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readUnsignedByte();
        int i2 = buf.readShort();
        this.itemStacks = new ItemStack[i2];
        int j2 = 0;
        while (j2 < i2) {
            this.itemStacks[j2] = buf.readItemStackFromBuffer();
            ++j2;
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.itemStacks.length);
        ItemStack[] itemStackArray = this.itemStacks;
        int n2 = this.itemStacks.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemstack = itemStackArray[n3];
            buf.writeItemStackToBuffer(itemstack);
            ++n3;
        }
    }

    @Override
    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleWindowItems(this);
    }

    public int func_148911_c() {
        return this.windowId;
    }

    public ItemStack[] getItemStacks() {
        return this.itemStacks;
    }
}

