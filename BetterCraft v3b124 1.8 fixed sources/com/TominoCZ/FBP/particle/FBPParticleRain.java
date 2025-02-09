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
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector2f;

public class FBPParticleRain
extends EntityDiggingFX {
    private final IBlockState sourceState;
    Minecraft mc;
    double AngleY;
    double particleHeight;
    double prevParticleScale;
    double prevParticleHeight;
    double prevParticleAlpha;
    double scalar = FBP.scaleMult;
    double endMult = 1.0;
    Vector2f[] par;

    public FBPParticleRain(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
        try {
            FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
        }
        catch (Throwable e1) {
            e1.printStackTrace();
        }
        this.AngleY = FBP.random.nextDouble() * 45.0;
        this.motionX = xSpeedIn;
        this.motionY = -ySpeedIn;
        this.motionZ = zSpeedIn;
        this.particleGravity = 0.025f;
        this.sourceState = state;
        this.mc = Minecraft.getMinecraft();
        this.particleMaxAge = (int)FBP.random.nextDouble(50.0, 70.0);
        this.particleAlpha = 0.0f;
        this.particleScale = 0.0f;
        this.isAirBorne = true;
        if (FBP.randomFadingSpeed) {
            this.endMult *= FBP.random.nextDouble(0.85, 1.0);
        }
    }

    @Override
    public void setParticleTextureIndex(int particleTextureIndex) {
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
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevParticleAlpha = this.particleAlpha;
        this.prevParticleScale = this.particleScale;
        this.prevParticleHeight = this.particleHeight;
        if (!this.mc.isGamePaused()) {
            ++this.particleAge;
            if (this.posY < this.mc.thePlayer.posY - (double)(this.mc.gameSettings.renderDistanceChunks * 9)) {
                this.setDead();
            }
            if (!this.onGround) {
                if (this.particleAge < this.particleMaxAge) {
                    double max = this.scalar * 0.5;
                    if ((double)this.particleScale < max) {
                        this.particleScale = FBP.randomFadingSpeed ? (this.particleScale += (float)((double)0.05f * this.endMult)) : (this.particleScale += 0.05f);
                        if ((double)this.particleScale > max) {
                            this.particleScale = (float)max;
                        }
                        this.particleHeight = this.particleScale;
                    }
                    if (this.particleAlpha < 0.65f) {
                        this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha += (float)((double)0.085f * this.endMult)) : (this.particleAlpha += 0.085f);
                        if (this.particleAlpha > 0.65f) {
                            this.particleAlpha = 0.65f;
                        }
                    }
                } else {
                    this.setDead();
                }
            }
            if (this.worldObj.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock().getMaterial().isLiquid()) {
                this.setDead();
            }
            this.motionY -= 0.04 * (double)this.particleGravity;
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionY *= 1.0002500019073486;
            if (this.onGround) {
                float max2;
                this.motionX = 0.0;
                this.motionY = -0.25;
                this.motionZ = 0.0;
                if (this.particleHeight > (double)0.075f) {
                    this.particleHeight *= (double)0.725f;
                }
                if (this.particleScale < (max2 = (float)this.scalar * 4.25f)) {
                    this.particleScale += max2 / 10.0f;
                    if (this.particleScale > max2) {
                        this.particleScale = max2;
                    }
                }
                if (this.particleScale >= max2 / 2.0f) {
                    this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha *= (float)(0.75 * this.endMult)) : (this.particleAlpha *= 0.75f);
                    if (this.particleAlpha <= 0.001f) {
                        this.setDead();
                    }
                }
            }
        }
        Vec3 rgb = this.mc.theWorld.getSkyColor(this.mc.thePlayer, 0.0f);
        this.particleRed = (float)rgb.xCoord;
        this.particleGreen = (float)MathHelper.clamp_double(rgb.yCoord + 0.25, 0.25, 1.0);
        this.particleBlue = (float)MathHelper.clamp_double(rgb.zCoord + 0.5, 0.5, 1.0);
        if (this.particleGreen > 1.0f) {
            this.particleGreen = 1.0f;
        }
        if (this.particleBlue > 1.0f) {
            this.particleBlue = 1.0f;
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
        this.resetPositionToBB();
        boolean bl2 = this.onGround = y2 != Y && Y < 0.0;
        if (x2 != X) {
            this.motionX *= (double)0.7f;
        }
        if (z2 != Z) {
            this.motionZ *= (double)0.7f;
        }
    }

    private void resetPositionToBB() {
        this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
        this.posY = this.getEntityBoundingBox().minY;
        this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
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
        float height = (float)(this.prevParticleHeight + (this.particleHeight - this.prevParticleHeight) * (double)partialTicks);
        this.par = new Vector2f[]{new Vector2f(f22, f4), new Vector2f(f22, f3), new Vector2f(f2, f3), new Vector2f(f2, f4)};
        FBPRenderUtil.renderCubeShaded_WH(buf, this.par, f5, f6 + height / 10.0f, f7, f8 / 10.0f, height / 10.0f, new FBPVector3d(0.0, this.AngleY, 0.0), i2 >> 16 & 0xFFFF, i2 & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha);
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

