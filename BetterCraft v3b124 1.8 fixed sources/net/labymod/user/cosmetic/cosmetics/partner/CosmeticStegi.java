/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.partner;

import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticStegi
extends CosmeticRenderer<CosmeticStegiData> {
    public static final int ID = 17;
    private ModelRenderer hat;
    private ModelRenderer feather;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 56;
        int height = 20;
        ModelRenderer baseHat = new ModelRenderer(modelCosmetics).setTextureSize(56, 20).setTextureOffset(0, 0);
        baseHat.addBox(-3.5f, 1.0f, -3.5f, 8, 2, 8, modelSize);
        baseHat.setRotationPoint(-1.56f, -1.2f, 0.5f);
        baseHat.rotateAngleX = -0.18f;
        baseHat.rotateAngleY = 0.03f;
        baseHat.rotateAngleZ = -0.18f;
        baseHat.isHidden = true;
        ModelRenderer layer = new ModelRenderer(modelCosmetics).setTextureSize(56, 20).setTextureOffset(0, 10);
        layer.addBox(-4.0f, -0.0f, -4.0f, 8, 2, 8, modelSize);
        layer.setRotationPoint(-0.0f, 0.0f, -0.0f);
        layer.rotateAngleX = -0.05f;
        layer.rotateAngleY = 0.05f;
        layer.rotateAngleZ = -0.05f;
        baseHat.addChild(layer);
        ModelRenderer layer2 = new ModelRenderer(modelCosmetics).setTextureSize(56, 20).setTextureOffset(32, 0);
        layer2.addBox(-3.0f, -0.5f, -3.0f, 6, 2, 6, modelSize);
        layer2.setRotationPoint(-2.0f, 0.0f, 2.0f);
        layer2.rotateAngleX = -0.1f;
        layer2.rotateAngleY = 0.1f;
        layer2.rotateAngleZ = -0.1f;
        layer.addChild(layer2);
        ModelRenderer feather = new ModelRenderer(modelCosmetics).setTextureSize(56, 20).setTextureOffset(32, 8);
        feather.addBox(0.0f, -1.0f, -1.0f, 8, 8, 1, modelSize);
        feather.setRotationPoint(3.0f, -1.0f, -1.0f);
        feather.rotateAngleX = -1.9707963f;
        feather.rotateAngleY = 3.4415927f;
        feather.rotateAngleZ = 1.5707964f;
        this.feather = feather;
        this.hat = baseHat;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.hat.showModel = invisible;
        this.feather.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticStegiData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, -0.54f, 0.0f);
        GlStateManager.scale(1.1f, 1.1f, 1.1f);
        ModelRenderer hat = this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_STEGI, this.hat);
        hat.isHidden = false;
        hat.render(scale);
        hat.isHidden = true;
        ModelRenderer feather = this.bindTextureAndColor(Color.WHITE, ModTextures.COSMETIC_STEGI, this.feather);
        GlStateManager.disableLighting();
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        if (!canAnimate) {
            tickValue += partialTicks;
        }
        AbstractClientPlayer entitylivingbaseIn = (AbstractClientPlayer)entityIn;
        double motionX = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
        double motionY = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
        double motionZ = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
        float motionYaw = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
        double yawSin = LabyModCore.getMath().sin(motionYaw * (float)Math.PI / 180.0f);
        double yawCos = -LabyModCore.getMath().cos(motionYaw * (float)Math.PI / 180.0f);
        float rotation = (float)motionY * 10.0f;
        rotation = LabyModCore.getMath().clamp_float(rotation, -6.0f, 32.0f);
        float motionAdd = (float)(motionX * yawSin + motionZ * yawCos) * 50.0f;
        float motionSub = (float)(motionX * yawCos - motionZ * yawSin) * 50.0f;
        if (motionAdd < 0.0f) {
            motionAdd = 0.0f;
        }
        if (motionAdd >= 60.0f) {
            motionAdd = 60.0f + (motionAdd - 60.0f) * 0.2f;
        }
        if (motionSub <= -100.0f) {
            motionSub = -100.0f + (motionSub + 100.0f) * 0.2f;
        }
        float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        GlStateManager.rotate(6.0f - motionAdd / 2.0f + (rotation += LabyModCore.getMath().sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * f4), 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-motionSub / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-motionSub / 2.0f, 0.0f, 1.0f, 0.0f);
        feather.isHidden = false;
        feather.render(scale);
        feather.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 17;
    }

    @Override
    public String getCosmeticName() {
        return "Stegi";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticStegiData
    extends CosmeticData {
        private Color color = Color.RED;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
        }

        public Color getColor() {
            return this.color;
        }
    }
}

