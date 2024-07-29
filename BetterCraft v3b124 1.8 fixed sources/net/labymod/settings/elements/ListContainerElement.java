/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.ModColor;

public class ListContainerElement
extends ControlElement {
    public ListContainerElement(String displayName, ControlElement.IconData iconData) {
        super(displayName, null, iconData);
        String key = "container_" + displayName;
        String translation = LanguageManager.translate(key);
        if (!key.equals(translation)) {
            this.setDisplayName(translation);
        }
        this.setSettingEnabled(true);
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
    }
}

