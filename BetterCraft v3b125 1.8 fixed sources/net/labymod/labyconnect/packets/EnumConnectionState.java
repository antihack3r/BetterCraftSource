/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.packets;

import net.labymod.main.lang.LanguageManager;

public enum EnumConnectionState {
    HELLO(-1, "d"),
    LOGIN(0, "b"),
    PLAY(1, "a"),
    ALL(2, "f", "ALL"),
    OFFLINE(3, "c");

    private int number;
    private String displayColor;
    private String buttonState;

    private EnumConnectionState(int number, String displayColor) {
        this.number = number;
        this.displayColor = displayColor;
        this.buttonState = LanguageManager.translate("chat_button_state_" + this.name().toLowerCase());
    }

    private EnumConnectionState(int number, String displayColor, String buttonState) {
        this.number = number;
        this.displayColor = displayColor;
        this.buttonState = buttonState;
    }

    public int getNumber() {
        return this.number;
    }

    public String getDisplayColor() {
        return this.displayColor;
    }

    public String getButtonState() {
        return this.buttonState;
    }
}

