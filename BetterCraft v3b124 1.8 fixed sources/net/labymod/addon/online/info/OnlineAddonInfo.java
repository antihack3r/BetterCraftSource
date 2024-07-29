/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addon.online.info;

import java.io.File;
import java.util.UUID;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.info.AddonInfo;

public class OnlineAddonInfo
extends AddonInfo {
    protected String hash;
    protected boolean enabled;
    protected boolean includeInJar;
    protected boolean restart;
    protected boolean verified;
    protected int[] sorting;

    public OnlineAddonInfo(UUID uuid, String name, int version, String hash, String author, String description, int category, boolean enabled, boolean includeInJar, boolean restart, boolean verified, int[] sorting) {
        super(uuid, name, version, author, description, category);
        this.hash = hash;
        this.enabled = enabled;
        this.includeInJar = includeInJar;
        this.restart = restart;
        this.verified = verified;
        this.sorting = sorting;
    }

    @Override
    public String getDownloadURL() {
        return String.format("http://dl.labymod.net/latest/?file=%s&a=1", this.uuid);
    }

    @Override
    public String getImageURL() {
        return String.format("http://dl.labymod.net/latest/addons/%s/icon.png", this.uuid);
    }

    @Override
    public File getFileDestination() {
        return new File(AddonLoader.getAddonsDirectory(), String.valueOf(this.name) + ".jar");
    }

    public String getHash() {
        return this.hash;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isIncludeInJar() {
        return this.includeInJar;
    }

    public boolean isRestart() {
        return this.restart;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public int[] getSorting() {
        return this.sorting;
    }

    public static enum AddonActionState {
        INSTALL,
        INSTALL_REVOKE,
        UNINSTALL,
        UNINSTALL_REVOKE,
        ERROR;

    }
}

