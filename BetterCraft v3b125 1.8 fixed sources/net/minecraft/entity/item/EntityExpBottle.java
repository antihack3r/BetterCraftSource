/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityExpBottle
extends EntityThrowable {
    public EntityExpBottle(World worldIn) {
        super(worldIn);
    }

    public EntityExpBottle(World worldIn, EntityLivingBase p_i1786_2_) {
        super(worldIn, p_i1786_2_);
    }

    public EntityExpBottle(World worldIn, double x2, double y2, double z2) {
        super(worldIn, x2, y2, z2);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.07f;
    }

    @Override
    protected float getVelocity() {
        return 0.7f;
    }

    @Override
    protected float getInaccuracy() {
        return -20.0f;
    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {
        if (!this.worldObj.isRemote) {
            this.worldObj.playAuxSFX(2002, new BlockPos(this), 0);
            int i2 = 3 + this.worldObj.rand.nextInt(5) + this.worldObj.rand.nextInt(5);
            while (i2 > 0) {
                int j2 = EntityXPOrb.getXPSplit(i2);
                i2 -= j2;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j2));
            }
            this.setDead();
        }
    }
}

