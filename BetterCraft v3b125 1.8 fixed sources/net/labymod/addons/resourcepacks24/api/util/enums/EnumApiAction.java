/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.api.util.enums;

public enum EnumApiAction {
    SEARCH("search"),
    DOWNLOAD("download"),
    FEED("feed"),
    RESOURCEPACKS("resourcepacks"),
    RESOURCEPACK("resourcepack"),
    CATEGORY("category");

    private String key;

    public String getKey() {
        return this.key;
    }

    private EnumApiAction(String key) {
        this.key = key;
    }
}

