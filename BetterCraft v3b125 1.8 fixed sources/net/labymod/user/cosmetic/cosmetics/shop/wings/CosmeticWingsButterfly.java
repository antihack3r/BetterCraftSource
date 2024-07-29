/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.wings;

import java.awt.Color;
import java.util.Map;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.AnimatedCosmeticData;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.lwjgl.opengl.GL11;

public class CosmeticWingsButterfly
extends CosmeticRenderer<CosmeticWingsButterflyData> {
    public static final int ID = 35;
    private ModelRenderer wingMain;
    private ModelRenderer wingSub;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 26;
        int height = 16;
        this.wingMain = new ModelRenderer(modelCosmetics).setTextureSize(26, 16).setTextureOffset(0, 0);
        this.wingMain.setRotationPoint(0.0f, -1.5f, 0.0f);
        this.wingMain.addBox(-0.0f, -2.0f, -1.0f, 12, 7, 1);
        this.wingMain.offsetZ = 0.115f;
        this.wingMain.offsetX = -0.02f;
        this.wingMain.rotateAngleZ = -0.1f;
        this.wingMain.isHidden = true;
        this.wingSub = new ModelRenderer(modelCosmetics).setTextureSize(26, 16).setTextureOffset(0, 8);
        this.wingSub.setRotationPoint(0.0f, 4.7f, 0.0f);
        this.wingSub.addBox(-0.0f, -2.0f, -0.5f, 12, 7, 1);
        this.wingSub.rotateAngleZ = 0.1f;
        this.wingSub.rotateAngleX = 0.3f;
        this.wingSub.offsetZ = 0.115f;
        this.wingSub.offsetX = 0.02f;
        this.wingSub.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.wingMain.showModel = invisible;
        this.wingSub.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWingsButterflyData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2, 1.2, 1.2);
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, (double)-0.02f, (double)0.03f);
            GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
            ModelRenderer wingSub = this.wingSub;
            ModelRenderer wingMain = this.wingMain;
            float n2 = -0.83f;
            wingMain.rotateAngleY = -0.83f;
            wingSub.rotateAngleY = -0.83f;
        } else {
            float n2;
            float idleAnimation = (float)Math.cos(tickValue / 13.0f) / 5.0f - 0.5f;
            float flyingAnimation = (float)Math.cos(tickValue / 1.0f) / 5.0f - 0.5f;
            cosmeticData.updateFadeAnimation(entityIn.onGround || System.currentTimeMillis() - cosmeticData.lastOnGround < 500L);
            ModelRenderer wingSub2 = this.wingSub;
            ModelRenderer wingMain2 = this.wingMain;
            wingMain2.rotateAngleY = n2 = (idleAnimation *= cosmeticData.getOnGroundStrength()) + (flyingAnimation *= cosmeticData.getAirStrength()) - walkingSpeed / 7.0f;
            wingSub2.rotateAngleY = n2;
        }
        if (entityIn.onGround) {
            cosmeticData.lastOnGround = System.currentTimeMillis();
        }
        if (cosmeticData.isCape() && ((AbstractClientPlayer)entityIn).isWearing(EnumPlayerModelParts.CAPE)) {
            GlStateManager.translate(0.0f, 0.0f, 0.037f);
            this.wingMain.rotationPointX = 1.0f;
            this.wingSub.rotationPointX = 1.0f;
        } else {
            this.wingMain.rotationPointX = 0.0f;
            this.wingSub.rotationPointX = 0.0f;
        }
        this.wingMain.isHidden = false;
        this.wingSub.isHidden = false;
        int i2 = 0;
        while (i2 < 2) {
            int blue;
            int green;
            int red;
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569f);
            if (i2 == 0) {
                int mainColor = cosmeticData.getMainColor().getRGB();
                red = mainColor >> 16 & 0xFF;
                green = mainColor >> 8 & 0xFF;
                blue = mainColor >> 0 & 0xFF;
                GL11.glColor4f((float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, 0.8f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_WINGS_BUTTERFLY);
            } else {
                int patternColor = cosmeticData.getPatternColor().getRGB();
                red = patternColor >> 16 & 0xFF;
                green = patternColor >> 8 & 0xFF;
                blue = patternColor >> 0 & 0xFF;
                GL11.glColor4f((float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, 0.8f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_WINGS_BUTTERFLY_OVERLAY);
            }
            this.wingMain.render(scale);
            this.wingSub.render(scale);
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            this.wingMain.render(scale);
            this.wingSub.render(scale);
            GlStateManager.popMatrix();
            ++i2;
        }
        this.wingMain.isHidden = true;
        this.wingSub.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 35;
    }

    @Override
    public String getCosmeticName() {
        return "Butterfly Wings";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticWingsButterflyData
    extends AnimatedCosmeticData {
        private Color mainColor = new Color(39, 131, 173);
        private Color patternColor = new Color(255, 255, 255);
        private boolean cape;
        protected long lastOnGround = 0L;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.mainColor = Color.decode("#" + data[0]);
            if (data.length > 1) {
                this.patternColor = Color.decode("#" + data[1]);
            }
        }

        @Override
        public void completed(User user) {
            Map<Integer, CosmeticData> cosmetics = user.getCosmetics();
            this.cape = cosmetics.containsKey(0);
        }

        public Color getMainColor() {
            return this.mainColor;
        }

        public Color getPatternColor() {
            return this.patternColor;
        }

        public boolean isCape() {
            return this.cape;
        }
    }
}

