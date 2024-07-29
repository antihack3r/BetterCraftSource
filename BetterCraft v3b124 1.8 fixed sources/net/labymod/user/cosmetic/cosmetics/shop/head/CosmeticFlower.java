/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.head;

import net.labymod.user.cosmetic.CosmeticRenderer;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CosmeticFlower
extends CosmeticRenderer<CosmeticFlowerData> {
    public static final int ID = 25;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticFlowerData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        if (cosmeticData.getItemStack() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            if (entityIn.isSneaking()) {
                GlStateManager.translate(0.0f, 0.07f, 0.0f);
            }
            GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(0.4, 0.4, 0.4);
            GlStateManager.translate(0.2, 0.9, 0.72);
            GlStateManager.rotate(70.0f, 0.0f, 0.0f, -1.0f);
            GlStateManager.rotate(20.0f, 1.0f, 1.0f, 1.0f);
            int i2 = 0;
            while (i2 <= 1) {
                Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn, cosmeticData.getItemStack(), ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
                ++i2;
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public int getCosmeticId() {
        return 25;
    }

    @Override
    public String getCosmeticName() {
        return "Flower";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticFlowerData
    extends CosmeticData {
        private ItemStack itemStack;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            int itemId = Integer.valueOf(data[0]);
            this.itemStack = new ItemStack(Item.getItemById(38), 1, itemId);
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }
}

