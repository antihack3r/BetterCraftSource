/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

import java.util.HashMap;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CosmeticWitchHat
extends CosmeticRenderer<CosmeticWitchHatData> {
    public static final int ID = 7;
    private static final HashMap<String, ResourceLocation> flags = new HashMap();
    private ModelRenderer witchHat;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.witchHat = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        this.witchHat.setRotationPoint(-5.0f, -10.03125f, -5.0f);
        this.witchHat.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
        ModelRenderer modelrenderer = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        modelrenderer.setRotationPoint(1.75f, -4.0f, 2.0f);
        modelrenderer.setTextureOffset(0, 12).addBox(0.0f, 0.0f, 0.0f, 7, 4, 7);
        modelrenderer.rotateAngleX = -0.05235988f;
        modelrenderer.rotateAngleZ = 0.02617994f;
        this.witchHat.addChild(modelrenderer);
        ModelRenderer modelrenderer2 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        modelrenderer2.setRotationPoint(1.75f, -4.0f, 2.0f);
        modelrenderer2.setTextureOffset(0, 23).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        modelrenderer2.rotateAngleX = -0.10471976f;
        modelrenderer2.rotateAngleZ = 0.05235988f;
        modelrenderer.addChild(modelrenderer2);
        ModelRenderer modelrenderer3 = new ModelRenderer(modelCosmetics).setTextureSize(40, 34);
        modelrenderer3.setRotationPoint(1.75f, -2.0f, 2.0f);
        modelrenderer3.setTextureOffset(0, 31).addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.25f);
        modelrenderer3.rotateAngleX = -0.20943952f;
        modelrenderer3.rotateAngleZ = 0.10471976f;
        modelrenderer2.addChild(modelrenderer3);
        this.witchHat.isHidden = true;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.witchHat.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticWitchHatData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        ResourceLocation bindedTexture = ModTextures.COSMETIC_WITCH;
        if (cosmeticData.getFlagTexture() != null) {
            ResourceLocation loadedTexture;
            if (!flags.containsKey(cosmeticData.getFlagTexture())) {
                flags.put(cosmeticData.getFlagTexture(), new ResourceLocation("labymod/textures/cosmetics/flags/" + cosmeticData.getFlagTexture() + ".png"));
            }
            if ((loadedTexture = flags.get(cosmeticData.getFlagTexture())) != null) {
                bindedTexture = loadedTexture;
            }
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(bindedTexture);
        GlStateManager.pushMatrix();
        float scaleDown = 0.995f;
        GlStateManager.scale(0.995f, 0.995f, 0.995f);
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.1, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        this.witchHat.isHidden = false;
        this.witchHat.render(scale);
        this.witchHat.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 7;
    }

    @Override
    public String getCosmeticName() {
        return "Hat";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.5f;
    }

    public static class CosmeticWitchHatData
    extends CosmeticData {
        private String flagTexture;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.flagTexture = data[0] == null || data[0].isEmpty() ? null : data[0].toLowerCase();
        }

        public String getFlagTexture() {
            return this.flagTexture;
        }
    }
}

