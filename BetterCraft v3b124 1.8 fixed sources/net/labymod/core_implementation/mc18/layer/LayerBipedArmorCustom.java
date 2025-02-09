/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.layer;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LayerBipedArmorCustom
extends LayerBipedArmor {
    private boolean swapped = false;

    public LayerBipedArmorCustom(RendererLivingEntity<?> rendererIn) {
        super(rendererIn);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float var1, float var2, float partialTicks, float var4, float var5, float var6, float scale) {
        int itemId;
        boolean swap = LabyMod.getSettings().leftHand;
        ItemStack itemStack = LabyModCore.getMinecraft().getMainHandItem();
        int n2 = itemId = itemStack != null && itemStack.getItem() != null ? Item.getIdFromItem(itemStack.getItem()) : 0;
        if (LabyMod.getSettings().swapBow && itemId == 261) {
            boolean bl2 = swap = !swap;
        }
        if (swap && LabyModCore.getMinecraft().getItemInUseMaxCount() != 0 && itemId == 261) {
            swap = false;
        }
        if (swap) {
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
        super.doRenderLayer(entity, var1, var2, partialTicks, var4, var5, var6, scale);
        if (swap) {
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
        if (this.swapped) {
            this.swapped = false;
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
    }

    public ModelBiped func_177175_a(int slot) {
        int itemId;
        boolean swap = LabyMod.getSettings().leftHand;
        ItemStack itemStack = LabyModCore.getMinecraft().getMainHandItem();
        int n2 = itemId = itemStack != null && itemStack.getItem() != null ? Item.getIdFromItem(itemStack.getItem()) : 0;
        if (LabyMod.getSettings().swapBow && itemId == 261) {
            boolean bl2 = swap = !swap;
        }
        if (swap && LabyModCore.getMinecraft().getItemInUseMaxCount() != 0 && itemId == 261) {
            swap = false;
        }
        if (this.swapped) {
            this.swapped = false;
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
        if (slot == 3 && swap) {
            this.swapped = true;
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            GlStateManager.disableCull();
        }
        return (ModelBiped)super.getArmorModel(slot);
    }

    @Override
    public boolean shouldCombineTextures() {
        return LabyMod.getSettings().oldDamage;
    }
}

