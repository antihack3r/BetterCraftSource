/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelSprite {
    private ModelRenderer modelRenderer = null;
    private int textureOffsetX = 0;
    private int textureOffsetY = 0;
    private float posX = 0.0f;
    private float posY = 0.0f;
    private float posZ = 0.0f;
    private int sizeX = 0;
    private int sizeY = 0;
    private int sizeZ = 0;
    private float sizeAdd = 0.0f;
    private float minU = 0.0f;
    private float minV = 0.0f;
    private float maxU = 0.0f;
    private float maxV = 0.0f;

    public ModelSprite(ModelRenderer modelRenderer, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int sizeX, int sizeY, int sizeZ, float sizeAdd) {
        this.modelRenderer = modelRenderer;
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.sizeAdd = sizeAdd;
        this.minU = (float)textureOffsetX / modelRenderer.textureWidth;
        this.minV = (float)textureOffsetY / modelRenderer.textureHeight;
        this.maxU = (float)(textureOffsetX + sizeX) / modelRenderer.textureWidth;
        this.maxV = (float)(textureOffsetY + sizeY) / modelRenderer.textureHeight;
    }

    public void render(Tessellator tessellator, float scale) {
        GlStateManager.translate(this.posX * scale, this.posY * scale, this.posZ * scale);
        float f2 = this.minU;
        float f1 = this.maxU;
        float f22 = this.minV;
        float f3 = this.maxV;
        if (this.modelRenderer.mirror) {
            f2 = this.maxU;
            f1 = this.minU;
        }
        if (this.modelRenderer.mirrorV) {
            f22 = this.maxV;
            f3 = this.minV;
        }
        ModelSprite.renderItemIn2D(tessellator, f2, f22, f1, f3, this.sizeX, this.sizeY, scale * (float)this.sizeZ, this.modelRenderer.textureWidth, this.modelRenderer.textureHeight);
        GlStateManager.translate(-this.posX * scale, -this.posY * scale, -this.posZ * scale);
    }

    public static void renderItemIn2D(Tessellator tess, float minU, float minV, float maxU, float maxV, int sizeX, int sizeY, float width, float texWidth, float texHeight) {
        if (width < 6.25E-4f) {
            width = 6.25E-4f;
        }
        float f2 = maxU - minU;
        float f1 = maxV - minV;
        double d0 = MathHelper.abs(f2) * (texWidth / 16.0f);
        double d1 = MathHelper.abs(f1) * (texHeight / 16.0f);
        WorldRenderer worldrenderer = tess.getWorldRenderer();
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, d1, 0.0).tex(minU, maxV).endVertex();
        worldrenderer.pos(d0, d1, 0.0).tex(maxU, maxV).endVertex();
        worldrenderer.pos(d0, 0.0, 0.0).tex(maxU, minV).endVertex();
        worldrenderer.pos(0.0, 0.0, 0.0).tex(minU, minV).endVertex();
        tess.draw();
        GL11.glNormal3f(0.0f, 0.0f, 1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0, 0.0, width).tex(minU, minV).endVertex();
        worldrenderer.pos(d0, 0.0, width).tex(maxU, minV).endVertex();
        worldrenderer.pos(d0, d1, width).tex(maxU, maxV).endVertex();
        worldrenderer.pos(0.0, d1, width).tex(minU, maxV).endVertex();
        tess.draw();
        float f22 = 0.5f * f2 / (float)sizeX;
        float f3 = 0.5f * f1 / (float)sizeY;
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        int i2 = 0;
        while (i2 < sizeX) {
            float f4 = (float)i2 / (float)sizeX;
            float f5 = minU + f2 * f4 + f22;
            worldrenderer.pos((double)f4 * d0, d1, width).tex(f5, maxV).endVertex();
            worldrenderer.pos((double)f4 * d0, d1, 0.0).tex(f5, maxV).endVertex();
            worldrenderer.pos((double)f4 * d0, 0.0, 0.0).tex(f5, minV).endVertex();
            worldrenderer.pos((double)f4 * d0, 0.0, width).tex(f5, minV).endVertex();
            ++i2;
        }
        tess.draw();
        GL11.glNormal3f(1.0f, 0.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        int j2 = 0;
        while (j2 < sizeX) {
            float f7 = (float)j2 / (float)sizeX;
            float f10 = minU + f2 * f7 + f22;
            float f6 = f7 + 1.0f / (float)sizeX;
            worldrenderer.pos((double)f6 * d0, 0.0, width).tex(f10, minV).endVertex();
            worldrenderer.pos((double)f6 * d0, 0.0, 0.0).tex(f10, minV).endVertex();
            worldrenderer.pos((double)f6 * d0, d1, 0.0).tex(f10, maxV).endVertex();
            worldrenderer.pos((double)f6 * d0, d1, width).tex(f10, maxV).endVertex();
            ++j2;
        }
        tess.draw();
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        int k2 = 0;
        while (k2 < sizeY) {
            float f8 = (float)k2 / (float)sizeY;
            float f11 = minV + f1 * f8 + f3;
            float f13 = f8 + 1.0f / (float)sizeY;
            worldrenderer.pos(0.0, (double)f13 * d1, width).tex(minU, f11).endVertex();
            worldrenderer.pos(d0, (double)f13 * d1, width).tex(maxU, f11).endVertex();
            worldrenderer.pos(d0, (double)f13 * d1, 0.0).tex(maxU, f11).endVertex();
            worldrenderer.pos(0.0, (double)f13 * d1, 0.0).tex(minU, f11).endVertex();
            ++k2;
        }
        tess.draw();
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        int l2 = 0;
        while (l2 < sizeY) {
            float f9 = (float)l2 / (float)sizeY;
            float f12 = minV + f1 * f9 + f3;
            worldrenderer.pos(d0, (double)f9 * d1, width).tex(maxU, f12).endVertex();
            worldrenderer.pos(0.0, (double)f9 * d1, width).tex(minU, f12).endVertex();
            worldrenderer.pos(0.0, (double)f9 * d1, 0.0).tex(minU, f12).endVertex();
            worldrenderer.pos(d0, (double)f9 * d1, 0.0).tex(maxU, f12).endVertex();
            ++l2;
        }
        tess.draw();
    }
}

