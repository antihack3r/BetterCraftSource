/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class CosmeticWitherArmor
implements LayerRenderer<AbstractClientPlayer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/wither.png");
    private final RenderPlayer renderPlayer;
    private final ModelPlayer playerModel;

    public CosmeticWitherArmor(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
        this.playerModel = renderPlayer.getMainModel();
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
        this.renderPlayer.bindTexture(TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f2 = (float)entitylivingbaseIn.ticksExisted + partialTicks;
        float f1 = MathHelper.cos(f2 * 0.004f) * 3.0f;
        float f22 = f2 * 0.004f;
        GlStateManager.translate(f1, f22, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f3 = 0.5f;
        GlStateManager.color(f3, f3, f3, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(1, 1);
        this.playerModel.setLivingAnimations(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks);
        this.playerModel.setModelAttributes(this.renderPlayer.getMainModel());
        this.playerModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

