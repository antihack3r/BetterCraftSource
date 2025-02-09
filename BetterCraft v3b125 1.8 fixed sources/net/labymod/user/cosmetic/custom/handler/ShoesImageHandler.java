/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class ShoesImageHandler
extends CosmeticImageHandler {
    public ShoesImageHandler(String userAgent) {
        super(userAgent, "labymodshoes", false);
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getShoesContainer();
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

