/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;

public class ChatLine {
    private final int updateCounterCreated;
    private IChatComponent lineString;
    private final int chatLineID;

    public ChatLine(int p_i45000_1_, IChatComponent p_i45000_2_, int p_i45000_3_) {
        this.lineString = p_i45000_2_;
        this.updateCounterCreated = p_i45000_1_;
        this.chatLineID = p_i45000_3_;
    }

    public void setChatComponent(IChatComponent lineString) {
        this.lineString = lineString;
    }

    public IChatComponent getChatComponent() {
        return this.lineString;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getChatLineID() {
        return this.chatLineID;
    }
}
