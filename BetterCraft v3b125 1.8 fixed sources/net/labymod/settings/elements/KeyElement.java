/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import org.lwjgl.input.Keyboard;

public class KeyElement
extends ControlElement {
    private Integer currentKey;
    private Consumer<Integer> changeListener;
    private ModTextField textField;
    private Consumer<Integer> callback;

    public KeyElement(String displayName, final String configEntryName, ControlElement.IconData iconData) {
        super(displayName, configEntryName, iconData);
        if (!configEntryName.isEmpty()) {
            try {
                this.currentKey = (Integer)ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
        if (this.currentKey == null) {
            this.currentKey = -1;
        }
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                try {
                    ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), accepted);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (KeyElement.this.callback != null) {
                    KeyElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public KeyElement(String displayName, final LabyModAddon labymodAddon, ControlElement.IconData iconData, final String attributeName, int currentKey) {
        super(displayName, iconData);
        this.currentKey = currentKey;
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                labymodAddon.getConfig().addProperty(attributeName, accepted);
                labymodAddon.loadConfig();
                if (KeyElement.this.callback != null) {
                    KeyElement.this.callback.accept(accepted);
                }
            }
        };
        this.createTextfield();
    }

    public KeyElement(String displayName, ControlElement.IconData iconData, int currentKey, Consumer<Integer> changeListener) {
        super(displayName, iconData);
        this.currentKey = currentKey;
        this.changeListener = changeListener;
        this.createTextfield();
    }

    public KeyElement(String configEntryName, ControlElement.IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public void createTextfield() {
        this.updateValue();
        this.textField.setCursorPositionEnd();
        this.textField.setFocused(false);
    }

    private void updateValue() {
        if (this.currentKey == -1) {
            this.textField.setText("NONE");
        } else {
            try {
                this.textField.setText(Keyboard.getKeyName(this.currentKey));
            }
            catch (Exception error) {
                this.currentKey = -1;
                error.printStackTrace();
            }
        }
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        if (this.textField == null) {
            return;
        }
        this.textField.yPosition = y2 + 1;
        this.textField.drawTextBox();
        LabyMod.getInstance().getDrawUtils().drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
        super.unfocus(mouseX, mouseY, mouseButton);
        this.textField.setFocused(false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, 0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            keyCode = -1;
        }
        if (this.textField.isFocused()) {
            this.textField.setFocused(false);
            this.currentKey = keyCode;
            this.changeListener.accept(keyCode);
            this.updateValue();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public KeyElement maxLength(int maxLength) {
        this.textField.setMaxStringLength(maxLength);
        return this;
    }

    public KeyElement addCallback(Consumer<Integer> callback) {
        this.callback = callback;
        return this;
    }

    public ModTextField getTextField() {
        return this.textField;
    }
}

