/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipProvider;

public class TooltipManager {
    private GuiScreen guiScreen;
    private TooltipProvider tooltipProvider;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public TooltipManager(GuiScreen guiScreen, TooltipProvider tooltipProvider) {
        this.guiScreen = guiScreen;
        this.tooltipProvider = tooltipProvider;
    }

    public void drawTooltips(int x2, int y2, List buttonList) {
        if (Math.abs(x2 - this.lastMouseX) <= 5 && Math.abs(y2 - this.lastMouseY) <= 5) {
            GuiButton guibutton;
            int i2 = 700;
            if (System.currentTimeMillis() >= this.mouseStillTime + (long)i2 && (guibutton = GuiScreenOF.getSelectedButton(x2, y2, buttonList)) != null) {
                Rectangle rectangle = this.tooltipProvider.getTooltipBounds(this.guiScreen, x2, y2);
                String[] astring = this.tooltipProvider.getTooltipLines(guibutton, rectangle.width);
                if (astring != null) {
                    if (astring.length > 8) {
                        astring = Arrays.copyOf(astring, 8);
                        astring[astring.length - 1] = String.valueOf(astring[astring.length - 1]) + " ...";
                    }
                    if (this.tooltipProvider.isRenderBorder()) {
                        int j2 = -528449408;
                        this.drawRectBorder(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, j2);
                    }
                    Gui.drawRect(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height, -536870912);
                    int l2 = 0;
                    while (l2 < astring.length) {
                        String s2 = astring[l2];
                        int k2 = 0xDDDDDD;
                        if (s2.endsWith("!")) {
                            k2 = 0xFF2020;
                        }
                        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
                        fontrenderer.drawStringWithShadow(s2, rectangle.x + 5, rectangle.y + 5 + l2 * 11, k2);
                        ++l2;
                    }
                }
            }
        } else {
            this.lastMouseX = x2;
            this.lastMouseY = y2;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private void drawRectBorder(int x1, int y1, int x2, int y2, int col) {
        Gui.drawRect(x1, y1 - 1, x2, y1, col);
        Gui.drawRect(x1, y2, x2, y2 + 1, col);
        Gui.drawRect(x1 - 1, y1, x1, y2, col);
        Gui.drawRect(x2, y1, x2 + 1, y2, col);
    }
}

