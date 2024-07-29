/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import wdl.gui.Utils;

class TextEntry
implements GuiListExtended.IGuiListEntry {
    private final String text;
    private final int color;
    protected final Minecraft mc;

    public TextEntry(Minecraft mc2, String text) {
        this(mc2, text, 1048575);
    }

    public TextEntry(Minecraft mc2, String text, int color) {
        this.mc = mc2;
        this.text = text;
        this.color = color;
    }

    @Override
    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (y2 < 0) {
            return;
        }
        Utils.drawStringWithShadow(this.text, x2, y2 + 1, this.color);
    }

    @Override
    public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
    }

    @Override
    public void setSelected(int slotIndex, int p_178011_2_, int p_178011_3_) {
    }
}

