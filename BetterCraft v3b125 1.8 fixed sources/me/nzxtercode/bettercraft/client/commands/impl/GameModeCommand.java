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
import net.minecraft.world.WorldSettings;

public class GameModeCommand
extends Command {
    private String tocopy;

    public GameModeCommand() {
        super("gm", "gamemode");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                Minecraft.getMinecraft().playerController.setGameType(WorldSettings.GameType.SURVIVAL);
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Your Gamemode switch to \u00a7f" + Minecraft.getMinecraft().playerController.getCurrentGameType().name()));
            } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                Minecraft.getMinecraft().playerController.setGameType(WorldSettings.GameType.CREATIVE);
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Your Gamemode switch to \u00a7f" + Minecraft.getMinecraft().playerController.getCurrentGameType().name()));
            } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                Minecraft.getMinecraft().playerController.setGameType(WorldSettings.GameType.ADVENTURE);
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Your Gamemode switch to \u00a7f" + Minecraft.getMinecraft().playerController.getCurrentGameType().name()));
            } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                Minecraft.getMinecraft().playerController.setGameType(WorldSettings.GameType.SPECTATOR);
                GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
                BetterCraft.getInstance();
                guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + "Your Gamemode switch to \u00a7f" + Minecraft.getMinecraft().playerController.getCurrentGameType().name()));
            }
        } else {
            GuiNewChat guiNewChat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " 0"));
            GuiNewChat guiNewChat2 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat2.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " 1"));
            GuiNewChat guiNewChat3 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat3.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " 2"));
            GuiNewChat guiNewChat4 = Minecraft.getMinecraft().ingameGUI.getChatGUI();
            BetterCraft.getInstance();
            guiNewChat4.printChatMessage(new ChatComponentText(String.valueOf(BetterCraft.clientPrefix) + CommandManager.getInstance().prefix + alias + " 3"));
        }
    }

    @Override
    public List<String> autocomplete(int arg2, String[] args) {
        return new ArrayList<String>();
    }
}

