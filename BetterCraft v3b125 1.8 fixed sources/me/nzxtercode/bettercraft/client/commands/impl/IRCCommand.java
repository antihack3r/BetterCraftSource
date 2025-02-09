/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import me.nzxtercode.bettercraft.client.commands.CommandManager;
import me.nzxtercode.bettercraft.client.misc.irc.IRC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import org.jibble.pircbot.User;

public class IRCCommand
extends Command {
    public IRCCommand() {
        super("irc", "clientchat");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("connect")) {
                IRC.getInstance().init();
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(BetterCraft.clientPrefix)).append("Connect to \u00a7f");
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(stringBuilder.append(BetterCraft.clientName).append("s Channel").toString()));
            } else if (args[0].equalsIgnoreCase("disconnect")) {
                IRC.getInstance().disconnectBot();
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Disconnect from \u00a7fIRC"));
            } else if (args[0].equalsIgnoreCase("users")) {
                User[] userArray = IRC.getInstance().getUsers(IRC.getInstance().currentChannel);
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Total users: \u00a7f" + userArray.length));
                GuiNewChat guiNewChat2 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat2.printChatMessage(new ChatComponentText(BetterCraft.clientPrefix));
                Arrays.asList(userArray).forEach(user -> {
                    GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                    Object[] objectArray = new Object[2];
                    BetterCraft.getInstance();
                    objectArray[0] = BetterCraft.clientPrefix;
                    objectArray[1] = user.isOp() ? "\u00a7c" : "\u00a7f";
                    guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(String.format("%s%s", objectArray)) + user.getNick()));
                });
            }
        } else {
            GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " connect"));
            GuiNewChat guiNewChat3 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat3.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " disconnect"));
            GuiNewChat guiNewChat4 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat4.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " users"));
        }
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

