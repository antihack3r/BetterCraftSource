/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MobAppearance;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;

public class EffectRenderer {
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    protected World worldObj;
    private List<EntityFX>[][] fxLayers = new List[4][];
    private List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
    private TextureManager renderer;
    private Random rand = new Random();
    private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

    public EffectRenderer(World worldIn, TextureManager rendererIn) {
        this.worldObj = worldIn;
        this.renderer = rendererIn;
        int i2 = 0;
        while (i2 < 4) {
            this.fxLayers[i2] = new List[2];
            int j2 = 0;
            while (j2 < 2) {
                this.fxLayers[i2][j2] = Lists.newArrayList();
                ++j2;
            }
            ++i2;
        }
        this.registerVanillaParticles();
    }

    private void registerVanillaParticles() {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
    }

    public void registerParticle(int id2, IParticleFactory particleFactory) {
        this.particleTypes.put(id2, particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
        this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    public EntityFX spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        EntityFX entityfx;
        IParticleFactory iparticlefactory = this.particleTypes.get(particleId);
        if (iparticlefactory != null && (entityfx = iparticlefactory.getEntityFX(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters)) != null) {
            this.addEffect(entityfx);
            return entityfx;
        }
        return null;
    }

    public void addEffect(EntityFX effect) {
        if (effect != null && (!(effect instanceof EntityFirework.SparkFX) || Config.isFireworkParticles())) {
            int j2;
            int i2 = effect.getFXLayer();
            int n2 = j2 = effect.getAlpha() != 1.0f ? 0 : 1;
            if (this.fxLayers[i2][j2].size() >= 4000) {
                this.fxLayers[i2][j2].remove(0);
            }
            this.fxLayers[i2][j2].add(effect);
        }
    }

    public void updateEffects() {
        int i2 = 0;
        while (i2 < 4) {
            this.updateEffectLayer(i2);
            ++i2;
        }
        ArrayList<EntityParticleEmitter> list = Lists.newArrayList();
        for (EntityParticleEmitter entityparticleemitter : this.particleEmitters) {
            entityparticleemitter.onUpdate();
            if (!entityparticleemitter.isDead) continue;
            list.add(entityparticleemitter);
        }
        this.particleEmitters.removeAll(list);
    }

    private void updateEffectLayer(int layer) {
        int i2 = 0;
        while (i2 < 2) {
            this.updateEffectAlphaLayer(this.fxLayers[layer][i2]);
            ++i2;
        }
    }

    private void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
        ArrayList<EntityFX> list = Lists.newArrayList();
        long i2 = System.currentTimeMillis();
        int j2 = entitiesFX.size();
        int k2 = 0;
        while (k2 < entitiesFX.size()) {
            EntityFX entityfx = entitiesFX.get(k2);
            this.tickParticle(entityfx);
            if (entityfx.isDead) {
                list.add(entityfx);
            }
            --j2;
            if (System.currentTimeMillis() > i2 + 20L) break;
            ++k2;
        }
        if (j2 > 0) {
            int l2 = j2;
            Iterator<EntityFX> iterator = entitiesFX.iterator();
            while (iterator.hasNext() && l2 > 0) {
                EntityFX entityfx1 = iterator.next();
                entityfx1.setDead();
                iterator.remove();
                --l2;
            }
        }
        entitiesFX.removeAll(list);
    }

