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
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CosmeticCrownKing
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/crownking.png");
    private CrownModel crownModel;

    public CosmeticCrownKing(RenderPlayer player) {
        super(player);
        this.crownModel = new CrownModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        if (player.isSneaking()) {
            GlStateManager.translate(0.0, 0.225, 0.0);
        }
        this.playerRenderer.bindTexture(TEXTURE);
        this.crownModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GL11.glPopMatrix();
    }

    @Override
    public int getId() {
        return 6;
    }

    private class CrownModel
    extends CosmeticModelBase {
        private final ModelRenderer crown;

        public CrownModel(RenderPlayer player) {
            super(player);
            float f2 = 5.5f;
            this.crown = new ModelRenderer(this);
            this.crown.setTextureSize(16, 16);
            this.crown.setRotationPoint(0.0f, 24.0f, 0.0f);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -2.0f - f2, -5.0f, 10, 2, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -2.0f - f2, -4.0f, 1, 2, 9);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -2.0f - f2, -4.0f, 1, 2, 8);
            this.crown.setTextureOffset(0, 2).addBox(3.0f, -3.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.2f, -3.0f - f2, -5.2f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-4.0f, -2.0f - f2, 4.0f, 9, 2, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -3.0f - f2, -4.2f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -4.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(0.0f, -3.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-0.5f, -3.0f - f2, -5.5f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-1.0f, -3.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-0.5f, -4.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-4.0f, -3.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.2f, -3.0f - f2, -5.2f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -4.0f - f2, -5.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -3.0f - f2, -4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -3.0f - f2, -1.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.5f, -3.0f - f2, -0.5f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -3.0f - f2, 0.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -4.0f - f2, -0.5f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -3.0f - f2, 3.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.2f, -3.0f - f2, 4.2f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-4.0f, -3.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-5.0f, -4.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-1.0f, -3.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-0.5f, -3.0f - f2, 4.5f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(0.0f, -3.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(-0.5f, -4.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(3.0f, -3.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.2f, -3.0f - f2, 4.2f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -3.0f - f2, 3.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -4.0f - f2, 4.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -3.0f - f2, 0.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -3.0f - f2, -1.0f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.5f, -3.0f - f2, -0.5f, 1, 1, 1);
            this.crown.setTextureOffset(0, 2).addBox(4.0f, -4.0f - f2, -0.5f, 1, 1, 1);
        }

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.crown.rotateAngleX = this.playerModel.bipedHead.rotateAngleX;
            this.crown.rotateAngleY = this.playerModel.bipedHead.rotateAngleY;
            this.crown.rotationPointX = this.playerModel.bipedHead.rotationPointX;
            this.crown.rotationPointY = this.playerModel.bipedHead.rotationPointY;
            this.crown.render(scale);
        }
    }
}

