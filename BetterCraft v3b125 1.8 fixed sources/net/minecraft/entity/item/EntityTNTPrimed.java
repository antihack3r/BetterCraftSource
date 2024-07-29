/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTNTPrimed
extends Entity {
    public int fuse;
    private EntityLivingBase tntPlacedBy;

    public EntityTNTPrimed(World worldIn) {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
    }

    public EntityTNTPrimed(World worldIn, double x2, double y2, double z2, EntityLivingBase igniter) {
        this(worldIn);
        this.setPosition(x2, y2, z2);
        float f2 = (float)(Math.random() * Math.PI * 2.0);
        this.motionX = -((float)Math.sin(f2)) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -((float)Math.cos(f2)) * 0.02f;
        this.fuse = 80;
        this.prevPosX = x2;
        this.prevPosY = y2;
        this.prevPosZ = z2;
        this.tntPlacedBy = igniter;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)0.04f;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
            this.motionY *= -0.5;
        }
        if (this.fuse-- <= 0) {
            this.setDead();
            if (!this.worldObj.isRemote) {
                this.explode();
            }
        } else {
            this.handleWaterMovement();
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    private void explode() {
        float f2 = 4.0f;
        this.worldObj.createExplosion(this, this.posX, this.posY + (double)(this.height / 16.0f), this.posZ, f2, true);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte("Fuse", (byte)this.fuse);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.fuse = tagCompund.getByte("Fuse");
    }

    public EntityLivingBase getTntPlacedBy() {
        return this.tntPlacedBy;
    }

    @Override
    public float getEyeHeight() {
        return 0.0f;
    }
}

