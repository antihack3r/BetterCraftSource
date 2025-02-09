/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.commands.impl;

import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.commands.Command;
import me.nzxtercode.bettercraft.client.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("hilfe", "?", "help");
    }

    @Override
    public void run(String alias, String[] args) {
        GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Commands: \u00a7f" + CommandManager.getInstance().getCommands().size()));
        GuiNewChat guiNewChat2 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat2.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix)));
        GuiNewChat guiNewChat3 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat3.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "help"));
        GuiNewChat guiNewChat4 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat4.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix)));
        GuiNewChat guiNewChat5 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat5.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "irc"));
        GuiNewChat guiNewChat6 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat6.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "copyip"));
        GuiNewChat guiNewChat7 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat7.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "gm"));
        GuiNewChat guiNewChat8 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat8.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "cc"));
        GuiNewChat guiNewChat9 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat9.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "getgeo"));
        GuiNewChat guiNewChat10 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat10.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "give"));
        GuiNewChat guiNewChat11 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        BetterCraft.getInstance();
        guiNewChat11.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "\u00a77" + CommandManager.getInstance().prefix + "crash"));
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

