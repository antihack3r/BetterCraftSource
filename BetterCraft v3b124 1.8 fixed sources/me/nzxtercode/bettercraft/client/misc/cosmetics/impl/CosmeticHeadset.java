/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticHeadset
extends CosmeticBase {
    HeadsetModel headsetModel;

    public CosmeticHeadset(RenderPlayer playerRenderer) {
        super(playerRenderer);
        this.headsetModel = new HeadsetModel(playerRenderer);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.headsetModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public int getId() {
        return 15;
    }

    class HeadsetModel
    extends CosmeticModelBase {
        private ModelRenderer earCup;
        private ModelRenderer headBandSide;
        private ModelRenderer headBandTop;
        private ModelRenderer mic;
        private ResourceLocation resourceLocation;
        private boolean modelSize;

        public HeadsetModel(RenderPlayer player) {
            super(player);
            this.resourceLocation = new ResourceLocation("client/cosmetic/headset.png");
            this.modelSize = true;
            int i2 = 18;
            int j2 = 7;
            this.earCup = new ModelRenderer(this, 0, 0).setTextureSize(i2, j2).setTextureOffset(0, 0);
            this.earCup.addBox(-1.5f, -1.5f, 0.0f, 3, 3, 1, this.modelSize);
            this.earCup.isHidden = true;
            this.headBandSide = new ModelRenderer(this, 0, 0).setTextureSize(i2, j2).setTextureOffset(8, 0);
            this.headBandSide.addBox(-0.5f, -4.0f, 0.0f, 1, 3, 1, this.modelSize);
            this.headBandSide.isHidden = true;
            this.headBandTop = new ModelRenderer(this, 0, 0).setTextureSize(i2, j2).setTextureOffset(0, 5);
            this.headBandTop.addBox(-4.0f, 0.0f, -2.0f, 8, 1, 1, this.modelSize);
            this.headBandTop.isHidden = true;
            this.mic = new ModelRenderer(this, 0, 0).setTextureSize(i2, j2).setTextureOffset(12, 0);
            this.mic.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 1, this.modelSize);
            this.mic.isHidden = true;
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableBlend();
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.2f, 1.2f, 1.2f);
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0, 0.22, 0.0);
            }
            GlStateManager.rotate(p_78088_5_, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(p_78088_6_, 1.0f, 0.0f, 0.0f);
            GlStateManager.translate(0.0, -0.3, 0.1);
            this.earCup.isHidden = false;
            this.headBandSide.isHidden = false;
            this.headBandTop.isHidden = false;
            this.mic.isHidden = false;
            double d0 = 0.21;
            double d1 = 0.1;
            double d2 = 0.6;
            double d3 = -0.0317;
            int i2 = -1;
            while (i2 < 2) {
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                if (i2 == 1) {
                    GlStateManager.scale(1.0f, 1.0f, -1.0f);
                }
                GlStateManager.translate(0.1, 0.1, d0);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0, d3, 0.0);
                this.headBandSide.render(scale);
                if (i2 == -1) {
                    GlStateManager.translate(0.028, -d3 + 0.05, 0.0);
                    GlStateManager.scale(0.8, 0.8, 0.8);
                    GlStateManager.rotate(120.0f, 0.0f, 0.0f, 1.0f);
                    this.headBandSide.render(scale);
                    GlStateManager.scale(0.65, 0.65, 0.65);
                    GlStateManager.translate(0.01, -0.37, 0.08);
                    GlStateManager.rotate(-30.0f, 0.0f, 0.0f, 1.0f);
                    GlStateManager.rotate(-60.0f, -1.0f, 0.0f, 0.0f);
                    this.mic.render(scale);
                }
                GlStateManager.popMatrix();
                this.earCup.render(scale);
                GlStateManager.scale(d2, d2, d2);
                GlStateManager.translate(0.0, 0.0, d1);
                GlStateManager.rotate(45.0f, 0.0f, 0.0f, 1.0f);
                this.earCup.render(scale);
                GlStateManager.popMatrix();
                i2 += 2;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0, -0.1817, -0.0063);
            GlStateManager.scale(0.83999, 1.0, 1.0);
            this.headBandTop.render(scale);
            GlStateManager.popMatrix();
            this.earCup.isHidden = true;
            this.headBandSide.isHidden = true;
            this.headBandTop.isHidden = true;
            this.mic.isHidden = true;
            GlStateManager.popMatrix();
        }
    }
}

