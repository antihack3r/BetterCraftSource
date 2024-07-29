/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.wings;

import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticWingsDragon
extends CosmeticRenderer<CosmeticWingsData> {
    public static final int ID = 2;
    private ModelRenderer wing;
    private ModelRenderer wingTip;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        modelCosmetics.setTextureOffset("body.scale", 220, 53);
        modelCosmetics.setTextureOffset("body.body", 0, 0);
        modelCosmetics.setTextureOffset("wingtip.bone", 112, 136);
        modelCosmetics.setTextureOffset("wing.skin", -56, 88);
        modelCosmetics.setTextureOffset("wing.bone", 112, 88);
        modelCosmetics.setTextureOffset("wingtip.skin", -56, 144);
        int bw2 = modelCosmetics.textureWidth;
        int bh2 = modelCosmetics.textureHeight;
        modelCosmetics.textureWidth = 256;
        modelCosmetics.textureHeight = 256;
        this.wing = new ModelRenderer(modelCosmetics, "wing");
        this.wing.setRotationPoint(-12.0f, 5.0f, 2.0f);
        this.wing.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8);
        this.wing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 1, 56);
        this.wing.isHidden = true;
        this.wingTip = new ModelRenderer(modelCosmetics, "wingtip");
        this.wingTip.setRotationPoint(-56.0f, 0.0f, 0.0f);
        this.wingTip.isHidden = true;
        this.wingTip.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4);
        this.wingTip.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 1, 56);
        this.wing.addChild(this.wingTip);
        modelCosmetics.textureWidth = bw2;
        modelCosmetics.textureHeight = bh2;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.wing.showModel = invisible;
        this.wingTip.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWingsData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        double movement = Math.abs(entityIn.motionX + entityIn.motionZ);
        float rotationTick = walkingSpeed + (entityIn.onGround && !entityIn.isSprinting() ? tickValue : tickValue * 12.0f + (float)movement + walkingSpeed) / 100.0f;
        int wingsScale = 25;
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.COSMETIC_ENDER_DRAGON);
        GlStateManager.scale(0.12, 0.12, 0.12);
        GlStateManager.translate(0.0, -0.3, 1.1);
        GlStateManager.rotate(50.0f, -50.0f, 0.0f, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        Color color = cosmeticData.getWingsColor();
        if (color != null) {
            GL11.glColor4f((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 0.5f);
        }
        int i2 = 0;
        while (i2 < 2) {
            GlStateManager.enableCull();
            float rotation = rotationTick * (float)Math.PI * 2.0f;
            this.wing.rotateAngleX = 0.125f - (float)Math.cos(rotation) * 0.2f;
            this.wing.rotateAngleY = 0.25f;
            this.wing.rotateAngleZ = (float)(Math.sin(rotation) + 1.225) * 0.3f;
            this.wingTip.rotateAngleZ = -((float)(Math.sin(rotation + 2.0f) + 0.5)) * 0.75f;
            this.wing.isHidden = false;
            this.wingTip.isHidden = false;
            this.wing.render(scale);
            this.wing.isHidden = true;
            this.wingTip.isHidden = true;
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            if (i2 == 0) {
                LabyModCore.getRenderImplementation().cullFaceFront();
                if (cosmeticData.getSecondColor() != null) {
                    Color secondColor = cosmeticData.getSecondColor();
                    GL11.glColor4f((float)secondColor.getRed() / 255.0f, (float)secondColor.getGreen() / 255.0f, (float)secondColor.getBlue() / 255.0f, 0.5f);
                }
            }
            ++i2;
        }
        LabyModCore.getRenderImplementation().cullFaceBack();
        GL11.glColor3d(1.0, 1.0, 1.0);
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 2;
    }

    @Override
    public String getCosmeticName() {
        return "Dragon Wings";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticWingsData
    extends CosmeticData {
        private long flying = -1L;
        private boolean direction;
        private float lastTick;
        private Color wingsColor = Color.WHITE;
        private Color secondColor = null;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.wingsColor = Color.decode("#" + data[0]);
            if (data.length > 1) {
                this.secondColor = Color.decode("#" + data[1]);
            }
        }

        public long getFlying() {
            return this.flying;
        }

        public void setFlying(long flying) {
            this.flying = flying;
        }

        public boolean isDirection() {
            return this.direction;
        }

        public void setDirection(boolean direction) {
            this.direction = direction;
        }

        public float getLastTick() {
            return this.lastTick;
        }

        public void setLastTick(float lastTick) {
            this.lastTick = lastTick;
        }

        public Color getWingsColor() {
            return this.wingsColor;
        }

        public Color getSecondColor() {
            return this.secondColor;
        }
    }
}

