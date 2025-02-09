/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector2f;

public class FBPParticleSnow
extends EntityDiggingFX {
    private final IBlockState sourceState;
    Minecraft mc;
    double scaleAlpha;
    double prevParticleScale;
    double prevParticleAlpha;
    double endMult = 1.0;
    FBPVector3d rot;
    FBPVector3d prevRot;
    FBPVector3d rotStep;
    Vector2f[] par;

    public FBPParticleSnow(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Blocks.snow.getDefaultState());
        try {
            FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
        }
        catch (Throwable e1) {
            e1.printStackTrace();
        }
        this.rot = new FBPVector3d();
        this.prevRot = new FBPVector3d();
        this.createRotationMatrix();
        this.motionX = xSpeedIn;
        this.motionY = -ySpeedIn;
        this.motionZ = zSpeedIn;
        this.sourceState = state;
        this.mc = Minecraft.getMinecraft();
        this.particleScale *= (float)FBP.random.nextDouble(FBP.scaleMult - 0.25, FBP.scaleMult + 0.25);
        this.particleMaxAge = (int)FBP.random.nextDouble(250.0, 300.0);
        float particleRed = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGreen = 1.0f;
        this.particleRed = 1.0f;
        this.scaleAlpha = (double)this.particleScale * 0.75;
        this.particleAlpha = 0.0f;
        this.particleScale = 0.0f;
        this.isAirBorne = true;
        if (FBP.randomFadingSpeed) {
            this.endMult *= FBP.random.nextDouble(0.7, 1.0);
        }
        this.particleIcon = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        this.multipleParticleScaleBy(1.0f);
    }

    private void createRotationMatrix() {
        double rx = FBP.random.nextDouble();
        double ry2 = FBP.random.nextDouble();
        double rz2 = FBP.random.nextDouble();
        this.rotStep = new FBPVector3d(rx > 0.5 ? 1.0 : -1.0, ry2 > 0.5 ? 1.0 : -1.0, rz2 > 0.5 ? 1.0 : -1.0);
        this.rot.copyFrom(this.rotStep);
    }

    @Override
    public void setParticleIcon(TextureAtlasSprite s2) {
    }

    @Override
    public EntityFX multipleParticleScaleBy(float scale) {
        EntityFX p2 = super.multipleParticleScaleBy(scale);
        float f2 = this.particleScale / 10.0f;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - (double)f2, this.posY, this.posZ - (double)f2, this.posX + (double)f2, this.posY + (double)(2.0f * f2), this.posZ + (double)f2));
        return p2;
    }

    public EntityFX MultiplyVelocity(float multiplier) {
        this.motionX *= (double)multiplier;
        this.motionY = (this.motionY - (double)0.1f) * (double)(multiplier / 2.0f) + (double)0.1f;
        this.motionZ *= (double)multiplier;
        return this;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void onUpdate() {
        this.prevRot.copyFrom(this.rot);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevParticleAlpha = this.particleAlpha;
        this.prevParticleScale = this.particleScale;
        if (!this.mc.isGamePaused()) {
            ++this.particleAge;
            if (this.posY < this.mc.thePlayer.posY - (double)(this.mc.gameSettings.renderDistanceChunks * 16)) {
                this.setDead();
            }
            this.rot.add(this.rotStep.multiply(FBP.rotationMult * 5.0));
            if (this.particleAge >= this.particleMaxAge) {
                this.particleScale = FBP.randomFadingSpeed ? (this.particleScale *= (float)(0.75 * this.endMult)) : (this.particleScale *= 0.75f);
                if ((double)this.particleAlpha > 0.01 && (double)this.particleScale <= this.scaleAlpha) {
                    this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha *= (float)((double)0.65f * this.endMult)) : (this.particleAlpha *= 0.65f);
                }
                if ((double)this.particleAlpha <= 0.01) {
                    this.setDead();
                }
            } else {
                if (this.particleScale < 1.0f) {
                    this.particleScale = FBP.randomFadingSpeed ? (this.particleScale += (float)((double)0.075f * this.endMult)) : (this.particleScale += 0.075f);
                    if (this.particleScale > 1.0f) {
                        this.particleScale = 1.0f;
                    }
                }
                if (this.particleAlpha < 1.0f) {
                    this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha += (float)((double)0.045f * this.endMult)) : (this.particleAlpha += 0.045f);
                    if (this.particleAlpha > 1.0f) {
                        this.particleAlpha = 1.0f;
                    }
                }
            }
            this.motionY -= 0.04 * (double)this.particleGravity;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            if (this.onGround && FBP.restOnFloor) {
                this.rot.x = (float)Math.round(this.rot.x / 90.0) * 90.0f;
                this.rot.z = (float)Math.round(this.rot.z / 90.0) * 90.0f;
            }
            if (this.worldObj.getBlockState(this.getPosition()).getBlock().getMaterial().isLiquid()) {
                this.setDead();
            }
            this.motionX *= (double)0.98f;
            if (this.motionY < -0.2) {
                this.motionY *= 0.7500000190734863;
            }
            this.motionZ *= (double)0.98f;
            if (this.onGround) {
                this.motionX *= 0.680000190734863;
                this.motionZ *= 0.6800000190734863;
                this.rotStep = this.rotStep.multiply(0.85);
                this.particleAge += 2;
            }
        }
    }

    @Override
    public void moveEntity(double x2, double y2, double z2) {
        double X = x2;
        double Y = y2;
        double Z = z2;
        List<AxisAlignedBB> list1 = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x2, y2, z2));
        for (AxisAlignedBB axisalignedbb1 : list1) {
            y2 = axisalignedbb1.calculateYOffset(this.getEntityBoundingBox(), y2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y2, 0.0));
        for (AxisAlignedBB axisalignedbb2 : list1) {
            x2 = axisalignedbb2.calculateXOffset(this.getEntityBoundingBox(), x2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x2, 0.0, 0.0));
        for (AxisAlignedBB axisalignedbb3 : list1) {
            z2 = axisalignedbb3.calculateZOffset(this.getEntityBoundingBox(), z2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z2));
        AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
        this.posX = (axisalignedbb4.minX + axisalignedbb4.maxX) / 2.0;
        this.posY = axisalignedbb4.minY;
        this.posZ = (axisalignedbb4.minZ + axisalignedbb4.maxZ) / 2.0;
        boolean bl2 = this.onGround = y2 != Y && Y < 0.0;
        if (x2 != X) {
            this.motionX *= (double)0.7f;
        }
        if (z2 != Z) {
            this.motionZ *= (double)0.7f;
        }
    }

    @Override
    public void renderParticle(WorldRenderer buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!FBP.isEnabled() && this.particleMaxAge != 0) {
            this.particleMaxAge = 0;
        }
        float f2 = 0.0f;
        float f22 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        if (this.particleIcon != null) {
            if (!FBP.cartoonMode) {
                f2 = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0f * 16.0f);
                f3 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0f * 16.0f);
            }
            f22 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
            f4 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
        } else {
            f2 = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0f) / 16.0f;
            f22 = f2 + 0.015609375f;
            f3 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0f) / 16.0f;
            f4 = f3 + 0.015609375f;
        }
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i2 = this.getBrightnessForRender(partialTicks);
        float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
        float f8 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
        if (FBP.restOnFloor) {
            f6 += f8 / 10.0f;
        }
        FBPVector3d smoothRot = new FBPVector3d(0.0, 0.0, 0.0);
        if (FBP.rotationMult > 0.0) {
            smoothRot.y = this.rot.y;
            smoothRot.z = this.rot.z;
            if (!FBP.randomRotation) {
                smoothRot.x = this.rot.x;
            }
            if (!FBP.frozen) {
                FBPVector3d vec = this.rot.partialVec(this.prevRot, partialTicks);
                if (FBP.randomRotation) {
                    smoothRot.y = vec.y;
                    smoothRot.z = vec.z;
                } else {
                    smoothRot.x = vec.x;
                }
            }
        }
        GlStateManager.enableCull();
        this.par = new Vector2f[]{new Vector2f(f22, f4), new Vector2f(f22, f3), new Vector2f(f2, f3), new Vector2f(f2, f4)};
        FBPRenderUtil.renderCubeShaded_S(buf, this.par, f5, f6, f7, f8 / 10.0f, smoothRot, i2 >> 16 & 0xFFFF, i2 & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha);
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        int i2 = super.getBrightnessForRender(p_189214_1_);
        int j2 = 0;
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, this.posY, this.posZ))) {
            j2 = this.worldObj.getCombinedLight(new BlockPos(this.posX, this.posY, this.posZ), 0);
        }
        return i2 == 0 ? j2 : i2;
    }
}

