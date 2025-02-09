// 
// Decompiled by Procyon v0.6.0
// 

package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.util.FBPRenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import javax.annotation.Nullable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.math.BlockPos;
import com.TominoCZ.FBP.FBP;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec2f;
import com.TominoCZ.FBP.vector.FBPVector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;

public class FBPParticleSnow extends ParticleDigging
{
    private final IBlockState sourceState;
    Minecraft mc;
    double scaleAlpha;
    double prevParticleScale;
    double prevParticleAlpha;
    double endMult;
    FBPVector3d rot;
    FBPVector3d prevRot;
    FBPVector3d rotStep;
    Vec2f[] par;
    
    public FBPParticleSnow(final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
        this.endMult = 1.0;
        try {
            FBP.setSourcePos.invokeExact((ParticleDigging)this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
        }
        catch (final Throwable e1) {
            e1.printStackTrace();
        }
        this.rot = new FBPVector3d();
        this.prevRot = new FBPVector3d();
        this.createRotationMatrix();
        this.motionX = xSpeedIn;
        this.motionY = -ySpeedIn;
        this.motionZ = zSpeedIn;
        this.particleGravity = 1.0f;
        this.sourceState = state;
        this.mc = Minecraft.getMinecraft();
        this.particleScale *= (float)FBP.random.nextDouble(FBP.scaleMult - 0.25, FBP.scaleMult + 0.25);
        this.particleMaxAge = (int)FBP.random.nextDouble(250.0, 300.0);
        final float particleRed = 1.0f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.scaleAlpha = this.particleScale * 0.75;
        this.particleAlpha = 0.0f;
        this.particleScale = 0.0f;
        this.canCollide = true;
        if (FBP.randomFadingSpeed) {
            this.endMult *= FBP.random.nextDouble(0.7, 1.0);
        }
        this.multipleParticleScaleBy(1.0f);
    }
    
    private void createRotationMatrix() {
        final double rx = FBP.random.nextDouble();
        final double ry = FBP.random.nextDouble();
        final double rz = FBP.random.nextDouble();
        this.rotStep = new FBPVector3d((rx > 0.5) ? 1 : -1, (ry > 0.5) ? 1 : -1, (rz > 0.5) ? 1 : -1);
        this.rot.copyFrom(this.rotStep);
    }
    
    @Override
    public void setParticleTextureIndex(final int particleTextureIndex) {
        if (!FBP.isEnabled()) {
            super.setParticleTextureIndex(particleTextureIndex);
        }
    }
    
    @Override
    public Particle multipleParticleScaleBy(final float scale) {
        final Particle p = super.multipleParticleScaleBy(scale);
        if (FBP.isEnabled()) {
            return p;
        }
        final float f = this.particleScale / 10.0f;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - f, this.posY, this.posZ - f, this.posX + f, this.posY + 2.0f * f, this.posZ + f));
        return p;
    }
    
    public Particle MultiplyVelocity(final float multiplier) {
        this.motionX *= multiplier;
        this.motionY = (this.motionY - 0.10000000149011612) * (multiplier / 2.0f) + 0.10000000149011612;
        this.motionZ *= multiplier;
        return this;
    }
    
    @Override
    protected void multiplyColor(@Nullable final BlockPos p_187154_1_) {
        if (!FBP.isEnabled()) {
            super.multiplyColor(p_187154_1_);
            return;
        }
        final int i = this.mc.getBlockColors().colorMultiplier(this.sourceState, this.worldObj, p_187154_1_, 0);
        this.particleRed *= (i >> 16 & 0xFF) / 255.0f;
        this.particleGreen *= (i >> 8 & 0xFF) / 255.0f;
        this.particleBlue *= (i & 0xFF) / 255.0f;
    }
    
    @Override
    public int getFXLayer() {
        if (!FBP.isEnabled()) {
            return super.getFXLayer();
        }
        return 1;
    }
    
    @Override
    public void onUpdate() {
        if (!FBP.isEnabled()) {
            super.onUpdate();
            return;
        }
        this.prevRot.copyFrom(this.rot);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevParticleAlpha = this.particleAlpha;
        this.prevParticleScale = this.particleScale;
        if (!this.mc.isGamePaused()) {
            ++this.particleAge;
            if (this.posY < this.mc.player.posY - this.mc.gameSettings.renderDistanceChunks * 16) {
                this.setExpired();
            }
            this.rot.add(this.rotStep.multiply(FBP.rotationMult * 5.0));
            if (this.particleAge >= this.particleMaxAge) {
                if (FBP.randomFadingSpeed) {
                    this.particleScale *= (float)(0.75 * this.endMult);
                }
                else {
                    this.particleScale *= 0.75f;
                }
                if (this.particleAlpha > 0.01 && this.particleScale <= this.scaleAlpha) {
                    if (FBP.randomFadingSpeed) {
                        this.particleAlpha *= (float)(0.6499999761581421 * this.endMult);
                    }
                    else {
                        this.particleAlpha *= 0.65f;
                    }
                }
                if (this.particleAlpha <= 0.01) {
                    this.setExpired();
                }
            }
            else {
                if (this.particleScale < 1.0f) {
                    if (FBP.randomFadingSpeed) {
                        this.particleScale += (float)(0.07500000298023224 * this.endMult);
                    }
                    else {
                        this.particleScale += 0.075f;
                    }
                    if (this.particleScale > 1.0f) {
                        this.particleScale = 1.0f;
                    }
                }
                if (this.particleAlpha < 1.0f) {
                    if (FBP.randomFadingSpeed) {
                        this.particleAlpha += (float)(0.04500000178813934 * this.endMult);
                    }
                    else {
                        this.particleAlpha += 0.045f;
                    }
                    if (this.particleAlpha > 1.0f) {
                        this.particleAlpha = 1.0f;
                    }
                }
            }
            if (this.worldObj.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial().isLiquid()) {
                this.setExpired();
            }
            this.motionY -= 0.04 * this.particleGravity;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            if (this.onGround && FBP.restOnFloor) {
                this.rot.x = Math.round(this.rot.x / 90.0) * 90.0f;
                this.rot.z = Math.round(this.rot.z / 90.0) * 90.0f;
            }
            this.motionX *= 0.9800000190734863;
            if (this.motionY < -0.2) {
                this.motionY *= 0.7500000190734863;
            }
            this.motionZ *= 0.9800000190734863;
            if (this.onGround) {
                this.motionX *= 0.680000190734863;
                this.motionZ *= 0.6800000190734863;
                this.rotStep = this.rotStep.multiply(0.85);
                this.particleAge += 2;
            }
        }
    }
    
    @Override
    public void moveEntity(double x, double y, double z) {
        if (!FBP.isEnabled()) {
            super.moveEntity(x, y, z);
            return;
        }
        final double X = x;
        final double Y = y;
        final double Z = z;
        final List<AxisAlignedBB> list = this.worldObj.getCollisionBoxes(null, this.getEntityBoundingBox().expand(x, y, z));
        for (final AxisAlignedBB axisalignedbb : list) {
            y = axisalignedbb.calculateYOffset(this.getEntityBoundingBox(), y);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
        for (final AxisAlignedBB axisalignedbb : list) {
            x = axisalignedbb.calculateXOffset(this.getEntityBoundingBox(), x);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));
        for (final AxisAlignedBB axisalignedbb : list) {
            z = axisalignedbb.calculateZOffset(this.getEntityBoundingBox(), z);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
        this.resetPositionToBB();
        this.onGround = (y != Y && Y < 0.0);
        if (!FBP.lowTraction && !FBP.bounceOffWalls) {
            if (x != X) {
                this.motionX *= 0.699999988079071;
            }
            if (z != Z) {
                this.motionZ *= 0.699999988079071;
            }
        }
    }
    
    @Override
    public void renderParticle(final BufferBuilder buf, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        if (!FBP.isEnabled()) {
            super.renderParticle(buf, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            return;
        }
        if (!FBP.isEnabled() && this.particleMaxAge != 0) {
            this.particleMaxAge = 0;
        }
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        if (this.particleTexture != null) {
            if (!FBP.cartoonMode) {
                f = this.particleTexture.getInterpolatedU(this.particleTextureJitterX / 4.0f * 16.0f);
                f3 = this.particleTexture.getInterpolatedV(this.particleTextureJitterY / 4.0f * 16.0f);
            }
            f2 = this.particleTexture.getInterpolatedU((this.particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f4 = this.particleTexture.getInterpolatedV((this.particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        }
        else {
            f = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0f) / 16.0f;
            f2 = f + 0.015609375f;
            f3 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0f) / 16.0f;
            f4 = f3 + 0.015609375f;
        }
        final float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - FBPParticleSnow.interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - FBPParticleSnow.interpPosY);
        final float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - FBPParticleSnow.interpPosZ);
        final int i = this.getBrightnessForRender(partialTicks);
        final float alpha = (float)(this.prevParticleAlpha + (this.particleAlpha - this.prevParticleAlpha) * partialTicks);
        final float f8 = (float)(this.prevParticleScale + (this.particleScale - this.prevParticleScale) * partialTicks);
        if (FBP.restOnFloor) {
            f6 += f8 / 10.0f;
        }
        final FBPVector3d smoothRot = new FBPVector3d(0.0, 0.0, 0.0);
        if (FBP.rotationMult > 0.0) {
            smoothRot.y = this.rot.y;
            smoothRot.z = this.rot.z;
            if (!FBP.randomRotation) {
                smoothRot.x = this.rot.x;
            }
            if (!FBP.frozen) {
                final FBPVector3d vec = this.rot.partialVec(this.prevRot, partialTicks);
                if (FBP.randomRotation) {
                    smoothRot.y = vec.y;
                    smoothRot.z = vec.z;
                }
                else {
                    smoothRot.x = vec.x;
                }
            }
        }
        GlStateManager.enableCull();
        FBPRenderUtil.renderCubeShaded_S(buf, this.par = new Vec2f[] { new Vec2f(f2, f4), new Vec2f(f2, f3), new Vec2f(f, f3), new Vec2f(f, f4) }, f5, f6, f7, f8 / 10.0f, smoothRot, i >> 16 & 0xFFFF, i & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha, FBP.cartoonMode);
    }
    
    @Override
    public int getBrightnessForRender(final float p_189214_1_) {
        if (!FBP.isEnabled()) {
            return super.getBrightnessForRender(p_189214_1_);
        }
        final int i = super.getBrightnessForRender(p_189214_1_);
        int j = 0;
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, this.posY, this.posZ))) {
            j = this.worldObj.getCombinedLight(new BlockPos(this.posX, this.posY, this.posZ), 0);
        }
        return (i == 0) ? j : i;
    }
}
