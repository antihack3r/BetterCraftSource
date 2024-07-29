/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods;

public enum ModType {
    RENDER("Render"),
    INGAME("Ingame");

    private String name;

    private ModType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

