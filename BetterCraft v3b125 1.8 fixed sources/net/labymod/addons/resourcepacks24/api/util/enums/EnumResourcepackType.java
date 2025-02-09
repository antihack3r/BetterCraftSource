/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.api.util.enums;

public enum EnumResourcepackType {
    NEW("new"),
    RANDOM("random");

    private String key;

    public String getKey() {
        return this.key;
    }

    private EnumResourcepackType(String key) {
        this.key = key;
    }
}

