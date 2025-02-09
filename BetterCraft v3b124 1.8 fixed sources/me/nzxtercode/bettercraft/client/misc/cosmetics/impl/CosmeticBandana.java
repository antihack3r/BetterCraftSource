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

public class CosmeticBandana
extends CosmeticBase {
    private BandanaModel bandanaModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/bandana.png");

    public CosmeticBandana(RenderPlayer player) {
        super(player);
        this.bandanaModel = new BandanaModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        float f2 = this.getFirstRotationX(player, partialTicks);
        float f1 = this.getSecondRotationX(player, partialTicks);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.rotate(f2, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1, 1.0f, 0.0f, 0.0f);
        if (player.isSneaking()) {
            GlStateManager.translate(0.0, 0.27, 0.0);
        }
        this.bandanaModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
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
        return 0;
    }

    public static class BandanaModel
    extends CosmeticModelBase {
        private final ModelRenderer Bandana1;
        private final ModelRenderer Bandana2;
        private final ModelRenderer Bandana3;
        private final ModelRenderer Bandana4;

        public BandanaModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.Bandana1 = new ModelRenderer(this, 0, 0);
            this.Bandana1.addBox(0.0f, 0.0f, 0.0f, 8, 2, 1);
            this.Bandana1.setRotationPoint(-4.0f, -7.0f, -5.0f);
            this.Bandana1.setTextureSize(64, 32);
            this.Bandana1.mirror = true;
            this.Bandana2 = new ModelRenderer(this, 0, 0);
            this.Bandana2.addBox(0.0f, 0.0f, 0.0f, 1, 2, 10);
            this.Bandana2.setRotationPoint(4.0f, -7.0f, -5.0f);
            this.Bandana2.setTextureSize(64, 32);
            this.Bandana2.mirror = true;
            this.Bandana3 = new ModelRenderer(this, 0, 0);
            this.Bandana3.addBox(0.0f, 0.0f, 0.0f, 8, 2, 1);
            this.Bandana3.setRotationPoint(-4.0f, -7.0f, 4.0f);
            this.Bandana3.setTextureSize(64, 32);
            this.Bandana3.mirror = true;
            this.Bandana4 = new ModelRenderer(this, 0, 0);
            this.Bandana4.addBox(0.0f, 0.0f, 0.0f, 1, 2, 10);
            this.Bandana4.setRotationPoint(-5.0f, -7.0f, -5.0f);
            this.Bandana4.setTextureSize(64, 32);
            this.Bandana4.mirror = true;
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.Bandana1.render(scale);
            this.Bandana2.render(scale);
            this.Bandana3.render(scale);
            this.Bandana4.render(scale);
        }
    }
}

