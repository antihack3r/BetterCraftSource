/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24;

import java.io.File;
import java.util.List;
import net.labymod.addons.resourcepacks24.api.Resourcepacks24Api;
import net.labymod.addons.resourcepacks24.loader.PackRepositoryLoader;
import net.labymod.settings.elements.SettingsElement;

public class Resourcepacks24 {
    private static final Resourcepacks24 INSTANCE = new Resourcepacks24();
    public final File resourcepacksDir = new File("resourcepacks");
    private Resourcepacks24Api rp24Api = new Resourcepacks24Api("6b514bb5-cb55-4f68-8c62-3031cf871a72");
    private PackRepositoryLoader packLoader = new PackRepositoryLoader(this.resourcepacksDir);

    public static Resourcepacks24 getInstance() {
        return INSTANCE;
    }

    public void onEnable() {
    }

    public void loadConfig() {
    }

    protected void fillSettings(List<SettingsElement> subSettings) {
    }

    public Resourcepacks24Api getRp24Api() {
        return this.rp24Api;
    }

    public PackRepositoryLoader getPackLoader() {
        return this.packLoader;
    }
}

