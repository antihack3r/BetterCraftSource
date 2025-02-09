/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.api.util.enums;

public enum EnumFeedType {
    TRENDING("trending"),
    RESOURCEPACK_OF_THE_WEEK("resourcepack-of-the-week"),
    PROMOTION("promotion");

    private String key;

    public String getKey() {
        return this.key;
    }

    private EnumFeedType(String key) {
        this.key = key;
    }
}

