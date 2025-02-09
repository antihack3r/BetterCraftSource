/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import java.lang.reflect.Field;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class NumberElement
extends ControlElement {
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private Integer currentValue;
    private Consumer<Integer> changeListener;
    private GuiTextField textField;
    private Consumer<Integer> callback;
    private int minValue = 0;
    private int maxValue = Integer.MAX_VALUE;
    private boolean hoverUp;
    private boolean hoverDown;
    private int steps = 1;
    private long fastTickerCounterValue = 0L;

    public NumberElement(String displayName, final String configEntryName, ControlElement.IconData iconData) {
        super(displayName, configEntryName, iconData);
        if (!configEntryName.isEmpty()) {
            try {
                this.currentValue = (Integer)ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
        if (this.currentValue == null) {
            this.currentValue = this.minValue;
        }
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                try {
                    Field f2 = ModSettings.class.getDeclaredField(configEntryName);
                    if (f2.getType().equals(Integer.TYPE)) {
                        f2.set(LabyMod.getSettings(), accepted);
                    } else {
                        f2.set(LabyMod.getSettings(), String.valueOf(accepted));
                    }
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (NumberElement.this.callback != null) {
                    NumberElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public NumberElement(String displayName, ControlElement.IconData iconData, int currentValue) {
        super(displayName, null, iconData);
        this.currentValue = currentValue;
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                if (NumberElement.this.callback != null) {
                    NumberElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public NumberElement(String configEntryName, ControlElement.IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public NumberElement setMinValue(int minValue) {
        this.minValue = minValue;
        if (this.currentValue < this.minValue) {
            this.currentValue = this.minValue;
        }
        return this;
    }

    public NumberElement setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (this.currentValue > this.maxValue) {
            this.currentValue = this.maxValue;
        }
        return this;
    }

    public NumberElement setRange(int min, int max) {
        this.setMinValue(min);
        this.setMaxValue(max);
        return this;
    }

    public NumberElement setSteps(int steps) {
        this.steps = steps;
        return this;
    }

    public void createTextfield() {
        this.textField = new GuiTextField(-2, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, this.getSubListButtonWidth(), 20);
        this.updateValue();
        this.textField.setFocused(false);
    }

    private void updateValue() {
        this.textField.setText(String.valueOf(this.currentValue));
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        if (this.textField == null) {
            return;
        }
        LabyModCore.getMinecraft().setTextFieldYPosition(this.textField, y2 + 1);
        this.textField.drawTextBox();
        LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        Minecraft.getMinecraft().getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.hoverUp = mouseX > maxX - 15 && mouseX < maxX - 15 + 11 && mouseY > y2 + 2 && mouseY < y2 + 2 + 7;
        this.hoverDown = mouseX > maxX - 15 && mouseX < maxX - 15 + 11 && mouseY > y2 + 12 && mouseY < y2 + 12 + 7;
        draw.drawTexture(maxX - 15, y2 + 2, 99.0, this.hoverUp ? 37.0 : 5.0, 11.0, 7.0, 11.0, 7.0);
        draw.drawTexture(maxX - 15, y2 + 12, 67.0, this.hoverDown ? 52.0 : 20.0, 11.0, 7.0, 11.0, 7.0);
        if (this.isMouseOver() && this.fastTickerCounterValue != 0L) {
            if (this.fastTickerCounterValue > 0L && this.fastTickerCounterValue + 80L < System.currentTimeMillis()) {
                this.fastTickerCounterValue = System.currentTimeMillis();
                if (this.currentValue < this.maxValue) {
                    this.currentValue = this.currentValue + this.steps;
                    this.updateValue();
                }
            }
            if (this.fastTickerCounterValue < 0L && this.fastTickerCounterValue - 80L > System.currentTimeMillis() * -1L) {
                this.fastTickerCounterValue = System.currentTimeMillis() * -1L;
                if (this.currentValue > this.minValue) {
                    this.currentValue = this.currentValue - this.steps;
                    this.updateValue();
                }
            }
        } else {
            this.mouseRelease(mouseX, mouseY, 0);
        }
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
        super.unfocus(mouseX, mouseY, mouseButton);
        this.textField.setFocused(false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hoverUp && this.currentValue < this.maxValue) {
            this.currentValue = this.currentValue + this.steps;
            this.updateValue();
            this.fastTickerCounterValue = System.currentTimeMillis() + 500L;
        }
        if (this.hoverDown && this.currentValue > this.minValue) {
            this.currentValue = this.currentValue - this.steps;
            this.updateValue();
            this.fastTickerCounterValue = System.currentTimeMillis() * -1L - 500L;
        }
        if (this.currentValue > this.maxValue) {
            this.currentValue = this.maxValue;
            this.updateValue();
        }
        if (this.currentValue < this.minValue) {
            this.currentValue = this.minValue;
            this.updateValue();
        }
        this.textField.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
        super.mouseRelease(mouseX, mouseY, mouseButton);
        if (this.fastTickerCounterValue != 0L) {
            this.fastTickerCounterValue = 0L;
            this.changeListener.accept(this.currentValue);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        int preNumber;
        int n2 = preNumber = this.textField.getText().isEmpty() ? this.minValue : Integer.valueOf(this.textField.getText());
        if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
            String newText;
            String numericCheck;
            String currentText = numericCheck = this.textField.getText();
            if (numericCheck.startsWith("-")) {
                numericCheck = numericCheck.replaceFirst("-", "");
            }
            boolean numeric = currentText.isEmpty() || StringUtils.isNumeric(numericCheck);
            int newNumber = 0;
            try {
                int n3 = currentText.isEmpty() || !numeric ? this.minValue : (newNumber = Integer.valueOf(currentText.isEmpty() ? String.valueOf(this.minValue) : currentText).intValue());
                if (!numeric) {
                    newNumber = preNumber;
                }
                if (newNumber > this.maxValue) {
                    newNumber = this.maxValue;
                }
                if (newNumber < this.minValue) {
                    newNumber = this.minValue;
                }
            }
            catch (NumberFormatException e2) {
                newNumber = this.maxValue;
            }
            if (!currentText.equals(newText = String.valueOf(newNumber))) {
                this.textField.setText(String.valueOf(newNumber));
            }
            this.changeListener.accept(newNumber);
            this.currentValue = newNumber;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public GuiTextField getTextField() {
        return this.textField;
    }

    public NumberElement addCallback(Consumer<Integer> callback) {
        this.callback = callback;
        return this;
    }

    public Integer getCurrentValue() {
        return this.currentValue;
    }
}

