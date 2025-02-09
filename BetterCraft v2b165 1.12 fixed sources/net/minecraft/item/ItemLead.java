// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.item;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

public class ItemLead extends Item
{
    public ItemLead() {
        this.setCreativeTab(CreativeTabs.TOOLS);
    }
    
    @Override
    public EnumActionResult onItemUse(final EntityPlayer stack, final World playerIn, final BlockPos worldIn, final EnumHand pos, final EnumFacing hand, final float facing, final float hitX, final float hitY) {
        final Block block = playerIn.getBlockState(worldIn).getBlock();
        if (!(block instanceof BlockFence)) {
            return EnumActionResult.PASS;
        }
        if (!playerIn.isRemote) {
            attachToFence(stack, playerIn, worldIn);
        }
        return EnumActionResult.SUCCESS;
    }
    
    public static boolean attachToFence(final EntityPlayer player, final World worldIn, final BlockPos fence) {
        EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
        boolean flag = false;
        final double d0 = 7.0;
        final int i = fence.getX();
        final int j = fence.getY();
        final int k = fence.getZ();
        for (final EntityLiving entityliving : worldIn.getEntitiesWithinAABB((Class<? extends EntityLiving>)EntityLiving.class, new AxisAlignedBB(i - 7.0, j - 7.0, k - 7.0, i + 7.0, j + 7.0, k + 7.0))) {
            if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == player) {
                if (entityleashknot == null) {
                    entityleashknot = EntityLeashKnot.createKnot(worldIn, fence);
                }
                entityliving.setLeashedToEntity(entityleashknot, true);
                flag = true;
            }
        }
        return flag;
    }
}
