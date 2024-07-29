/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.layout;

import java.util.List;
import net.labymod.gui.layout.WindowLayout;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public abstract class WindowElement<T extends WindowLayout>
extends Gui {
    public static final boolean SHOW_GRID = false;
    protected DrawUtils draw;
    protected T layout;
    protected int left;
    protected int top;
    protected int right;
    protected int bottom;
    protected boolean mouseOver;

    public WindowElement(T layout) {
        this.layout = layout;
        this.draw = LabyMod.getInstance().getDrawUtils();
    }

    public WindowElement<T> construct(double left, double top, double right, double bottom) {
        this.left = (int)left;
        this.top = (int)top;
        this.right = (int)right;
        this.bottom = (int)bottom;
        this.init(((WindowLayout)this.layout).getButtonList(), this.left, this.top, this.right, this.bottom);
        return this;
    }

    protected abstract void init(List<GuiButton> var1, int var2, int var3, int var4, int var5);

    public void draw(int mouseX, int mouseY) {
        this.mouseOver = mouseX > this.left && mouseX < this.right && mouseY > this.top && mouseY < this.bottom;
    }

    public abstract boolean mouseClicked(int var1, int var2, int var3);

    public abstract void mouseClickMove(int var1, int var2);

    public abstract void mouseReleased(int var1, int var2, int var3);

    public abstract void mouseInput();

    public abstract void actionPerformed(GuiButton var1);

    public abstract void keyTyped(char var1, int var2);

    public abstract void updateScreen();

    public DrawUtils getDraw() {
        return this.draw;
    }

    public T getLayout() {
        return this.layout;
    }

    public int getLeft() {
        return this.left;
    }

    public int getTop() {
        return this.top;
    }

    public int getRight() {
        return this.right;
    }

    public int getBottom() {
        return this.bottom;
    }

    public boolean isMouseOver() {
        return this.mouseOver;
    }
}

