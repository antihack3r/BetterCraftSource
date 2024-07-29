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

public class CosmeticVexWings
extends CosmeticBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetic/vex.png");
    BatWingsModel batWingsModel;

    public CosmeticVexWings(RenderPlayer player) {
        super(player);
        this.batWingsModel = new BatWingsModel(player);
    }

    @Override
    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, 0.125, 0.0);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.125, 0.0);
        }
        this.playerRenderer.bindTexture(TEXTURE);
        this.batWingsModel.render(player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
        GlStateManager.popMatrix();
    }

    @Override
    public int getId() {
        return 23;
    }

    public static class BatWingsModel
    extends CosmeticModelBase {
        private final ModelRenderer leftWing;
        private final ModelRenderer rightWing;

        public BatWingsModel(RenderPlayer player) {
            super(player);
            this.textureWidth = 64;
            this.textureHeight = 64;
            this.rightWing = new ModelRenderer(this, 0, 32);
            this.rightWing.addBox(-20.0f, 0.0f, 0.0f, 20, 12, 1);
            this.leftWing = new ModelRenderer(this, 0, 32);
            this.leftWing.mirror = true;
            this.leftWing.addBox(0.0f, 0.0f, 0.0f, 20, 12, 1);
        }

        @Override
        public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
            this.rightWing.rotationPointZ = 2.0f;
            this.leftWing.rotationPointZ = 2.0f;
            this.rightWing.rotationPointY = 1.0f;
            this.leftWing.rotationPointY = 1.0f;
            this.rightWing.rotateAngleY = 0.47123894f + MathHelper.cos(p_78088_4_ * 0.3f) * (float)Math.PI * 0.05f;
            this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -0.47123894f;
            this.leftWing.rotateAngleX = 0.47123894f;
            this.rightWing.rotateAngleX = 0.47123894f;
            this.rightWing.rotateAngleZ = 0.47123894f;
            this.leftWing.render(scale);
            this.rightWing.render(scale);
        }
    }
}

