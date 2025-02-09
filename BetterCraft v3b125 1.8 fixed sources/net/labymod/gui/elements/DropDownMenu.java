/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.elements;

import java.awt.Color;
import java.util.ArrayList;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.Gui;

public class DropDownMenu<T>
extends Gui {
    private static final DropDownEntryDrawer defaultDrawer = new DropDownEntryDrawer(){

        @Override
        public void draw(Object object, int x2, int y2, String trimmedEntry) {
            LabyMod.getInstance().getDrawUtils().drawString(trimmedEntry, x2, y2);
        }
    };
    private String title;
    private T selected = null;
    private boolean enabled = true;
    private boolean open;
    private T hoverSelected = null;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int maxY = Integer.MAX_VALUE;
    private ArrayList<T> list = new ArrayList();
    private DropDownEntryDrawer entryDrawer = null;
    private Scrollbar scrollbar;
    private Consumer<T> hoverCallback;

    public DropDownMenu(String title, int x2, int y2, int width, int height) {
        this.title = title;
        this.x = x2;
        this.y = y2;
        this.width = width;
        this.height = height;
    }

    public DropDownMenu<T> fill(T[] values) {
        T[] TArray = values;
        int n2 = values.length;
        int n3 = 0;
        while (n3 < n2) {
            T value = TArray[n3];
            this.list.add(value);
            ++n3;
        }
        return this;
    }

    public void onScroll() {
        if (this.scrollbar != null) {
            this.scrollbar.mouseInput();
            this.scrollbar.setScrollY((int)(this.scrollbar.getScrollY() / (double)this.scrollbar.getSpeed()) * this.scrollbar.getSpeed());
        }
    }

    public void onDrag(int mouseX, int mouseY, int mouseButton) {
        if (this.scrollbar != null) {
            this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        }
    }

    public void onRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.scrollbar != null) {
            this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        }
    }

    public boolean onClick(int mouseX, int mouseY, int mouseButton) {
        if (this.scrollbar != null && this.scrollbar.isHoverTotalScrollbar(mouseX, mouseY)) {
            this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
            return true;
        }
        if (!this.enabled || this.list.isEmpty()) {
            return false;
        }
        if (mouseX > this.x - 1 && mouseX < this.x + this.width + 1 && mouseY > this.y - 1 && mouseY < this.y + this.height + 1) {
            boolean bl2 = this.open = !this.open;
            if (this.open && this.list.size() > 10) {
                this.scrollbar = new Scrollbar(13);
                this.scrollbar.setSpeed(13);
                this.scrollbar.setListSize(this.list.size());
                this.scrollbar.setPosition(this.x + this.width - 5, this.y + this.height + 1, this.x + this.width, this.maxY == Integer.MAX_VALUE ? this.y + this.height + 1 + 130 - 1 : this.maxY);
            }
            return true;
        }
        if (this.hoverSelected != null) {
            this.selected = this.hoverSelected;
            this.open = false;
            return true;
        }
        this.open = false;
        if (!this.open && this.hoverCallback != null) {
            this.hoverCallback.accept(null);
        }
        return false;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX > this.x - 1 && mouseX < this.x + this.width + 1 && mouseY > this.y - 1 && mouseY < this.y + this.height + 1;
    }

    public void draw(int mouseX, int mouseY) {
        T prevHover = this.hoverSelected;
        this.hoverSelected = null;
        DropDownMenu.drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, Color.GRAY.getRGB());
        DropDownMenu.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, Color.BLACK.getRGB());
        if (this.selected != null) {
            String trimmedEntry = LabyMod.getInstance().getDrawUtils().trimStringToWidth(String.valueOf(ModColor.cl("f")) + this.selected, this.width - 5);
            (this.entryDrawer == null ? defaultDrawer : this.entryDrawer).draw(this.selected, this.x + 5, this.y + this.height / 2 - 4, trimmedEntry);
        }
        if (this.enabled && !this.list.isEmpty()) {
            int i2 = 0;
            while (i2 <= 5) {
                DropDownMenu.drawRect(this.x + this.width - 16 + i2, this.y + this.height / 2 - 2 + i2, this.x + this.width - 5 - i2, this.y + this.height / 2 + 1 - 2 + i2, Color.LIGHT_GRAY.getRGB());
                ++i2;
            }
        }
        if (this.title != null) {
            LabyMod.getInstance().getDrawUtils().drawString(LabyModCore.getMinecraft().getFontRenderer().trimStringToWidth(this.title, this.width), this.x, this.y - 13);
        }
        if (this.open) {
            int yPositionList;
            int entryHeight = 13;
            int maxPointY = this.y + this.height + 2 + 13 * this.list.size();
            boolean buildUp = maxPointY > this.maxY;
            int n2 = yPositionList = buildUp ? this.y - 13 - 1 : this.y + this.height + 1;
            if (this.scrollbar != null) {
                yPositionList += (int)this.scrollbar.getScrollY();
            }
            for (T option : this.list) {
                if (this.scrollbar == null || yPositionList > this.y + 5 && yPositionList + 13 < this.scrollbar.getPosBottom() + 2) {
                    boolean hover;
                    boolean bl2 = hover = mouseX > this.x && mouseX < this.x + this.width && mouseY > yPositionList && mouseY < yPositionList + 13;
                    if (hover) {
                        this.hoverSelected = option;
                    }
                    DropDownMenu.drawRect(this.x - 1, yPositionList, this.x + this.width + 1, yPositionList + 13, ModColor.toRGB(0, 30, 70, 250));
                    DropDownMenu.drawRect(this.x, yPositionList + (buildUp ? 1 : 0), this.x + this.width, yPositionList + 13 - 1 + (buildUp ? 1 : 0), hover ? ModColor.toRGB(55, 55, 155, 215) : ModColor.toRGB(0, 10, 10, 250));
                    String trimmedEntry2 = LabyMod.getInstance().getDrawUtils().trimStringToWidth(String.valueOf(ModColor.cl("f")) + option, this.width - 5);
                    (this.entryDrawer == null ? defaultDrawer : this.entryDrawer).draw(option, this.x + 5, yPositionList + 3, trimmedEntry2);
                }
                yPositionList += buildUp ? -13 : 13;
            }
            if (this.scrollbar != null) {
                this.scrollbar.draw();
            }
        }
        if ((this.hoverSelected != null && prevHover == null || prevHover != null && !prevHover.equals(this.hoverSelected)) && this.hoverCallback != null) {
            this.hoverCallback.accept(this.hoverSelected);
        }
    }

    public void clear() {
        this.open = false;
        this.selected = null;
        this.list.clear();
        this.setSelected(null);
    }

    public void remove(T type) {
        this.list.remove(type);
    }

    public void addOption(T option) {
        this.list.add(option);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getSelected() {
        return this.selected;
    }

    public void setSelected(T selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public T getHoverSelected() {
        return this.hoverSelected;
    }

    public void setHoverSelected(T hoverSelected) {
        this.hoverSelected = hoverSelected;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public DropDownEntryDrawer getEntryDrawer() {
        return this.entryDrawer;
    }

    public void setEntryDrawer(DropDownEntryDrawer entryDrawer) {
        this.entryDrawer = entryDrawer;
    }

    public void setHoverCallback(Consumer<T> hoverCallback) {
        this.hoverCallback = hoverCallback;
    }

    public static interface DropDownEntryDrawer {
        public void draw(Object var1, int var2, int var3, String var4);
    }
}

