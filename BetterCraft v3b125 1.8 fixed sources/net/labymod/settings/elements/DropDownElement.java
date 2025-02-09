/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.gui.elements.DropDownMenu;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;

public class DropDownElement<T>
extends ControlElement {
    private DropDownMenu dropDownMenu;
    private Consumer<T> changeListener;
    private Consumer<T> changeCallback;

    public DropDownElement(String diplayName, final String configEntryName, DropDownMenu dropDownMenu, ControlElement.IconData iconData, DrowpDownLoadValue<T> loadValue) {
        super(diplayName, configEntryName, iconData);
        this.dropDownMenu = dropDownMenu;
        if (!configEntryName.isEmpty()) {
            try {
                this.dropDownMenu.setSelected(loadValue.load(String.valueOf(ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings()))));
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.changeListener = new Consumer<T>(){

            @Override
            public void accept(T accepted) {
                try {
                    ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), String.valueOf(accepted));
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (DropDownElement.this.changeCallback != null) {
                    DropDownElement.this.changeCallback.accept(accepted);
                }
            }
        };
    }

    public DropDownElement(String diplayName, DropDownMenu dropDownMenu) {
        super(diplayName, (String)null, (ControlElement.IconData)null);
        this.dropDownMenu = dropDownMenu;
    }

    public DropDownElement(String configEntryName, DropDownMenu dropDownMenu, ControlElement.IconData iconData, DrowpDownLoadValue<T> loadValue) {
        this(configEntryName, configEntryName, dropDownMenu, iconData, loadValue);
    }

    @Override
    public void init() {
        if (this.dropDownMenu != null) {
            this.dropDownMenu.setOpen(false);
        }
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        this.mouseOver = this.dropDownMenu.isMouseOver(mouseX, mouseY);
        if (this.iconData == null) {
            this.dropDownMenu.setX(x2);
            this.dropDownMenu.setY(y2 + 15);
            this.dropDownMenu.setWidth(maxX - x2 - 2);
            this.dropDownMenu.setHeight(maxY - y2 - 15 - 3);
            this.dropDownMenu.draw(mouseX, mouseY);
        } else {
            super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
            LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
            int width = 100;
            this.dropDownMenu.setX(maxX - 100 - 5);
            this.dropDownMenu.setY(y2 + 3);
            this.dropDownMenu.setWidth(100);
            this.dropDownMenu.setHeight(maxY - y2 - 6);
            this.dropDownMenu.draw(mouseX, mouseY);
        }
    }

    @Override
    public int getEntryHeight() {
        return this.iconData == null ? 35 : 23;
    }

    public boolean onClickDropDown(int mouseX, int mouseY, int mouseButton) {
        if (this.dropDownMenu.onClick(mouseX, mouseY, mouseButton)) {
            if (this.changeListener != null) {
                this.changeListener.accept(this.dropDownMenu.getSelected());
            }
            return true;
        }
        return false;
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
        super.mouseRelease(mouseX, mouseY, mouseButton);
        this.dropDownMenu.onRelease(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
        super.mouseClickMove(mouseX, mouseY, mouseButton);
        this.dropDownMenu.onDrag(mouseX, mouseY, mouseButton);
    }

    public void onScrollDropDown() {
        this.dropDownMenu.onScroll();
    }

    public DropDownElement setCallback(Consumer<T> consumer) {
        this.changeCallback = consumer;
        return this;
    }

    public DropDownMenu getDropDownMenu() {
        return this.dropDownMenu;
    }

    public void setChangeListener(Consumer<T> changeListener) {
        this.changeListener = changeListener;
    }

    public static interface DrowpDownLoadValue<T> {
        public T load(String var1);
    }
}

