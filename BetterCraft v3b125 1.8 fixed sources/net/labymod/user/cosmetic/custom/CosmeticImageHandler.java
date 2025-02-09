/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.labymod.main.LabyMod;
import net.labymod.support.util.Debug;
import net.labymod.user.User;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.utils.Consumer;
import net.labymod.utils.texture.ThreadDownloadTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public abstract class CosmeticImageHandler {
    protected Map<UUID, ResourceLocation> resourceLocations = new HashMap<UUID, ResourceLocation>();
    private final String userAgent;
    private final String resourceName;
    private final boolean canUnload;

    public CosmeticImageHandler(String userAgent, String resourceName, boolean canUnload) {
        this.userAgent = userAgent;
        this.resourceName = resourceName;
        this.canUnload = canUnload;
    }

    public void loadUserTexture(final UUID uuid, String url) {
        if (url == null || this.resourceLocations.containsKey(uuid) && !this.canUnload) {
            return;
        }
        this.resolveImageTexture(url, new Consumer<ResourceLocation>(){

            @Override
            public void accept(ResourceLocation accepted) {
                if (accepted != null) {
                    CosmeticImageHandler.this.resourceLocations.put(uuid, accepted);
                }
            }
        });
    }

    private void resolveImageTexture(String url, final Consumer<ResourceLocation> callback) {
        final ResourceLocation cosmeticResourceLocation = new ResourceLocation(String.valueOf(this.resourceName) + "/" + FilenameUtils.getBaseName(url));
        ThreadDownloadTextureImage textureCosmetic = new ThreadDownloadTextureImage(url, cosmeticResourceLocation, new Consumer<Boolean>(){

            @Override
            public void accept(Boolean success) {
                callback.accept(cosmeticResourceLocation);
            }
        }, this.userAgent);
        textureCosmetic.setTextureImageParser(this.geTextureImageParser());
        textureCosmetic.setDebugMode(Debug.EnumDebugMode.COSMETIC_IMAGE_MANAGER);
        Debug.log(Debug.EnumDebugMode.COSMETIC_IMAGE_MANAGER, "Load " + cosmeticResourceLocation.getResourcePath());
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.loadTexture(cosmeticResourceLocation, textureCosmetic);
    }

    public ResourceLocation getResourceLocation(AbstractClientPlayer player) {
        User user = LabyMod.getInstance().getUserManager().getUser(player.getUniqueID());
        UserTextureContainer container = this.getContainer(user);
        if (container == null) {
            return null;
        }
        container.validateTexture(this);
        return this.resourceLocations.get(container.getFileName());
    }

    public abstract UserTextureContainer getContainer(User var1);

    public void validate(User user) {
        UserTextureContainer container = this.getContainer(user);
        if (container != null) {
            container.validateTexture(this);
        }
    }

    public abstract void unload();

    public abstract ThreadDownloadTextureImage.TextureImageParser geTextureImageParser();

    public Map<UUID, ResourceLocation> getResourceLocations() {
        return this.resourceLocations;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public boolean isCanUnload() {
        return this.canUnload;
    }
}

