/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;

public class RenderXPOrb
extends Render<EntityXPOrb> {
    private static final ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

    public RenderXPOrb(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }

    @Override
    public void doRender(EntityXPOrb entity, double x2, double y2, double z2, float entityYaw, float partialTicks) {
        int j2;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x2, (float)y2, (float)z2);
        this.bindEntityTexture(entity);
        int i2 = entity.getTextureByXP();
        float f2 = (float)(i2 % 4 * 16 + 0) / 64.0f;
        float f1 = (float)(i2 % 4 * 16 + 16) / 64.0f;
        float f22 = (float)(i2 / 4 * 16 + 0) / 64.0f;
        float f3 = (float)(i2 / 4 * 16 + 16) / 64.0f;
        float f4 = 1.0f;
        float f5 = 0.5f;
        float f6 = 0.25f;
        int j3 = entity.getBrightnessForRender(partialTicks);
        int k2 = j3 % 65536;
        int l2 = j3 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k2 / 1.0f, (float)l2 / 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f7 = 255.0f;
        float f8 = ((float)entity.xpColor + partialTicks) / 2.0f;
        if (Config.isCustomColors()) {
            f8 = CustomColors.getXpOrbTimer(f8);
        }
        l2 = (int)((MathHelper.sin(f8 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int i1 = 255;
        int j1 = (int)((MathHelper.sin(f8 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        GlStateManager.rotate(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        float f9 = 0.3f;
        GlStateManager.scale(0.3f, 0.3f, 0.3f);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        int k1 = l2;
        int l1 = 255;
        int i22 = j1;
        if (Config.isCustomColors() && (j2 = CustomColors.getXpOrbColor(f8)) >= 0) {
            k1 = j2 >> 16 & 0xFF;
            l1 = j2 >> 8 & 0xFF;
            i22 = j2 >> 0 & 0xFF;
        }
        worldrenderer.pos(0.0f - f5, 0.0f - f6, 0.0).tex(f2, f3).color(k1, l1, i22, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(f4 - f5, 0.0f - f6, 0.0).tex(f1, f3).color(k1, l1, i22, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(f4 - f5, 1.0f - f6, 0.0).tex(f1, f22).color(k1, l1, i22, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(0.0f - f5, 1.0f - f6, 0.0).tex(f2, f22).color(k1, l1, i22, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x2, y2, z2, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityXPOrb entity) {
        return experienceOrbTextures;
    }
}

