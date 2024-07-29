/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

import java.awt.Color;
import java.util.Map;
import net.labymod.main.ModTextures;
import net.labymod.user.User;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticHalo
extends CosmeticRenderer<CosmeticHaloData> {
    public static final int ID = 9;
    private ModelRenderer halo;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.halo = new ModelRenderer(modelCosmetics).setTextureSize(14, 2);
        this.halo.addBox(-3.0f, -12.5f, -4.0f, 6, 1, 1, modelSize);
        this.halo.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.halo.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticHaloData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        float bounceAnimation = (float)Math.cos((double)tickValue / 10.0) / 20.0f;
        GlStateManager.rotate(firstRotationX + tickValue / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, bounceAnimation - (cosmeticData.isHat() ? 0.4f : 0.0f), 0.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_HALO);
        GlStateManager.disableLighting();
        ModelRenderer targetModel = this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_HALO, this.halo);
        targetModel.isHidden = false;
        int i2 = 0;
        while (i2 < 4) {
            targetModel.render(scale);
            GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
            ++i2;
        }
        targetModel.isHidden = true;
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 9;
    }

    @Override
    public String getCosmeticName() {
        return "Halo";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticHaloData
    extends CosmeticData {
        private Color color = Color.YELLOW;
        private boolean hat;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) {
            this.color = Color.decode("#" + data[0]);
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
    }
}

