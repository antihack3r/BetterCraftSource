/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticXmasHat
extends CosmeticRenderer<CosmeticXmasHatData> {
    public static final int ID = 16;
    private static float LOCAL_XMAS_YAW = 0.0f;
    private static float LOCAL_XMAS_TICK_VALUE = 0.0f;
    private static float LOCAL_XMAS_FPS_VALUE = 0.0f;
    private ModelRenderer xmasHat;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.xmasHat = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        this.xmasHat.setRotationPoint(-5.0f, -10.03125f, -5.0f);
        this.xmasHat.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
        ModelRenderer xmasLayer2 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer2.setRotationPoint(4.0f, -2.7f, 4.0f);
        xmasLayer2.setTextureOffset(0, 12).addBox(-3.0f, 0.0f, -3.0f, 8, 3, 8);
        xmasLayer2.rotateAngleZ = 0.1f;
        this.xmasHat.addChild(xmasLayer2);
        ModelRenderer xmasLayer3 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer3.setRotationPoint(1.0f, -1.7f, 1.0f);
        xmasLayer3.setTextureOffset(0, 12).addBox(-3.0f, 0.0f, -3.0f, 6, 2, 6);
        xmasLayer3.rotateAngleZ = 0.1f;
        xmasLayer2.addChild(xmasLayer3);
        ModelRenderer xmasLayer4 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer4.setRotationPoint(1.0f, -2.0f, 0.0f);
        xmasLayer4.setTextureOffset(0, 12).addBox(-1.0f, 0.0f, -2.0f, 4, 4, 4);
        xmasLayer4.rotateAngleZ = 0.6f;
        xmasLayer3.addChild(xmasLayer4);
        ModelRenderer xmasLayer5 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer5.setRotationPoint(2.0f, -3.0f, 0.0f);
        xmasLayer5.setTextureOffset(0, 12).addBox(-2.0f, 1.4f, -1.5f, 3, 2, 3);
        xmasLayer5.rotateAngleZ = 0.2f;
        xmasLayer4.addChild(xmasLayer5);
        ModelRenderer xmasLayer6 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer6.setRotationPoint(0.0f, 0.0f, 0.0f);
        xmasLayer6.setTextureOffset(0, 12).addBox(-0.5f, 0.5f, -1.0f, 4, 2, 2);
        xmasLayer6.rotateAngleZ = -0.4f;
        xmasLayer5.addChild(xmasLayer6);
        ModelRenderer xmasLayer7 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer7.setRotationPoint(0.0f, 0.0f, 0.0f);
        xmasLayer7.setTextureOffset(0, 12).addBox(3.5f, -0.5f, -0.5f, 3, 1, 1);
        xmasLayer7.rotateAngleZ = 0.8f;
        xmasLayer6.addChild(xmasLayer7);
        ModelRenderer xmasLayer8 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        xmasLayer8.setRotationPoint(0.0f, 0.0f, 0.0f);
        xmasLayer8.setTextureOffset(0, 0).addBox(5.0f, -1.2f, -1.0f, 2, 2, 2);
        xmasLayer8.rotateAngleZ = 0.05f;
        xmasLayer7.addChild(xmasLayer8);
        this.xmasHat.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.xmasHat.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticXmasHatData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        float partialTicks = LabyMod.getInstance().getPartialTicks();
        AbstractClientPlayer clientPlayer = (AbstractClientPlayer)entityIn;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_XMAS);
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.95f, 0.95f, 0.95f);
        if (canAnimate) {
            AbstractClientPlayer entity = (AbstractClientPlayer)entityIn;
            double d0 = entity.prevChasingPosX + (entity.chasingPosX - entity.prevChasingPosX) * (double)partialTicks - (entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks);
            double d2 = entity.prevChasingPosY + (entity.chasingPosY - entity.prevChasingPosY) * (double)partialTicks - (entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks);
            double d3 = entity.prevChasingPosZ + (entity.chasingPosZ - entity.prevChasingPosZ) * (double)partialTicks - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks);
            float ft2 = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
            double d4 = LabyModCore.getMath().sin(ft2 * (float)Math.PI / 180.0f);
            double d5 = -LabyModCore.getMath().cos(ft2 * (float)Math.PI / 180.0f);
            float k1 = (float)d2 * 10.0f;
            k1 = LabyModCore.getMath().clamp_float(k1, -40.0f, 12.0f);
            float f2t = (float)(d0 * d4 + d3 * d5) * 100.0f;
            float pitch = clientPlayer.prevRotationPitch + (clientPlayer.rotationPitch - clientPlayer.prevRotationPitch) * partialTicks;
            float zFloat = (90.0f - Math.abs(pitch)) / 100.0f;
            float walkFloat = f2t > 120.0f ? 120.0f : f2t;
            float fallFloat = (float)(d2 / 3.0 > 0.7 ? -0.7 : -d2 / 3.0);
            float shakingFloat = (float)Math.cos(tickValue / 10.0f) / 40.0f;
            float bf2 = entity.distanceWalkedModified - entity.prevDistanceWalkedModified;
            float bf22 = -(entity.distanceWalkedModified + bf2 * partialTicks);
            float bf3 = entity.prevCameraYaw + (entity.cameraYaw - entity.prevCameraYaw) * partialTicks;
            walkFloat += Math.abs(LabyModCore.getMath().cos(bf22 * (float)Math.PI - 0.2f) * bf3) * 70.0f;
            float centrifugalPoint = 0.0f;
            if (entity == LabyModCore.getMinecraft().getPlayer()) {
                float speedValue = 3.0f / (float)(Minecraft.getDebugFPS() + 1);
                if (LOCAL_XMAS_FPS_VALUE < LOCAL_XMAS_TICK_VALUE) {
                    LOCAL_XMAS_FPS_VALUE += speedValue;
                }
                if (LOCAL_XMAS_FPS_VALUE > LOCAL_XMAS_TICK_VALUE) {
                    LOCAL_XMAS_FPS_VALUE -= speedValue;
                }
                float centrifugal = -LOCAL_XMAS_FPS_VALUE;
                centrifugalPoint = centrifugal / -1.5f;
                fallFloat += centrifugal;
            }
            ModelRenderer ch1 = this.xmasHat.childModels.get(0);
            ModelRenderer ch2 = ch1.childModels.get(0);
            ch2.rotateAngleY = pitch / 300.0f - walkFloat / 200.0f;
            ch2.rotateAngleZ = 0.1f + fallFloat / 2.0f;
            ModelRenderer ch3 = ch2.childModels.get(0);
            ch3.rotateAngleY = pitch / 200.0f;
            ch2.rotateAngleZ = 0.1f + fallFloat / 4.0f;
            ModelRenderer ch4 = ch3.childModels.get(0);
            ch4.rotateAngleY = pitch / 100.0f - walkFloat / 100.0f;
            ModelRenderer ch5 = ch4.childModels.get(0);
            ch5.rotateAngleZ = fallFloat;
            ch5.rotateAngleY = shakingFloat;
            ch5.rotationPointY = centrifugalPoint;
            ModelRenderer ch6 = ch5.childModels.get(0);
            ch6.rotateAngleZ = zFloat - 0.3f;
            ch6.rotationPointY = 3.0f - zFloat * 4.0f;
            ModelRenderer ch7 = ch6.childModels.get(0);
            ch7.rotateAngleZ = shakingFloat / -2.0f;
            ch7.rotateAngleY = shakingFloat / 4.0f;
        }
        this.xmasHat.isHidden = false;
        this.xmasHat.render(scale);
        this.xmasHat.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public void onTick() {
        if (LabyModCore.getMinecraft().getPlayer() == null) {
            return;
        }
        UUID uuid = LabyMod.getInstance().getPlayerUUID();
        User user = LabyMod.getInstance().getUserManager().getUser(uuid);
        boolean hasXmasCos = user.hasCosmeticById(16);
        if (!hasXmasCos) {
            return;
        }
        float pos = LabyModCore.getMinecraft().getPlayer().rotationYaw;
        if (pos != LOCAL_XMAS_YAW) {
            LOCAL_XMAS_TICK_VALUE += Math.abs(pos - LOCAL_XMAS_YAW) / 190.0f;
        }
        if (LOCAL_XMAS_TICK_VALUE > 0.0f) {
            LOCAL_XMAS_TICK_VALUE -= 0.15f;
        }
        if (LOCAL_XMAS_TICK_VALUE > 1.0f) {
            LOCAL_XMAS_TICK_VALUE = 1.0f;
        }
        if (LOCAL_XMAS_TICK_VALUE < 0.0f) {
            LOCAL_XMAS_TICK_VALUE = 0.0f;
        }
        LOCAL_XMAS_YAW = pos;
    }

    @Override
    public int getCosmeticId() {
        return 16;
    }

    @Override
    public String getCosmeticName() {
        return "Xmas Hat";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.5f;
    }

    public static class CosmeticXmasHatData
    extends CosmeticData {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
        }
    }
}

