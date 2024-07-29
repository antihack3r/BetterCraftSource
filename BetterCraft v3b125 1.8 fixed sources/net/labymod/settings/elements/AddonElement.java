/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.util.ArrayList;
import java.util.List;
import net.labymod.addon.AddonLoader;
import net.labymod.addon.online.info.AddonInfo;
import net.labymod.addon.online.info.OnlineAddonInfo;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class AddonElement {
    private List<SettingsElement> subSettings = new ArrayList<SettingsElement>();
    private AddonInfo addonInfo;
    private LabyModAddon installedAddon;
    private Consumer<AddonInfo.AddonActionState> callbackAction;
    private AddonInfo.AddonActionState lastActionState;
    private boolean mouseOver = false;
    private int hoverButtonId = -1;
    private boolean canHover = false;
    private double installProgress;

    public AddonElement(AddonInfo addonInfo, LabyModAddon installedAddon, Consumer<AddonInfo.AddonActionState> callbackAction) {
        this.addonInfo = addonInfo;
        this.installedAddon = installedAddon;
        this.callbackAction = callbackAction;
        this.lastActionState = this.installedAddon == null ? AddonInfo.AddonActionState.INSTALL_REVOKE : AddonInfo.AddonActionState.UNINSTALL_REVOKE;
    }

    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        this.draw(x2, y2, maxX, maxY, mouseX, mouseY, true);
    }

    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY, boolean showSettingsButton) {
        boolean hoverVerified;
        boolean verified;
        this.mouseOver = mouseX > x2 && mouseX < maxX && mouseY > y2 && mouseY < maxY && this.canHover;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int iconWidth = this.getEntryHeight();
        int textLineY = y2 + 3;
        if (this.lastActionState == AddonInfo.AddonActionState.ERROR) {
            draw.drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(100, 50, 50, this.isMouseOver() ? 90 : 70));
        } else if (this.lastActionState == AddonInfo.AddonActionState.UNINSTALL || this.lastActionState == AddonInfo.AddonActionState.INSTALL && this.installProgress >= 100.0) {
            draw.drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(50, 50, 100, this.isMouseOver() ? 90 : 70));
        } else if (this.lastActionState == AddonInfo.AddonActionState.INSTALL) {
            double totalWidth = maxX - x2 - iconWidth;
            double progressbarWidth = totalWidth / 100.0 * this.installProgress;
            DrawUtils.drawRect((double)(x2 + iconWidth), (double)y2, (double)(x2 + iconWidth) + progressbarWidth, (double)maxY, ModColor.toRGB(50, 100, 50, this.isMouseOver() ? 90 : 70));
            DrawUtils.drawRect((double)(x2 + iconWidth) + totalWidth, (double)y2, (double)(x2 + iconWidth) + progressbarWidth, (double)maxY, ModColor.toRGB(50, this.isAddonInstalled() ? 100 : 50, 50, this.isMouseOver() ? 90 : 70));
        } else {
            draw.drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(50, this.isAddonInstalled() ? 100 : 50, 50, this.isMouseOver() ? 90 : 70));
        }
        this.drawIcon(x2, y2, iconWidth, this.getEntryHeight());
        boolean bl2 = verified = this.addonInfo instanceof OnlineAddonInfo && ((OnlineAddonInfo)this.addonInfo).isVerified();
        String titleString = String.valueOf(ModColor.cl(this.isAddonInstalled() ? "a" : (verified ? "e" : "f"))) + ModColor.cl("l") + ModColor.createColors(this.addonInfo.getName());
        draw.drawString(titleString, x2 + iconWidth + 5, textLineY);
        textLineY += 15;
        int vX = x2 + iconWidth + 5 + draw.getStringWidth(titleString) + 3;
        int vY = y2 + 3;
        boolean bl3 = hoverVerified = mouseX > vX && mouseX < vX + 8 && mouseY > vY && mouseY < vY + 8;
        if (verified) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_FEATURED);
            draw.drawTexture(vX, vY, 0.0, 0.0, 255.0, 255.0, 8.0, 8.0);
            if (hoverVerified) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, String.valueOf(ModColor.cl("e")) + "Featured");
            }
        }
        draw.drawString(String.valueOf(ModColor.cl("a")) + "by " + this.addonInfo.getAuthor(), x2 + iconWidth + 5, y2 + 12, 0.5);
        List<String> descriptionLines = draw.listFormattedStringToWidth(ModColor.createColors(this.addonInfo.getDescription()), (int)((double)(maxX - x2 - iconWidth - 60) / 0.7));
        int lineCount = 0;
        for (String descriptionLine : descriptionLines) {
            if (lineCount >= 3) {
                descriptionLine = String.valueOf(descriptionLine) + "...";
            }
            draw.drawString(String.valueOf(ModColor.cl("7")) + descriptionLine, x2 + iconWidth + 5, textLineY, 0.7);
            textLineY += 7;
            if (++lineCount >= 3) break;
        }
        this.hoverButtonId = -1;
        if (this.lastActionState == AddonInfo.AddonActionState.INSTALL_REVOKE) {
            int marginX = 10;
            int marginY = (maxY - y2 - 14) / 2;
            if (this.drawButton(ModTextures.BUTTON_DOWNLOAD, y2, 14, 2, 10, marginY, maxX, maxY, mouseX, mouseY)) {
                this.hoverButtonId = 0;
            }
        }
        if (this.lastActionState == AddonInfo.AddonActionState.UNINSTALL_REVOKE) {
            int marginX = 10;
            int marginY = (maxY - y2 - 14) / 2;
            if (this.drawButton(ModTextures.BUTTON_TRASH, y2, 14, 6, marginX, marginY, maxX, maxY, mouseX, mouseY)) {
                this.hoverButtonId = 1;
            }
            marginX = 30;
            if (showSettingsButton && !this.subSettings.isEmpty() && this.drawButton(ModTextures.BUTTON_ADDON_SETTINGS, y2, 14, 6, marginX, marginY, maxX, maxY, mouseX, mouseY)) {
                this.hoverButtonId = 2;
            }
        }
        if (this.lastActionState == AddonInfo.AddonActionState.UNINSTALL) {
            int marginX = 10;
            int marginY = (maxY - y2 - 14) / 2;
            if (this.drawButton(ModTextures.BUTTON_UNDO, y2, 14, 2, 10, marginY, maxX, maxY, mouseX, mouseY)) {
                this.hoverButtonId = 3;
            }
            draw.drawRightString(String.valueOf(ModColor.cl("4")) + "Restart required to uninstall", maxX - 2, y2 + 2, 0.75);
        }
        if (this.lastActionState == AddonInfo.AddonActionState.INSTALL) {
            if (this.installProgress >= 100.0) {
                draw.drawRightString(String.valueOf(ModColor.cl("4")) + "Restart required to install", maxX - 2, y2 + 2, 0.75);
                int marginX = 10;
                int marginY = (maxY - y2 - 14) / 2;
                if (this.drawButton(ModTextures.BUTTON_UNDO, y2, 14, 2, 10, marginY, maxX, maxY, mouseX, mouseY)) {
                    this.hoverButtonId = 4;
                }
            } else {
                draw.drawRightString(String.valueOf((double)((int)(this.installProgress * 10.0)) / 10.0) + "%", maxX - 10, y2 + (maxY - y2 - 8) / 2);
            }
        }
        if (this.isMouseOver() && !hoverVerified && this.hoverButtonId == -1) {
            String[] array = new String[descriptionLines.size()];
            descriptionLines.toArray(array);
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, array);
        }
        if (this.lastActionState == AddonInfo.AddonActionState.ERROR) {
            draw.drawRightString(String.valueOf(ModColor.cl("4")) + "ERROR", maxX - 10, y2 + (maxY - y2 - 8) / 2);
        }
        if (this.isMouseOver()) {
            switch (this.hoverButtonId) {
                case 0: {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Install");
                    break;
                }
                case 1: {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Uninstall");
                    break;
                }
                case 2: {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Settings");
                    break;
                }
                case 3: {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Enable again");
                    break;
                }
                case 4: {
                    TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, "Delete again");
                }
            }
        }
    }

    private boolean drawButton(ResourceLocation resourceLocation, int y2, int buttonSize, int buttonPadding, int marginX, int marginY, int maxX, int maxY, int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        boolean hover = mouseX > maxX - buttonSize - marginX + 1 && mouseX < maxX - buttonSize + buttonSize - marginX + 1 && mouseY > y2 + marginY + 1 && mouseY < y2 + buttonSize + marginY + 1;
        int colorA = hover ? ModColor.toRGB(10, 10, 10, 255) : ModColor.toRGB(220, 220, 220, 255);
        int colorB = hover ? ModColor.toRGB(150, 150, 150, 255) : ModColor.toRGB(0, 0, 0, 255);
        int colorC = hover ? ModColor.toRGB(150, 150, 150, 255) : ModColor.toRGB(180, 180, 180, 255);
        draw.drawRectangle(maxX - buttonSize - (marginX += hover ? 1 : 0), y2 + marginY, maxX - buttonSize + buttonSize - marginX, y2 + buttonSize + marginY, colorA);
        draw.drawRectangle(maxX - buttonSize - marginX + 1, y2 + marginY + 1, maxX - buttonSize + buttonSize - marginX + 1, y2 + buttonSize + marginY + 1, colorB);
        draw.drawRectangle(maxX - buttonSize - marginX + 1, y2 + marginY + 1, maxX - buttonSize + buttonSize - marginX, y2 + buttonSize + marginY, colorC);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        draw.drawTexture(maxX - buttonSize - marginX + buttonPadding / 2 + (hover ? 1 : 0), y2 + marginY + buttonPadding / 2 + (hover ? 1 : 0), 256.0, 256.0, buttonSize - buttonPadding, buttonSize - buttonPadding, 0.8f);
        return hover;
    }

    public void drawIcon(int x2, int y2, int width, int height) {
        if (this.addonInfo.getImageURL() == null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_HEAD_QUESTION);
            LabyMod.getInstance().getDrawUtils().drawTexture(x2, y2, 256.0, 256.0, width, height);
        } else {
            LabyMod.getInstance().getDrawUtils().drawImageUrl(this.addonInfo.getImageURL(), x2, y2, 256.0, 256.0, width, height);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        switch (this.hoverButtonId) {
            case 0: {
                this.callbackAction.accept(AddonInfo.AddonActionState.INSTALL);
                break;
            }
            case 1: {
                this.callbackAction.accept(AddonInfo.AddonActionState.UNINSTALL);
            }
            case 3: {
                this.callbackAction.accept(AddonInfo.AddonActionState.UNINSTALL_REVOKE);
                break;
            }
            case 4: {
                this.callbackAction.accept(AddonInfo.AddonActionState.INSTALL_REVOKE);
            }
        }
    }

    public int getEntryHeight() {
        return 40;
    }

    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    public boolean isHoverSubSettingsButton() {
        return this.hoverButtonId == 2;
    }

    public boolean isAddonInstalled() {
        return this.installedAddon != null;
    }

    public AddonElement canHover(boolean flag) {
        this.canHover = flag;
        return this;
    }

    public void onMouseClickedPreview(int mouseX, int mouseY, int mouseButton) {
        LabyModAddon loadedAddon;
        if (this.addonInfo != null && (loadedAddon = AddonLoader.getInstalledAddonByInfo(this.addonInfo)) != null) {
            loadedAddon.onMouseClickedPreview(mouseX, mouseY, mouseButton);
        }
    }

    public void onRenderPreview(int mouseX, int mouseY, float partialTicks) {
        LabyModAddon loadedAddon;
        if (this.addonInfo != null && (loadedAddon = AddonLoader.getInstalledAddonByInfo(this.addonInfo)) != null) {
            loadedAddon.onRenderPreview(mouseX, mouseY, partialTicks);
        }
    }

    public LabyModAddon getInstalledAddon() {
        return this.installedAddon;
    }

    public Consumer<AddonInfo.AddonActionState> getCallbackAction() {
        return this.callbackAction;
    }

    public AddonInfo.AddonActionState getLastActionState() {
        return this.lastActionState;
    }

    public boolean isMouseOver() {
        return this.mouseOver;
    }

    public int getHoverButtonId() {
        return this.hoverButtonId;
    }

    public boolean isCanHover() {
        return this.canHover;
    }

    public double getInstallProgress() {
        return this.installProgress;
    }

    public List<SettingsElement> getSubSettings() {
        return this.subSettings;
    }

    public AddonInfo getAddonInfo() {
        return this.addonInfo;
    }

    public void setInstalledAddon(LabyModAddon installedAddon) {
        this.installedAddon = installedAddon;
    }

    public void setLastActionState(AddonInfo.AddonActionState lastActionState) {
        this.lastActionState = lastActionState;
    }

    public void setCanHover(boolean canHover) {
        this.canHover = canHover;
    }

    public void setInstallProgress(double installProgress) {
        this.installProgress = installProgress;
    }
}

