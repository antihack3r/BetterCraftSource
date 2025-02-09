/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.mojang.inventory.scale;

public enum EnumGuiScale {
    DEFAULT("Default"),
    SMALL("Small"),
    NORMAL("Normal"),
    LARGE("Large"),
    AUTO("Auto");

    private String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    private EnumGuiScale(String displayName) {
        this.displayName = displayName;
    }
}

