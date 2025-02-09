/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.gui.GuiNBTTree;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiNBTNode
extends Gui {
    public static final ResourceLocation WIDGET_TEXTURE = new ResourceLocation("nbtedit", "textures/gui/widgets.png");
    private Minecraft mc = Minecraft.getMinecraft();
    private Node<NamedNBT> node;
    private GuiNBTTree tree;
    protected int width;
    protected int height;
    protected int x;
    protected int y;
    private String displayString;

    public GuiNBTNode(GuiNBTTree tree, Node<NamedNBT> node, int x2, int y2) {
        this.tree = tree;
        this.node = node;
        this.x = x2;
        this.y = y2;
        this.height = this.mc.fontRendererObj.FONT_HEIGHT;
        this.updateDisplay();
    }

    private boolean inBounds(int mx2, int my2) {
        return mx2 >= this.x && my2 >= this.y && mx2 < this.width + this.x && my2 < this.height + this.y;
    }

    private boolean inHideShowBounds(int mx2, int my2) {
        return mx2 >= this.x - 9 && my2 >= this.y && mx2 < this.x && my2 < this.y + this.height;
    }

    public boolean shouldDrawChildren() {
        return this.node.shouldDrawChildren();
    }

    public boolean clicked(int mx2, int my2) {
        return this.inBounds(mx2, my2);
    }

    public boolean hideShowClicked(int mx2, int my2) {
        if (this.node.hasChildren() && this.inHideShowBounds(mx2, my2)) {
            this.node.setDrawChildren(!this.node.shouldDrawChildren());
            return true;
        }
        return false;
    }

    public Node<NamedNBT> getNode() {
        return this.node;
    }

    public void shift(int dy2) {
        this.y += dy2;
    }

    public void updateDisplay() {
        this.displayString = NBTStringHelper.getNBTNameSpecial(this.node.getObject());
        this.width = this.mc.fontRendererObj.getStringWidth(this.displayString) + 12;
    }

    public void draw(int mx2, int my2) {
        boolean selected = this.tree.getFocused() == this.node;
        boolean hover = this.inBounds(mx2, my2);
        boolean chHover = this.inHideShowBounds(mx2, my2);
        int color = selected ? 255 : (hover ? 0xFFFFA0 : (this.node.hasParent() ? 0xE0E0E0 : -6250336));
        this.mc.renderEngine.bindTexture(WIDGET_TEXTURE);
        if (selected) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawRect(this.x + 11, this.y, this.x + this.width, this.y + this.height, Integer.MIN_VALUE);
        }
        if (this.node.hasChildren()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexturedModalRect(this.x - 9, this.y, this.node.shouldDrawChildren() ? 9 : 0, chHover ? this.height : 0, 9, this.height);
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(this.x + 1, this.y, (this.node.getObject().getNBT().getId() - 1) * 9, 18, 9, 9);
        this.drawString(this.mc.fontRendererObj, this.displayString, this.x + 11, this.y + (this.height - 8) / 2, color);
    }

    public boolean shouldDraw(int top, int bottom) {
        return this.y + this.height >= top && this.y <= bottom;
    }
}

