/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.texture;

import java.beans.ConstructorProperties;
import net.minecraft.util.ResourceLocation;

public class DynamicModTexture {
    private ResourceLocation resourceLocation;
    private String url;

    @ConstructorProperties(value={"resourceLocation", "url"})
    public DynamicModTexture(ResourceLocation resourceLocation, String url) {
        this.resourceLocation = resourceLocation;
        this.url = url;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

