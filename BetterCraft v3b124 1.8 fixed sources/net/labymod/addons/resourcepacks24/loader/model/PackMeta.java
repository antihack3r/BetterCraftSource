/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.loader.model;

import java.io.File;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.addons.resourcepacks24.loader.model.MCPack;
import net.minecraft.util.ResourceLocation;

public class PackMeta {
    public File file;
    public MCPack pack;
    public String displayName;
    public ResourceLocation icon = PackElement.DEFAULT_ICON;

    public PackMeta(MCPack pack) {
        this.pack = pack;
    }

    public PackMeta(File file, MCPack pack, String displayName) {
        this.file = file;
        this.pack = pack;
        this.displayName = displayName;
    }

    public String getFileName() {
        return this.file.getName();
    }
}

