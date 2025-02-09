/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.custom;

import java.util.UUID;
import net.labymod.user.cosmetic.custom.CosmeticImageHandler;

public class UserTextureContainer {
    private String directory;
    private UUID fileName;
    private final boolean globalTexture;
    private String resolvedURL = null;
    private boolean loaded = false;

    public UserTextureContainer(String directory) {
        this(directory, null);
    }

    public UserTextureContainer(String directory, UUID fileName) {
        this.directory = directory;
        this.fileName = fileName;
        this.globalTexture = fileName == null;
    }

    public void validateTexture(CosmeticImageHandler handler) {
        if (this.loaded || this.resolvedURL == null) {
            return;
        }
        this.loaded = true;
        handler.loadUserTexture(this.fileName, this.resolvedURL);
    }

    public void unload() {
        this.loaded = false;
    }

    public void resolved() {
        if (this.fileName != null) {
            this.resolvedURL = String.format("http://dl.labymod.net/textures/%s", String.valueOf(this.directory) + "/" + this.fileName.toString());
            this.unload();
        }
    }

    public boolean isGlobalTexture() {
        return this.globalTexture;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setFileName(UUID fileName) {
        this.fileName = fileName;
    }

    public UUID getFileName() {
        return this.fileName;
    }

    public String getResolvedURL() {
        return this.resolvedURL;
    }

    public boolean isLoaded() {
        return this.loaded;
    }
}

