/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.texture;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;
import net.labymod.core.LabyModCore;
import net.labymod.main.ModTextures;
import net.labymod.utils.Consumer;
import net.labymod.utils.ReflectionHelper;
import net.labymod.utils.texture.DynamicModTexture;
import net.labymod.utils.texture.ThreadDownloadTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class DynamicTextureManager {
    protected final Map<String, DynamicModTexture> resourceLocations = new HashMap<String, DynamicModTexture>();
    private final String resourceName;
    private final ResourceLocation defaultTexture;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36";
    private SSLSocketFactory socketFactory = null;
    private ThreadDownloadTextureImage.TextureImageParser textureImageParser = null;
    private Map<ResourceLocation, ITextureObject> mapTextureObjects = new HashMap<ResourceLocation, ITextureObject>();

    public DynamicTextureManager(String resourceName, ResourceLocation defaultTexture) {
        this.resourceName = resourceName;
        this.defaultTexture = defaultTexture;
    }

    public void init() {
        try {
            Field field = ReflectionHelper.findField(TextureManager.class, LabyModCore.getMappingAdapter().getMapTextureObjects());
            field.setAccessible(true);
            this.mapTextureObjects = (Map)field.get(Minecraft.getMinecraft().getTextureManager());
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Deprecated
    public ResourceLocation getHeadTexture(GameProfile gameProfile) {
        return ModTextures.MISC_HEAD_QUESTION;
    }

    public ResourceLocation getTexture(String identifier, String url) {
        boolean unloadImage;
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        DynamicModTexture dynamicModTexture = this.resourceLocations.get(identifier);
        boolean bl2 = unloadImage = dynamicModTexture != null && dynamicModTexture.getUrl() != null && !dynamicModTexture.getUrl().equals(url);
        if (dynamicModTexture == null || unloadImage) {
            if (unloadImage) {
                textureManager.deleteTexture(dynamicModTexture.getResourceLocation());
                dynamicModTexture.setResourceLocation(this.defaultTexture);
                dynamicModTexture.setUrl(null);
            } else {
                dynamicModTexture = new DynamicModTexture(this.defaultTexture, null);
            }
            this.resourceLocations.put(identifier, dynamicModTexture);
            this.resolveImageTexture(identifier, url);
        }
        return dynamicModTexture.getResourceLocation();
    }

    private void resolveImageTexture(final String identifier, final String url) {
        if (identifier == null || url == null) {
            return;
        }
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        final ResourceLocation resourceLocation = new ResourceLocation(String.valueOf(this.resourceName) + "/" + this.getHash(url));
        ThreadDownloadTextureImage threadDownloadImageData = new ThreadDownloadTextureImage(url, resourceLocation, new Consumer<Boolean>(){

            @Override
            public void accept(Boolean accepted) {
                DynamicTextureManager.this.resourceLocations.put(identifier, new DynamicModTexture(resourceLocation, url));
            }
        }, this.userAgent);
        threadDownloadImageData.setSocketFactory(this.socketFactory);
        threadDownloadImageData.setTextureImageParser(this.textureImageParser);
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
    }

    public void unloadAll() {
        Minecraft.getMinecraft().addScheduledTask(new Runnable(){

            @Override
            public void run() {
                try {
                    for (Map.Entry<String, DynamicModTexture> entry : DynamicTextureManager.this.resourceLocations.entrySet()) {
                        ResourceLocation resourceLocation = entry.getValue().getResourceLocation();
                        DynamicTextureManager.this.mapTextureObjects.remove(resourceLocation);
                    }
                    DynamicTextureManager.this.resourceLocations.clear();
                }
                catch (Exception error) {
                    error.printStackTrace();
                }
            }
        });
    }

    private int getHash(String url) {
        int hash = 7;
        int i2 = 0;
        while (i2 < url.length()) {
            hash = hash * 31 + url.charAt(i2);
            ++i2;
        }
        return hash;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public ResourceLocation getDefaultTexture() {
        return this.defaultTexture;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public SSLSocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    public void setSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    public ThreadDownloadTextureImage.TextureImageParser getTextureImageParser() {
        return this.textureImageParser;
    }

    public void setTextureImageParser(ThreadDownloadTextureImage.TextureImageParser textureImageParser) {
        this.textureImageParser = textureImageParser;
    }

    public Map<String, DynamicModTexture> getResourceLocations() {
        return this.resourceLocations;
    }

    public Map<ResourceLocation, ITextureObject> getMapTextureObjects() {
        return this.mapTextureObjects;
    }
}

