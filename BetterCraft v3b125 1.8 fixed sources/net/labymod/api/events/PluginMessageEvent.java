/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.api.events;

import net.minecraft.network.PacketBuffer;

public interface PluginMessageEvent {
    public void receiveMessage(String var1, PacketBuffer var2);
}

