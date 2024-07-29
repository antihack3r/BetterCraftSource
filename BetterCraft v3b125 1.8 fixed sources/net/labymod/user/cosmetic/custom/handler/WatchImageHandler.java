/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class WatchImageHandler
extends CosmeticImageHandler {
    public WatchImageHandler(String userAgent) {
        super(userAgent, "labymodwatches", false);
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getWatchContainer();
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

