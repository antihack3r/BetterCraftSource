/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.views;

import net.labymod.addons.resourcepacks24.Resourcepacks24;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.basement.PackElement;
import net.labymod.addons.resourcepacks24.gui.views.shared.SharedView;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;

public abstract class View {
    public static final int PADDING = 3;
    protected final GuiResourcepacks24 gui;
    protected final SharedView shared;
    protected String title;
    public double x;
    public double y;
    public double width;
    public double height;
    protected double margin;
    protected double postRenderCursorY = 0.0;
    protected Scrollbar scrollbar = new Scrollbar(1);
    private boolean lastMouseOver;
    protected boolean loaded = false;
    protected boolean pendingScrollbarUpdate = false;

    public View(GuiResourcepacks24 gui, String title) {
        this.gui = gui;
        this.shared = gui.getSharedView();
        this.title = title;
    }

    public void init(double x2, double y2, double width, double height, double margin) {
        this.x = x2;
        this.y = y2;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.scrollbar.update(1);
        this.scrollbar.setPosition(x2 + width - 5.0, y2, x2 + width, y2 + height);
        this.scrollbar.setSpeed(25);
        this.pendingScrollbarUpdate = true;
    }

    public void renderPre(int mouseX, int mouseY) {
        this.lastMouseOver = (double)mouseX > this.x && (double)mouseX < this.x + this.width && (double)mouseY > this.y && (double)mouseY < this.y + this.height;
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
    }

    public void renderPost(int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawCenteredString(String.valueOf(ModColor.cl('l')) + ModColor.cl('n') + this.title, this.x + this.width / 2.0, this.y - 12.0);
        draw.drawGradientShadowTop(this.y, this.x, this.x + this.width);
        draw.drawGradientShadowBottom(this.y + this.height, this.x, this.x + this.width);
        if (this.loaded) {
            if (this.shared.draggingElement != null && this.lastMouseOver && this.shared.hoverElement == null) {
                draw.drawRectBorder(this.x, this.y, this.x + this.width, this.y + this.height, ModColor.toRGB(255, 255, 255, 100), 1.0);
                this.shared.hoverView = this;
            }
            if (this.pendingScrollbarUpdate) {
                this.pendingScrollbarUpdate = false;
                this.scrollbar.setEntryHeight(this.postRenderCursorY - this.y);
            }
            this.scrollbar.draw(mouseX, mouseY);
        } else {
            draw.drawCenteredString("Loading..", this.x + this.width / 2.0, this.y + this.height / 2.0);
        }
    }

    protected void drawPack(PackElement element, double x2, double y2, double width, double height, int mouseX, int mouseY) {
        this.drawPack(element, x2, y2, width, height, false, -1, -1, mouseX, mouseY);
    }

    protected void drawPack(PackElement element, double x2, double y2, double width, double height, boolean selected, int index, int total, int mouseX, int mouseY) {
        boolean inView;
        boolean visible = this.shared.draggingElement == null || !this.shared.draggingElement.isValid() || this.shared.draggingElement.getElement() != element;
        boolean bl2 = inView = y2 + height > 0.0 && y2 < (double)GuiResourcepacks24.height;
        if (inView && visible && element.draw(x2 + 3.0, y2, width - 6.0, height, mouseX, mouseY) && (index != -1 || !selected)) {
            element.drawControls(x2 + 3.0, y2, width, height, index == 0, index == total - 1, selected, mouseX, mouseY, this.gui);
            this.shared.hoverElement = element;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    public void handleMouseInput() {
        if (this.lastMouseOver) {
            this.scrollbar.mouseInput();
        }
    }

    public boolean isInsideView(int mouseX, int mouseY) {
        return (double)mouseX > this.x && (double)mouseX < this.x + this.width && (double)mouseY > this.y && (double)mouseY < this.y + this.height;
    }

    protected void throwError(String message) {
        this.shared.lastErrorMessage = message;
    }

    public void onLoaded(Class<? extends View> clazz) {
        if (clazz.equals(this.getClass())) {
            this.loaded = true;
            this.pendingScrollbarUpdate = true;
        }
    }

    public void loadRepository(Resourcepacks24 context) {
    }
}

