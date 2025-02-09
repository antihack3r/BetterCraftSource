/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.wings;

import java.awt.Color;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticWingsCrystal
extends CosmeticRenderer<CosmeticWingsCrystalData> {
    public static final int ID = 13;
    private ModelRenderer model;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 30;
        int height = 24;
        this.model = new ModelRenderer(modelCosmetics).setTextureSize(30, 24).setTextureOffset(0, 8);
        this.model.setRotationPoint(-0.0f, 1.0f, 0.0f);
        this.model.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
        this.model.isHidden = true;
        ModelRenderer model = new ModelRenderer(modelCosmetics).setTextureSize(30, 24).setTextureOffset(0, 16);
        model.setRotationPoint(-0.0f, 0.0f, 0.2f);
        model.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
        this.model.addChild(model);
        ModelRenderer model2 = new ModelRenderer(modelCosmetics).setTextureSize(30, 24).setTextureOffset(0, 0);
        model2.setRotationPoint(-0.0f, 0.0f, 0.2f);
        model2.addBox(0.0f, -3.0f, 0.0f, 14, 7, 1);
        model.addChild(model2);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.model.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWingsCrystalData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        float animation = (float)Math.cos(tickValue / 10.0f) / 20.0f - 0.03f - walkingSpeed / 20.0f;
        ModelRenderer firstModel = this.model.childModels.get(0);
        ModelRenderer secondModel = firstModel.childModels.get(0);
        this.model.rotateAngleZ = animation * 3.0f;
        firstModel.rotateAngleZ = animation / 2.0f;
        secondModel.rotateAngleZ = animation / 2.0f;
        this.model.rotateAngleY = -0.3f - walkingSpeed / 3.0f;
        this.model.rotateAngleX = 0.3f;
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.6, 1.6, 1.0);
        GlStateManager.translate(0.0, (double)0.05f, (double)0.05f);
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, (double)-0.08f, (double)0.03f);
            GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
            this.model.rotateAngleZ = 0.8f;
            firstModel.rotateAngleZ = 0.0f;
            secondModel.rotateAngleZ = 0.0f;
        } else {
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            if (manager != null) {
                GlStateManager.rotate(manager.playerViewX / 3.0f, 1.0f, 0.0f, 0.0f);
            }
        }
        Color color = cosmeticData.getColor();
        this.model.isHidden = false;
        int i2 = -1;
        while (i2 <= 1) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 0.3f);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569f);
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_WINGS_CRYSTAL);
            if (i2 == 1) {
                GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            }
            GlStateManager.translate(0.05, 0.0, 0.0);
            GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5f);
            if (i2 == 1 && cosmeticData.getSecondColor() != null) {
                Color secondColor = cosmeticData.getSecondColor();
                GL11.glColor4f((float)secondColor.getRed() / 255.0f, (float)secondColor.getGreen() / 255.0f, (float)secondColor.getBlue() / 255.0f, 0.5f);
            }
            this.model.render(scale);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            i2 += 2;
        }
        this.model.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 13;
    }

    @Override
    public String getCosmeticName() {
        return "Crystal Wings";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticWingsCrystalData
    extends CosmeticData {
        public long lastParticle;
        private Color color = new Color(255, 255, 255);
        private Color secondColor = null;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
            if (data.length > 1) {
                this.secondColor = Color.decode("#" + data[1]);
            }
        }

        public long getLastParticle() {
            return this.lastParticle;
        }

        public void setLastParticle(long lastParticle) {
            this.lastParticle = lastParticle;
        }

        public Color getColor() {
            return this.color;
        }

        public Color getSecondColor() {
            return this.secondColor;
        }
    }
}

