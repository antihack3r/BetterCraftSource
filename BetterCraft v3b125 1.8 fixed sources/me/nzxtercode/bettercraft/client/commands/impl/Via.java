/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import io.netty.buffer.Unpooled;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatComponentText;

public class Via
extends Command {
    public Via() {
        super("via", "clearchat");
    }

    @Override
    public void run(String alias, String[] args) {
        this.sendServerMessage("This is a predefined message from the client." + ThreadLocalRandom.current().nextInt());
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Message sent to server."));
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }

    private void sendServerMessage(String message) {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.sendQueue != null) {
            try {
                C17PacketCustomPayload packet = new C17PacketCustomPayload("CustomPayloadChannel", new PacketBuffer(Unpooled.buffer()).writeString(message));
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}

