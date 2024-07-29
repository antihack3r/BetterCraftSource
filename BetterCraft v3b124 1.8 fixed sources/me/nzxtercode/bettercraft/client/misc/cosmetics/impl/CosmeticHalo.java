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

public class CosmeticHalo
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/halo.png");
    private HaloModel haloModel;

    public CosmeticHalo(RenderPlayer player) {
        super(player);
        this.haloModel = new HaloModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        if (player.isSneaking()) {
            GlStateManager.translate(0.0, 0.225, 0.0);
        }
        this.playerRenderer.bindTexture(TEXTURE);
        this.haloModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GL11.glPopMatrix();
    }

    @Override
    public int getId() {
        return 14;
    }

    private class HaloModel
    extends CosmeticModelBase {
        private final ModelRenderer halo;

        public HaloModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 32;
            this.textureHeight = 32;
            this.halo = new ModelRenderer(this);
            this.halo.setRotationPoint(0.0f, 24.0f, 0.0f);
            this.halo.setTextureOffset(8, 0).addBox(-3.0f, -12.0f, -4.0f, 6, 1, 1);
            this.halo.setTextureOffset(8, 8).addBox(-3.0f, -12.0f, 3.0f, 6, 1, 1);
            this.halo.setTextureOffset(0, 7).addBox(-4.0f, -12.0f, -3.0f, 1, 1, 6);
            this.halo.setTextureOffset(0, 0).addBox(3.0f, -12.0f, -3.0f, 1, 1, 6);
        }

        @Override
        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.halo.rotateAngleX = this.playerModel.bipedHead.rotateAngleX;
            this.halo.rotateAngleY = this.playerModel.bipedHead.rotateAngleY;
            this.halo.rotationPointX = this.playerModel.bipedHead.rotationPointX;
            this.halo.rotationPointY = this.playerModel.bipedHead.rotationPointY;
            this.halo.render(scale);
        }
    }
}

