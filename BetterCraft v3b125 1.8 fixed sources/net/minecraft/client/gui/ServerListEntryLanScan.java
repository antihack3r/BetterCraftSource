/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan
implements GuiListExtended.IGuiListEntry {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        String s2;
        int i2 = y2 + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
        GuiScreen cfr_ignored_0 = this.mc.currentScreen;
        this.mc.fontRendererObj.drawString(I18n.format("lanServer.scanning", new Object[0]), GuiScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning", new Object[0])) / 2, i2, 0xFFFFFF);
        switch ((int)(Minecraft.getSystemTime() / 300L % 4L)) {
            default: {
                s2 = "O o o";
                break;
            }
            case 1: 
            case 3: {
                s2 = "o O o";
                break;
            }
            case 2: {
                s2 = "o o O";
            }
        }
        GuiScreen cfr_ignored_1 = this.mc.currentScreen;
        this.mc.fontRendererObj.drawString(s2, GuiScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(s2) / 2, i2 + this.mc.fontRendererObj.FONT_HEIGHT, 0x808080);
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
    }
}