    private void tickParticle(final EntityFX particle) {
        try {
            particle.onUpdate();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
            final int i2 = particle.getFXLayer();
            crashreportcategory.addCrashSectionCallable("Particle", new Callable<String>(){

                @Override
                public String call() throws Exception {
                    return particle.toString();
                }
            });
            crashreportcategory.addCrashSectionCallable("Particle Type", new Callable<String>(){

                @Override
                public String call() throws Exception {
                    return i2 == 0 ? "MISC_TEXTURE" : (i2 == 1 ? "TERRAIN_TEXTURE" : (i2 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i2));
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    public void renderParticles(Entity entityIn, float partialTicks) {
        float f2 = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f22 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.003921569f);
        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.worldObj, entityIn, partialTicks);
        boolean flag = block.getMaterial() == Material.water;
        int i2 = 0;
        while (i2 < 3) {
            int j2 = 0;
            while (j2 < 2) {
                final int i_f = i2;
                if (!this.fxLayers[i2][j2].isEmpty()) {
                    switch (j2) {
                        case 0: {
                            GlStateManager.depthMask(false);
                            break;
                        }
                        case 1: {
                            GlStateManager.depthMask(true);
                        }
                    }
                    switch (i2) {
                        default: {
                            this.renderer.bindTexture(particleTextures);
                            break;
                        }
                        case 1: {
                            this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                        }
                    }
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                    int k2 = 0;
                    while (k2 < this.fxLayers[i2][j2].size()) {
                        final EntityFX entityfx = this.fxLayers[i2][j2].get(k2);
                        try {
                            if (flag || !(entityfx instanceof EntitySuspendFX)) {
                                entityfx.renderParticle(worldrenderer, entityIn, partialTicks, f2, f4, f1, f22, f3);
                            }
                        }
                        catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                            crashreportcategory.addCrashSectionCallable("Particle", new Callable<String>(){

                                @Override
                                public String call() throws Exception {
                                    return entityfx.toString();
                                }
                            });
                            crashreportcategory.addCrashSectionCallable("Particle Type", new Callable<String>(){

                                @Override
                                public String call() throws Exception {
                                    return i_f == 0 ? "MISC_TEXTURE" : (i_f == 1 ? "TERRAIN_TEXTURE" : (i_f == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i_f));
                                }
                            });
                            throw new ReportedException(crashreport);
                        }
                        ++k2;
                    }
                    tessellator.draw();
                }
                ++j2;
            }
            ++i2;
        }
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
    }

    public void renderLitParticles(Entity entityIn, float partialTick) {
        float f2 = (float)Math.PI / 180;
        float f1 = MathHelper.cos(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f22 = MathHelper.sin(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f3 = -f22 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f5 = MathHelper.cos(entityIn.rotationPitch * ((float)Math.PI / 180));
        int i2 = 0;
        while (i2 < 2) {
            List<EntityFX> list = this.fxLayers[3][i2];
            if (!list.isEmpty()) {
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                int j2 = 0;
                while (j2 < list.size()) {
                    EntityFX entityfx = list.get(j2);
                    entityfx.renderParticle(worldrenderer, entityIn, partialTick, f1, f5, f22, f3, f4);
                    ++j2;
                }
            }
            ++i2;
        }
    }

    public void clearEffects(World worldIn) {
        this.worldObj = worldIn;
        int i2 = 0;
        while (i2 < 4) {
            int j2 = 0;
            while (j2 < 2) {
                this.fxLayers[i2][j2].clear();
                ++j2;
            }
            ++i2;
        }
        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        boolean flag;
        if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
            Block block = state.getBlock();
            flag = !Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos) && !Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this);
        } else {
            boolean bl2 = flag = state.getBlock().getMaterial() != Material.air;
        }
        if (flag) {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            int l2 = 4;
            int i2 = 0;
            while (i2 < l2) {
                int j2 = 0;
                while (j2 < l2) {
                    int k2 = 0;
                    while (k2 < l2) {
                        double d0 = (double)pos.getX() + ((double)i2 + 0.5) / (double)l2;
                        double d1 = (double)pos.getY() + ((double)j2 + 0.5) / (double)l2;
                        double d2 = (double)pos.getZ() + ((double)k2 + 0.5) / (double)l2;
                        this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - (double)pos.getX() - 0.5, d1 - (double)pos.getY() - 0.5, d2 - (double)pos.getZ() - 0.5, state).setBlockPos(pos));
                        ++k2;
                    }
                    ++j2;
                }
                ++i2;
            }
        }
    }

    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = this.worldObj.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (block.getRenderType() != -1) {
            int i2 = pos.getX();
            int j2 = pos.getY();
            int k2 = pos.getZ();
            float f2 = 0.1f;
            double d0 = (double)i2 + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double)(f2 * 2.0f)) + (double)f2 + block.getBlockBoundsMinX();
            double d1 = (double)j2 + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double)(f2 * 2.0f)) + (double)f2 + block.getBlockBoundsMinY();
            double d2 = (double)k2 + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double)(f2 * 2.0f)) + (double)f2 + block.getBlockBoundsMinZ();
            if (side == EnumFacing.DOWN) {
                d1 = (double)j2 + block.getBlockBoundsMinY() - (double)f2;
            }
            if (side == EnumFacing.UP) {
                d1 = (double)j2 + block.getBlockBoundsMaxY() + (double)f2;
            }
            if (side == EnumFacing.NORTH) {
                d2 = (double)k2 + block.getBlockBoundsMinZ() - (double)f2;
            }
            if (side == EnumFacing.SOUTH) {
                d2 = (double)k2 + block.getBlockBoundsMaxZ() + (double)f2;
            }
            if (side == EnumFacing.WEST) {
                d0 = (double)i2 + block.getBlockBoundsMinX() - (double)f2;
            }
            if (side == EnumFacing.EAST) {
                d0 = (double)i2 + block.getBlockBoundsMaxX() + (double)f2;
            }
            this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, 0.0, 0.0, 0.0, iblockstate).setBlockPos(pos).multiplyVelocity(0.2f).multipleParticleScaleBy(0.6f));
        }
    }

    public void moveToAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(EntityFX effect, int layerFrom, int layerTo) {
        int i2 = 0;
        while (i2 < 4) {
            if (this.fxLayers[i2][layerFrom].contains(effect)) {
                this.fxLayers[i2][layerFrom].remove(effect);
                this.fxLayers[i2][layerTo].add(effect);
            }
            ++i2;
        }
    }

    public String getStatistics() {
        int i2 = 0;
        int j2 = 0;
        while (j2 < 4) {
            int k2 = 0;
            while (k2 < 2) {
                i2 += this.fxLayers[j2][k2].size();
                ++k2;
            }
            ++j2;
        }
        return "" + i2;
    }

    public void addBlockHitEffects(BlockPos p_addBlockHitEffects_1_, MovingObjectPosition p_addBlockHitEffects_2_) {
        IBlockState iblockstate = this.worldObj.getBlockState(p_addBlockHitEffects_1_);
        if (iblockstate != null) {
            boolean flag = Reflector.callBoolean(iblockstate.getBlock(), Reflector.ForgeBlock_addHitEffects, this.worldObj, p_addBlockHitEffects_2_, this);
            if (iblockstate != null && !flag) {
                this.addBlockHitEffects(p_addBlockHitEffects_1_, p_addBlockHitEffects_2_.sideHit);
            }
        }
    }
}

