// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.item;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemWritableBook extends Item
{
    public ItemWritableBook() {
        this.setMaxStackSize(1);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World itemStackIn, final EntityPlayer worldIn, final EnumHand playerIn) {
        final ItemStack itemstack = worldIn.getHeldItem(playerIn);
        worldIn.openBook(itemstack, playerIn);
        worldIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }
    
    public static boolean isNBTValid(final NBTTagCompound nbt) {
        if (nbt == null) {
            return false;
        }
        if (!nbt.hasKey("pages", 9)) {
            return false;
        }
        final NBTTagList nbttaglist = nbt.getTagList("pages", 8);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final String s = nbttaglist.getStringTagAt(i);
            if (s.length() > 32767) {
                return false;
            }
        }
        return true;
    }
}
