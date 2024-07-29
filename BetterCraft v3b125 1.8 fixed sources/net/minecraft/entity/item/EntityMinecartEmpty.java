/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty
extends EntityMinecart {
    public EntityMinecartEmpty(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartEmpty(World worldIn, double x2, double y2, double z2) {
        super(worldIn, x2, y2, z2);
    }

    @Override
    public boolean interactFirst(EntityPlayer playerIn) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn) {
            return true;
        }
        if (this.riddenByEntity != null && this.riddenByEntity != playerIn) {
            return false;
        }
        if (!this.worldObj.isRemote) {
            playerIn.mountEntity(this);
        }
        return true;
    }

    @Override
    public void onActivatorRailPass(int x2, int y2, int z2, boolean receivingPower) {
        if (receivingPower) {
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(null);
            }
            if (this.getRollingAmplitude() == 0) {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setDamage(50.0f);
                this.setBeenAttacked();
            }
        }
    }

    @Override
    public EntityMinecart.EnumMinecartType getMinecartType() {
        return EntityMinecart.EnumMinecartType.RIDEABLE;
    }
}

