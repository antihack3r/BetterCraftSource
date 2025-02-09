/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.loader.model;

import java.beans.ConstructorProperties;

public class MCPack {
    public int pack_format;
    public String description;

    @ConstructorProperties(value={"pack_format", "description"})
    public MCPack(int pack_format, String description) {
        this.pack_format = pack_format;
        this.description = description;
    }
}

