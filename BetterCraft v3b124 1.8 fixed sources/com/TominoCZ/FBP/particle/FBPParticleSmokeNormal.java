/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import java.util.List;
import javax.vecmath.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FBPParticleSmokeNormal
extends EntitySmokeFX {
    Minecraft mc;
    double startScale;
    double scaleAlpha;
    double prevParticleScale;
    double prevParticleAlpha;
    double endMult = 0.75;
    Vec3[] cube;
    Vector2f par;
    EntitySmokeFX original;

    protected FBPParticleSmokeNormal(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mX, double mY, double mZ, float scale, boolean b2, TextureAtlasSprite tex, EntitySmokeFX original) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, mX, mY, mZ, scale);
        this.original = original;
        this.motionX = mX;
        this.motionY = mY;
        this.motionZ = mZ;
        this.mc = Minecraft.getMinecraft();
        this.particleIcon = tex;
        this.scaleAlpha = (double)this.particleScale * 0.85;
        Block block = worldIn.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock();
        if (block == Blocks.fire) {
            this.particleScale *= 0.65f;
            this.particleGravity *= 0.25f;
            this.motionX = FBP.random.nextDouble(-0.05, 0.05);
            this.motionY = FBP.random.nextDouble() * 0.5;
            this.motionZ = FBP.random.nextDouble(-0.05, 0.05);
            this.motionY *= (double)0.35f;
            this.scaleAlpha = (double)this.particleScale * 0.5;
            this.particleMaxAge = FBP.random.nextInt(7, 18);
        } else if (block == Blocks.torch) {
            this.particleScale *= 0.45f;
            this.motionX = FBP.random.nextDouble(-0.05, 0.05);
            this.motionY = FBP.random.nextDouble() * 0.5;
            this.motionZ = FBP.random.nextDouble(-0.05, 0.05);
            this.motionX *= (double)0.925f;
            this.motionY = 0.005f;
            this.motionZ *= (double)0.925f;
            this.particleRed = 0.275f;
            this.particleGreen = 0.275f;
            this.particleBlue = 0.275f;
            this.scaleAlpha = (double)this.particleScale * 0.75;
            this.particleMaxAge = FBP.random.nextInt(5, 10);
        } else {
            this.particleScale = scale;
            this.motionY *= 0.935;
        }
        this.particleScale *= (float)FBP.scaleMult;
        this.startScale = this.particleScale;
        float angleY = this.rand.nextFloat() * 80.0f;
        this.cube = new Vec3[FBP.CUBE.length];
        int i2 = 0;
        while (i2 < FBP.CUBE.length) {
            Vec3 vec = FBP.CUBE[i2];
            this.cube[i2] = FBPRenderUtil.rotatef_d(vec, 0.0f, angleY, 0.0f);
            ++i2;
        }
        this.particleAlpha = 1.0f;
        if (FBP.randomFadingSpeed) {
            this.endMult = MathHelper.clamp_double(FBP.random.nextDouble(0.425, 1.15), 0.5432, 1.0);
        }
        this.multipleParticleScaleBy(1.0f);
    }

    @Override
    public EntityFX multipleParticleScaleBy(float scale) {
        EntityFX p2 = super.multipleParticleScaleBy(scale);
        float f2 = this.particleScale / 20.0f;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - (double)f2, this.posY - (double)f2, this.posZ - (double)f2, this.posX + (double)f2, this.posY + (double)f2, this.posZ + (double)f2));
        return p2;
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
        if (!FBP.fancySmoke) {
            this.isDead = true;
        }
        if (++this.particleAge >= this.particleMaxAge) {
            this.particleScale = FBP.randomFadingSpeed ? (this.particleScale *= (float)(0.8876543045043945 * this.endMult)) : (this.particleScale *= 0.8876543f);
            if ((double)this.particleAlpha > 0.01 && (double)this.particleScale <= this.scaleAlpha) {
                this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha *= (float)(0.7654321193695068 * this.endMult)) : (this.particleAlpha *= 0.7654321f);
            }
            if ((double)this.particleAlpha <= 0.01) {
                this.setDead();
            }
        }
        this.motionY += 0.004;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }
        this.motionX *= (double)0.96f;
        this.motionY *= (double)0.96f;
        this.motionZ *= (double)0.96f;
        if (this.onGround) {
            this.motionX *= 0.899999988079071;
            this.motionZ *= 0.899999988079071;
        }
    }

    @Override
    public void moveEntity(double x2, double y2, double z2) {
        double X = x2;
        double Y = y2;
        double Z = z2;
        List<AxisAlignedBB> list = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x2, y2, z2));
        for (AxisAlignedBB axisalignedbb : list) {
            y2 = axisalignedbb.calculateYOffset(this.getEntityBoundingBox(), y2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y2, 0.0));
        for (AxisAlignedBB axisalignedbb : list) {
            x2 = axisalignedbb.calculateXOffset(this.getEntityBoundingBox(), x2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x2, 0.0, 0.0));
        for (AxisAlignedBB axisalignedbb : list) {
            z2 = axisalignedbb.calculateZOffset(this.getEntityBoundingBox(), z2);
        }
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z2));
        AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
        this.posX = (axisalignedbb2.minX + axisalignedbb2.maxX) / 2.0;
        this.posY = (axisalignedbb2.minY + axisalignedbb2.maxY) / 2.0;
        this.posZ = (axisalignedbb2.minZ + axisalignedbb2.maxZ) / 2.0;
        this.onGround = y2 != Y;
    }

    @Override
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!FBP.isEnabled() && this.particleMaxAge != 0) {
            this.particleMaxAge = 0;
        }
        float f2 = this.particleIcon.getInterpolatedU(4.4f);
        float f22 = this.particleIcon.getInterpolatedV(4.4f);
        float f3 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f4 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f5 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i2 = this.getBrightnessForRender(partialTicks);
        float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
        float f6 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
        this.par = new Vector2f(f2, f22);
        worldRendererIn.setTranslation(f3, f4, f5);
        this.putCube(worldRendererIn, f6 / 20.0f, i2 >> 16 & 0xFFFF, i2 & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha);
        worldRendererIn.setTranslation(0.0, 0.0, 0.0);
    }

    public void putCube(WorldRenderer worldRendererIn, double scale, int j2, int k2, float r2, float g2, float b2, float a2) {
        float brightnessForRender = 1.0f;
        float R = 0.0f;
        float G = 0.0f;
        float B = 0.0f;
        int i2 = 0;
        while (i2 < this.cube.length) {
            Vec3 v1 = this.cube[i2];
            Vec3 v2 = this.cube[i2 + 1];
            Vec3 v3 = this.cube[i2 + 2];
            Vec3 v4 = this.cube[i2 + 3];
            R = r2 * brightnessForRender;
            G = g2 * brightnessForRender;
            B = b2 * brightnessForRender;
            brightnessForRender = (float)((double)brightnessForRender * 0.875);
            this.addVt(worldRendererIn, scale, v1, this.par.x, this.par.y, j2, k2, R, G, B, a2);
            this.addVt(worldRendererIn, scale, v2, this.par.x, this.par.y, j2, k2, R, G, B, a2);
            this.addVt(worldRendererIn, scale, v3, this.par.x, this.par.y, j2, k2, R, G, B, a2);
            this.addVt(worldRendererIn, scale, v4, this.par.x, this.par.y, j2, k2, R, G, B, a2);
            i2 += 4;
        }
    }

    private void addVt(WorldRenderer worldRendererIn, double scale, Vec3 pos, double u2, double v2, int j2, int k2, float r2, float g2, float b2, float a2) {
        worldRendererIn.pos(pos.xCoord * scale, pos.yCoord * scale, pos.zCoord * scale).tex(u2, v2).color(r2, g2, b2, a2).lightmap(j2, k2).endVertex();
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

    @Override
    public void setDead() {
        this.isDead = true;
        this.original.setDead();
    }

    public void setMaxAge(int maxAge) {
        this.particleMaxAge = maxAge;
    }
}

