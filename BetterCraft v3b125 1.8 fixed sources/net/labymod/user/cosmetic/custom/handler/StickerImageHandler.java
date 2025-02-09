/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;

public class StickerImageHandler
extends CosmeticImageHandler {
    private Map<Short, UserTextureContainer> userTextureContainers = new HashMap<Short, UserTextureContainer>();

    public StickerImageHandler(String userAgent) {
        super(userAgent, "labymodsticker", false);
    }

    public UserTextureContainer getContainer(User user, short stickerId) {
        UserTextureContainer container = this.userTextureContainers.get(stickerId);
        if (container != null) {
            return container;
        }
        container = new UserTextureContainer("sticker", new UUID(stickerId, 0L));
        container.resolved();
        this.userTextureContainers.put(stickerId, container);
        return container;
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        short playingSticker = user.getPlayingSticker();
        if (playingSticker == -1) {
            return null;
        }
        return this.getContainer(user, playingSticker);
    }

    @Override
    public void unload() {
        this.userTextureContainers.clear();
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return null;
    }
}

