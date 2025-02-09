/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

class GuiNumericTextField
extends GuiTextField {
    private String lastSafeText = "0";

    public GuiNumericTextField(int id2, FontRenderer fontRenderer, int x2, int y2, int width, int height) {
        super(id2, fontRenderer, x2, y2, width, height);
        this.setText("0");
    }

    @Override
    public void drawTextBox() {
        try {
            Integer.parseInt("0" + this.getText());
            this.lastSafeText = this.getText();
        }
        catch (NumberFormatException e2) {
            this.setText(this.lastSafeText);
        }
        super.drawTextBox();
    }

    public int getValue() {
        try {
            return Integer.parseInt("0" + this.getText());
        }
        catch (NumberFormatException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public void setValue(int value) {
        String text;
        this.lastSafeText = text = String.valueOf(value);
        this.setText(text);
    }

    @Override
    public String getText() {
        String text = super.getText();
        try {
            int value = Integer.parseInt("0" + text);
            return String.valueOf(value);
        }
        catch (NumberFormatException e2) {
            this.setText(this.lastSafeText);
            return this.lastSafeText;
        }
    }

    @Override
    public void setText(String text) {
        String value;
        try {
            value = String.valueOf(Integer.parseInt("0" + text));
        }
        catch (NumberFormatException e2) {
            value = this.lastSafeText;
        }
        super.setText(value);
        this.lastSafeText = value;
    }
}

