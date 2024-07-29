/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class CosmeticCreeperLightning
implements LayerRenderer<AbstractClientPlayer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/creeper.png");
    private final RenderPlayer renderPlayer;
    private ModelPlayer playerModel;

    public CosmeticCreeperLightning(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
        this.playerModel = renderPlayer.getMainModel();
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        boolean flag = entitylivingbaseIn.isInvisible();
        GlStateManager.depthMask(!flag);
        this.playerModel = this.renderPlayer.getMainModel();
        this.renderPlayer.bindTexture(TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f2 = (float)entitylivingbaseIn.ticksExisted + partialTicks;
        float velocity = 0.004f;
        GlStateManager.translate(f2 * -velocity, f2 * velocity, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f1 = 0.5f;
        GlStateManager.color(f1, f1, f1, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(1, 1);
        this.playerModel.setModelAttributes(this.renderPlayer.getMainModel());
        this.playerModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

