/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils;

import com.mojang.authlib.GameProfile;
import net.labymod.main.LabyMod;
import net.minecraft.util.ResourceLocation;

@Deprecated
public class ModTextureUtils {
    @Deprecated
    public static ModTextureUtils INSTANCE = new ModTextureUtils();

    @Deprecated
    public ResourceLocation getSkinTexture(GameProfile gameProfile) {
        return LabyMod.getInstance().getDynamicTextureManager().getHeadTexture(gameProfile);
    }

    @Deprecated
    public void drawImageURL(String username, String url, double x2, double y2, double imageWidth, double imageHeight, double maxWidth, double maxHeight) {
        LabyMod.getInstance().getDrawUtils().drawImageUrl(url, x2, y2, imageWidth, imageHeight, maxWidth, maxHeight);
    }
}

