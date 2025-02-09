/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic;

import java.awt.Color;
import net.labymod.user.cosmetic.ModelCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class CosmeticRenderer<CosmeticData> {
    public abstract int getCosmeticId();

    public abstract String getCosmeticName();

    public abstract boolean isOfflineAvailable();

    public abstract void addModels(ModelCosmetics var1, float var2);

    public abstract void setInvisible(boolean var1);

    public abstract void render(ModelCosmetics var1, Entity var2, CosmeticData var3, float var4, float var5, float var6, float var7, float var8, float var9, boolean var10);

    public float getNameTagHeight() {
        return 0.0f;
    }

    public void setRotationAngles(ModelCosmetics modelCosmetics, float movementFactor, float walkingSpeed, float tickValue, float var4, float var5, float var6, Entity entityIn) {
    }

    public void onTick() {
    }

    protected ModelRenderer bindTextureAndColor(Color color, ResourceLocation resourceLocation, ModelRenderer model) {
        return this.bindTextureAndColor(color == null ? 0 : color.getRGB(), resourceLocation, model);
    }

    protected ModelRenderer bindTextureAndColor(int color, ResourceLocation resourceLocation, ModelRenderer model) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color >> 0 & 0xFF;
        GL11.glColor4f((float)red / 255.0f, (float)green / 255.0f, (float)blue / 255.0f, 1.0f);
        if (resourceLocation != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        }
        return model;
    }
}

