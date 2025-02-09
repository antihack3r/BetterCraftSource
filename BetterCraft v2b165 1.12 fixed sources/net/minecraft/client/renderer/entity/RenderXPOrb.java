// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.MathHelper;
import optifine.CustomColors;
import optifine.Config;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.item.EntityXPOrb;

public class RenderXPOrb extends Render<EntityXPOrb>
{
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES;
    
    static {
        EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
    }
    
    public RenderXPOrb(final RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15f;
        this.shadowOpaque = 0.75f;
    }
    
    @Override
    public void doRender(final EntityXPOrb entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (!this.renderOutlines) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            this.bindEntityTexture(entity);
            RenderHelper.enableStandardItemLighting();
            final int i = entity.getTextureByXP();
            final float f = (i % 4 * 16 + 0) / 64.0f;
            final float f2 = (i % 4 * 16 + 16) / 64.0f;
            final float f3 = (i / 4 * 16 + 0) / 64.0f;
            final float f4 = (i / 4 * 16 + 16) / 64.0f;
            final float f5 = 1.0f;
            final float f6 = 0.5f;
            final float f7 = 0.25f;
            final int j = entity.getBrightnessForRender();
            final int k = j % 65536;
            int l = j / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)k, (float)l);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            final float f8 = 255.0f;
            float f9 = (entity.xpColor + partialTicks) / 2.0f;
            if (Config.isCustomColors()) {
                f9 = CustomColors.getXpOrbTimer(f9);
            }
            l = (int)((MathHelper.sin(f9 + 0.0f) + 1.0f) * 0.5f * 255.0f);
            final int i2 = 255;
            final int j2 = (int)((MathHelper.sin(f9 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
            GlStateManager.translate(0.0f, 0.1f, 0.0f);
            GlStateManager.rotate(180.0f - RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(((this.renderManager.options.thirdPersonView == 2) ? -1 : 1) * -this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
            final float f10 = 0.3f;
            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            int k2 = l;
            int l2 = 255;
            int i3 = j2;
            if (Config.isCustomColors()) {
                final int j3 = CustomColors.getXpOrbColor(f9);
                if (j3 >= 0) {
                    k2 = (j3 >> 16 & 0xFF);
                    l2 = (j3 >> 8 & 0xFF);
                    i3 = (j3 >> 0 & 0xFF);
                }
            }
            bufferbuilder.pos(-0.5, -0.25, 0.0).tex(f, f4).color(k2, l2, i3, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferbuilder.pos(0.5, -0.25, 0.0).tex(f2, f4).color(k2, l2, i3, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferbuilder.pos(0.5, 0.75, 0.0).tex(f2, f3).color(k2, l2, i3, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferbuilder.pos(-0.5, 0.75, 0.0).tex(f, f3).color(k2, l2, i3, 128).normal(0.0f, 1.0f, 0.0f).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(final EntityXPOrb entity) {
        return RenderXPOrb.EXPERIENCE_ORB_TEXTURES;
    }
}
