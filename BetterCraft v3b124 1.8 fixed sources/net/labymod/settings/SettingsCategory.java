/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import java.util.ArrayList;
import net.labymod.api.permissions.Permissions;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.Settings;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.CategorySettingsElement;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class SettingsCategory {
    private String title;
    private Settings settings = new Settings();
    private ArrayList<SettingsCategory> subList;
    private GuiButton guiButton;
    private SettingsCategory parentCategory;
    private ResourceLocation resourceLocation;
    private int iconSize = 10;

    public SettingsCategory(String title) {
        this(title, null);
    }

    public SettingsCategory(String title, SettingsCategory parentCategory) {
        this.title = LanguageManager.translate(title);
        this.subList = new ArrayList();
        this.parentCategory = parentCategory;
    }

    public SettingsCategory setParent(SettingsCategory parentCategory) {
        this.parentCategory = parentCategory;
        return this;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public SettingsCategory setSettings(Settings openSettings) {
        this.settings = openSettings;
        return this;
    }

    public SettingsCategory setSettings(ArrayList<SettingsElement> elements) {
        this.settings = new Settings(elements.toArray(new SettingsElement[elements.size()]));
        return this;
    }

    public SettingsCategory addSetting(SettingsElement settingsElement) {
        this.settings.add(settingsElement);
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public ArrayList<SettingsCategory> getSubList() {
        return this.subList;
    }

    public void setGuiButton(GuiButton guiButton) {
        this.guiButton = guiButton;
    }

    public GuiButton getGuiButton() {
        return this.guiButton;
    }

    public SettingsCategory getParentCategory() {
        return this.parentCategory;
    }

    public boolean isCategoryElementsOnly() {
        for (SettingsElement element : this.settings.getElements()) {
            if (element instanceof CategorySettingsElement) continue;
            return false;
        }
        return true;
    }

    public SettingsCategory setIcon(String imageName) {
        this.resourceLocation = new ResourceLocation("labymod/textures/settings/category/" + imageName);
        return this;
    }

    public SettingsCategory setIcon(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public SettingsCategory setIconSize(int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    public void bindPermissionToAll(Permissions.Permission ... permission) {
        for (SettingsElement element : this.settings.getElements()) {
            element.bindPermission(permission);
        }
    }

    public void bindCustomBooleanToAll(String ... args) {
        for (SettingsElement element : this.settings.getElements()) {
            if (!(element instanceof BooleanElement)) continue;
            ((BooleanElement)element).custom(args);
        }
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }

    public int getIconSize() {
        return this.iconSize;
    }
}

