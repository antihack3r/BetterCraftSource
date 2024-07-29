/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class BandanaImageHandler
extends CosmeticImageHandler {
    public BandanaImageHandler(String userAgent) {
        super(userAgent, "labymodbandana", true);
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getBandanaContainer();
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

