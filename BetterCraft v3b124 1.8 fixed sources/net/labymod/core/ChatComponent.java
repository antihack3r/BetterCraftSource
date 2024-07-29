/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

public class ChatComponent {
    private String unformattedText;
    private String formattedText;
    private String json;

    public ChatComponent(String unformattedText, String formattedText) {
        this(unformattedText, formattedText, null);
    }

    public ChatComponent(String unformattedText, String formattedText, String json) {
        this.unformattedText = unformattedText;
        this.formattedText = formattedText;
        this.json = json;
    }

    public String getUnformattedText() {
        return this.unformattedText;
    }

    public String getFormattedText() {
        return this.formattedText;
    }

    public String getJson() {
        return this.json;
    }
}

