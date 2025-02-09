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

public class CosmeticCap
extends CosmeticBase {
    private CapModel capModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/cap.png");

    public CosmeticCap(RenderPlayer player) {
        super(player);
        this.capModel = new CapModel(player);
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
        this.capModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 4;
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

    public static class CapModel
    extends CosmeticModelBase {
        private final ModelRenderer Cap1;
        private final ModelRenderer Cap2;
        private final ModelRenderer Cap3;
        private final ModelRenderer Cap4;
        private final ModelRenderer Cap5;
        private final ModelRenderer Cap6;
        private final ModelRenderer Cap7;

        public CapModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 64;
            this.textureHeight = 32;
            this.Cap1 = new ModelRenderer(this, 0, 0);
            this.Cap1.addBox(0.0f, 0.0f, 0.0f, 8, 1, 11);
            this.Cap1.setRotationPoint(-4.0f, -9.0f, -7.0f);
            this.Cap1.setTextureSize(64, 32);
            this.Cap1.mirror = true;
            this.Cap2 = new ModelRenderer(this, 0, 0);
            this.Cap2.addBox(0.0f, 0.0f, 0.0f, 7, 1, 1);
            this.Cap2.setRotationPoint(-4.0f, -9.0f, -8.0f);
            this.Cap2.setTextureSize(64, 32);
            this.Cap2.mirror = true;
            this.Cap3 = new ModelRenderer(this, 0, 0);
            this.Cap3.addBox(0.0f, 0.0f, 0.0f, 8, 3, 1);
            this.Cap3.setRotationPoint(-4.0f, -12.0f, -4.0f);
            this.Cap3.setTextureSize(64, 32);
            this.Cap3.mirror = true;
            this.Cap4 = new ModelRenderer(this, 0, 0);
            this.Cap4.addBox(0.0f, 0.0f, 0.0f, 8, 3, 1);
            this.Cap4.setRotationPoint(-4.0f, -12.0f, 3.0f);
            this.Cap4.setTextureSize(64, 32);
            this.Cap4.mirror = true;
            this.Cap5 = new ModelRenderer(this, 0, 0);
            this.Cap5.addBox(0.0f, 0.0f, 0.0f, 1, 3, 6);
            this.Cap5.setRotationPoint(-4.0f, -12.0f, -3.0f);
            this.Cap5.setTextureSize(64, 32);
            this.Cap5.mirror = true;
            this.Cap6 = new ModelRenderer(this, 0, 0);
            this.Cap6.addBox(0.0f, 0.0f, 0.0f, 1, 3, 6);
            this.Cap6.setRotationPoint(3.0f, -12.0f, -3.0f);
            this.Cap6.setTextureSize(64, 32);
            this.Cap6.mirror = true;
            this.Cap7 = new ModelRenderer(this, 0, 0);
            this.Cap7.addBox(0.0f, 0.0f, 0.0f, 6, 1, 6);
            this.Cap7.setRotationPoint(-3.0f, -12.0f, -3.0f);
            this.Cap7.setTextureSize(64, 32);
            this.Cap7.mirror = true;
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.Cap1.render(scale);
            this.Cap2.render(scale);
            this.Cap3.render(scale);
            this.Cap4.render(scale);
            this.Cap5.render(scale);
            this.Cap6.render(scale);
            this.Cap7.render(scale);
        }
    }
}

