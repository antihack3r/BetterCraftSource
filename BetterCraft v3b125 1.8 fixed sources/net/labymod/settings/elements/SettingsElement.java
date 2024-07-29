/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.permissions.Permissions;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class SettingsElement {
    public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation BUTTON_PRESS_SOUND = new ResourceLocation(Source.ABOUT_MC_VERSION.startsWith("1.8") ? "gui.button.press" : "ui.button.click");
    protected Minecraft mc = Minecraft.getMinecraft();
    protected Settings subSettings = new Settings();
    protected String displayName;
    private String descriptionText;
    protected boolean mouseOver;
    private int sortingId = 0;
    private boolean visible = true;
    private String configEntryName;
    protected List<Permissions.Permission> permissions = new ArrayList<Permissions.Permission>();

    public SettingsElement(String displayName, String description, String configEntryName) {
        this.displayName = displayName;
        this.descriptionText = description;
        this.configEntryName = configEntryName;
        this.preInit();
    }

    public SettingsElement(String displayName, String configEntryName) {
        this.displayName = displayName;
        this.configEntryName = configEntryName;
        this.initTranslation(this.configEntryName);
        this.preInit();
    }

    protected SettingsElement initTranslation(String key) {
        String titleKey;
        String displayName;
        if (key == null) {
            return this;
        }
        String descriptionKey = "description_" + key.toLowerCase();
        this.descriptionText = LanguageManager.translate(descriptionKey);
        if (this.descriptionText.equals(descriptionKey)) {
            this.descriptionText = null;
        }
        if (!(displayName = LanguageManager.translate(titleKey = "setting_" + key.toLowerCase())).equals(titleKey)) {
            this.displayName = displayName;
        }
        return this;
    }

    public SettingsElement bindPermission(Permissions.Permission ... permissions) {
        Permissions.Permission[] permissionArray = permissions;
        int n2 = permissions.length;
        int n3 = 0;
        while (n3 < n2) {
            Permissions.Permission permission = permissionArray[n3];
            this.permissions.add(permission);
            ++n3;
        }
        return this;
    }

    public SettingsElement bindDescription(String customDescription) {
        this.descriptionText = customDescription;
        return this;
    }

    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        this.mouseOver = mouseX > x2 && mouseX < maxX && mouseY > y2 && mouseY < maxY;
    }

    public abstract void drawDescription(int var1, int var2, int var3);

    public abstract void mouseClicked(int var1, int var2, int var3);

    public abstract void mouseRelease(int var1, int var2, int var3);

    public abstract void mouseClickMove(int var1, int var2, int var3);

    public abstract void keyTyped(char var1, int var2);

    public abstract void unfocus(int var1, int var2, int var3);

    public abstract int getEntryHeight();

    public void preInit() {
    }

    public void init() {
    }

    public void updateScreen() {
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public Settings getSubSettings() {
        return this.subSettings;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescriptionText() {
        return this.descriptionText;
    }

    public boolean isMouseOver() {
        return this.mouseOver;
    }

    public int getSortingId() {
        return this.sortingId;
    }

    public String getConfigEntryName() {
        return this.configEntryName;
    }

    public List<Permissions.Permission> getPermissions() {
        return this.permissions;
    }

    public void setMc(Minecraft mc2) {
        this.mc = mc2;
    }

    public void setSubSettings(Settings subSettings) {
        this.subSettings = subSettings;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setSortingId(int sortingId) {
        this.sortingId = sortingId;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setConfigEntryName(String configEntryName) {
        this.configEntryName = configEntryName;
    }

    public void setPermissions(List<Permissions.Permission> permissions) {
        this.permissions = permissions;
    }
}

