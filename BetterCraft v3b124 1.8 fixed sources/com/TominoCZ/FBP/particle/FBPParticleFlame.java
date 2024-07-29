/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import java.util.List;
import javax.vecmath.Vector2f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FBPParticleFlame
extends EntityFlameFX {
    Minecraft mc;
    double startScale;
    double scaleAlpha;
    double prevParticleScale;
    double prevParticleAlpha;
    double endMult = 1.0;
    boolean spawnAnother = true;
    Vec3 startPos;
    Vec3[] cube;
    Vector2f par;

    protected FBPParticleFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mX, double mY, double mZ, boolean spawnAnother) {
        super(worldIn, xCoordIn, yCoordIn - 0.06, zCoordIn, mX, mY, mZ);
        IBlockState bs2 = worldIn.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
        this.spawnAnother = spawnAnother;
        if (bs2.getBlock() != Blocks.torch) {
            spawnAnother = false;
        }
        if (bs2 == Blocks.torch.getDefaultState()) {
            double n2;
            this.posY = n2 = this.posY + (double)0.04f;
            this.prevPosY = n2;
        }
        this.startPos = new Vec3(this.posX, this.posY, this.posZ);
        this.mc = Minecraft.getMinecraft();
        this.motionY = -8.5E-4f;
        this.particleGravity = -0.05f;
        this.particleIcon = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.snow.getDefaultState());
        this.particleScale *= (float)(FBP.scaleMult * 2.5);
        this.particleMaxAge = FBP.random.nextInt(3, 5);
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 0.0f;
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
            this.endMult *= FBP.random.nextDouble(0.9875, 1.0);
        }
        this.multipleParticleScaleBy(1.0f);
    }

    @Override
    public EntityFX multipleParticleScaleBy(float scale) {
        EntityFX p2 = super.multipleParticleScaleBy(scale);
        this.startScale = this.particleScale;
        this.scaleAlpha = (double)this.particleScale * 0.35;
        float f2 = this.particleScale / 80.0f;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - (double)f2, this.posY - (double)f2, this.posZ - (double)f2, this.posX + (double)f2, this.posY + (double)f2, this.posZ + (double)f2));
        return p2;
    }

    @Override
    public int getFXLayer() {
        return 0;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevParticleAlpha = this.particleAlpha;
        this.prevParticleScale = this.particleScale;
        if (!FBP.fancyFlame) {
            this.isDead = true;
        }
        if (++this.particleAge >= this.particleMaxAge) {
            this.particleScale = FBP.randomFadingSpeed ? (this.particleScale *= (float)((double)0.95f * this.endMult)) : (this.particleScale *= 0.95f);
            if ((double)this.particleAlpha > 0.01 && (double)this.particleScale <= this.scaleAlpha) {
                this.particleAlpha = FBP.randomFadingSpeed ? (this.particleAlpha *= (float)((double)0.9f * this.endMult)) : (this.particleAlpha *= 0.9f);
            }
            if ((double)this.particleAlpha <= 0.01) {
                this.setDead();
            } else if ((double)this.particleAlpha <= 0.325 && this.spawnAnother && this.worldObj.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getBlock() == Blocks.torch) {
                this.spawnAnother = false;
                this.mc.effectRenderer.addEffect(new FBPParticleFlame(this.worldObj, this.startPos.xCoord, this.startPos.yCoord, this.startPos.zCoord, 0.0, 0.0, 0.0, this.spawnAnother));
            }
        }
        this.moveEntity(0.0, this.motionY -= 0.02 * (double)this.particleGravity, 0.0);
        this.motionY *= 0.95;
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
        if (this.particleAge >= this.particleMaxAge) {
            this.particleGreen = (float)((double)f6 / this.startScale);
        }
        GlStateManager.enableCull();
        this.par = new Vector2f(f2, f22);
        Tessellator.getInstance().draw();
        this.mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        worldRendererIn.setTranslation(f3, f4, f5);
        this.putCube(worldRendererIn, f6 / 80.0f, i2 >> 16 & 0xFFFF, i2 & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha);
        worldRendererIn.setTranslation(0.0, 0.0, 0.0);
        Tessellator.getInstance().draw();
        Minecraft.getMinecraft().getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
        worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
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
            brightnessForRender *= 0.95f;
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
}

