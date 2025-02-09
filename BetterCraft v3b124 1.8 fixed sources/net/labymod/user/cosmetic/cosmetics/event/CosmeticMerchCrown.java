/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

import java.awt.Color;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticMerchCrown
extends CosmeticRenderer<CosmeticMerchCrownData> {
    public static final int ID = 18;
    private ModelRenderer base;
    private ModelRenderer diamond;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 30;
        int height = 16;
        float crownScale = 0.02f;
        this.base = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(30, 16);
        this.base.setTextureOffset(4, 0).addBox(-4.0f, 0.0f, -5.0f, 8, 2, 1, 0.02f);
        this.base.setTextureOffset(0, 0).addBox(-5.0f, -2.0f, -5.0f, 1, 4, 1, 0.02f);
        this.base.setTextureOffset(0, 5).addBox(-4.0f, -1.0f, -5.0f, 1, 1, 1, 0.02f);
        this.base.setTextureOffset(0, 5).addBox(3.0f, -1.0f, -5.0f, 1, 1, 1, 0.02f);
        this.base.setTextureOffset(4, 5).addBox(-1.5f, -1.0f, -5.0f, 3, 1, 1, 0.02f);
        this.base.setTextureOffset(0, 5).addBox(-0.5f, -2.0f, -5.0f, 1, 1, 1, 0.02f);
        this.base.isHidden = true;
        this.diamond = new ModelRenderer(modelCosmetics, 12, 5).setTextureSize(30, 16);
        this.diamond.addBox(-0.5f, -0.0f, -6.0f, 1, 1, 1, 0.02f);
        this.diamond.rotateAngleZ = 0.8f;
        this.diamond.rotationPointZ = 0.5f;
        this.diamond.rotationPointX = 0.4f;
        this.diamond.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.base.showModel = invisible;
        this.diamond.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticMerchCrownData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        int i2 = 0;
        while (i2 < 4) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
            float scaleUp = 1.085f;
            GlStateManager.scale(1.085f, 1.085f, 1.085f);
            if (entityIn.isSneaking()) {
                float m2 = entityIn.rotationPitch * -7.0E-4f;
                GlStateManager.translate(0.0, (double)(0.06f - Math.abs(m2)) + 0.02, (double)m2);
            }
            GlStateManager.rotate(90 * i2, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0, -0.4753, 0.0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_CROWN);
            this.base.isHidden = false;
            this.base.render(0.0571f);
            this.base.isHidden = true;
            this.diamond.isHidden = false;
            this.diamond.rotateAngleZ = 0.8f;
            this.diamond.rotationPointZ = 0.6f;
            this.diamond.rotationPointX = 0.4f;
            GlStateManager.translate(-0.22f, 0.0f, 0.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            Color color = cosmeticData.getDiamondColor();
            if (color != null) {
                GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5f);
            }
            int d2 = 0;
            while (d2 < 3) {
                this.diamond.render(0.0561f);
                GlStateManager.translate(0.218f, 0.0f, 0.0f);
                ++d2;
            }
            GL11.glColor3d(1.0, 1.0, 1.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            this.diamond.isHidden = true;
            GlStateManager.popMatrix();
            ++i2;
        }
    }

    @Override
    public int getCosmeticId() {
        return 18;
    }

    @Override
    public String getCosmeticName() {
        return "Merch Crown";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticMerchCrownData
    extends CosmeticData {
        private Color diamondColor;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.diamondColor = Color.decode("#" + data[0]);
        }

        public Color getDiamondColor() {
            return this.diamondColor;
        }
    }
}

