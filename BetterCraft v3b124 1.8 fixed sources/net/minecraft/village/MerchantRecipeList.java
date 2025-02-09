/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeList
extends ArrayList<MerchantRecipe> {
    public MerchantRecipeList() {
    }

    public MerchantRecipeList(NBTTagCompound compound) {
        this.readRecipiesFromTags(compound);
    }

    public MerchantRecipe canRecipeBeUsed(ItemStack p_77203_1_, ItemStack p_77203_2_, int p_77203_3_) {
        if (p_77203_3_ > 0 && p_77203_3_ < this.size()) {
            MerchantRecipe merchantrecipe1 = (MerchantRecipe)this.get(p_77203_3_);
            return !this.func_181078_a(p_77203_1_, merchantrecipe1.getItemToBuy()) || (p_77203_2_ != null || merchantrecipe1.hasSecondItemToBuy()) && (!merchantrecipe1.hasSecondItemToBuy() || !this.func_181078_a(p_77203_2_, merchantrecipe1.getSecondItemToBuy())) || p_77203_1_.stackSize < merchantrecipe1.getItemToBuy().stackSize || merchantrecipe1.hasSecondItemToBuy() && p_77203_2_.stackSize < merchantrecipe1.getSecondItemToBuy().stackSize ? null : merchantrecipe1;
        }
        int i2 = 0;
        while (i2 < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i2);
            if (this.func_181078_a(p_77203_1_, merchantrecipe.getItemToBuy()) && p_77203_1_.stackSize >= merchantrecipe.getItemToBuy().stackSize && (!merchantrecipe.hasSecondItemToBuy() && p_77203_2_ == null || merchantrecipe.hasSecondItemToBuy() && this.func_181078_a(p_77203_2_, merchantrecipe.getSecondItemToBuy()) && p_77203_2_.stackSize >= merchantrecipe.getSecondItemToBuy().stackSize)) {
                return merchantrecipe;
            }
            ++i2;
        }
        return null;
    }

    private boolean func_181078_a(ItemStack p_181078_1_, ItemStack p_181078_2_) {
        return ItemStack.areItemsEqual(p_181078_1_, p_181078_2_) && (!p_181078_2_.hasTagCompound() || p_181078_1_.hasTagCompound() && NBTUtil.func_181123_a(p_181078_2_.getTagCompound(), p_181078_1_.getTagCompound(), false));
    }

    public void writeToBuf(PacketBuffer buffer) {
        buffer.writeByte((byte)(this.size() & 0xFF));
        int i2 = 0;
        while (i2 < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i2);
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToBuy());
            buffer.writeItemStackToBuffer(merchantrecipe.getItemToSell());
            ItemStack itemstack = merchantrecipe.getSecondItemToBuy();
            buffer.writeBoolean(itemstack != null);
            if (itemstack != null) {
                buffer.writeItemStackToBuffer(itemstack);
            }
            buffer.writeBoolean(merchantrecipe.isRecipeDisabled());
            buffer.writeInt(merchantrecipe.getToolUses());
            buffer.writeInt(merchantrecipe.getMaxTradeUses());
            ++i2;
        }
    }

    public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException {
        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        int i2 = buffer.readByte() & 0xFF;
        int j2 = 0;
        while (j2 < i2) {
            ItemStack itemstack = buffer.readItemStackFromBuffer();
            ItemStack itemstack1 = buffer.readItemStackFromBuffer();
            ItemStack itemstack2 = null;
            if (buffer.readBoolean()) {
                itemstack2 = buffer.readItemStackFromBuffer();
            }
            boolean flag = buffer.readBoolean();
            int k2 = buffer.readInt();
            int l2 = buffer.readInt();
            MerchantRecipe merchantrecipe = new MerchantRecipe(itemstack, itemstack2, itemstack1, k2, l2);
            if (flag) {
                merchantrecipe.compensateToolUses();
            }
            merchantrecipelist.add(merchantrecipe);
            ++j2;
        }
        return merchantrecipelist;
    }

    public void readRecipiesFromTags(NBTTagCompound compound) {
        NBTTagList nbttaglist = compound.getTagList("Recipes", 10);
        int i2 = 0;
        while (i2 < nbttaglist.tagCount()) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
            this.add(new MerchantRecipe(nbttagcompound));
            ++i2;
        }
    }

    public NBTTagCompound getRecipiesAsTags() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        int i2 = 0;
        while (i2 < this.size()) {
            MerchantRecipe merchantrecipe = (MerchantRecipe)this.get(i2);
            nbttaglist.appendTag(merchantrecipe.writeToTags());
            ++i2;
        }
        nbttagcompound.setTag("Recipes", nbttaglist);
        return nbttagcompound;
    }
}

