/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.shop.body;

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

public class CosmeticTool
extends CosmeticRenderer<CosmeticToolData> {
    public static final int ID = 8;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticToolData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        if (cosmeticData.getItemStack() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            if (entityIn.isSneaking()) {
                GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
            }
            GlStateManager.scale(0.8, 0.8, 0.8);
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(0.0, 0.3, -0.22);
            Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn, cosmeticData.getItemStack(), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public int getCosmeticId() {
        return 8;
    }

    @Override
    public String getCosmeticName() {
        return "Tool";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticToolData
    extends CosmeticData {
        private ItemStack itemStack;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            int mode = Integer.valueOf(data[0]);
            int itemId = Integer.valueOf(data[1]);
            switch (mode) {
                case 0: {
                    this.itemStack = new ItemStack(Item.getItemById(itemId));
                }
            }
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }
}

