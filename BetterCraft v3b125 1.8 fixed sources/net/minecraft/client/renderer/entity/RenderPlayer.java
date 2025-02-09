/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import java.util.Arrays;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticInstance;
import net.labymod.core.CoreAdapter;
import net.labymod.core.LabyModCore;
import net.labymod.main.Source;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer
extends RendererLivingEntity<AbstractClientPlayer> {
    private boolean smallArms;

    public RenderPlayer(RenderManager renderManager) {
        this(renderManager, false);
    }

    public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, new ModelPlayer(0.0f, useSmallArms), 0.5f);
        this.mainModel = new ModelCosmetics(0.0f, useSmallArms);
        String version = "mc" + Source.getMajorVersion();
        String coreImplementationPackage = "net.labymod.core_implementation." + version;
        try {
            LabyModCore.setCoreAdapter((CoreAdapter)Class.forName(String.valueOf(coreImplementationPackage) + ".CoreImplementation").newInstance());
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Arrays.asList(LabyModCore.getRenderPlayerImplementation().getLayerRenderers(this)).forEach(this::addLayer);
        CosmeticInstance.registerLayers(this, this::addLayer);
    }

    @Override
    public ModelPlayer getMainModel() {
        return (ModelPlayer)super.getMainModel();
    }

    @Override
    public void doRender(AbstractClientPlayer entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        if (!entity.isUser() || this.renderManager.livingPlayer == entity) {
            double d0 = y2;
            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
                d0 = y2 - 0.125;
            }
            this.setModelVisibilities(entity);
            super.doRender(entity, x2, d0, z2, entityYaw, partialTicks);
        }
    }

    private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelPlayer modelplayer = this.getMainModel();
        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();
            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;
                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();
                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        return entity.getLocationSkin();
    }

    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
        float f2 = 0.9375f;
        GlStateManager.scale(f2, f2, f2);
    }

    @Override
    protected void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x2, double y2, double z2, String str, float p_177069_9_, double p_177069_10_) {
        Scoreboard scoreboard;
        ScoreObjective scoreobjective;
        if (p_177069_10_ < 100.0 && (scoreobjective = (scoreboard = entityIn.getWorldScoreboard()).getObjectiveInDisplaySlot(2)) != null) {
            Score score = scoreboard.getValueFromObjective(entityIn.getName(), scoreobjective);
            this.renderLivingLabel(entityIn, String.valueOf(score.getScorePoints()) + " " + scoreobjective.getDisplayName(), x2, y2, z2, 64);
            y2 += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15f * p_177069_9_);
        }
        super.renderOffsetLivingLabel(entityIn, x2, y2, z2, str, p_177069_9_, p_177069_10_);
    }

    public void renderRightArm(AbstractClientPlayer clientPlayer) {
        float f2 = 1.0f;
        GlStateManager.color(f2, f2, f2);
        ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.swingProgress = 0.0f;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, clientPlayer);
        modelplayer.renderRightArm();
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer) {
        float f2 = 1.0f;
        GlStateManager.color(f2, f2, f2);
        ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0f;
        modelplayer.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, clientPlayer);
        modelplayer.renderLeftArm();
    }

    @Override
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x2, double y2, double z2) {
        if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
            super.renderLivingAt(entityLivingBaseIn, x2 + (double)entityLivingBaseIn.renderOffsetX, y2 + (double)entityLivingBaseIn.renderOffsetY, z2 + (double)entityLivingBaseIn.renderOffsetZ);
        } else {
            super.renderLivingAt(entityLivingBaseIn, x2, y2, z2);
        }
    }

    @Override
    protected void rotateCorpse(AbstractClientPlayer bat2, float p_77043_2_, float p_77043_3_, float partialTicks) {
        if (bat2.isEntityAlive() && bat2.isPlayerSleeping()) {
            GlStateManager.rotate(bat2.getBedOrientationInDegrees(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(this.getDeathMaxRotation(bat2), 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(270.0f, 0.0f, 1.0f, 0.0f);
        } else {
            super.rotateCorpse(bat2, p_77043_2_, p_77043_3_, partialTicks);
        }
    }
}

