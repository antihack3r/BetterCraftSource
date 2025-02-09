/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticBandana
extends CosmeticRenderer<CosmeticBandanaData> {
    public static final int ID = 22;
    private ModelRenderer front;
    private ModelRenderer right;
    private ModelRenderer left;
    private ModelRenderer back;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 20;
        int height = 16;
        int heightPerSide = 100;
        this.front = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(20, 16).setTextureOffset(0, 0);
        this.front.addBox(0.0f, 0.0f, 0.0f, 9, 3, 1, modelSize);
        this.front.isHidden = true;
        this.right = new ModelRenderer(modelCosmetics, 100, 0).setTextureSize(20, 16).setTextureOffset(0, 100);
        this.right.addBox(0.0f, 0.0f, 0.0f, 9, 3, 1, modelSize);
        this.right.isHidden = true;
        this.left = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(20, 16).setTextureOffset(0, 200);
        this.left.addBox(0.0f, 0.0f, 0.0f, 9, 3, 1, modelSize);
        this.left.isHidden = true;
        this.back = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(20, 16).setTextureOffset(0, 300);
        this.back.addBox(0.0f, 0.0f, 0.0f, 9, 3, 1, modelSize);
        this.back.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.front.showModel = invisible;
        this.right.showModel = invisible;
        this.left.showModel = invisible;
        this.back.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticBandanaData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ResourceLocation bandanaLocation = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getBandanaImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (bandanaLocation == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(bandanaLocation);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        if (entityIn.isSneaking()) {
            float m2 = entityIn.rotationPitch * -7.0E-4f;
            GlStateManager.translate(0.0, (double)(0.06f - Math.abs(m2)) + 0.02, (double)m2);
        }
        if (cosmeticData.isUnderSecondLayer()) {
            double scaling = 0.88;
            GlStateManager.scale(0.88, 0.88, 0.88);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.292, -0.51, -0.318);
        GlStateManager.scale(1.039, 1.0, 0.6);
        this.front.isHidden = false;
        this.front.render(scale);
        this.front.isHidden = true;
        GlStateManager.popMatrix();
        int i2 = 0;
        while (i2 < 2) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(10.0f, -1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.086, 0.05, 0.0);
            GlStateManager.translate(-0.267, -0.5, (double)(i2 == 0 ? 1 : -1) * -0.28 - 0.015);
            if (i2 == 1) {
                GlStateManager.translate(0.0, 0.0, 0.03);
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(-0.485, 0.0, 0.0);
            }
            GlStateManager.scale(1.078, 1.0, 0.5);
            GlStateManager.translate(-0.057, 0.0, 0.0);
            ModelRenderer model = i2 == 0 ? this.right : this.left;
            model.isHidden = false;
            model.render(scale);
            model.isHidden = true;
            GlStateManager.popMatrix();
            ++i2;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.293, -0.404, 0.29);
        GlStateManager.rotate(5.0f, -1.0f, 0.0f, 0.0f);
        GlStateManager.scale(1.039, 1.0, 0.5);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.563, 0.0, 0.0);
        GlStateManager.translate(0.0, 0.0, -0.06);
        this.back.isHidden = false;
        this.back.render(scale);
        this.back.isHidden = true;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
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
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GlStateManager.translate(0.0, -0.28, 0.32);
        double scaling2 = 0.06;
        GlStateManager.scale(0.06, 0.06, 0.06);
        GlStateManager.scale(-1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5, -0.5, 0.2);
        draw.drawTexture(0.0, 0.0, 0.0, 64.0, 12.8, 16.0, 1.0, 1.0, 1.1f);
        GlStateManager.popMatrix();
        int t2 = 0;
        while (t2 < 2) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)t2 * -1.3 + 0.15, 0.0, 0.0);
            GlStateManager.rotate(6.0f + motionAdd / 2.0f + rotation, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(-motionSub / 2.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotate(motionSub / 2.0f, 0.0f, 1.0f, 0.0f);
            int j2 = 0;
            while (j2 < 3) {
                double animation = 1.0 + (double)motionAdd / 160.0;
                GlStateManager.rotate(j2 * 4 * (j2 % 2 == (t2 == 0 ? 2 : 1) ? 1 : -1), 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0.0, 0.0, (double)j2 / 50.0);
                GlStateManager.translate((double)j2 / -4.0 * (double)(t2 == 0 ? -1 : 1) * animation, (double)j2 / 4.0 * animation, 0.0);
                GlStateManager.rotate(motionAdd * (float)(t2 == 0 ? -1 : 1) / 20.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(rotation * (float)(t2 == 0 ? -1 : 1), 0.0f, 0.0f, 1.0f);
                if (t2 == 1) {
                    draw.drawTexture(0.0, 0.0, 243.2, 0.0, 12.8, 16.0, 1.0, 1.0, 1.1f);
                } else {
                    draw.drawTexture(0.0, 0.0, 0.0, 0.0, 12.8, 16.0, 1.0, 1.0, 1.1f);
                }
                ++j2;
            }
            GlStateManager.popMatrix();
            ++t2;
        }
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 22;
    }

    @Override
    public String getCosmeticName() {
        return "Bandana";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticBandanaData
    extends CosmeticData {
        private boolean underSecondLayer;

        @Override
        public boolean isEnabled() {
            return LabyMod.getSettings().cosmeticsCustomTextures;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.underSecondLayer = Integer.parseInt(data[0]) == 1;
        }

        public boolean isUnderSecondLayer() {
            return this.underSecondLayer;
        }
    }
}

