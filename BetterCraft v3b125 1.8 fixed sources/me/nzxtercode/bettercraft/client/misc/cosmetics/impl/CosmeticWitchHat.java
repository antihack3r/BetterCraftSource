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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class CosmeticWitchHat
extends CosmeticBase {
    public final WitchHatModel witchHatModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/witchhat.png");

    public CosmeticWitchHat(RenderPlayer player) {
        super(player);
        this.witchHatModel = new WitchHatModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        float f2 = this.getFirstRotationX(player, partialTicks);
        float f1 = this.getSecondRotationX(player, partialTicks);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        if (player.isSneaking()) {
            GlStateManager.translate(0.0f, 0.27f, 0.0f);
        }
        GlStateManager.rotate(f2, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1, 1.0f, 0.0f, 0.0f);
        this.witchHatModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    private float getFirstRotationX(AbstractClientPlayer Player2, float partialTicks) {
        float f2 = this.interpolateRotation(Player2.prevRenderYawOffset, Player2.renderYawOffset, partialTicks);
        float f1 = this.interpolateRotation(Player2.prevRotationYawHead, Player2.rotationYawHead, partialTicks);
        float f22 = f1 - f2;
        if (Player2.isRiding() && Player2.ridingEntity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)Player2.ridingEntity;
            f2 = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
            f22 = f1 - f2;
            float f3 = MathHelper.wrapAngleTo180_float(f22);
            if (f3 < -85.0f) {
                f3 = -85.0f;
            }
            if (f3 >= 85.0f) {
                f3 = 85.0f;
            }
            f2 = f1 - f3;
            if (f3 * f3 > 2500.0f) {
                float f4 = f2 + f3 * 0.2f;
            }
        }
        return f22;
    }

    private float getSecondRotationX(AbstractClientPlayer Player2, float partialTicks) {
        return Player2.prevRotationPitch + (Player2.rotationPitch - Player2.prevRotationPitch) * partialTicks;
    }

    private float interpolateRotation(float par1, float par2, float par3) {
        float f2 = par2 - par1;
        while (f2 < -180.0f) {
            f2 += 360.0f;
        }
        while (f2 >= 180.0f) {
            f2 -= 360.0f;
        }
        return par1 + par3 * f2;
    }

    @Override
    public int getId() {
        return 25;
    }

    public static class WitchHatModel
    extends CosmeticModelBase {
        private final ModelRenderer witchHat = new ModelRenderer(this).setTextureSize(64, 128);
        int textureWidth = 64;
        int textureHeight = 128;

        public WitchHatModel(RenderPlayer player) {
            super(player);
            this.witchHat.setRotationPoint(-5.0f, -10.03125f, -5.0f);
            this.witchHat.setTextureOffset(0, 64).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
            ModelRenderer modelrenderer = new ModelRenderer(this).setTextureSize(64, 128);
            modelrenderer.setRotationPoint(1.75f, -4.0f, 2.0f);
            modelrenderer.setTextureOffset(0, 76).addBox(0.0f, 0.0f, 0.0f, 7, 4, 7);
            modelrenderer.rotateAngleX = -0.05235988f;
            modelrenderer.rotateAngleZ = 0.02617994f;
            this.witchHat.addChild(modelrenderer);
            ModelRenderer modelrenderer1 = new ModelRenderer(this).setTextureSize(64, 128);
            modelrenderer1.setRotationPoint(1.75f, -4.0f, 2.0f);
            modelrenderer1.setTextureOffset(0, 87).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
            modelrenderer1.rotateAngleX = -0.10471976f;
            modelrenderer1.rotateAngleZ = 0.05235988f;
            modelrenderer.addChild(modelrenderer1);
            ModelRenderer modelrenderer2 = new ModelRenderer(this).setTextureSize(64, 128);
            modelrenderer2.setRotationPoint(1.75f, -2.0f, 2.0f);
            modelrenderer2.setTextureOffset(0, 95).addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.25f);
            modelrenderer2.rotateAngleX = -0.20943952f;
            modelrenderer2.rotateAngleZ = 0.10471976f;
            modelrenderer1.addChild(modelrenderer2);
            this.witchHat.isHidden = true;
            this.playerModel.bipedHead.addChild(this.witchHat);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.witchHat.isHidden = false;
            this.witchHat.render(scale);
            this.witchHat.isHidden = true;
        }
    }
}

