/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.util.ArrayList;
import java.util.List;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.ColorPicker;
import net.labymod.settings.elements.ControlElement;

public class ColorPickerCheckBoxBulkElement
extends ControlElement {
    private List<ColorPicker> colorPickers = new ArrayList<ColorPicker>();
    private List<CheckBox> checkBoxes = new ArrayList<CheckBox>();
    private boolean checkBoxRightBound = false;

    public ColorPickerCheckBoxBulkElement(String displayName) {
        super(displayName, "", (ControlElement.IconData)null);
    }

    public void addColorPicker(ColorPicker colorPicker) {
        this.colorPickers.add(colorPicker);
    }

    public void addCheckbox(CheckBox checkBox) {
        this.checkBoxes.add(checkBox);
    }

    @Override
    public void init() {
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        int bulkList = 0;
        for (ColorPicker colorPicker : this.colorPickers) {
            colorPicker.setX(x2 + bulkList);
            colorPicker.setY(y2 + 7);
            colorPicker.setWidth(20);
            colorPicker.setHeight(20);
            colorPicker.drawColorPicker(mouseX, mouseY);
            bulkList += 25;
        }
        if (this.checkBoxRightBound) {
            bulkList = maxX;
            for (CheckBox checkBox : this.checkBoxes) {
                checkBox.setX(bulkList - 20);
                checkBox.setY(y2 + 7);
                checkBox.setWidth(20);
                checkBox.setHeight(20);
                checkBox.drawCheckbox(mouseX, mouseY);
                bulkList -= 25;
            }
        } else {
            for (CheckBox checkBox : this.checkBoxes) {
                checkBox.setX(x2 + bulkList);
                checkBox.setY(y2 + 7);
                checkBox.setWidth(20);
                checkBox.setHeight(20);
                checkBox.drawCheckbox(mouseX, mouseY);
                bulkList += 25;
            }
        }
    }

    @Override
    public int getEntryHeight() {
        return 30;
    }

    @Override
    public void drawDescription(int x2, int y2, int screenWidth) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public boolean onClickBulkEntry(int mouseX, int mouseY, int mouseButton) {
        for (ColorPicker colorPicker : this.colorPickers) {
            if (colorPicker.isMouseOver(mouseX, mouseY) || colorPicker.isHoverAdvancedButton() || colorPicker.isHoverSlider()) continue;
            colorPicker.mouseClicked(mouseX, mouseY, mouseButton);
        }
        for (ColorPicker colorPicker : this.colorPickers) {
            if (!colorPicker.mouseClicked(mouseX, mouseY, mouseButton)) continue;
            return true;
        }
        for (CheckBox checkBox : this.checkBoxes) {
            if (!checkBox.mouseClicked(mouseX, mouseY, mouseButton)) continue;
            return true;
        }
        return false;
    }

    public void setCheckBoxRightBound(boolean checkBoxRightBound) {
        this.checkBoxRightBound = checkBoxRightBound;
    }
}

