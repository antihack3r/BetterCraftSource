/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

import net.labymod.core.LabyModCore;
import net.labymod.main.ModTextures;
import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CosmeticAntlers
extends CosmeticRenderer<CosmeticAntlersData> {
    public static final int ID = 10;
    private ModelRenderer antler;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
        this.antler = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        this.antler.setRotationPoint(-3.0f, -7.5f, -1.0f);
        this.antler.addBox(0.0f, 0.0f, 0.0f, 1, 5, 1);
        this.antler.rotateAngleX = (float)Math.PI;
        this.antler.rotateAngleZ = -0.8f;
        ModelRenderer firstTip = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        firstTip.setRotationPoint(0.0f, 5.0f, 0.0f);
        firstTip.addBox(0.0f, 0.0f, 0.0f, 1, 5, 1);
        firstTip.rotateAngleZ = -0.6f;
        firstTip.rotateAngleX = -0.2f;
        this.antler.addChild(firstTip);
        ModelRenderer secondTip = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        secondTip.setRotationPoint(0.0f, 3.0f, 0.0f);
        secondTip.addBox(0.0f, 0.0f, 0.0f, 1, 5, 1);
        secondTip.rotateAngleZ = 0.2f;
        secondTip.rotateAngleX = 0.4f;
        this.antler.addChild(secondTip);
        ModelRenderer firstHook = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        firstHook.setRotationPoint(0.0f, 5.0f, 0.0f);
        firstHook.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        firstHook.rotateAngleZ = -0.8f;
        firstHook.rotateAngleX = 0.4f;
        firstTip.addChild(firstHook);
        ModelRenderer secondHook = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        secondHook.setRotationPoint(0.0f, 5.0f, 0.0f);
        secondHook.addBox(0.0f, 0.0f, 0.0f, 1, 3, 1);
        secondHook.rotateAngleZ = -0.8f;
        secondHook.rotateAngleX = -0.4f;
        secondTip.addChild(secondHook);
        ModelRenderer mainHook = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        mainHook.setRotationPoint(0.0f, 2.0f, 0.0f);
        mainHook.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        mainHook.rotateAngleZ = -0.6f;
        mainHook.rotateAngleX = -0.2f;
        this.antler.addChild(mainHook);
        ModelRenderer besideHook = new ModelRenderer(modelCosmetics).setTextureSize(4, 8);
        besideHook.setRotationPoint(0.0f, 2.0f, 0.0f);
        besideHook.addBox(0.0f, 0.0f, 0.0f, 1, 2, 1);
        besideHook.rotateAngleZ = 0.6f;
        besideHook.rotateAngleX = 0.2f;
        secondTip.addChild(besideHook);
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.antler.showModel = invisible;
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticAntlersData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        ModelRenderer antler = this.bindTextureAndColor(null, ModTextures.COSMETIC_ANTLER, this.antler);
        int i2 = 0;
        while (i2 < 2) {
            GlStateManager.enableCull();
            antler.isHidden = false;
            antler.render(scale);
            antler.isHidden = true;
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            if (i2 == 0) {
                LabyModCore.getRenderImplementation().cullFaceFront();
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
        return 10;
    }

    @Override
    public String getCosmeticName() {
        return "Antlers";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    @Override
    public float getNameTagHeight() {
        return 0.5f;
    }

    public static class CosmeticAntlersData
    extends CosmeticData {
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) {
        }
    }
}

