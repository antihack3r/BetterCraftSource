/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.gui.GuiNBTNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class GuiNBTButton
extends Gui {
    public static final int WIDTH = 9;
    public static final int HEIGHT = 9;
    private Minecraft mc = Minecraft.getMinecraft();
    private byte id;
    private int x;
    private int y;
    private boolean enabled;
    private long hoverTime;

    public GuiNBTButton(byte id2, int x2, int y2) {
        this.id = id2;
        this.x = x2;
        this.y = y2;
    }

    public void draw(int mx2, int my2) {
        this.mc.renderEngine.bindTexture(GuiNBTNode.WIDGET_TEXTURE);
        if (this.inBounds(mx2, my2)) {
            Gui.drawRect(this.x, this.y, this.x + 9, this.y + 9, -2130706433);
            if (this.hoverTime == -1L) {
                this.hoverTime = System.currentTimeMillis();
            }
        } else {
            this.hoverTime = -1L;
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.enabled) {
            this.drawTexturedModalRect(this.x, this.y, (this.id - 1) * 9, 18, 9, 9);
        }
        if (this.hoverTime != -1L && System.currentTimeMillis() - this.hoverTime > 300L) {
            this.drawToolTip(mx2, my2);
        }
    }

    private void drawToolTip(int mx2, int my2) {
        String s2 = NBTStringHelper.getButtonName(this.id);
        int width = this.mc.fontRendererObj.getStringWidth(s2);
        GuiNBTButton.drawRect(mx2 + 4, my2 + 7, mx2 + 5 + width, my2 + 17, -16777216);
        this.mc.fontRendererObj.drawString(s2, mx2 + 5, my2 + 8, 0xFFFFFF);
    }

    public void setEnabled(boolean aFlag) {
        this.enabled = aFlag;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean inBounds(int mx2, int my2) {
        return this.enabled && mx2 >= this.x && my2 >= this.y && mx2 < this.x + 9 && my2 < this.y + 9;
    }

    public byte getId() {
        return this.id;
    }
}

