/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom.handler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

public class CloakImageHandler
extends CosmeticImageHandler {
    private static final ThreadDownloadTextureImage.TextureImageParser TEXTURE_IMAGE_PARSER = new ThreadDownloadTextureImage.TextureImageParser(){

        @Override
        public BufferedImage parse(BufferedImage input) {
            int imageWidth = 64;
            int imageHeight = 32;
            BufferedImage srcImg = input;
            int srcWidth = srcImg.getWidth();
            int srcHeight = srcImg.getHeight();
            while (imageWidth < srcWidth || imageHeight < srcHeight) {
                imageWidth *= 2;
                imageHeight *= 2;
            }
            BufferedImage imgNew = new BufferedImage(imageWidth, imageHeight, 2);
            Graphics g2 = imgNew.getGraphics();
            g2.drawImage(input, 0, 0, null);
            g2.dispose();
            return imgNew;
        }
    };
    private EnumCapePriority priority;

    public CloakImageHandler(String userAgent) {
        super(userAgent, "labymodcloak", true);
        try {
            this.priority = EnumCapePriority.valueOf(LabyMod.getSettings().capePriority);
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public UserTextureContainer getContainer(User user) {
        return user.getCloakContainer();
    }

    public boolean canRenderMojangCape(User user, AbstractClientPlayer player) {
        boolean canRenderMojangCape;
        boolean enabled;
        NetHandlerPlayClient netHandlerPlayClient = LabyModCore.getMinecraft().getConnection();
        NetworkPlayerInfo networkPlayerInfo = netHandlerPlayClient != null ? netHandlerPlayClient.getPlayerInfo(player.getUniqueID()) : null;
        ResourceLocation locationOptifine = player.getLocationCape();
        ResourceLocation locationMinecon = networkPlayerInfo == null ? null : networkPlayerInfo.getLocationCape();
        ResourceLocation locationLabyMod = (ResourceLocation)this.resourceLocations.get(user.getCloakContainer().getFileName());
        user.setMojangCapeModified(locationMinecon == null || !locationMinecon.equals(locationOptifine));
        boolean bl2 = enabled = LabyMod.getSettings().cosmetics && LabyMod.getSettings().cosmeticsCustomTextures;
        if (enabled && locationLabyMod == null) {
            user.getCloakContainer().validateTexture(this);
        }
        boolean bl3 = canRenderMojangCape = locationLabyMod == null || !enabled;
        if (this.priority != EnumCapePriority.LABYMOD && (locationMinecon != null || locationOptifine != null)) {
            canRenderMojangCape = true;
        }
        return canRenderMojangCape;
    }

    @Override
    public void unload() {
    }

    @Override
    public ThreadDownloadTextureImage.TextureImageParser geTextureImageParser() {
        return TEXTURE_IMAGE_PARSER;
    }

    public void setPriority(EnumCapePriority priority) {
        this.priority = priority;
    }

    public static enum EnumCapePriority {
        LABYMOD,
        OTHER;

    }
}

