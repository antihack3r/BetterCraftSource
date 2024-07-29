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

public class CosmeticCape
extends CosmeticBase {
    private static final ResourceLocation CAPE = new ResourceLocation("client/cosmetic/cape.png");
    CapeModel capeModel;

    public CosmeticCape(RenderPlayer player) {
        super(player);
        this.capeModel = new CapeModel(player);
    }

    @Override
    public void render(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.playerRenderer.bindTexture(CAPE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 0.125f);
        double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
        double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
        double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
        float f2 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
        double d3 = MathHelper.sin(f2 * (float)Math.PI / 180.0f);
        double d4 = -MathHelper.cos(f2 * (float)Math.PI / 180.0f);
        float f1 = (float)d1 * 10.0f;
        f1 = MathHelper.clamp_float(f1, -6.0f, 32.0f);
        float f22 = (float)(d0 * d3 + d2 * d4) * 100.0f;
        float f3 = (float)(d0 * d4 - d2 * d3) * 100.0f;
        if (f22 < 0.0f) {
            f22 = 0.0f;
        }
        if (f22 > 165.0f) {
            f22 = 165.0f;
        }
        if (f1 < -5.0f) {
            f1 = -5.0f;
        }
        float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        f1 += MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * f4;
        if (entitylivingbaseIn.isSneaking()) {
            f1 += 25.0f;
            GlStateManager.translate(0.0f, 0.142f, -0.0178f);
        }
        GlStateManager.rotate(6.0f + f22 / 2.0f + f1, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f3 / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-f3 / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        this.capeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 5;
    }

    public static class CapeModel
    extends CosmeticModelBase {
        private ModelRenderer capeModel;

        public CapeModel(RenderPlayer player) {
            super(player);
            this.capeModel = new ModelRenderer(this.playerModel, 0, 0);
            this.capeModel.setTextureSize(22, 17);
            this.capeModel.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, player.getMainModel().smallArms);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.capeModel.render(scale);
        }
    }
}

