/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.util.FBPMathUtil;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.lwjgl.util.vector.Vector2f;

public class FBPParticleDigging
extends EntityDiggingFX {
    private final IBlockState sourceState;
    Minecraft mc;
    float prevGravity;
    double startY;
    double scaleAlpha;
    double prevParticleScale;
    double prevParticleAlpha;
    double prevMotionX;
    double prevMotionZ;
    double endMult = 0.75;
    boolean modeDebounce;
    boolean wasFrozen;
    boolean destroyed;
    boolean killToggle;
    EnumFacing facing;
    FBPVector3d rot;
    FBPVector3d prevRot;
    FBPVector3d rotStep;
    Vector2f uvMin;
    Vector2f uvMax;

    protected FBPParticleDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale, float R, float G, float B, IBlockState state, EnumFacing facing, TextureAtlasSprite texture) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
        boolean modeDebounce;
        this.particleRed = R;
        this.particleGreen = G;
        this.particleBlue = B;
        this.mc = Minecraft.getMinecraft();
        this.rot = new FBPVector3d();
        this.prevRot = new FBPVector3d();
        this.facing = facing;
        this.createRotationMatrix();
        try {
            FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
        }
        catch (Throwable e1) {
            e1.printStackTrace();
        }
        if (scale > -1.0f) {
            this.particleScale = scale;
        }
        if (scale < -1.0f && facing != null && facing == EnumFacing.UP && FBP.smartBreaking) {
            this.motionX *= 1.5;
            this.motionY *= 0.1;
            this.motionZ *= 1.5;
            double particleSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            Vec3 vec = this.mc.thePlayer.getLookVec();
            double x2 = FBPMathUtil.add(vec.xCoord, 0.01);
            double z2 = FBPMathUtil.add(vec.zCoord, 0.01);
            this.motionX = x2 * particleSpeed;
            this.motionZ = z2 * particleSpeed;
        }
        this.modeDebounce = modeDebounce = !FBP.randomRotation;
        if (modeDebounce) {
            this.rot.zero();
            this.calculateYAngle();
        }
        this.sourceState = state;
        Block b2 = state.getBlock();
        this.particleGravity = (float)((double)b2.blockParticleGravity * FBP.gravityMult);
        this.particleScale = (float)(FBP.scaleMult * (double)(FBP.randomizedScale ? this.particleScale : 1.0f));
        this.particleMaxAge = (int)FBP.random.nextDouble(FBP.minAge, (double)FBP.maxAge + 0.5);
        this.scaleAlpha = (double)this.particleScale * 0.82;
        boolean bl2 = this.destroyed = facing == null;
        if (texture == null) {
            BlockModelShapes blockModelShapes = this.mc.getBlockRendererDispatcher().getBlockModelShapes();
            if (!this.destroyed) {
                try {
                    IBakedModel model = blockModelShapes.getModelForState(state);
                    this.particleIcon = model.getParticleTexture();
                    List<BakedQuad> quads = model.getFaceQuads(facing);
                    if (quads != null && !quads.isEmpty()) {
                        int[] data = quads.get(0).getVertexData();
                        float u1 = Float.intBitsToFloat(data[4]);
                        float v1 = Float.intBitsToFloat(data[5]);
                        float u2 = Float.intBitsToFloat(data[18]);
                        float v2 = Float.intBitsToFloat(data[19]);
                        this.uvMin = new Vector2f(u1, v1);
                        this.uvMax = new Vector2f(u2, v2);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (this.particleIcon == null || this.particleIcon.getIconName().equals("missingno")) {
                this.particleIcon = blockModelShapes.getTexture(state);
                if (this.particleIcon != null) {
                    this.uvMin = new Vector2f(this.particleIcon.getMinU(), this.particleIcon.getMinV());
                    this.uvMax = new Vector2f(this.particleIcon.getMaxU(), this.particleIcon.getMaxV());
                }
            }
        } else {
            this.particleIcon = texture;
        }
        if (FBP.randomFadingSpeed) {
            this.endMult = MathHelper.clamp_double(FBP.random.nextDouble(0.5, 0.9), 0.55, 0.8);
        }
        this.prevGravity = this.particleGravity;
        this.startY = this.posY;
        this.multipleParticleScaleBy(1.0f);
    }

    @Override
    public FBPParticleDigging setBlockPos(BlockPos pos) {
        if (this.sourceState.getBlock() == Blocks.grass && this.facing != EnumFacing.UP) {
            return this;
        }
        int i2 = this.sourceState.getBlock().colorMultiplier(this.worldObj, pos);
        this.particleRed *= (float)(i2 >> 16 & 0xFF) / 255.0f;
        this.particleGreen *= (float)(i2 >> 8 & 0xFF) / 255.0f;
        this.particleBlue *= (float)(i2 & 0xFF) / 255.0f;
        return this;
    }

    @Override
    public FBPParticleDigging func_174845_l() {
        if (this.sourceState.getBlock() == Blocks.grass && this.facing != EnumFacing.UP) {
            return this;
        }
        int i2 = this.sourceState.getBlock().colorMultiplier(this.worldObj, new BlockPos(this.posX, this.posY, this.posZ));
        this.particleRed *= (float)(i2 >> 16 & 0xFF) / 255.0f;
        this.particleGreen *= (float)(i2 >> 8 & 0xFF) / 255.0f;
        this.particleBlue *= (float)(i2 & 0xFF) / 255.0f;
        return this;
    }

    @Override
    public EntityFX multipleParticleScaleBy(float scale) {
        EntityFX p2 = super.multipleParticleScaleBy(scale);
        float f2 = this.particleScale / 10.0f;
        if (FBP.restOnFloor && this.destroyed) {
            double n2;
            this.prevPosY = n2 = this.startY - (double)f2;
            this.posY = n2;
        }
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
        boolean allowedToMove;
        boolean bl2 = allowedToMove = (double)MathHelper.abs((float)this.motionX) > 1.0E-4 || (double)MathHelper.abs((float)this.motionZ) > 1.0E-4;
        if (!FBP.frozen && FBP.bounceOffWalls && !this.mc.isGamePaused() && this.particleAge > 0) {
            if (!this.wasFrozen && allowedToMove) {
                boolean zCollided;
                boolean xCollided = this.prevPosX == this.posX;
                boolean bl3 = zCollided = this.prevPosZ == this.posZ;
                if (xCollided) {
                    this.motionX = -this.prevMotionX * 0.625;
                }
                if (zCollided) {
                    this.motionZ = -this.prevMotionZ * 0.625;
                }
                if (!FBP.randomRotation && (xCollided || zCollided)) {
                    this.calculateYAngle();
                }
            } else {
                this.wasFrozen = false;
            }
        }
        if (FBP.frozen && FBP.bounceOffWalls && !this.wasFrozen) {
            this.wasFrozen = true;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRot.copyFrom(this.rot);
        this.prevParticleAlpha = this.particleAlpha;
        this.prevParticleScale = this.particleScale;
        if (!(this.mc.isGamePaused() || FBP.frozen && !this.killToggle)) {
            if (!this.killToggle) {
                if (!FBP.randomRotation) {
                    if (!this.modeDebounce) {
                        this.modeDebounce = true;
                        this.rot.z = 0.0;
                        this.calculateYAngle();
                    }
                    if (allowedToMove) {
                        double x2 = MathHelper.abs((float)(this.rotStep.x * this.getMult()));
                        if (this.motionX > 0.0) {
                            if (this.motionZ > 0.0) {
                                FBPVector3d rot = this.rot;
                                rot.x -= x2;
                            } else if (this.motionZ < 0.0) {
                                FBPVector3d rot2 = this.rot;
                                rot2.x += x2;
                            }
                        } else if (this.motionX < 0.0) {
                            if (this.motionZ < 0.0) {
                                FBPVector3d rot3 = this.rot;
                                rot3.x += x2;
                            } else if (this.motionZ > 0.0) {
                                FBPVector3d rot4 = this.rot;
                                rot4.x -= x2;
                            }
                        }
                    }
                } else {
                    if (this.modeDebounce) {
                        this.modeDebounce = false;
                        this.rot.z = FBP.random.nextDouble(30.0, 400.0);
                    }
                    if (allowedToMove) {
                        this.rot.add(this.rotStep.multiply(this.getMult()));
                    }
                }
            }
            if (!FBP.infiniteDuration) {
                ++this.particleAge;
            }
            if (this.particleAge >= this.particleMaxAge || this.killToggle) {
                this.particleScale *= (float)(0.8876543045043945 * this.endMult);
                if ((double)this.particleAlpha > 0.01 && (double)this.particleScale <= this.scaleAlpha) {
                    this.particleAlpha *= (float)((double)0.68752f * this.endMult);
                }
                if ((double)this.particleAlpha <= 0.01) {
                    this.setDead();
                }
            }
            if (!this.killToggle) {
                if (!this.isCollided) {
                    this.motionY -= 0.04 * (double)this.particleGravity;
                }
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                if (this.isCollided && FBP.restOnFloor) {
                    this.rot.x = (float)Math.round(this.rot.x / 90.0) * 90.0f;
                    this.rot.z = (float)Math.round(this.rot.z / 90.0) * 90.0f;
                }
                if ((double)MathHelper.abs((float)this.motionX) > 1.0E-5) {
                    this.prevMotionX = this.motionX;
                }
                if ((double)MathHelper.abs((float)this.motionZ) > 1.0E-5) {
                    this.prevMotionZ = this.motionZ;
                }
                if (allowedToMove) {
                    this.motionX *= (double)0.98f;
                    this.motionZ *= (double)0.98f;
                }
                this.motionY *= (double)0.98f;
                if (FBP.entityCollision) {
                    List<Entity> list = this.worldObj.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox());
                    for (Entity entityIn : list) {
                        double d2;
                        double d0;
                        double d3;
                        if (entityIn.noClip || (d3 = MathHelper.abs_max(d0 = this.posX - entityIn.posX, d2 = this.posZ - entityIn.posZ)) < (double)0.01f) continue;
                        d3 = Math.sqrt(d3);
                        d0 /= d3;
                        d2 /= d3;
                        double d4 = 1.0 / d3;
                        if (d4 > 1.0) {
                            d4 = 1.0;
                        }
                        this.motionX += d0 * d4 / 20.0;
                        this.motionZ += d2 * d4 / 20.0;
                        if (!FBP.randomRotation) {
                            this.calculateYAngle();
                        }
                        if (FBP.frozen) continue;
                        this.isCollided = false;
                    }
                }
                if (FBP.waterPhysics) {
                    if (this.isInWater()) {
                        this.handleWaterMovement();
                        if (FBP.getInstance().doesMaterialFloat(this.sourceState.getBlock().getMaterial())) {
                            this.motionY = 0.11f + this.particleScale / 1.25f * 0.02f;
                        } else {
                            this.motionX *= 0.932515086137662;
                            this.motionZ *= 0.932515086137662;
                            this.particleGravity = 0.35f;
                            this.motionY *= (double)0.85f;
                        }
                        if (!FBP.randomRotation) {
                            this.calculateYAngle();
                        }
                        if (this.isCollided) {
                            this.isCollided = false;
                        }
                    } else {
                        this.particleGravity = this.prevGravity;
                    }
                }
                if (this.isCollided) {
                    if (FBP.lowTraction) {
                        this.motionX *= 0.932515086137662;
                        this.motionZ *= 0.932515086137662;
                    } else {
                        this.motionX *= 0.6654999988079071;
                        this.motionZ *= 0.6654999988079071;
                    }
                }
            }
        }
    }

    @Override
    public boolean isInWater() {
        int maxZ;
        double scale = this.particleScale / 20.0f;
        int minX = MathHelper.floor_double(this.posX - scale);
        int maxX = MathHelper.ceiling_double_int(this.posX + scale);
        int minY = MathHelper.floor_double(this.posY - scale);
        int maxY = MathHelper.ceiling_double_int(this.posY + scale);
        int minZ = MathHelper.floor_double(this.posZ - scale);
        if (this.worldObj.isAreaLoaded(new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ = MathHelper.ceiling_double_int(this.posZ + scale)), true)) {
            int x2 = minX;
            while (x2 < maxX) {
                int y2 = minY;
                while (y2 < maxY) {
                    int z2 = minZ;
                    while (z2 < maxZ) {
                        double d0;
                        IBlockState block = this.worldObj.getBlockState(new BlockPos(x2, y2, z2));
                        if (block.getBlock().getMaterial() == Material.water && this.posY <= (d0 = (double)((float)(y2 + 1) - BlockLiquid.getLiquidHeightPercent(block.getValue(BlockLiquid.LEVEL))))) {
                            return true;
                        }
                        ++z2;
                    }
                    ++y2;
                }
                ++x2;
            }
        }
        return false;
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
        this.posY = axisalignedbb2.minY;
        this.posZ = (axisalignedbb2.minZ + axisalignedbb2.maxZ) / 2.0;
        boolean bl2 = this.isCollided = y2 != Y && Y < 0.0;
        if (!FBP.lowTraction && !FBP.bounceOffWalls) {
            if (x2 != X) {
                this.motionX *= (double)0.7f;
            }
            if (z2 != Z) {
                this.motionZ *= (double)0.7f;
            }
        }
    }

    @Override
    public void renderParticle(WorldRenderer buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (!FBP.isEnabled() && this.particleMaxAge != 0) {
            this.particleMaxAge = 0;
        }
        float minX = 0.0f;
        float maxX = 0.0f;
        float minY = 0.0f;
        float maxY = 0.0f;
        float f4 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
        if (this.particleIcon != null) {
            if (this.uvMin == null && this.uvMax == null) {
                minX = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0f * 16.0f);
                minY = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0f * 16.0f);
                maxX = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0f) / 4.0f * 16.0f);
                maxY = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0f) / 4.0f * 16.0f);
            } else {
                int size = 4;
                float sizeX = this.uvMax.x - this.uvMin.x;
                float sizeY = this.uvMax.y - this.uvMin.y;
                float startX = (this.particleTextureJitterX + 1.0f) * 4.0f - 4.0f;
                float startY = (this.particleTextureJitterY + 1.0f) * 4.0f - 4.0f;
                minX = this.uvMin.x + sizeX / 16.0f * startX;
                minY = this.uvMin.y + sizeY / 16.0f * startY;
                maxX = this.uvMax.x - sizeX / 16.0f * (16.0f - startX - 4.0f);
                maxY = this.uvMax.y - sizeY / 16.0f * (16.0f - startY - 4.0f);
            }
        } else {
            minX = (0.0f + this.particleTextureJitterX / 4.0f) / 16.0f;
            minY = (0.0f + this.particleTextureJitterY / 4.0f) / 16.0f;
            maxX = minX + 0.015609375f;
            maxY = minY + 0.015609375f;
        }
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i2 = this.getBrightnessForRender(partialTicks);
        float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
        if (FBP.restOnFloor) {
            f6 += f4 / 10.0f;
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
        FBPRenderUtil.renderCubeShaded_S(buf, new Vector2f[]{new Vector2f(maxX, maxY), new Vector2f(maxX, minY), new Vector2f(minX, minY), new Vector2f(minX, maxY)}, f5, f6, f7, f4 / 10.0f, smoothRot, i2 >> 16 & 0xFFFF, i2 & 0xFFFF, this.particleRed, this.particleGreen, this.particleBlue, alpha);
    }

    private void createRotationMatrix() {
        double rx0 = FBP.random.nextDouble();
        double ry0 = FBP.random.nextDouble();
        double rz0 = FBP.random.nextDouble();
        this.rotStep = new FBPVector3d(rx0 > 0.5 ? 1.0 : -1.0, ry0 > 0.5 ? 1.0 : -1.0, rz0 > 0.5 ? 1.0 : -1.0);
        this.rot.copyFrom(this.rotStep);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        AxisAlignedBB box2 = this.getEntityBoundingBox();
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
            double d0 = (box2.maxY - box2.minY) * 0.66;
            double k2 = this.posY + d0 + 0.01 - (double)(FBP.restOnFloor ? this.particleScale / 10.0f : 0.0f);
            return this.worldObj.getCombinedLight(new BlockPos(this.posX, k2, this.posZ), 0);
        }
        return 0;
    }

    private void calculateYAngle() {
        double angleSin = Math.toDegrees(Math.asin(this.motionX / Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)));
        this.rot.y = this.motionX > 0.0 ? (this.motionZ > 0.0 ? -angleSin : angleSin) : (this.motionZ > 0.0 ? -angleSin : angleSin);
    }

    double getMult() {
        return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * (double)(FBP.randomRotation ? 200 : 500) * FBP.rotationMult;
    }

    public static class Factory
    implements IParticleFactory {
        @Override
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_) {
            return new FBPParticleDigging(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, -1.0f, 1.0f, 1.0f, 1.0f, Block.getStateById(p_178902_15_[0]), null, null);
        }
    }
}

