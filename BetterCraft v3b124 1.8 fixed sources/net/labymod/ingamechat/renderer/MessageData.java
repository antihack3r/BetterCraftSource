/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.renderer;

import java.beans.ConstructorProperties;
import net.labymod.ingamechat.tools.filter.Filters;

public class MessageData {
    private boolean displayInSecondChat;
    private Filters.Filter filter;

    public boolean isDisplayInSecondChat() {
        return this.displayInSecondChat;
    }

    public Filters.Filter getFilter() {
        return this.filter;
    }

    @ConstructorProperties(value={"displayInSecondChat", "filter"})
    public MessageData(boolean displayInSecondChat, Filters.Filter filter) {
        this.displayInSecondChat = displayInSecondChat;
        this.filter = filter;
    }
}

