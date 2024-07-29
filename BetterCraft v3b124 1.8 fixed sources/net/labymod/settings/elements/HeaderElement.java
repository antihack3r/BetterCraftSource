/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.main.LabyMod;
import net.labymod.settings.elements.SettingsElement;

public class HeaderElement
extends SettingsElement {
    public HeaderElement(String displayName) {
        super(displayName, null);
    }

    @Override
    public void init() {
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        int absoluteY = y2 + 7;
        LabyMod.getInstance().getDrawUtils().drawCenteredString(this.getDisplayName(), x2 + (maxX - x2) / 2, absoluteY);
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

