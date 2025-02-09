/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.gui.GuiNBTNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiCharacterButton
extends Gui {
    public static final int WIDTH = 14;
    public static final int HEIGHT = 14;
    private Minecraft mc = Minecraft.getMinecraft();
    private byte id;
    private int x;
    private int y;
    private boolean enabled;

    public GuiCharacterButton(byte id2, int x2, int y2) {
        this.id = id2;
        this.x = x2;
        this.y = y2;
    }

    public void draw(int mx2, int my2) {
        this.mc.renderEngine.bindTexture(GuiNBTNode.WIDGET_TEXTURE);
        if (this.inBounds(mx2, my2)) {
            Gui.drawRect(this.x, this.y, this.x + 14, this.y + 14, -2130706433);
        }
        if (this.enabled) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            GlStateManager.color(0.5f, 0.5f, 0.5f, 1.0f);
        }
        this.drawTexturedModalRect(this.x, this.y, this.id * 14, 27, 14, 14);
    }

    public void setEnabled(boolean aFlag) {
        this.enabled = aFlag;
    }

    public boolean inBounds(int mx2, int my2) {
        return this.enabled && mx2 >= this.x && my2 >= this.y && mx2 < this.x + 14 && my2 < this.y + 14;
    }

    public byte getId() {
        return this.id;
    }
}

