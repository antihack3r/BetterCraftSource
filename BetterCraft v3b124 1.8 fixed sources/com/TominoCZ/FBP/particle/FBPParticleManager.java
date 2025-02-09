/*
 * Decompiled with CFR 0.152.
 */
package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.particle.FBPParticleDigging;
import com.TominoCZ.FBP.particle.FBPParticleFlame;
import com.TominoCZ.FBP.particle.FBPParticleRain;
import com.TominoCZ.FBP.particle.FBPParticleSmokeNormal;
import com.TominoCZ.FBP.particle.FBPParticleSnow;
import com.TominoCZ.FBP.util.FBPReflectionHelper;
import com.google.common.base.Throwables;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FBPParticleManager
extends EffectRenderer {
    private static MethodHandle getBlockDamage;
    private static MethodHandle getParticleScale;
    private static MethodHandle getParticleTexture;
    private static MethodHandle getParticleTypes;
    private static MethodHandle getSourceState;
    private static MethodHandle getParticleMaxAge;
    private static MethodHandle X;
    private static MethodHandle Y;
    private static MethodHandle Z;
    private static MethodHandle mX;
    private static MethodHandle mY;
    private static MethodHandle mZ;
    private static IParticleFactory particleFactory;
    private static IBlockState blockState;
    private static TextureAtlasSprite white;
    private static MethodHandles.Lookup lookup;
    Minecraft mc = Minecraft.getMinecraft();

    public FBPParticleManager(World worldIn, TextureManager rendererIn, IParticleFactory particleFactory) {
        super(worldIn, rendererIn);
        FBPParticleManager.particleFactory = particleFactory;
        white = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.snow.getDefaultState());
        lookup = MethodHandles.publicLookup();
        try {
            getParticleTypes = lookup.unreflectGetter(FBPReflectionHelper.findField(EffectRenderer.class, "particleTypes", "particleTypes"));
            X = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "posX", "posX"));
            Y = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "posY", "posY"));
            Z = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "posZ", "posZ"));
            mX = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "motionX", "motionX"));
            mY = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "motionY", "motionY"));
            mZ = lookup.unreflectGetter(FBPReflectionHelper.findField(Entity.class, "motionZ", "motionZ"));
            getParticleScale = lookup.unreflectGetter(FBPReflectionHelper.findField(EntityFX.class, "particleScale", "particleScale"));
            getParticleTexture = lookup.unreflectGetter(FBPReflectionHelper.findField(EntityFX.class, "particleIcon", "particleIcon"));
            getParticleMaxAge = lookup.unreflectGetter(FBPReflectionHelper.findField(EntityFX.class, "particleMaxAge", "particleMaxAge"));
            getSourceState = lookup.unreflectGetter(FBPReflectionHelper.findField(EntityDiggingFX.class, "field_174847_a"));
            getBlockDamage = lookup.unreflectGetter(FBPReflectionHelper.findField(RenderGlobal.class, "damagedBlocks", "damagedBlocks"));
        }
        catch (Throwable e2) {
            throw Throwables.propagate(e2);
        }
    }

    public void carryOver() {
        if (Minecraft.getMinecraft().effectRenderer == this) {
            return;
        }
        Field f1 = FBPReflectionHelper.findField(EffectRenderer.class, "fxLayers", "fxLayers");
        Field f2 = FBPReflectionHelper.findField(EffectRenderer.class, "particleEmitters", "particleEmitters");
        try {
            MethodHandle getF1 = lookup.unreflectGetter(f1);
            MethodHandle setF1 = lookup.unreflectSetter(f1);
            MethodHandle getF2 = lookup.unreflectGetter(f2);
            MethodHandle setF2 = lookup.unreflectSetter(f2);
            setF1.invokeExact(this, getF1.invokeExact(this.mc.effectRenderer));
            setF2.invokeExact(this, getF2.invokeExact(this.mc.effectRenderer));
        }
        catch (Throwable e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void addEffect(EntityFX efx) {
        EntityFX toAdd;
        EntityFX effect = toAdd = efx;
        if (toAdd != null && !(toAdd instanceof FBPParticleSnow) && !(toAdd instanceof FBPParticleRain)) {
            if (FBP.fancyFlame && toAdd instanceof EntityFlameFX && !(toAdd instanceof FBPParticleFlame) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
                try {
                    toAdd = new FBPParticleFlame(this.worldObj, X.invokeExact(effect), Y.invokeExact(effect), Z.invokeExact(effect), 0.0, FBP.random.nextDouble() * 0.25, 0.0, true);
                    effect.setDead();
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
            } else if (FBP.fancySmoke && toAdd instanceof EntitySmokeFX && !(toAdd instanceof FBPParticleSmokeNormal) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
                EntitySmokeFX p2 = (EntitySmokeFX)effect;
                try {
                    toAdd = new FBPParticleSmokeNormal(this.worldObj, X.invokeExact(effect), Y.invokeExact(effect), Z.invokeExact(effect), mX.invokeExact(effect), mY.invokeExact(effect), mZ.invokeExact(effect), getParticleScale.invokeExact(effect), true, white, p2);
                }
                catch (Throwable t2) {
                    t2.printStackTrace();
                }
            } else if (FBP.fancyRain && toAdd instanceof EntityRainFX) {
                efx.setAlphaF(0.0f);
            } else if (toAdd instanceof EntityDiggingFX && !(toAdd instanceof FBPParticleDigging)) {
                try {
                    blockState = getSourceState.invokeExact((EntityDiggingFX)effect);
                    if (!(blockState == null || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && blockState.getBlock() == Blocks.redstone_block)) {
                        effect.setDead();
                        if (blockState.getBlock() instanceof BlockLiquid || FBP.getInstance().isBlacklisted(blockState.getBlock())) {
                            return;
                        }
                        toAdd = new FBPParticleDigging(this.worldObj, X.invokeExact(effect), Y.invokeExact(effect), Z.invokeExact(effect), 0.0, 0.0, 0.0, getParticleScale.invokeExact(effect), toAdd.getRedColorF(), toAdd.getGreenColorF(), toAdd.getBlueColorF(), blockState, null, getParticleTexture.invokeExact(effect));
                    }
                }
                catch (Throwable e2) {
                    e2.printStackTrace();
                }
            } else if (toAdd instanceof FBPParticleDigging) {
                try {
                    blockState = getSourceState.invokeExact((EntityDiggingFX)effect);
                    if (!(blockState == null || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && blockState.getBlock() == Blocks.redstone_block || !(blockState.getBlock() instanceof BlockLiquid) && !FBP.getInstance().isBlacklisted(blockState.getBlock()))) {
                        effect.setDead();
                        return;
                    }
                }
                catch (Throwable e3) {
                    e3.printStackTrace();
                }
            }
        }
        super.addEffect(toAdd);
    }

    @Override
    public EntityFX spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        IParticleFactory iparticlefactory = null;
        try {
            iparticlefactory = (IParticleFactory)getParticleTypes.invokeExact(this).get(particleId);
        }
        catch (Throwable e2) {
            e2.printStackTrace();
        }
        if (iparticlefactory != null) {
            EntityFX toSpawn = iparticlefactory.getEntityFX(particleId, this.mc.theWorld, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            EntityFX particle = toSpawn;
            if (!(!(particle instanceof EntityDiggingFX) || particle instanceof FBPParticleDigging || (blockState = Block.getStateById(parameters[0])) == null || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && blockState.getBlock() == Blocks.redstone_block)) {
                toSpawn = !(blockState.getBlock() instanceof BlockLiquid) && !FBP.getInstance().isBlacklisted(blockState.getBlock()) ? new FBPParticleDigging(this.mc.theWorld, xCoord, yCoord + (double)0.1f, zCoord, xSpeed, ySpeed, zSpeed, -1.0f, 1.0f, 1.0f, 1.0f, blockState, null, null).func_174845_l().multipleParticleScaleBy(0.6f) : particle;
            }
            this.addEffect(toSpawn);
            return toSpawn;
        }
        return null;
    }

    @Override
    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        Block b2 = state.getBlock();
        state = state.getBlock().getActualState(state, this.worldObj, pos);
        b2 = state.getBlock();
        int i2 = 4;
        TextureAtlasSprite texture = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        int j2 = 0;
        while (j2 < FBP.particlesPerAxis) {
            int k2 = 0;
            while (k2 < FBP.particlesPerAxis) {
                int l2 = 0;
                while (l2 < FBP.particlesPerAxis) {
                    double d0 = (double)pos.getX() + ((double)j2 + 0.5) / (double)FBP.particlesPerAxis;
                    double d2 = (double)pos.getY() + ((double)k2 + 0.5) / (double)FBP.particlesPerAxis;
                    double d3 = (double)pos.getZ() + ((double)l2 + 0.5) / (double)FBP.particlesPerAxis;
                    if (!(state == null || b2 instanceof BlockLiquid || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && b2 == Blocks.redstone_block || FBP.getInstance().isBlacklisted(b2))) {
                        float scale = (float)FBP.random.nextDouble(0.75, 1.0);
                        FBPParticleDigging toSpawn = new FBPParticleDigging(this.worldObj, d0, d2, d3, d0 - (double)pos.getX() - 0.5, -0.001, d3 - (double)pos.getZ() - 0.5, scale, 1.0f, 1.0f, 1.0f, state, null, texture).setBlockPos(pos);
                        this.addEffect(toSpawn);
                    }
                    ++l2;
                }
                ++k2;
            }
            ++j2;
        }
    }

    @Override
    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = this.worldObj.getBlockState(pos);
        if (iblockstate.getBlock().getRenderType() != -1) {
            int i2 = pos.getX();
            int j2 = pos.getY();
            int k2 = pos.getZ();
            float f2 = 0.1f;
            AxisAlignedBB axisalignedbb = iblockstate.getBlock().getSelectedBoundingBox(this.worldObj, pos);
            double d0 = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            MovingObjectPosition obj = Minecraft.getMinecraft().objectMouseOver;
            if (obj == null || obj.hitVec == null) {
                obj = new MovingObjectPosition(null, new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
            }
            if (!(!FBP.smartBreaking || iblockstate == null || iblockstate.getBlock() instanceof BlockLiquid || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && iblockstate.getBlock() == Blocks.redstone_block)) {
                d0 = obj.hitVec.xCoord + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxX - axisalignedbb.minX);
                d2 = obj.hitVec.yCoord + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxY - axisalignedbb.minY);
                d3 = obj.hitVec.zCoord + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxZ - axisalignedbb.minZ);
            } else {
                d0 = (double)i2 + this.worldObj.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - (double)0.2f) + (double)0.1f + axisalignedbb.minX;
                d2 = (double)j2 + this.worldObj.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - (double)0.2f) + (double)0.1f + axisalignedbb.minY;
                d3 = (double)k2 + this.worldObj.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - (double)0.2f) + (double)0.1f + axisalignedbb.minZ;
            }
            switch (side) {
                case DOWN: {
                    d2 = (double)j2 + iblockstate.getBlock().getBlockBoundsMinY() - (double)0.1f;
                    break;
                }
                case EAST: {
                    d0 = (double)i2 + iblockstate.getBlock().getBlockBoundsMaxX() + (double)0.1f;
                    break;
                }
                case NORTH: {
                    d3 = (double)k2 + iblockstate.getBlock().getBlockBoundsMinZ() - (double)0.1f;
                    break;
                }
                case SOUTH: {
                    d3 = (double)k2 + iblockstate.getBlock().getBlockBoundsMaxZ() + (double)0.1f;
                    break;
                }
                case UP: {
                    d2 = (double)j2 + iblockstate.getBlock().getBlockBoundsMaxY() + (double)0.1f;
                    break;
                }
                case WEST: {
                    d0 = (double)i2 + iblockstate.getBlock().getBlockBoundsMinX() - (double)0.1f;
                }
            }
            if (!(iblockstate == null || iblockstate.getBlock() instanceof BlockLiquid || FBP.frozen && !FBP.spawnWhileFrozen || !FBP.spawnRedstoneBlockParticles && iblockstate.getBlock() == Blocks.redstone_block)) {
                int damage;
                block19: {
                    damage = 0;
                    try {
                        DestroyBlockProgress progress2 = null;
                        Map mp = getBlockDamage.invokeExact(Minecraft.getMinecraft().renderGlobal);
                        if (mp.isEmpty()) break block19;
                        for (DestroyBlockProgress progress2 : mp.values()) {
                            if (!progress2.getPosition().equals(pos)) continue;
                            damage = progress2.getPartialBlockDamage();
                            break;
                        }
                    }
                    catch (Throwable progress2) {
                        // empty catch block
                    }
                }
                if (!FBP.getInstance().isBlacklisted(iblockstate.getBlock())) {
                    EntityFX toSpawn = new FBPParticleDigging(this.worldObj, d0, d2, d3, 0.0, 0.0, 0.0, -2.0f, 1.0f, 1.0f, 1.0f, iblockstate, side, null).setBlockPos(pos);
                    if (FBP.smartBreaking) {
                        toSpawn = toSpawn.MultiplyVelocity(side == EnumFacing.UP ? 0.7f : 0.15f);
                        toSpawn = ((EntityFX)toSpawn).multipleParticleScaleBy(0.325f + (float)damage / 10.0f * 0.5f);
                    } else {
                        toSpawn = toSpawn.MultiplyVelocity(0.2f);
                        toSpawn = ((EntityFX)toSpawn).multipleParticleScaleBy(0.6f);
                    }
                    this.addEffect(toSpawn);
                }
            }
        }
    }
}

