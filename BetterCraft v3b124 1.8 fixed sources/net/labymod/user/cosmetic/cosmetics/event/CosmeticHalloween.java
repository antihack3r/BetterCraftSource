/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.cosmetics.event;

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

public class CosmeticHalloween
extends CosmeticRenderer<CosmeticHalloweenData> {
    public static final int ID = 15;

    @Override
    public void addModels(ModelCosmetics modelCosmetics, float modelSize) {
    }

    @Override
    public void setInvisible(boolean invisible) {
    }

    @Override
    public void render(ModelCosmetics modelCosmetics, Entity entityIn, CosmeticHalloweenData cosmeticData, float scale, float movementFactor, float walkingSpeed, float tickValue, float firstRotationX, float secondRotationX, boolean canAnimate) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.rotate(firstRotationX, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(secondRotationX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.8, 0.8, 0.8);
        ItemStack item = null;
        int itemId = 0;
        switch (cosmeticData.getEnumHalloweenType()) {
            case AXE_RIGHT: {
                itemId = 258;
                GlStateManager.rotate(-120.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(40.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(0.4, -0.4, 0.3);
                break;
            }
            case PICKAXE_TOP: {
                itemId = 257;
                GlStateManager.rotate(-30.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(-30.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(0.2, -0.8, -0.22);
                break;
            }
            case AXE_LEFT: {
                itemId = 258;
                GlStateManager.rotate(70.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(-30.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(-0.3, -0.7, -0.02);
                break;
            }
            case AXE_TOP: {
                itemId = 292;
                GlStateManager.rotate(-50.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(70.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.translate(0.2, -0.6, 0.4);
                break;
            }
            case ARROW_UPPER_LEFT_BACK_CORNER: {
                itemId = 262;
                GlStateManager.rotate(-180.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(-100.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(30.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translate(0.1, 0.3, -0.5);
                break;
            }
            case ARROW_DIAGONALLY: {
                itemId = 262;
                GlStateManager.rotate(-50.0f, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(0.1, -0.1, 0.0);
                GlStateManager.translate(-0.19, -0.19, 0.0);
                item = new ItemStack(Item.getItemById(itemId));
                if (item != null && item.getItem() != null) {
                    Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn, item, ItemCameraTransforms.TransformType.NONE);
                }
                GlStateManager.translate(0.38, 0.38, 0.0);
            }
        }
        if (itemId != 0) {
            if (item == null) {
                item = new ItemStack(Item.getItemById(itemId));
            }
            if (item != null && item.getItem() != null) {
                Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn, item, ItemCameraTransforms.TransformType.NONE);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public int getCosmeticId() {
        return 15;
    }

    @Override
    public String getCosmeticName() {
        return "Halloween";
    }

    @Override
    public boolean isOfflineAvailable() {
        return false;
    }

    public static class CosmeticHalloweenData
    extends CosmeticData {
        private EnumHalloweenType enumHalloweenType = EnumHalloweenType.AXE_RIGHT;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void loadData(String[] data) throws Exception {
            this.enumHalloweenType = EnumHalloweenType.values()[Integer.parseInt(data[0])];
        }

        public EnumHalloweenType getEnumHalloweenType() {
            return this.enumHalloweenType;
        }

        public static enum EnumHalloweenType {
            AXE_RIGHT,
            PICKAXE_TOP,
            AXE_LEFT,
            AXE_TOP,
            ARROW_UPPER_LEFT_BACK_CORNER,
            ARROW_DIAGONALLY;

        }
    }
}

