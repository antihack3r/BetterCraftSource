/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEgg
extends EntityThrowable {
    public EntityEgg(World worldIn) {
        super(worldIn);
    }

    public EntityEgg(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityEgg(World worldIn, double x2, double y2, double z2) {
        super(worldIn, x2, y2, z2);
    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {
        if (p_70184_1_.entityHit != null) {
            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0f);
        }
        if (!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
            int i2 = 1;
            if (this.rand.nextInt(32) == 0) {
                i2 = 4;
            }
            int j2 = 0;
            while (j2 < i2) {
                EntityChicken entitychicken = new EntityChicken(this.worldObj);
                entitychicken.setGrowingAge(-24000);
                entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
                this.worldObj.spawnEntityInWorld(entitychicken);
                ++j2;
            }
        }
        double d0 = 0.08;
        int k2 = 0;
        while (k2 < 8) {
            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5) * 0.08, ((double)this.rand.nextFloat() - 0.5) * 0.08, ((double)this.rand.nextFloat() - 0.5) * 0.08, Item.getIdFromItem(Items.egg));
            ++k2;
        }
        if (!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}

