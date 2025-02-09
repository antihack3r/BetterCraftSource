/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface RenderAdapter {
    public ResourceLocation getOptionsBackground();

    public ResourceLocation getInventoryBackground();

    public ResourceLocation getButtonsTexture();

    public ResourceLocation getIcons();

    public void drawActivePotionEffects(double var1, double var3, ResourceLocation var5);

    public void cullFaceBack();

    public void cullFaceFront();

    public void renderItemIntoGUI(ItemStack var1, double var2, double var4);

    public void renderItemOverlayIntoGUI(ItemStack var1, double var2, double var4, String var6);

    public void renderEntity(RenderManager var1, Entity var2, double var3, double var5, double var7, float var9, float var10, boolean var11);
}

