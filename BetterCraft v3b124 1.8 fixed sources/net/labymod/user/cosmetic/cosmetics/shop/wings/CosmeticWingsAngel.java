/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.wings;

import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.user.cosmetic.util.AnimatedCosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticWingsAngel
extends CosmeticRenderer<CosmeticAngelData> {
    public static final int ID = 24;
    private ModelRenderer model;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 30;
        int height = 23;
        this.model = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(30, 23).setTextureOffset(0, 0);
        this.model.addBox(0.0f, 0.0f, 0.0f, 9, 3, 1, modelSize);
        this.model.isHidden = true;
        ModelRenderer featherLayer1 = new ModelRenderer(modelCosmetics).setTextureSize(30, 23).setTextureOffset(0, 4);
        featherLayer1.setRotationPoint(0.0f, 1.0f, 0.0f);
        featherLayer1.addBox(-1.0f, 0.0f, -0.5f, 14, 4, 1);
        featherLayer1.rotateAngleZ = -0.06f;
        this.model.addChild(featherLayer1);
        ModelRenderer test = new ModelRenderer(modelCosmetics).setTextureSize(30, 23).setTextureOffset(18, 10);
        test.addBox(2.0f, -1.0f, 0.0f, 2, 1, 1, modelSize);
        this.model.addChild(test);
        ModelRenderer featherLayer2 = new ModelRenderer(modelCosmetics).setTextureSize(30, 23).setTextureOffset(0, 9);
        featherLayer2.setRotationPoint(0.0f, 0.0f, 0.0f);
        featherLayer2.addBox(2.0f, 3.0f, -0.8f, 8, 4, 1);
        featherLayer2.rotateAngleZ = -0.2f;
        featherLayer2.rotateAngleX = 0.2f;
        featherLayer1.addChild(featherLayer2);
        ModelRenderer featherLayer3 = new ModelRenderer(modelCosmetics).setTextureSize(30, 23).setTextureOffset(0, 14);
        featherLayer3.setRotationPoint(0.0f, 0.0f, 0.0f);
        featherLayer3.addBox(0.0f, 1.0f, 0.0f, 14, 7, 1);
        featherLayer3.rotateAngleZ = 0.5f;
        featherLayer2.addChild(featherLayer3);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.model.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticAngelData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ResourceLocation textureDesign = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getAngelWingsImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (textureDesign == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        this.model.isHidden = false;
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
        double motionSin = LabyModCore.getMath().sin(motionYaw * (float)Math.PI / 180.0f);
        double motionCos = -LabyModCore.getMath().cos(motionYaw * (float)Math.PI / 180.0f);
        float rotation = (float)motionY * 10.0f;
        rotation = LabyModCore.getMath().clamp_float(rotation, -6.0f, 32.0f);
        float motionAdd = (float)(motionX * motionSin + motionZ * motionCos) * 100.0f;
        float motionSub = (float)(motionX * motionCos - motionZ * motionSin) * 100.0f;
        if (motionAdd < 0.0f) {
            motionAdd = 0.0f;
        }
        if (motionAdd >= 60.0f) {
            motionAdd = 60.0f + (motionAdd - 60.0f) * 0.2f;
        }
        float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        rotation += LabyModCore.getMath().sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * f4;
        if (entityIn.isSneaking()) {
            GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
        }
        GlStateManager.translate(0.0f, 0.05f, 0.0f);
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        if (manager != null) {
            GlStateManager.rotate(manager.playerViewX / 3.0f, 1.0f, 0.0f, 0.0f);
        }
        cosmeticData.updateFadeAnimation(entityIn.onGround);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureDesign);
        int i2 = -1;
        while (i2 <= 1) {
            GlStateManager.pushMatrix();
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(0.1, 0.0, 0.13);
            float walkShakeFloat = rotation / -260.0f;
            float idleStrength = entityIn.isSneaking() ? 100.0f : 30.0f;
            float idleAnimationFloatCos = (float)(Math.cos(tickValue / 26.0f) / (double)idleStrength);
            float idleAnimationFloatSin = (float)(Math.sin(tickValue / 15.0f) / (double)idleStrength);
            float idleAnimationFloatWithWalking = idleAnimationFloatCos - walkingSpeed / 5.0f;
            float scretch = (cosmeticData.getFadeAnimation() + 1.0f) / 15.0f;
            float onGroundStrength = cosmeticData.getOnGroundStrength();
            float inAirStrength = cosmeticData.getAirStrength();
            idleAnimationFloatWithWalking *= onGroundStrength;
            this.model.rotateAngleZ = walkShakeFloat + (idleAnimationFloatWithWalking -= inAirStrength * 0.1f) * 2.0f - 0.3f;
            ModelRenderer feather1 = this.model.childModels.get(0);
            feather1.rotateAngleZ = walkShakeFloat + idleAnimationFloatWithWalking / 3.0f + idleAnimationFloatCos - 0.1f + walkingSpeed / 10.0f + scretch / 2.0f;
            ModelRenderer feather2 = feather1.childModels.get(0);
            feather2.rotateAngleZ = walkShakeFloat + idleAnimationFloatWithWalking / 3.0f + idleAnimationFloatCos - 0.2f + scretch;
            ModelRenderer feather3 = feather2.childModels.get(0);
            feather3.rotateAngleZ = walkShakeFloat + idleAnimationFloatCos + 0.5f;
            if (entityIn.isSneaking()) {
                GlStateManager.rotate(30.0f, 0.0f, 0.0f, 1.0f);
            } else {
                GlStateManager.rotate(6.0f + motionAdd / 2.0f * onGroundStrength + 20.0f * inAirStrength, 0.0f, -1.0f, 0.0f);
                float idleValue = (idleAnimationFloatSin * 500.0f - 10.0f) * (1.0f - walkingSpeed);
                GlStateManager.rotate(idleValue *= onGroundStrength, 0.0f, 1.0f, 0.0f);
                float flyingValue = (float)Math.cos(tickValue / 2.8f) * 30.0f;
                GlStateManager.rotate(motionAdd / 3.0f * inAirStrength, 1.0f, 1.0f, 0.0f);
                GlStateManager.rotate((flyingValue *= inAirStrength) / 5.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(flyingValue / 2.0f + 15.0f * inAirStrength, 0.0f, -1.0f, 0.0f);
            }
            GlStateManager.rotate(motionSub / 2.0f * (float)(i2 == 1 ? 1 : -1) * (onGroundStrength * 0.4f + 0.3f), 0.0f, 1.0f, 0.0f);
            GlStateManager.disableLighting();
            this.model.render(scale);
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            i2 += 2;
        }
        this.model.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 24;
    }

    @Override
    public String getCosmeticName() {
        return "AngelWings";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticAngelData
    extends AnimatedCosmeticData {
        private UserTextureContainer userTextureContainer;

        @Override
        public void init(User user) {
            this.userTextureContainer = user.getAngelWingsContainer();
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.userTextureContainer.setFileName(UUID.fromString(data[0]));
        }
    }
}

