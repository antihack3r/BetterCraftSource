/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class CoverMaskImageHandler
extends CosmeticImageHandler {
    public CoverMaskImageHandler(String userAgent) {
        super(userAgent, "labymodcovermask", false);
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getCoverMaskContainer();
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

