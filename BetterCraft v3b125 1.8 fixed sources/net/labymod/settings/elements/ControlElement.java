/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.util.List;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ControlElement
extends SettingsElement {
    protected IconData iconData;
    private GuiButton buttonAdvanced;
    private boolean selected;
    private boolean hoverable;
    private boolean hideSubList;
    private boolean settingEnabled;
    private int lastMaxX;
    private boolean blocked = false;

    public ControlElement(String elementName, String configEntryName, IconData iconData) {
        super(elementName, configEntryName);
        this.iconData = iconData;
        if (this.iconData != null && this.iconData.isUseConfigName()) {
            this.iconData.apply(configEntryName == null ? elementName : configEntryName);
        }
        this.createButton();
    }

    public ControlElement(String displayName, IconData iconData) {
        super(displayName, null);
        this.iconData = iconData;
        this.createButton();
    }

    private void createButton() {
        this.buttonAdvanced = new GuiButton(-2, 0, 0, 23, 20, "");
    }

    public GuiButton getButtonAdvanced() {
        return this.buttonAdvanced;
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        this.lastMaxX = maxX;
        if (this.displayName != null) {
            int iconWidth;
            LabyMod.getInstance().getDrawUtils().drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(80, 80, 80, this.selected ? 130 : (this.hoverable && this.mouseOver ? 80 : 60)));
            int n2 = iconWidth = this.iconData != null ? 25 : 2;
            if (this.iconData != null) {
                if (this.iconData.hastextureIcon()) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(this.iconData.gettextureIcon());
                    LabyMod.getInstance().getDrawUtils().drawTexture(x2 + 3, y2 + 3, 256.0, 256.0, 16.0, 16.0);
                } else if (this.iconData.hasMaterialIcon()) {
                    LabyMod.getInstance().getDrawUtils().drawItem(this.iconData.getMaterialIcon().createItemStack(), x2 + 3, y2 + 2, null);
                }
            }
            this.hideSubList = false;
            if (!this.permissions.isEmpty()) {
                boolean defaultDisabled = false;
                boolean isEnabledByServer = true;
                String allowedPermissions = "";
                for (Permissions.Permission permission : this.permissions) {
                    if (!permission.isDefaultEnabled()) {
                        defaultDisabled = true;
                    }
                    if (!allowedPermissions.isEmpty()) {
                        allowedPermissions = String.valueOf(allowedPermissions) + ModColor.cl("7") + ", ";
                    }
                    allowedPermissions = String.valueOf(allowedPermissions) + ModColor.cl("e") + permission.getDisplayName();
                    if (Permissions.isAllowed(permission)) continue;
                    isEnabledByServer = false;
                }
                if (defaultDisabled || !isEnabledByServer) {
                    boolean hover = mouseX > x2 - 13 && mouseX < x2 - 13 + 7 && mouseY > y2 + 3 && mouseY < y2 + 3 + 16;
                    DrawUtils draw = LabyMod.getInstance().getDrawUtils();
                    if (isEnabledByServer) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_CHECKBOX);
                        draw.drawTexture(x2 - 13, y2 + 7, 0.0, 0.0, 255.0, 255.0, 10.0, 10.0);
                    } else {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_EXCLAMATION);
                        draw.drawTexture(x2 - 13, y2 + 3, hover ? 127.0 : 0.0, 0.0, 127.0, 255.0, 7.0, 16.0);
                    }
                    if (hover) {
                        String text = String.valueOf(LanguageManager.translate("permission_information_" + (isEnabledByServer ? "enabled" : "disabled"))) + (isEnabledByServer ? "" : allowedPermissions);
                        TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, (String[])draw.listFormattedStringToWidth(text, draw.getWidth() / 3).toArray());
                    }
                }
            }
            if (this.blocked) {
                LabyMod.getInstance().getDrawUtils().drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(0, 0, 0, 200));
            }
        }
    }

    private void renderAdvancedButton(int x2, int y2, int maxX, int maxY, boolean mouseOver, int mouseX, int mouseY) {
        if (!this.hasSubList()) {
            return;
        }
        if (this.buttonAdvanced == null) {
            return;
        }
        if (this.hideSubList) {
            return;
        }
        boolean enabled = this.settingEnabled;
        LabyModCore.getMinecraft().setButtonXPosition(this.buttonAdvanced, maxX - this.getSubListButtonWidth() - 2);
        LabyModCore.getMinecraft().setButtonYPosition(this.buttonAdvanced, y2 + 1);
        this.buttonAdvanced.enabled = enabled;
        LabyModCore.getMinecraft().drawButton(this.buttonAdvanced, mouseX, mouseY);
        this.mc.getTextureManager().bindTexture(ModTextures.BUTTON_ADVANCED);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, enabled ? 1.0f : 0.2f);
        LabyMod.getInstance().getDrawUtils().drawTexture(LabyModCore.getMinecraft().getXPosition(this.buttonAdvanced) + 4, LabyModCore.getMinecraft().getYPosition(this.buttonAdvanced) + 3, 0.0, 0.0, 256.0, 256.0, 14.0, 14.0, 2.0f);
    }

    public int getSubListButtonWidth() {
        return this.hasSubList() ? 23 : 0;
    }

    @Override
    public void drawDescription(int x2, int y2, int screenWidth) {
        String description = this.getDescriptionText();
        if (description == null) {
            return;
        }
        if (this.buttonAdvanced != null && this.buttonAdvanced.hovered) {
            return;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        List<String> list = draw.listFormattedStringToWidth(description, screenWidth / 3);
        TooltipHelper.getHelper().pointTooltip(x2, y2, 500L, (String[])list.toArray());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public int getEntryHeight() {
        return 23;
    }

    public boolean hasSubList() {
        return !this.subSettings.getElements().isEmpty() && !this.hideSubList;
    }

    public ControlElement setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public ControlElement setHoverable(boolean hoverable) {
        this.hoverable = hoverable;
        return this;
    }

    public ControlElement hideSubListButton() {
        this.hideSubList = true;
        return this;
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
    }

    public IconData getIconData() {
        return this.iconData;
    }

    public void setSettingEnabled(boolean settingEnabled) {
        this.settingEnabled = settingEnabled;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public static class IconData {
        private boolean useConfigName = false;
        private Material materialIcon;
        private ResourceLocation textureIcon;

        public IconData(Material materialIcon) {
            this.materialIcon = materialIcon;
        }

        public IconData(ResourceLocation textureIcon) {
            this.textureIcon = textureIcon;
        }

        public IconData(String resourceLocationPath) {
            this.textureIcon = new ResourceLocation(resourceLocationPath);
        }

        public IconData() {
            this.useConfigName = true;
        }

        public Material getMaterialIcon() {
            return this.materialIcon;
        }

        public ResourceLocation gettextureIcon() {
            return this.textureIcon;
        }

        public boolean hastextureIcon() {
            return this.textureIcon != null;
        }

        public boolean hasMaterialIcon() {
            return this.materialIcon != null;
        }

        public boolean isUseConfigName() {
            return this.useConfigName;
        }

        public void apply(String configEntryName) {
            this.textureIcon = new ResourceLocation("labymod/textures/settings/settings/" + configEntryName.toLowerCase() + ".png");
        }
    }
}

