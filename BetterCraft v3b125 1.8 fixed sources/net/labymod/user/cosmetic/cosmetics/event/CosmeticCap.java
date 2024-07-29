/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

import net.labymod.main.LabyMod;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticCap
extends CosmeticRenderer<CosmeticCapData> {
    public static final int ID = 19;
    private ModelRenderer model;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        float capScale = 0.02f;
        this.model = new ModelRenderer(modelCosmetics, 0, 0).setTextureSize(38, 38);
        this.model.setTextureOffset(0, 0).addBox(-4.0f, -6.0f, -7.0f, 8, 1, 11, 0.02f);
        this.model.setTextureOffset(0, 12).addBox(-4.0f, -8.0f, -4.0f, 8, 2, 8, 0.02f);
        this.model.setTextureOffset(0, 22).addBox(-4.0f, -9.0f, -3.0f, 8, 1, 6, 0.02f);
        this.model.setTextureOffset(0, 29).addBox(-3.0f, -9.0f, -4.0f, 6, 1, 1, 0.02f);
        this.model.setTextureOffset(14, 29).addBox(-3.0f, -9.0f, 3.0f, 6, 1, 1, 0.02f);
        this.model.setTextureOffset(0, 31).addBox(-3.0f, -6.0f, -8.0f, 6, 1, 1, 0.02f);
        this.model.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.model.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticCapData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(cosmeticData.getEnumCapType().getBaseTexture());
        float scaleUp = 1.226f;
        GlStateManager.scale(1.226f, 1.226f, 1.226f);
        if (cosmeticData.isSnapBack()) {
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        }
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        this.model.isHidden = false;
        this.model.render(0.0571f);
        this.model.isHidden = true;
        if (cosmeticData.getEnumCapType().getFrontTexture() != null) {
            float scaleWolf = 0.3f;
            GlStateManager.translate(0.0, -0.425, -0.24);
            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(cosmeticData.getEnumCapType().getFrontTexture());
            LabyMod.getInstance().getDrawUtils().drawTexture(-0.2, -0.2, 256.0, 256.0, 0.4, 0.4);
            GlStateManager.enableAlpha();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 19;
    }

    @Override
    public String getCosmeticName() {
        return "Cap";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticCapData
    extends CosmeticData {
        private EnumCapType enumCapType = EnumCapType.DEFAULT;
        private boolean snapBack = false;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.snapBack = Integer.parseInt(data[0]) == 1;
            this.enumCapType = EnumCapType.values()[Integer.parseInt(data[1])];
        }

        public EnumCapType getEnumCapType() {
            return this.enumCapType;
        }

        public boolean isSnapBack() {
            return this.snapBack;
        }

        public static enum EnumCapType {
            DEFAULT("default", null),
            MERCH("merch", "wolf"),
            LABYMOD3("labymod3", "number");

            private ResourceLocation baseTexture;
            private ResourceLocation frontTexture;

            private EnumCapType(String baseName, String frontName) {
                this.baseTexture = new ResourceLocation("labymod/textures/cosmetics/caps/" + baseName + ".png");
                if (frontName != null) {
                    this.frontTexture = new ResourceLocation("labymod/textures/cosmetics/caps/" + baseName + "_" + frontName + ".png");
                }
            }

            private EnumCapType(ResourceLocation baseTexture, ResourceLocation frontTexture) {
                this.baseTexture = baseTexture;
                this.frontTexture = frontTexture;
            }

            public ResourceLocation getBaseTexture() {
                return this.baseTexture;
            }

            public ResourceLocation getFrontTexture() {
                return this.frontTexture;
            }
        }
    }
}

