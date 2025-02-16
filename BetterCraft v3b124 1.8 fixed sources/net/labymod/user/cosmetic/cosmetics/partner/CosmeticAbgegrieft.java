/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.partner;

import java.awt.Color;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticAbgegrieft
extends CosmeticRenderer<CosmeticAbgegrieftData> {
    public static final int ID = 29;
    private ModelRenderer modelBelt;
    private ModelRenderer modelBuckle;
    private ModelRenderer modelLogo;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 28;
        int height = 12;
        ModelRenderer modelBelt = new ModelRenderer(modelCosmetics).setTextureSize(28, 12).setTextureOffset(0, 0);
        modelBelt.addBox(-4.5f, 9.5f, -2.5f, 9, 2, 5, modelSize);
        modelBelt.setRotationPoint(0.0f, 0.0f, 0.0f);
        modelBelt.rotateAngleX = 0.0f;
        modelBelt.rotateAngleY = 0.0f;
        modelBelt.rotateAngleZ = 0.0f;
        modelBelt.isHidden = true;
        ModelRenderer modelBuckle = new ModelRenderer(modelCosmetics).setTextureSize(28, 12).setTextureOffset(0, 7);
        modelBuckle.addBox(-1.5f, 9.5f, -2.6f, 3, 2, 1, modelSize);
        modelBuckle.setRotationPoint(0.0f, 0.0f, 0.0f);
        modelBuckle.rotateAngleX = 0.0f;
        modelBuckle.rotateAngleY = 0.0f;
        modelBuckle.rotateAngleZ = 0.0f;
        modelBuckle.isHidden = true;
        ModelRenderer modelLogo = new ModelRenderer(modelCosmetics).setTextureSize(28, 12).setTextureOffset(8, 7);
        modelLogo.addBox(0.0f, 0.0f, 0.0f, 2, 1, 1, modelSize);
        modelLogo.addBox(2.0f, 0.5f, 0.0f, 1, 1, 1, modelSize);
        modelLogo.addBox(-1.0f, 0.5f, 0.0f, 1, 4, 1, modelSize);
        modelLogo.addBox(0.0f, 4.0f, 0.0f, 2, 1, 1, modelSize);
        modelLogo.addBox(2.0f, 2.5f, 0.0f, 1, 2, 1, modelSize);
        modelLogo.addBox(1.0f, 2.5f, 0.0f, 1, 1, 1, modelSize);
        modelLogo.isHidden = true;
        this.modelBelt = modelBelt;
        this.modelBuckle = modelBuckle;
        this.modelLogo = modelLogo;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.modelBelt.showModel = invisible;
        this.modelBuckle.showModel = invisible;
        this.modelLogo.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticAbgegrieftData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        GlStateManager.enableLighting();
        GlStateManager.translate(0.0, 0.03, 0.0);
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0f, -0.12f, -0.08f);
            GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.97, 0.9, 0.99);
        GlStateManager.translate(0.0, 0.075, 0.0);
        ModelRenderer modelBelt = this.bindTextureAndColor(cosmeticData.getColorBelt(), ModTextures.COSMETIC_ABGEGRIEFT, this.modelBelt);
        modelBelt.isHidden = false;
        modelBelt.render(scale);
        modelBelt.isHidden = true;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2, 1.05, 1.0);
        GlStateManager.translate(0.0, -0.03, 0.0);
        ModelRenderer modelBuckle = this.bindTextureAndColor(cosmeticData.getColorBuckle(), ModTextures.COSMETIC_ABGEGRIEFT, this.modelBuckle);
        modelBuckle.isHidden = false;
        modelBuckle.render(scale);
        GlStateManager.scale(0.9, 1.1, 1.0);
        GlStateManager.translate(0.0, -0.06, 1.0E-4);
        modelBuckle.render(scale);
        modelBuckle.isHidden = true;
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        double size = 0.32;
        GlStateManager.scale(0.32, 0.32, 0.32);
        GlStateManager.translate(-0.21875000000000003, 1.890625, -0.578125);
        ModelRenderer modelLogo = this.bindTextureAndColor(cosmeticData.getColorLogo(), ModTextures.COSMETIC_ABGEGRIEFT, this.modelLogo);
        modelLogo.isHidden = false;
        modelLogo.render(scale);
        GlStateManager.translate(0.3125, 0.0, 0.0);
        modelLogo.render(scale);
        modelLogo.isHidden = true;
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 29;
    }

    @Override
    public String getCosmeticName() {
        return "Abgegrieft";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.0f;
    }

    public static class CosmeticAbgegrieftData
    extends CosmeticData {
        private Color colorBelt = new Color(137, 77, 55);
        private Color colorBuckle = new Color(225, 205, 104);
        private Color colorLogo = new Color(225, 205, 104);

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.colorBelt = Color.decode("#" + data[0]);
            this.colorBuckle = Color.decode("#" + data[1]);
            this.colorLogo = Color.decode("#" + data[2]);
        }

        public Color getColorBelt() {
            return this.colorBelt;
        }

        public Color getColorBuckle() {
            return this.colorBuckle;
        }

        public Color getColorLogo() {
            return this.colorLogo;
        }
    }
}

