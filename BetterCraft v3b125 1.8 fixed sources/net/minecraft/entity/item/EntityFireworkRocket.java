/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket
extends Entity {
    private int fireworkAge;
    private int lifetime;

    public EntityFireworkRocket(World worldIn) {
        super(worldIn);
        this.setSize(0.25f, 0.25f);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObjectByDataType(8, 5);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0;
    }

    public EntityFireworkRocket(World worldIn, double x2, double y2, double z2, ItemStack givenItem) {
        super(worldIn);
        this.fireworkAge = 0;
        this.setSize(0.25f, 0.25f);
        this.setPosition(x2, y2, z2);
        int i2 = 1;
        if (givenItem != null && givenItem.hasTagCompound()) {
            this.dataWatcher.updateObject(8, givenItem);
            NBTTagCompound nbttagcompound = givenItem.getTagCompound();
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Fireworks");
            if (nbttagcompound1 != null) {
                i2 += nbttagcompound1.getByte("Flight");
            }
        }
        this.motionX = this.rand.nextGaussian() * 0.001;
        this.motionZ = this.rand.nextGaussian() * 0.001;
        this.motionY = 0.05;
        this.lifetime = 10 * i2 + this.rand.nextInt(6) + this.rand.nextInt(7);
    }

    @Override
    public void setVelocity(double x2, double y2, double z2) {
        this.motionX = x2;
        this.motionY = y2;
        this.motionZ = z2;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f2 = MathHelper.sqrt_double(x2 * x2 + z2 * z2);
            this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x2, z2) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y2, f2) * 180.0 / Math.PI);
        }
    }

    @Override
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        this.motionX *= 1.15;
        this.motionZ *= 1.15;
        this.motionY += 0.04;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f2) * 180.0 / Math.PI);
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
        if (this.fireworkAge == 0 && !this.isSilent()) {
            this.worldObj.playSoundAtEntity(this, "fireworks.launch", 3.0f, 1.0f);
        }
        ++this.fireworkAge;
        if (this.worldObj.isRemote && this.fireworkAge % 2 < 2) {
            this.worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY - 0.3, this.posZ, this.rand.nextGaussian() * 0.05, -this.motionY * 0.5, this.rand.nextGaussian() * 0.05, new int[0]);
        }
        if (!this.worldObj.isRemote && this.fireworkAge > this.lifetime) {
            this.worldObj.setEntityState(this, (byte)17);
            this.setDead();
        }
    }

    @Override
    public void handleStatusUpdate(byte id2) {
        if (id2 == 17 && this.worldObj.isRemote) {
            ItemStack itemstack = this.dataWatcher.getWatchableObjectItemStack(8);
            NBTTagCompound nbttagcompound = null;
            if (itemstack != null && itemstack.hasTagCompound()) {
                nbttagcompound = itemstack.getTagCompound().getCompoundTag("Fireworks");
            }
            this.worldObj.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, nbttagcompound);
        }
        super.handleStatusUpdate(id2);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("Life", this.fireworkAge);
        tagCompound.setInteger("LifeTime", this.lifetime);
        ItemStack itemstack = this.dataWatcher.getWatchableObjectItemStack(8);
        if (itemstack != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            itemstack.writeToNBT(nbttagcompound);
            tagCompound.setTag("FireworksItem", nbttagcompound);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        ItemStack itemstack;
        this.fireworkAge = tagCompund.getInteger("Life");
        this.lifetime = tagCompund.getInteger("LifeTime");
        NBTTagCompound nbttagcompound = tagCompund.getCompoundTag("FireworksItem");
        if (nbttagcompound != null && (itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound)) != null) {
            this.dataWatcher.updateObject(8, itemstack);
        }
    }

    @Override
    public float getBrightness(float partialTicks) {
        return super.getBrightness(partialTicks);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return super.getBrightnessForRender(partialTicks);
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }
}

