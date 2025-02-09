/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;

public class CosmeticEnchantingGlint
implements LayerRenderer<AbstractClientPlayer> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/enchantGlint.png");
    private final RenderPlayer renderPlayer;
    private final ModelPlayer playerModel;

    public CosmeticEnchantingGlint(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
        this.playerModel = new ModelPlayer(0.2f, false);
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        this.createEnchantGlint(entitylivingbaseIn, this.renderPlayer.getMainModel(), p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }

    private void createEnchantGlint(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
        if (!(Config.isCustomItems() && !CustomItems.isUseGlint() || Config.isShaders() && Shaders.isShadowPass)) {
            float f2 = (float)entitylivingbaseIn.ticksExisted + p_177183_5_;
            this.renderPlayer.bindTexture(TEXTURE);
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintBegin();
            }
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            float f1 = 0.5f;
            GlStateManager.color(f1, f1, f1, 1.0f);
            int i2 = 0;
            while (i2 < 2) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                float f22 = 0.76f;
                GlStateManager.color(0.5f * f22, 0.25f * f22, 0.8f * f22, 1.0f);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                float f3 = 0.33333334f;
                GlStateManager.scale(f3, f3, f3);
                GlStateManager.rotate(30.0f - (float)i2 * 60.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0.0f, f2 * (0.001f + (float)i2 * 0.003f) * 20.0f, 0.0f);
                GlStateManager.matrixMode(5888);
                modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
                ++i2;
            }
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

