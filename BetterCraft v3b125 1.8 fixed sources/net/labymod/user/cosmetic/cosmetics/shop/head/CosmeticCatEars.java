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

public class CosmeticCatEars
extends CosmeticRenderer<CosmeticCatEarsData> {
    public static final int ID = 21;
    private ModelRenderer catEar;
    private ModelRenderer middle;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        ModelRenderer target = new ModelRenderer(modelCosmetics).setTextureSize(10, 6).setTextureOffset(0, 0);
        target.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, modelSize);
        target.addBox(-1.0f, -5.0f, 0.0f, 1, 5, 1, modelSize);
        target.addBox(0.0f, -4.0f, 0.0f, 1, 1, 1, modelSize);
        target.addBox(1.0f, -3.0f, 0.0f, 1, 1, 1, modelSize);
        target.addBox(2.0f, -2.0f, 0.0f, 1, 1, 1, modelSize);
        target.isHidden = true;
        this.catEar = target;
        ModelRenderer backSide = new ModelRenderer(modelCosmetics).setTextureSize(10, 6).setTextureOffset(4, 0);
        backSide.addBox(0.0f, -3.0f, 0.7f, 2, 3, 1, modelSize);
        target.addChild(backSide);
        ModelRenderer middlePart = new ModelRenderer(modelCosmetics).setTextureSize(10, 6).setTextureOffset(4, 0);
        middlePart.addBox(0.0f, -3.0f, 0.4f, 2, 3, 1, modelSize - 0.01f);
        middlePart.isHidden = true;
        this.middle = middlePart;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.catEar.showModel = invisible;
        this.middle.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticCatEarsData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0, -0.44, -0.15);
        float partialTicks = LabyMod.getInstance().getPartialTicks();
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
        this.catEar.isHidden = false;
        this.middle.isHidden = false;
        double distanceToMid = 0.155;
        int i2 = -1;
        while (i2 < 2) {
            GlStateManager.pushMatrix();
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(-0.31, 0.0, 0.0);
            if (canAnimate) {
                GlStateManager.rotate(6.0f - motionAdd / 3.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-motionSub / 4.0f * (float)(-i2) + rotation / 8.0f, 0.0f, 0.0f, 1.0f);
            }
            if (entityIn.isSneaking()) {
                GlStateManager.rotate(Math.abs(entityIn.rotationPitch) / -5.0f, 0.0f, 1.0f, 1.0f);
            }
            this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_CAT_EARS, this.catEar).render(scale);
            this.bindTextureAndColor(cosmeticData.getColorInside(), ModTextures.COSMETIC_CAT_EARS, this.middle).render(scale);
            GlStateManager.popMatrix();
            i2 += 2;
        }
        this.catEar.isHidden = true;
        this.middle.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 21;
    }

    @Override
    public String getCosmeticName() {
        return "Cat Ears";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticCatEarsData
    extends CosmeticData {
        private Color color = Color.DARK_GRAY;
        private Color colorInside = Color.WHITE;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
            this.colorInside = Color.decode("#" + data[1]);
        }

        public Color getColor() {
            return this.color;
        }

        public Color getColorInside() {
            return this.colorInside;
        }
    }
}

