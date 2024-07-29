/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements.basement;

import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;

public abstract class Element {
    public boolean draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        return (double)mouseX > x2 && (double)mouseX < x2 + width && (double)mouseY > y2 && (double)mouseY < y2 + height;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton, GuiResourcepacks24 gui) {
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick, GuiResourcepacks24 gui) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton, GuiResourcepacks24 gui) {
    }
}

