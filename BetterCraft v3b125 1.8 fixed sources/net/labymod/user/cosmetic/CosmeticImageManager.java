/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;
import net.labymod.user.cosmetic.custom.handler.AngelWingsImageHandler;
import net.labymod.user.cosmetic.custom.handler.BandanaImageHandler;
import net.labymod.user.cosmetic.custom.handler.CloakImageHandler;
import net.labymod.user.cosmetic.custom.handler.CoverMaskImageHandler;
import net.labymod.user.cosmetic.custom.handler.KawaiiMaskImageHandler;
import net.labymod.user.cosmetic.custom.handler.ShoesImageHandler;
import net.labymod.user.cosmetic.custom.handler.StickerImageHandler;
import net.labymod.user.cosmetic.custom.handler.WatchImageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CosmeticImageManager {
    private List<CosmeticImageHandler> cosmeticImageHandlers = new ArrayList<CosmeticImageHandler>();
    private final CloakImageHandler cloakImageHandler;
    private final BandanaImageHandler bandanaImageHandler;
    private final ShoesImageHandler shoesImageHandler;
    private final StickerImageHandler stickerImageHandler;
    private final KawaiiMaskImageHandler kawaiiMaskImageHandler;
    private final CoverMaskImageHandler coverMaskImageHandler;
    private final WatchImageHandler watchImageHandler;
    private final AngelWingsImageHandler angelWingsImageHandler;

    public CosmeticImageManager(String userAgent) {
        this.cloakImageHandler = new CloakImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.cloakImageHandler);
        this.bandanaImageHandler = new BandanaImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.bandanaImageHandler);
        this.shoesImageHandler = new ShoesImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.shoesImageHandler);
        this.stickerImageHandler = new StickerImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.stickerImageHandler);
        this.kawaiiMaskImageHandler = new KawaiiMaskImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.kawaiiMaskImageHandler);
        this.coverMaskImageHandler = new CoverMaskImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.coverMaskImageHandler);
        this.watchImageHandler = new WatchImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.watchImageHandler);
        this.angelWingsImageHandler = new AngelWingsImageHandler(userAgent);
        this.cosmeticImageHandlers.add(this.angelWingsImageHandler);
    }

    public void loadPlayersInView() {
        if (!LabyMod.getInstance().isInGame()) {
            return;
        }
        for (EntityPlayer player : LabyModCore.getMinecraft().getWorld().playerEntities) {
            if (player.equals(LabyModCore.getMinecraft().getPlayer())) continue;
            User user = LabyMod.getInstance().getUserManager().getUser(player.getUniqueID());
            for (CosmeticImageHandler handler : this.cosmeticImageHandlers) {
                handler.validate(user);
            }
        }
    }

    public void unloadUnusedTextures(boolean forceNotIngame, boolean forceSelf) {
        for (CosmeticImageHandler handler : this.cosmeticImageHandlers) {
            if (!handler.isCanUnload() && !forceSelf) continue;
            try {
                ArrayList<UUID> toRemove = new ArrayList<UUID>(handler.getResourceLocations().keySet());
                if (LabyMod.getInstance().isInGame() && !forceNotIngame) {
                    for (EntityPlayer player : LabyModCore.getMinecraft().getWorld().playerEntities) {
                        UUID uuid = player.getUniqueID();
                        if (!toRemove.contains(uuid)) continue;
                        toRemove.remove(uuid);
                    }
                }
                int noImage = 0;
                for (UUID rem : toRemove) {
                    if (rem.equals(LabyMod.getInstance().getPlayerUUID()) && !forceSelf) continue;
                    ResourceLocation resourceLocation = handler.getResourceLocations().get(rem);
                    if (resourceLocation == null || !resourceLocation.getResourcePath().startsWith(String.valueOf(handler.getResourceName()) + "/")) {
                        ++noImage;
                    } else {
                        Minecraft.getMinecraft().getTextureManager().deleteTexture(resourceLocation);
                        Debug.log(Debug.EnumDebugMode.COSMETIC_IMAGE_MANAGER, "Unloaded " + resourceLocation.getResourcePath());
                    }
                    handler.getResourceLocations().remove(rem);
                    User user = LabyMod.getInstance().getUserManager().getUser(rem);
                    if (user == null) continue;
                    user.unloadCosmeticTextures();
                }
                handler.unload();
                Debug.log(Debug.EnumDebugMode.COSMETIC_IMAGE_MANAGER, "Unloaded " + toRemove.size() + " unused " + handler.getResourceName() + " and " + noImage + " had no labymod textures! " + handler.getResourceLocations().size() + " " + handler.getResourceName() + " left.");
            }
            catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public CloakImageHandler getCloakImageHandler() {
        return this.cloakImageHandler;
    }

    public BandanaImageHandler getBandanaImageHandler() {
        return this.bandanaImageHandler;
    }

    public ShoesImageHandler getShoesImageHandler() {
        return this.shoesImageHandler;
    }

    public StickerImageHandler getStickerImageHandler() {
        return this.stickerImageHandler;
    }

    public KawaiiMaskImageHandler getKawaiiMaskImageHandler() {
        return this.kawaiiMaskImageHandler;
    }

    public CoverMaskImageHandler getCoverMaskImageHandler() {
        return this.coverMaskImageHandler;
    }

    public WatchImageHandler getWatchImageHandler() {
        return this.watchImageHandler;
    }

    public AngelWingsImageHandler getAngelWingsImageHandler() {
        return this.angelWingsImageHandler;
    }
}

