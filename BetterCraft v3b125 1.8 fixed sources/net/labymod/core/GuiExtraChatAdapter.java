/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core;

public interface GuiExtraChatAdapter {
    public void drawChat(int var1);

    public void clearChatMessages();

    public void setChatLine(Object var1, int var2, int var3, boolean var4, boolean var5);

    public void refreshChat();

    public void scroll(int var1);

    public Object getChatComponent(int var1, int var2);
}

