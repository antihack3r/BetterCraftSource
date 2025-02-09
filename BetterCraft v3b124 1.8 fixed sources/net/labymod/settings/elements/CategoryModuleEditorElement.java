/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;

public class CategoryModuleEditorElement
extends SettingsElement {
    private ControlElement.IconData iconData;

    public CategoryModuleEditorElement(String displayName, ControlElement.IconData iconData) {
        super(displayName, null);
        this.iconData = iconData;
    }

    @Override
    public void init() {
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        int absoluteY = y2 + 7;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawRectangle(x2, y2, maxX, maxY, ModColor.toRGB(200, 200, 200, this.mouseOver ? 50 : 30));
        int imageSize = maxY - y2;
        if (this.iconData.hastextureIcon()) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.iconData.gettextureIcon());
            LabyMod.getInstance().getDrawUtils().drawTexture(x2 + 2, y2 + 2, 256.0, 256.0, 18.0, 18.0);
        } else if (this.iconData.hasMaterialIcon()) {
            LabyMod.getInstance().getDrawUtils().drawItem(this.iconData.getMaterialIcon().createItemStack(), x2 + 3, y2 + 3, null);
        }
        draw.drawString(this.getDisplayName(), x2 + imageSize + 5, absoluteY);
        int totalSubCount = 0;
        int enabledCount = 0;
        for (SettingsElement element : this.getSubSettings().getElements()) {
            boolean bl2 = false;
        }
        draw.drawRightString(String.valueOf(enabledCount) + ModColor.cl("7") + "/" + ModColor.cl("f") + totalSubCount, maxX - 5, absoluteY);
    }

    public ControlElement.IconData getIconData() {
        return this.iconData;
    }

    @Override
    public int getEntryHeight() {
        return 22;
    }

    @Override
    public void drawDescription(int x2, int y2, int screenWidth) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
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
}

