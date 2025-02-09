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

public class CosmeticBatWings
extends CosmeticBase {
    private BatWingsModel batWingsModel;
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/batwings.png");

    public CosmeticBatWings(RenderPlayer player) {
        super(player);
        this.batWingsModel = new BatWingsModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        float f2 = 0.5f;
        GlStateManager.translate(0.0, 0.125, 0.0);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.125, 0.0);
        }
        GlStateManager.enableBlend();
        this.playerRenderer.bindTexture(TEXTURE);
        this.batWingsModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 1;
    }

    public static class BatWingsModel
    extends CosmeticModelBase {
        private final ModelRenderer batRightWing;
        private final ModelRenderer batLeftWing;
        private final ModelRenderer batOuterLeftWing;
        private final ModelRenderer batOuterRightWing;

        public BatWingsModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.batRightWing = new ModelRenderer(this, 42, 0);
            this.batRightWing.addBox(-12.0f, -5.0f, 2.8f, 10, 16, 1);
            this.batLeftWing = new ModelRenderer(this, 42, 0);
            this.batLeftWing.mirror = true;
            this.batLeftWing.addBox(2.0f, -5.0f, 2.8f, 10, 16, 1);
            this.batOuterRightWing = new ModelRenderer(this, 24, 16);
            this.batOuterRightWing.addBox(-8.0f, -5.0f, 1.3f, 8, 12, 1);
            this.batOuterLeftWing = new ModelRenderer(this, 24, 16);
            this.batOuterLeftWing.mirror = true;
            this.batOuterLeftWing.addBox(0.0f, -5.0f, 1.3f, 8, 12, 1);
            this.batRightWing.addChild(this.batOuterRightWing);
            this.batLeftWing.addChild(this.batOuterLeftWing);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.batRightWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.batLeftWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.batOuterRightWing.setRotationPoint(-12.0f, 1.0f, 1.5f);
            this.batOuterLeftWing.setRotationPoint(12.0f, 1.0f, 1.5f);
            float f2 = (float)(System.currentTimeMillis() % 1000L) / 1000.0f * (float)Math.PI * 2.0f;
            this.batRightWing.rotateAngleY = (float)Math.toRadians(20.0) + (float)Math.sin(f2) * 0.4f;
            this.batLeftWing.rotateAngleY = -this.batRightWing.rotateAngleY;
            this.batOuterRightWing.rotateAngleY = this.batRightWing.rotateAngleY * 0.5f;
            this.batOuterLeftWing.rotateAngleY = -this.batRightWing.rotateAngleY * 0.5f;
            this.batRightWing.render(scale);
            this.batLeftWing.render(scale);
        }
    }
}

