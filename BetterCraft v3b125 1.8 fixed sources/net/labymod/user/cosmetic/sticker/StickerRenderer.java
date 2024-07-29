/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.sticker;

import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class StickerRenderer {
    private ModelCosmetics modelCosmetics;
    private ModelRenderer bubble;
    private ModelRenderer image;

    public StickerRenderer(ModelCosmetics modelCosmetics, float modelSize) {
        this.modelCosmetics = modelCosmetics;
        this.initModel(modelSize);
    }

    public void initModel(float modelSize) {
        int bubbleWidth = 12;
        int bubbleHeight = 10;
        ModelRenderer bubble = new ModelRenderer(this.modelCosmetics);
        bubble.addBox(-6.0f, -5.0f, 0.0f, 12, 10, 1, modelSize);
        bubble.isHidden = true;
        ModelRenderer edgeTop = new ModelRenderer(this.modelCosmetics);
        edgeTop.addBox(-5.0f, -6.0f, 0.0f, 10, 1, 1, modelSize);
        bubble.addChild(edgeTop);
        ModelRenderer edgeLeft = new ModelRenderer(this.modelCosmetics);
        edgeLeft.addBox(-7.0f, -4.0f, 0.0f, 1, 8, 1, modelSize);
        bubble.addChild(edgeLeft);
        ModelRenderer edgeRight = new ModelRenderer(this.modelCosmetics);
        edgeRight.addBox(6.0f, -4.0f, 0.0f, 1, 8, 1, modelSize);
        bubble.addChild(edgeRight);
        ModelRenderer edgeBottom = new ModelRenderer(this.modelCosmetics);
        edgeBottom.addBox(-5.0f, 5.0f, 0.0f, 10, 1, 1, modelSize);
        bubble.addChild(edgeBottom);
        ModelRenderer tip = new ModelRenderer(this.modelCosmetics);
        tip.addBox(-3.0f, 6.0f, 0.0f, 3, 1, 1, modelSize);
        tip.addBox(-2.6f, 7.0f, 0.2f, 2, 1, 1, modelSize);
        tip.addBox(-1.5f, 8.0f, 0.4f, 1, 1, 1, modelSize);
        bubble.addChild(tip);
        this.bubble = bubble;
        this.image = new ModelRenderer(this.modelCosmetics).setTextureSize(22, 11).setTextureOffset(0, 0);
        this.image.addBox(-5.0f, -5.0f, 0.0f, 10, 10, 1, modelSize);
        this.image.isHidden = true;
    }

    public void render(Entity entityIn, User user, long timePassed, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX) {
        ResourceLocation location = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getStickerImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (location == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();
        double popAnimation = 1.0 / ((double)timePassed / 1000.0 * (double)1.7f) - 2.0;
        popAnimation = Math.max(-1.0, popAnimation);
        double scaleAnimation = Math.min(0.0, popAnimation) / 1.0;
        GlStateManager.translate(0.0, -1.0 / (popAnimation * 2.0) - 2.5, 0.0);
        GlStateManager.scale(-scaleAnimation, -scaleAnimation, -scaleAnimation);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        if (entityIn instanceof AbstractClientPlayer) {
            AbstractClientPlayer entity = (AbstractClientPlayer)entityIn;
            float rotation = renderManager.playerViewY - entity.rotationYawHead;
            if (entity != Minecraft.getMinecraft().getRenderViewEntity()) {
                GlStateManager.rotate(rotation + 180.0f, 0.0f, 1.0f, 0.0f);
            } else if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 1) {
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            }
            GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.VOID);
        this.bubble.isHidden = false;
        this.bubble.render(scale);
        this.bubble.isHidden = true;
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        GlStateManager.scale(0.95f, 0.95f, 0.95f);
        GlStateManager.scale(1.0f, 1.0f, 1.5f);
        GlStateManager.translate(0.0f, 0.0f, -0.01f);
        this.image.isHidden = false;
        this.image.render(scale);
        this.image.isHidden = true;
        GlStateManager.popMatrix();
    }
}

