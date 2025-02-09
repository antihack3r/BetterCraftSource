/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

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

public class CosmeticDog
extends CosmeticRenderer<CosmeticDogData> {
    public static final int ID = 26;
    private ModelRenderer dogEar;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 8;
        int height = 8;
        this.dogEar = new ModelRenderer(modelCosmetics).setTextureSize(8, 8).setTextureOffset(0, 0);
        this.dogEar.addBox(0.0f, -3.0f, 0.0f, 3, 3, 1, modelSize);
        this.dogEar.rotateAngleX = 0.3f;
        this.dogEar.rotateAngleY = -0.2f;
        this.dogEar.rotateAngleZ = 0.3f;
        this.dogEar.isHidden = true;
        ModelRenderer dogEarEnd = new ModelRenderer(modelCosmetics).setTextureSize(8, 8).setTextureOffset(0, 4);
        dogEarEnd.addBox(0.0f, -3.0f, 0.0f, 3, 3, 1, modelSize);
        dogEarEnd.setRotationPoint(-0.1f, -2.3f, -0.2f);
        dogEarEnd.rotateAngleX = 1.7f;
        dogEarEnd.rotateAngleY = -0.2f;
        dogEarEnd.rotateAngleZ = -0.2f;
        this.dogEar.addChild(dogEarEnd);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.dogEar.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticDogData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0, -0.5, -0.1);
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        if (!canAnimate) {
            tickValue += partialTicks;
            partialTicks = 0.0f;
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
        float motionAdd = (float)(motionX * yawSin + motionZ * yawCos) * 100.0f;
        float motionSub = (float)(motionX * yawCos - motionZ * yawSin) * 100.0f;
        if (motionAdd < 0.0f) {
            motionAdd = 0.0f;
        }
        if (motionAdd >= 130.0f) {
            motionAdd = 130.0f + (motionAdd - 180.0f) * 0.2f;
        }
        float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        rotation += LabyModCore.getMath().sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * f4;
        ModelRenderer targetModel = this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_DOG, this.dogEar);
        targetModel.isHidden = false;
        double distanceToMid = 0.1;
        int i2 = -1;
        while (i2 < 2) {
            GlStateManager.pushMatrix();
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(0.1, 0.0, 0.0);
            GlStateManager.rotate(6.0f - motionAdd / 2.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate((-motionSub / 2.0f * (float)(-i2) - rotation) / 2.0f, 0.0f, 0.0f, 1.0f);
            if (entityIn.isSneaking()) {
                GlStateManager.rotate(30 * (i2 + 1), 0.0f, -1.0f, 1.0f);
            }
            targetModel.render(scale);
            GlStateManager.popMatrix();
            i2 += 2;
        }
        targetModel.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 26;
    }

    @Override
    public String getCosmeticName() {
        return "Dog Ears";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticDogData
    extends CosmeticData {
        private Color color = Color.WHITE;

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

