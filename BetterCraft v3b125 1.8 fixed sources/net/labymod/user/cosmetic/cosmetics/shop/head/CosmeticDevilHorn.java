/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

import java.awt.Color;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class CosmeticDevilHorn
extends CosmeticRenderer<CosmeticDevilHornData> {
    public static final int ID = 12;
    private ModelRenderer devilHorn;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        int width = 12;
        int height = 8;
        ModelRenderer devilHorn = new ModelRenderer(modelCosmetics).setTextureSize(12, 8).setTextureOffset(0, 0);
        devilHorn.addBox(-1.0f, 0.0f, -0.5f, 2, 3, 1, modelSize);
        devilHorn.setRotationPoint(-2.5f, 0.0f, 0.0f);
        devilHorn.rotateAngleY = 0.0f;
        devilHorn.rotateAngleZ = 2.5f;
        devilHorn.isHidden = true;
        ModelRenderer subHorn = new ModelRenderer(modelCosmetics).setTextureSize(12, 8).setTextureOffset(6, 0);
        subHorn.addBox(-1.0f, 0.0f, -0.5f, 2, 3, 1, modelSize);
        subHorn.setRotationPoint(0.1f, 2.6f, -0.2f);
        subHorn.rotateAngleY = -0.1f;
        subHorn.rotateAngleZ = 0.4f;
        devilHorn.addChild(subHorn);
        ModelRenderer tipHorn = new ModelRenderer(modelCosmetics).setTextureSize(12, 8).setTextureOffset(6, 4);
        tipHorn.addBox(-1.0f, 0.0f, -0.5f, 1, 3, 1, modelSize);
        tipHorn.setRotationPoint(1.0f, 3.0f, -0.2f);
        tipHorn.rotateAngleY = -0.1f;
        tipHorn.rotateAngleZ = 1.05f;
        subHorn.addChild(tipHorn);
        this.devilHorn = devilHorn;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.devilHorn.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticDevilHornData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, -0.45f, 0.0f);
        ModelRenderer devilHorn = this.bindTextureAndColor(cosmeticData.getColor(), ModTextures.COSMETIC_DEVIL_HORNS, this.devilHorn);
        devilHorn.isHidden = false;
        int j2 = 0;
        while (j2 < 2) {
            devilHorn.render(scale);
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            ++j2;
        }
        devilHorn.isHidden = true;
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 12;
    }

    @Override
    public String getCosmeticName() {
        return "Devil Horn";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.2f;
    }

    public static class CosmeticDevilHornData
    extends CosmeticData {
        private Color color = Color.RED;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.color = Color.decode("#" + data[0]);
        }

        public Color getColor() {
            return this.color;
        }
    }
}

