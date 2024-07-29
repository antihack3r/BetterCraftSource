/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.renderer;

import java.beans.ConstructorProperties;

public class ChatLine {
    private String message;
    private boolean secondChat;
    private String room;
    private Object component;
    private int updateCounter;
    private int chatLineId;
    private Integer highlightColor;

    public String getRoom() {
        return this.room == null ? "Global" : this.room;
    }

    @ConstructorProperties(value={"message", "secondChat", "room", "component", "updateCounter", "chatLineId", "highlightColor"})
    public ChatLine(String message, boolean secondChat, String room, Object component, int updateCounter, int chatLineId, Integer highlightColor) {
        this.message = message;
        this.secondChat = secondChat;
        this.room = room;
        this.component = component;
        this.updateCounter = updateCounter;
        this.chatLineId = chatLineId;
        this.highlightColor = highlightColor;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSecondChat() {
        return this.secondChat;
    }

    public Object getComponent() {
        return this.component;
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public int getChatLineId() {
        return this.chatLineId;
    }

    public Integer getHighlightColor() {
        return this.highlightColor;
    }
}

