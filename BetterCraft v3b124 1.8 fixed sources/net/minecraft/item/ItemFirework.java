/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework
extends Item {
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(worldIn, (float)pos.getX() + hitX, (float)pos.getY() + hitY, (float)pos.getZ() + hitZ, stack);
            worldIn.spawnEntityInWorld(entityfireworkrocket);
            if (!playerIn.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        NBTTagCompound nbttagcompound;
        if (stack.hasTagCompound() && (nbttagcompound = stack.getTagCompound().getCompoundTag("Fireworks")) != null) {
            NBTTagList nbttaglist;
            if (nbttagcompound.hasKey("Flight", 99)) {
                tooltip.add(String.valueOf(StatCollector.translateToLocal("item.fireworks.flight")) + " " + nbttagcompound.getByte("Flight"));
            }
            if ((nbttaglist = nbttagcompound.getTagList("Explosions", 10)) != null && nbttaglist.tagCount() > 0) {
                int i2 = 0;
                while (i2 < nbttaglist.tagCount()) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i2);
                    ArrayList<String> list = Lists.newArrayList();
                    ItemFireworkCharge.addExplosionInfo(nbttagcompound1, list);
                    if (list.size() > 0) {
                        int j2 = 1;
                        while (j2 < list.size()) {
                            list.set(j2, "  " + (String)list.get(j2));
                            ++j2;
                        }
                        tooltip.addAll(list);
                    }
                    ++i2;
                }
            }
        }
    }
}

