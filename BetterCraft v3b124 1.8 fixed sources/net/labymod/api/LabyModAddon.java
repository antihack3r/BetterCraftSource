/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.labymod.addon.About;
import net.labymod.addon.AddonConfig;
import net.labymod.addon.AddonLoader;
import net.labymod.api.LabyModAPI;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.manager.ConfigManager;

public abstract class LabyModAddon {
    private List<SettingsElement> subSettings = new ArrayList<SettingsElement>();
    private JsonObject config;
    private ConfigManager<AddonConfig> configManager;
    public About about;
    public LabyModAPI api;

    public abstract void onEnable();

    @Deprecated
    public void onDisable() {
    }

    public abstract void loadConfig();

    protected abstract void fillSettings(List<SettingsElement> var1);

    public void init(String addonName, UUID uuid) {
        this.about = new About(uuid, addonName);
        this.configManager = new ConfigManager<AddonConfig>(new File(AddonLoader.getConfigDirectory(), String.valueOf(addonName) + ".json"), AddonConfig.class);
        this.config = this.configManager.getSettings().getConfig();
        this.loadConfig();
        this.fillSettings(this.subSettings);
        this.about.loaded = true;
    }

    public void saveConfig() {
        if (this.configManager != null) {
            this.configManager.save();
        }
    }

    public LabyModAPI getApi() {
        return this.api;
    }

    public void onRenderPreview(int mouseX, int mouseY, float partialTicks) {
    }

    public void onMouseClickedPreview(int mouseX, int mouseY, int mouseButton) {
    }

    public List<SettingsElement> getSubSettings() {
        return this.subSettings;
    }

    public JsonObject getConfig() {
        return this.config;
    }
}

