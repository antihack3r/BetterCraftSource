/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import wdl.gui.TextEntry;
import wdl.gui.Utils;

class LinkEntry
extends TextEntry {
    private final String link;
    private final int textWidth;
    private final int linkWidth;

    public LinkEntry(Minecraft mc2, String text, String link) {
        super(mc2, text, 0x5555FF);
        this.link = link;
        this.textWidth = mc2.fontRendererObj.getStringWidth(text);
        this.linkWidth = mc2.fontRendererObj.getStringWidth(link);
    }

    @Override
    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (y2 < 0) {
            return;
        }
        super.drawEntry(slotIndex, x2, y2, listWidth, slotHeight, mouseX, mouseY, isSelected);
        int relativeX = mouseX - x2;
        int relativeY = mouseY - y2;
        if (relativeX >= 0 && relativeX <= this.textWidth && relativeY >= 0 && relativeY <= slotHeight) {
            int drawX = mouseX - 2;
            if (drawX + this.linkWidth + 4 > listWidth + x2) {
                drawX = listWidth + x2 - (4 + this.linkWidth);
            }
            Gui.drawRect(drawX, mouseY - 2, drawX + this.linkWidth + 4, mouseY + this.mc.fontRendererObj.FONT_HEIGHT + 2, Integer.MIN_VALUE);
            Utils.drawStringWithShadow(this.link, drawX + 2, mouseY, 0xFFFFFF);
        }
    }

    @Override
    public boolean mousePressed(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
        if (relativeX >= 0 && relativeX <= this.textWidth) {
            Utils.openLink(this.link);
            return true;
        }
        return false;
    }
}

