/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;

public class CosmeticCloak
extends CosmeticRenderer<CosmeticCloakData> {
    public static final int ID = 0;
    private ModelRenderer cloak;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 64;
        int height = 32;
        this.cloak = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(64, 32).setTextureOffset(0, 0);
        this.cloak.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, modelSize);
        this.cloak.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.cloak.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticCloakData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        UserManager userManager = LabyMod.getInstance().getUserManager();
        User user = userManager.getUser(entityIn.getUniqueID());
        boolean mojangCapeVisible = userManager.getCosmeticImageManager().getCloakImageHandler().canRenderMojangCape(user, (AbstractClientPlayer)entityIn);
        boolean wearingCape = ((AbstractClientPlayer)entityIn).isWearing(EnumPlayerModelParts.CAPE);
        if (mojangCapeVisible || !wearingCape || LabyModCore.getMinecraft().isWearingElytra(entityIn)) {
            return;
        }
        ResourceLocation cloakLocation = LabyMod.getInstance().getUserManager().getCosmeticImageManager().getCloakImageHandler().getResourceLocation((AbstractClientPlayer)entityIn);
        if (cloakLocation == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(cloakLocation);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 0.125f);
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        AbstractClientPlayer entitylivingbaseIn = (AbstractClientPlayer)entityIn;
        double motionX = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
        double motionY = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
        double motionZ = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
        float motionYaw = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
        double yawSin = LabyModCore.getMath().sin(motionYaw * (float)Math.PI / 180.0f);
        double yawCos = -LabyModCore.getMath().cos(motionYaw * (float)Math.PI / 180.0f);
        float cloakRotation = (float)motionY * 10.0f;
        cloakRotation = LabyModCore.getMath().clamp_float(cloakRotation, -6.0f, 32.0f);
        float motionAdd = (float)(motionX * yawSin + motionZ * yawCos) * 100.0f;
        float motionSub = (float)(motionX * yawCos - motionZ * yawSin) * 100.0f;
        if (motionAdd < 0.0f) {
            motionAdd = 0.0f;
        }
        if (motionAdd >= 130.0f) {
            motionAdd = 130.0f + (motionAdd - 180.0f) * 0.2f;
        }
        float cameraMotionYaw = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
        cloakRotation += LabyModCore.getMath().sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0f) * 32.0f * cameraMotionYaw;
        if (entitylivingbaseIn.isSneaking()) {
            cloakRotation += 25.0f;
        }
        GlStateManager.rotate(6.0f + motionAdd / 2.0f + cloakRotation, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(motionSub / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-motionSub / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        this.cloak.isHidden = false;
        this.cloak.render(scale);
        this.cloak.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(ModelCosmetics modelCosmetics, float movementFactor, float walkingSpeed, float tickValue, float var4, float var5, float var6, Entity entityIn) {
        this.cloak.rotationPointY = entityIn.isSneaking() ? -1.0f : 0.0f;
    }

    @Override
    public int getCosmeticId() {
        return 0;
    }

    @Override
    public String getCosmeticName() {
        return "Cloak";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticCloakData
    extends CosmeticData {
        @Override
        public boolean isEnabled() {
            return LabyMod.getSettings().cosmeticsCustomTextures;
        }

        @Override
        public void loadData(String[] data) throws Exception {
        }
    }
}

