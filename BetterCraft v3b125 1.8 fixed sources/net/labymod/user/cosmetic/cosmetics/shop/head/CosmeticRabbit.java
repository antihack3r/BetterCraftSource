/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

import java.awt.Color;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticRabbit
extends CosmeticRenderer<CosmeticRabbitData> {
    public static final int ID = 11;
    private ModelRenderer rabbitEar;
    private ModelRenderer rabbitEarPlayerSkin;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int i2 = 0;
        while (i2 <= 1) {
            boolean playerSkin = i2 != 0;
            ModelRenderer target = new ModelRenderer(modelCosmetics);
            if (playerSkin) {
                target.setTextureOffset(24, 0);
            } else {
                target.setTextureSize(6, 7);
            }
            target.addBox(0.0f, 0.0f, 0.0f, 2, 6, 1, modelSize);
            target.rotationPointY = -6.0f;
            target.isHidden = true;
            if (i2 == 0) {
                this.rabbitEar = target;
            } else {
                this.rabbitEarPlayerSkin = target;
            }
            ++i2;
        }
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.rabbitEar.showModel = invisible;
        this.rabbitEarPlayerSkin.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticRabbitData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        double rescale = 1.15;
        GlStateManager.translate(0.0, -0.44, -0.1);
        GlStateManager.scale(1.15, 1.15, 1.15);
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
        ModelRenderer targetModel = this.bindTextureAndColor(cosmeticData.getColor(), cosmeticData.isUseSkinTexture() ? null : ModTextures.COSMETIC_RABBIT, cosmeticData.isUseSkinTexture() ? this.rabbitEarPlayerSkin : this.rabbitEar);
        targetModel.isHidden = false;
        double distanceToMid = 0.09;
        boolean hat = cosmeticData.isHat();
        if (hat) {
            motionSub /= 5.0f;
        }
        int i2 = -1;
        while (i2 < 2) {
            GlStateManager.pushMatrix();
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(-0.18, 0.0, 0.0);
            if (canAnimate) {
                GlStateManager.rotate(6.0f - motionAdd / 3.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(-motionSub / 4.0f * (float)(-i2) + rotation / 8.0f, 0.0f, 0.0f, 1.0f);
            }
            if (entityIn.isSneaking()) {
                GlStateManager.rotate(Math.abs(entityIn.rotationPitch) / -5.0f, 0.0f, 1.0f, 1.0f);
            }
            if (hat) {
                GlStateManager.rotate(-30.0f, 0.0f, 0.0f, 1.0f);
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
        return 11;
    }

    @Override
    public String getCosmeticName() {
        return "Rabbit Ears";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticRabbitData
    extends CosmeticData {
        private Color color = Color.WHITE;
        private boolean hat;
        private boolean useSkinTexture = false;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
            this.useSkinTexture = Integer.parseInt(data[1]) == 1;
        }

        @Override
        public void completed(User user) {
            Map<Integer, CosmeticData> cosmetics = user.getCosmetics();
            this.hat = cosmetics.containsKey(16) || cosmetics.containsKey(7);
        }

        public Color getColor() {
            return this.color;
        }

        public boolean isHat() {
            return this.hat;
        }

        public boolean isUseSkinTexture() {
            return this.useSkinTexture;
        }
    }
}

