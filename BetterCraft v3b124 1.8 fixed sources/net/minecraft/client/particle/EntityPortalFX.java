/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX
extends EntityFX {
    private float portalParticleScale;
    private double portalPosX;
    private double portalPosY;
    private double portalPosZ;

    protected EntityPortalFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.portalPosX = this.posX = xCoordIn;
        this.portalPosY = this.posY = yCoordIn;
        this.portalPosZ = this.posZ = zCoordIn;
        float f2 = this.rand.nextFloat() * 0.6f + 0.4f;
        this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2f + 0.5f;
        this.particleGreen = this.particleBlue = 1.0f * f2;
        this.particleRed = this.particleBlue;
        this.particleGreen *= 0.3f;
        this.particleRed *= 0.9f;
        this.particleMaxAge = (int)(Math.random() * 10.0) + 40;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 8.0));
    }

    @Override
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f2 = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
        f2 = 1.0f - f2;
        f2 *= f2;
        f2 = 1.0f - f2;
        this.particleScale = this.portalParticleScale * f2;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        int i2 = super.getBrightnessForRender(partialTicks);
        float f2 = (float)this.particleAge / (float)this.particleMaxAge;
        f2 *= f2;
        f2 *= f2;
        int j2 = i2 & 0xFF;
        int k2 = i2 >> 16 & 0xFF;
        if ((k2 += (int)(f2 * 15.0f * 16.0f)) > 240) {
            k2 = 240;
        }
        return j2 | k2 << 16;
    }

    @Override
    public float getBrightness(float partialTicks) {
        float f2 = super.getBrightness(partialTicks);
        float f1 = (float)this.particleAge / (float)this.particleMaxAge;
        f1 = f1 * f1 * f1 * f1;
        return f2 * (1.0f - f1) + f1;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float f2 = (float)this.particleAge / (float)this.particleMaxAge;
        f2 = -f2 + f2 * f2 * 2.0f;
        f2 = 1.0f - f2;
        this.posX = this.portalPosX + this.motionX * (double)f2;
        this.posY = this.portalPosY + this.motionY * (double)f2 + (double)(1.0f - f2);
        this.posZ = this.portalPosZ + this.motionZ * (double)f2;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            return new EntityPortalFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}

