/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics.impl;

import java.awt.Color;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticBase;
import me.nzxtercode.bettercraft.client.misc.cosmetics.CosmeticModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticCrystalWings
extends CosmeticBase {
    private CrytsalWingsModel crytsalWingsModel;

    public CosmeticCrystalWings(RenderPlayer playerRenderer) {
        super(playerRenderer);
        this.crytsalWingsModel = new CrytsalWingsModel(playerRenderer);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.crytsalWingsModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
    }

    @Override
    public int getId() {
        return 7;
    }

    class CrytsalWingsModel
    extends CosmeticModelBase {
        private ModelRenderer model;
        ResourceLocation resourceLocation;

        public CrytsalWingsModel(RenderPlayer player) {
            super(player);
            this.resourceLocation = new ResourceLocation("client/cosmetic/crystalwings.png");
            int i2 = 30;
            int j2 = 24;
            this.model = new ModelRenderer(this).setTextureSize(i2, j2).setTextureOffset(0, 8);
            this.model.setRotationPoint(-0.0f, 1.0f, 0.0f);
            this.model.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
            this.model.isHidden = true;
            ModelRenderer modelrenderer = new ModelRenderer(this).setTextureSize(i2, j2).setTextureOffset(0, 16);
            modelrenderer.setRotationPoint(-0.0f, 0.0f, 0.2f);
            modelrenderer.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
            this.model.addChild(modelrenderer);
            ModelRenderer modelrenderer1 = new ModelRenderer(this).setTextureSize(i2, j2).setTextureOffset(0, 0);
            modelrenderer1.setRotationPoint(-0.0f, 0.0f, 0.2f);
            modelrenderer1.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
            modelrenderer.addChild(modelrenderer1);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float walkingSpeed, float tickValue, float p_78088_5_, float p_78088_6_, float scale) {
            float f2 = (float)Math.cos(tickValue / 10.0f) / 20.0f - 0.03f - walkingSpeed / 20.0f;
            ModelRenderer modelrenderer = this.model.childModels.get(0);
            ModelRenderer modelrenderer1 = modelrenderer.childModels.get(0);
            this.model.rotateAngleZ = f2 * 3.0f;
            modelrenderer.rotateAngleZ = f2 / 2.0f;
            modelrenderer1.rotateAngleZ = f2 / 2.0f;
            this.model.rotateAngleY = -0.3f - walkingSpeed / 3.0f;
            this.model.rotateAngleX = 0.3f;
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.6, 1.6, 1.0);
            GlStateManager.translate(0.0, (double)0.05f, (double)0.05f);
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0, (double)0.08f, (double)0.03f);
                GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
                this.model.rotateAngleZ = 0.8f;
                modelrenderer.rotateAngleZ = 0.0f;
                modelrenderer1.rotateAngleZ = 0.0f;
            } else {
                RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                if (rendermanager != null) {
                    GlStateManager.rotate(rendermanager.playerViewX / 3.0f, 1.0f, 0.0f, 0.0f);
                }
            }
            Color color = Color.WHITE;
            this.model.isHidden = false;
            int i2 = -1;
            while (i2 <= 1) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.3f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569f);
                GlStateManager.disableLighting();
                Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);
                if (i2 == 1) {
                    GlStateManager.scale(-1.0f, 1.0f, 1.0f);
                }
                GlStateManager.translate(0.05, 0.0, 0.0);
                GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5f);
                this.model.render(scale);
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
                i2 += 2;
            }
            this.model.isHidden = true;
            GlStateManager.popMatrix();
        }
    }
}

