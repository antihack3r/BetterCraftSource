/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticBlaze
extends CosmeticBase {
    BlazeModel blazeModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/blaze.png");

    public CosmeticBlaze(RenderPlayer player) {
        super(player);
        this.blazeModel = new BlazeModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        this.playerRenderer.bindTexture(TEXTURE);
        this.blazeModel.setModelAttributes(this.playerRenderer.getMainModel());
        float[] color = new float[]{1.0f, 1.0f, 0.9f};
        GL11.glColor3f(color[0], color[1], color[2]);
        this.blazeModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 3;
    }

    private static class BlazeModel
    extends CosmeticModelBase {
        private ModelRenderer[] blazeSticks = new ModelRenderer[12];

        public BlazeModel(RenderPlayer player) {
            super(player);
            int i2 = 0;
            while (i2 < this.blazeSticks.length) {
                this.blazeSticks[i2] = new ModelRenderer(this.playerModel, 0, 16);
                this.blazeSticks[i2].addBox(0.0f, 0.0f, 0.0f, 2, 8, 2);
                ++i2;
            }
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
            ModelRenderer[] modelRendererArray = this.blazeSticks;
            int n2 = this.blazeSticks.length;
            int n3 = 0;
            while (n3 < n2) {
                ModelRenderer blazeStick = modelRendererArray[n3];
                blazeStick.render(scale);
                ++n3;
            }
        }

        @Override
        public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity entityIn) {
            float f2 = p_78087_3_ * (float)Math.PI * -0.1f;
            int i2 = 0;
            while (i2 < 4) {
                this.blazeSticks[i2].rotationPointY = -2.0f + MathHelper.cos(((float)(i2 * 2) + p_78087_3_) * 0.25f);
                this.blazeSticks[i2].rotationPointX = MathHelper.cos(f2) * 9.0f;
                this.blazeSticks[i2].rotationPointZ = MathHelper.sin(f2) * 9.0f;
                f2 += 1.0f;
                ++i2;
            }
            f2 = 0.7853982f + p_78087_3_ * (float)Math.PI * 0.03f;
            int j2 = 4;
            while (j2 < 8) {
                this.blazeSticks[j2].rotationPointY = 2.0f + MathHelper.cos(((float)(j2 * 2) + p_78087_3_) * 0.25f);
                this.blazeSticks[j2].rotationPointX = MathHelper.cos(f2) * 7.0f;
                this.blazeSticks[j2].rotationPointZ = MathHelper.sin(f2) * 7.0f;
                f2 += 1.0f;
                ++j2;
            }
            f2 = 0.47123894f + p_78087_3_ * (float)Math.PI * -0.05f;
            int k2 = 8;
            while (k2 < 12) {
                this.blazeSticks[k2].rotationPointY = 11.0f + MathHelper.cos(((float)k2 * 1.5f + p_78087_3_) * 0.5f);
                this.blazeSticks[k2].rotationPointX = MathHelper.cos(f2) * 5.0f;
                this.blazeSticks[k2].rotationPointZ = MathHelper.sin(f2) * 5.0f;
                f2 += 1.0f;
                ++k2;
            }
        }
    }
}

