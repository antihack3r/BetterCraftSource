/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.events;

import com.google.gson.JsonElement;

public interface ServerMessageEvent {
    public void onServerMessage(String var1, JsonElement var2);
}

