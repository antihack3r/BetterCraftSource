/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class AngelWingsImageHandler
extends CosmeticImageHandler {
    public AngelWingsImageHandler(String userAgent) {
        super(userAgent, "labymodangelwings", false);
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getAngelWingsContainer();
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

