/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.staff;

import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticMoehritz
extends CosmeticRenderer<CosmeticMoehritzData> {
    public static final int ID = 4;
    private boolean mc18;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.mc18 = Source.ABOUT_MC_VERSION.startsWith("1.8");
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticMoehritzData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        double distanceSq;
        if (!this.mc18) {
            return;
        }
        GlStateManager.pushMatrix();
        AbstractClientPlayer player = (AbstractClientPlayer)entityIn;
        LabyModCore.getMinecraft().setSecondLayerBit(player, 6, (byte)0);
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.translate(-0.25, -0.5, -0.26);
        GlStateManager.scale(0.0625, 0.0625, 1.0);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-8.0, 0.0, 0.004);
        int f2 = (int)(55.0f - (90.0f + entityIn.rotationPitch));
        if (f2 < 0) {
            f2 = 0;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        GlStateManager.translate(0.0, 0.0, entityIn.isSneaking() ? -0.008 : -0.011);
        DrawUtils.drawRect(1.9f, 5.9f, 6.1f, 8.0, ModColor.toRGB(242 - f2, 215 - f2, 159 - f2, 255));
        int colorBrow = ModColor.toRGB(73, 33, 2, 255);
        int colorEye = ModColor.toRGB(150, 0, 0, 255);
        int colorMouth = ModColor.toRGB(30, 30, 30, 255);
        DrawUtils.drawRect(1, 3, 3, 4, colorBrow);
        DrawUtils.drawRect(5, 3, 7, 4, colorBrow);
        Entity targetEntity = Minecraft.getMinecraft().getRenderViewEntity();
        if (cosmeticData.targetPlayerUUID != null) {
            if (cosmeticData.lastTargetEntityCheck + 5000L < System.currentTimeMillis()) {
                cosmeticData.lastTargetEntityCheck = System.currentTimeMillis();
                cosmeticData.targetEntity = LabyModCore.getMinecraft().getWorld().getPlayerEntityByUUID(cosmeticData.targetPlayerUUID);
            }
            if (cosmeticData.targetEntity != null) {
                targetEntity = cosmeticData.targetEntity;
            }
        }
        double eyeX = 2.0;
        double movement = LabyModCore.getMinecraft().calculateEyeMovement(entityIn, targetEntity);
        if (movement > 0.5) {
            movement = 0.5;
        }
        if (movement < -0.5) {
            movement = -0.5;
        }
        eyeX -= movement;
        int eyeY = 4;
        int i2 = 0;
        while (i2 < 2) {
            DrawUtils.drawRect(eyeX - 0.5, 4.0, eyeX + 0.5, 5.0, colorEye);
            eyeX += 4.0;
            ++i2;
        }
        double smile = 0.0;
        if (targetEntity != null && (smile += (distanceSq = targetEntity.getDistanceSq(entityIn.posX, entityIn.posY, entityIn.posZ)) / 50.0) > 1.0) {
            smile = 1.0;
        }
        GlStateManager.translate(0.0, 0.0, 0.002);
        DrawUtils.drawRect(2.0, 6.0 + smile, 3.0, 7.0 + smile, colorMouth);
        DrawUtils.drawRect(5.0, 6.0 + smile, 6.0, 7.0 + smile, colorMouth);
        DrawUtils.drawRect(3.0, 7.0 - smile, 5.0, 8.0 - smile, colorMouth);
        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 4;
    }

    @Override
    public String getCosmeticName() {
        return "Moehritz";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticMoehritzData
    extends CosmeticData {
        protected UUID targetPlayerUUID;
        protected Entity targetEntity;
        protected long lastTargetEntityCheck;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.targetPlayerUUID = UUID.fromString(data[0]);
        }

        @Override
        public void init(User user) {
        }
    }
}

